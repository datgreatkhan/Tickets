<script setup>
import {onMounted, ref} from "vue";

import {useRouter} from "vue-router";

import {getUser, getUsersSummary} from "@/api/user";
import {getUsersTickets} from "@/api/ticket";

import {toDateTime} from "@/util/date";
import {getRoleColor} from "@/util/mapping";
import {formatTicketSummary, formatUserSummary} from "@/util/data_helper";

import StatCard from "@/components/overview/StatCard.vue";

const props = defineProps({
  userId: String,
})

const router = useRouter()

const user = ref(null)
const userSummary = ref(undefined)
const ticketsSummary = ref(undefined)
const recentTickets = ref(undefined)

const isLoading = ref(true)

function onRecentTicketSelect(id) {
  router.replace("/dashboard/view-ticket/" + id);
}

function fetchUserInfo() {
  getUser(props.userId).then(response => {
    user.value = response.data;

  }).catch(e => console.log(e));

  getUsersSummary(props.userId).then(response => {
    userSummary.value = formatUserSummary(response.data);
    ticketsSummary.value = formatTicketSummary(response.data);
  }).catch(e => console.log(e));

  getUsersTickets(props.userId, 1, { ref: "updatedAt", desc: true }).then(response => {
    recentTickets.value = response.data.content;

    if(isLoading) {
      isLoading.value = false;
    }
  }).catch(e => console.log(e));
}

onMounted(() => { fetchUserInfo()})
</script>

<template>
  <div v-if="!isLoading" class="w-100 fill-height d-flex flex-column justify-start ">

    <div class="w-100 pa-8 d-flex align-center justify-center" style="height: 10%">
      <h4>{{ user.name }}</h4>
      <v-chip
        :color="getRoleColor(user.role)"
        class="ml-4"
      >
        {{ user.role }}
      </v-chip>
    </div>

    <v-divider />

    <div class="w-100 pa-8 d-flex flex-column justify-center align-start">
      <v-row class="w-100">
        <v-col cols="4">
          <StatCard title="User Info" :data="userSummary"/>
        </v-col>

        <v-col cols="4">
          <StatCard title="Ticket Summary" :data="ticketsSummary"/>
        </v-col>
      </v-row>
    </div>
    <div class="w-100 pa-8 d-flex flex-column justify-center">
      <h4 class="mb-8 align-self-center">{{ user.name + '\'s Recent Tickets' }}</h4>
      <div
        v-if="recentTickets.length > 0"
        class="w-100 d-flex flex-column justify-center align-start"
      >
        <v-row
          v-for="ticket in recentTickets" class="w-100"
          @click="onRecentTicketSelect(ticket.id)"
        >
          <v-col>
            {{ toDateTime(ticket.createdAt) }}
          </v-col>
          <v-col>
            {{ ticket.title }}
          </v-col>
          <v-col>
            {{ ticket.type }}
          </v-col>
          <v-col>
            {{ ticket.status }}
          </v-col>
          <v-col>
            {{ toDateTime(ticket.createdAt) }}
          </v-col>
        </v-row>
      </div>
      <div
        v-else
        class="w-100 d-flex justify-center align-center"
      >
        <p>This user has no tickets.</p>
      </div>
    </div>
  </div>
  <v-progress-circular indeterminate size="64" v-else />
</template>
