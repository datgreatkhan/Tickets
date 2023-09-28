

<script setup>
import {computed, ref} from "vue";

import {useAppStore} from "@/store/app";
import {onBeforeRouteUpdate, useRouter} from "vue-router";

import {getAllTickets, getAssignedTickets, getTickets} from "@/api/ticket";

import {toDateTime} from "@/util/date";
import {getStatusColor, getTypeColor} from "@/util/mapping";

const props = defineProps({
  currentPage: String,
  adminMode: Boolean,
  assignedOnly: Boolean,
  withHeaders: {
    type: Boolean,
    default: true
  }
})

const appStore = useAppStore()
const router = useRouter()

const tickets = ref([])
const isLoading = ref(true)

const totalPages = computed(() => tickets.value.totalPages)

const columns = ref([
  {
    id: 0,
    name: "Created",
    ref: "createdAt"
  },
  {
    id: 1,
    name: "Title",
    ref: "title"
  },
  {
    id: 2,
    name: "Type",
    ref: "type"
  },
  {
    id: 3,
    name: "Status",
    ref: "status"
  },
  {
    id: 4,
    name: "Last Updated",
    ref: "updatedAt"
  }
])

const sortBy = ref({header: { id: 4, name: "Last Updated", ref: "updatedAt" }, desc: true})

function fetchTickets() {
  const currentPage = props.currentPage;

  const sortByValue = {
    ref: sortBy.value.header.ref,
    desc: sortBy.value.desc
  }

  var fetchCall;

  if(props.adminMode) {
    if(props.assignedOnly) {
      fetchCall = getAssignedTickets(Number(currentPage), sortByValue);
    }else{
      fetchCall = getAllTickets(Number(currentPage), sortByValue);
    }
  }else{
    fetchCall = getTickets(Number(currentPage), sortByValue);
  }

  fetchCall.then(response => {
    tickets.value = response.data;

    isLoading.value = false;
  }).catch(e => console.log(e))
}

function isSelectedHeader(headerId) {
  return (sortBy.value.header.id === headerId);
}

function getHeaderIcon() {
  return sortBy.value.desc ? "mdi-arrow-down" : "mdi-arrow-up";
}

function onHeaderSelect(header) {
  if(isSelectedHeader(header.id)) {
    sortBy.value.desc = !sortBy.value.desc;
  }else{
    sortBy.value.header = header;
    sortBy.value.desc = true;
  }

  fetchTickets();
}

function onTicketSelect(id) {
  router.replace("/dashboard/view-ticket/" + id);
}

function onPageSelect(page) {
  if(page === props.currentPage) { return; }

  if(props.adminMode) {
    router.replace("/dashboard/all-tickets/" + page);
  }else{
    router.replace("/dashboard/my-tickets/" + page);
  }
}

fetchTickets();

onBeforeRouteUpdate((to, from, next) => {
  fetchTickets(to.params.page);

  next();
})
</script>

<template>
  <div v-if="!isLoading" class="w-100 d-flex flex-column fill-height">
    <v-table v-if="!isLoading" class="w-100 fill-height">
      <thead>
      <tr v-if="withHeaders">
        <th
          v-for="columnHeader in columns"
          :key="columnHeader.id"
          @click="onHeaderSelect(columnHeader)"
        >
          <div class="d-flex flex-row">
            <h4>{{ columnHeader.name }}{{ isSelectedHeader(columnHeader.id) ? '*' : '' }}</h4>
            <v-icon v-if="isSelectedHeader(columnHeader.id)" :icon="getHeaderIcon(columnHeader)" class="ml-2"></v-icon>
          </div>
        </th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="ticket in tickets.content" :key="ticket.id" @click="onTicketSelect(ticket.id)">
        <td>{{ toDateTime(ticket.createdAt) }}</td>

        <td>{{ ticket.title }}</td>

        <td>
          <v-chip :color="getTypeColor(ticket.type)">
            {{ ticket.type }}
          </v-chip>
        </td>

        <td>
          <v-chip :color="getStatusColor(ticket.status)">
            {{ ticket.status }}
          </v-chip>
        </td>

        <td>{{ toDateTime(ticket.updatedAt) }}</td>
      </tr>
      </tbody>
    </v-table>

    <div class="w-100 pa-4 d-flex justify-center align-center">
      <v-row class="w-25 justify-center">
        <v-col v-for="page in totalPages" :key="page" class="flex-grow-0">
          <v-btn @click="onPageSelect(page)" rounded density="compact" size="34">
            {{ page }}
          </v-btn>
        </v-col>
      </v-row>
    </div>
  </div>

  <v-progress-circular indeterminate size="64" v-else />
</template>
