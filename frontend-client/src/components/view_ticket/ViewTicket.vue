<script setup>
import {computed, onMounted, ref, toRaw} from "vue";

import {useAppStore} from "@/store/app";

import {editTicket, getSpecificTicket, getTicket, replyToTicket, updateAssignees, updateStatus} from "@/api/ticket";
import {toDateTime} from "@/util/date";
import {getTypeColor, getStatusColor} from "@/util/mapping";
import {getAllAdminOrSupportUsers} from "@/api/user";

const props = defineProps({
  ticketId: String,
  adminMode: Boolean
})

const appStore = useAppStore()

const ticket = ref(null)
const isLoading = ref(true)

const ticketAssignees = ref([])
const potentialAssignees = ref([])
const assigneesUpdated = ref(false)

const isClosed = computed(() => ticket.value.status === "CLOSED")

const typeColor = computed(() => {
  return getTypeColor(ticket.value.type);
})
const statusColor = computed(() => {
  return getStatusColor(ticket.value.status);
})

const edit = ref("")
const editing = ref(undefined)
const isEditEmpty = computed(() => edit.value.length === 0)
const isEditing = computed(() => editing.value !== undefined)

const hasComments = computed(() => ticket.value && ticket.value.comments.length > 0)

const reply = ref("")
const replyTo = ref(undefined)
const isReplyEmpty = computed(() => reply.value.length === 0)
const isReplyingToComment = computed(() => replyTo.value !== undefined)

function getCommentClass(id) {
  const baseClasses = "w-100 mb-4";

  if(replyTo.value != undefined && replyTo.value.id === id) {
    return (baseClasses + " bg-amber-lighten-4 mt-4 mb-8");
  }else{
    return baseClasses;
  }
}

function fetchTicket() {
  var fetchCall;

  if(props.adminMode) {
    fetchCall = getSpecificTicket(props.ticketId);

    getAllAdminOrSupportUsers(1).then(response => {
      potentialAssignees.value = response.data.content

      console.log(potentialAssignees.value);
    }).catch(e => console.log(e))
  }else{
    fetchCall = getTicket(props.ticketId);
  }

  fetchCall.then(response => {
    ticket.value = response.data
    ticketAssignees.value = response.data.assignees.map(assignee => assignee.assignee);

    console.log(ticketAssignees.value);

    if(isLoading) {
      isLoading.value = false
    }
  }).catch(e => console.log(e))
}

onMounted(() => { fetchTicket()})

function onAssigneesUpdated() {
  updateAssignees(props.ticketId, ticketAssignees.value).then(response => {
    if(assigneesUpdated.value) {
      assigneesUpdated.value = false;
    }

    fetchTicket();
  }).catch(e => console.log(e));
}

function onAssigneesChanged() {
  if(!assigneesUpdated.value) {
    assigneesUpdated.value = true;
  }
}

function onAssigneesMenuState(menuOpen) {
  if(!menuOpen && assigneesUpdated.value) {
    onAssigneesUpdated();
  }
}

function onUnassignSelf() {
  ticketAssignees.value = ticketAssignees.value.filter(a => a.name !== appStore.user.name)

  onAssigneesUpdated();
}

function onAssignSelf() {
  const user = potentialAssignees.value.find(a => a.name === appStore.user.name);

  ticketAssignees.value.push(user);

  onAssigneesUpdated();
}

function onTicketClose() {
  updateStatus(props.ticketId, "CLOSED").then(response => {
    fetchTicket();
  }).catch(e => console.log(e));
}

function onEditStart() {
  edit.value = ticket.value.body;
  editing.value = ticket.value;
}

function onEditCancel() {
  clearEditing();
}

function onEditConfirm() {
  editTicket(editing.value.id, edit.value).then(response => {
    clearEditing();

    fetchTicket();
  }).catch(e => {
    alert("Error while attempting to edit ticket.");

    console.log(e);

    clearEditing();
  });
}

function clearEditing() {
  edit.value = "";
  editing.value = undefined;
}

function setReplyTo(comment) {
  replyTo.value = comment;

  window.scrollTo({
    top: document.body.scrollHeight,
    behavior: 'smooth'
  })
}

function clearReplyTo() {
  replyTo.value = undefined;
}

function onClearReply() {
  reply.value = ""
  clearReplyTo()
}

function onReply() {
  const replyData = {
    ticket: Number(props.ticketId),
    comment: {
      content: reply.value
    }
  }

  if(replyTo.value) {
    const replyingTo = replyTo.value.id

    if (replyingTo >= 1) {
      replyData.comment.replyTo = {
        id: replyingTo
      }
    }
  }

  replyToTicket(replyData).then(response => {
    onClearReply()

    fetchTicket()
  }).catch(e => {
    console.log(e)
    alert("Error while attempting to send reply.")

    onClearReply()
  })
}
</script>

<template>
  <div v-if="!isLoading" class="w-100 fill-height d-flex flex-column justify-start ">

    <div class="w-100 pa-8 d-flex align-center justify-center" style="height: 10%">
      <v-row>
        <v-col>
          <h4>Title</h4>
          {{ ticket.title }}
        </v-col>

        <v-col>
          <h4>Created At</h4>
          <p>{{ toDateTime(ticket.createdAt) }}</p>
        </v-col>

        <v-col>
          <h4>Type</h4>
          <v-chip :color="typeColor">
            {{ ticket.type }}
          </v-chip>
        </v-col>

        <v-col>
          <h4>Status</h4>
          <v-chip :color="statusColor">
            {{ ticket.status }}
          </v-chip>
        </v-col>

        <v-col v-if="!props.adminMode">
          <h4>Assigned to</h4>
          {{ ticketAssignees.map(a => a.name).toString() }}
        </v-col>
        <v-col v-else>
          <h4>Assigned to</h4>
          <v-select
            @update:modelValue="onAssigneesChanged"
            @update:menu="onAssigneesMenuState"
            v-model="ticketAssignees"
            :items="potentialAssignees"
            item-title="name"
            item-value="id"
            chips
            multiple
            return-object
            :disabled="isClosed"
            density="compact" />
        </v-col>

        <v-col v-if="props.adminMode && !isClosed" class="d-flex pt-8">
          <v-btn
            v-if="(ticketAssignees.find(a => a.name == appStore.user.name) !== undefined)"
            @click="onUnassignSelf"
            color="default"
            density="compact"
          >
            Unassign Self
          </v-btn>
          <v-btn
            v-else
            @click="onAssignSelf"
            color="indigo"
            density="compact"
          >
            Assign Self
          </v-btn>
        </v-col>

        <v-col>
          <h4>Last Updated</h4>
          <p>{{ toDateTime(ticket.updatedAt) }}</p>
        </v-col>

        <v-col v-if="props.adminMode" class="d-flex pt-8">
          <v-btn
            v-if="!isClosed"
            @click="onTicketClose"
            color="red"
            density="compact"
          >
            Close
          </v-btn>
        </v-col>
      </v-row>
    </div>

    <v-divider />

    <div class="w-100 pa-8 d-flex flex-column justify-center align-start">
      <div
        v-if="props.adminMode"
        class="w-100 mb-4 d-flex flex-row justify-space-between align-start"
      >
        <h4>{{ ticket.owner.name }}</h4>
        <div v-if="ticket.owner.name === appStore.user.name" class="float-right">
          <v-btn
            v-if="!isEditing"
            @click="onEditStart"
            color="indigo"
            density="compact"
          >
            Edit
          </v-btn>
        </div>
      </div>
      <div
        v-if="!isEditing"
      >
        {{ ticket.body }}
      </div>
      <div v-else class="w-100">
        <v-textarea
          class="w-100"
          no-resize
          v-model="edit"
          label="Edit"
        />
      </div>
      <div v-if="isEditing" class="w-100 d-flex justify-space-between align-center">
        <v-btn
          class="float-right"
          @click="onEditCancel"
          color="white"
          density="compact"
        >
          Cancel
        </v-btn>
        <v-btn
          class="float-left"
          @click="onEditConfirm"
          color="green"
          density="compact"
          :disabled="isEditEmpty"
        >
          Confirm
        </v-btn>
      </div>
    </div>

    <v-divider />

    <div v-if="hasComments" class="w-100 ml-8 pa-4 d-flex flex-column justify-center align-center">
      <div v-for="(comment, index) in ticket.comments" class="w-100 mb-4">
        <v-row>
          <v-col cols="auto">
            <h4>{{ comment.owner.name }}</h4>
          </v-col>
          <v-col>
            Posted at: {{ toDateTime(comment.createdAt) }}
          </v-col>
        </v-row>

        <v-row
          v-if="comment.replyTo"
          class="ml-2"
        >

          <div class="pa-4 bg-grey-lighten-4 d-flex flex-column align-content-start">
            <h4 class="mb-4">{{ comment.replyTo.owner.name }}</h4>
            <p>{{ comment.replyTo.content }}</p>
          </div>
        </v-row>

        <v-row class="pr-8">
          <v-col cols="11">
            {{ comment.content }}
          </v-col>

          <v-col cols="1" v-if="!isReplyingToComment && !isClosed">
            <v-btn
              @click="setReplyTo(comment)"
              color="indigo"
              density="compact"
            >
              Reply
            </v-btn>
          </v-col>
        </v-row>

        <v-divider v-if="index < (ticket.comments.length - 1)" class="mt-4"/>
      </div>
    </div>

    <v-divider v-if="hasComments"/>

    <div v-if="!isClosed" class="w-100 pa-8 d-flex justify-center align-center">
      <v-form class="w-100 d-flex flex-column">
        <div v-if="isReplyingToComment" class="w-100 ml-4 mb-4 d-flex flex-column justify-center align-start">
          <p>Replying to:</p>
          <div class="pa-4 bg-grey-lighten-4 d-flex flex-column align-content-start">
            <h4 class="mb-4">{{ replyTo.owner.name }}</h4>
            <p>{{ replyTo.content }}</p>
          </div>
        </div>

        <v-textarea
          no-resize
          v-model="reply"
          label="Reply"
        />

        <div class="w-100 d-flex flex-row justify-space-between align-center">
          <v-btn @click="onClearReply" :disabled="isReplyEmpty && !isReplyingToComment">
            Clear
          </v-btn>

          <v-btn @click="onReply" :disabled="isReplyEmpty" color="indigo" class="float-right">
            Send
          </v-btn>
        </div>
      </v-form>
    </div>

  </div>
  <v-progress-circular indeterminate size="64" v-else />
</template>
