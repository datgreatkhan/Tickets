

<script setup>
import {computed, ref} from "vue";

import {useAppStore} from "@/store/app";
import {onBeforeRouteUpdate, useRouter} from "vue-router";

import {getAllUsers} from "@/api/user";

import {toDateTime} from "@/util/date";
import {getRoleColor} from "@/util/mapping";

const props = defineProps({
  currentPage: String
})

const appStore = useAppStore()
const router = useRouter()

const users = ref([])
const isLoading = ref(true)

const totalPages = computed(() => users.value.totalPages)

const columns = ref([
  {
    id: 0,
    name: "Created",
    ref: "createdAt"
  },
  {
    id: 1,
    name: "Name",
    ref: "name",
  },
  {
    id: 2,
    name: "Email",
    ref: "email"
  },
  {
    id: 3,
    name: "Role",
    ref: "role"
  },
  {
    id: 4,
    name: "Last Updated",
    ref: "updatedAt"
  }
])

const sortBy = ref({header: { id: 4, name: "Last Updated", ref: "updatedAt" }, desc: true})

function fetchUsers() {
  const currentPage = props.currentPage;

  const sortByValue = {
    ref: sortBy.value.header.ref,
    desc: sortBy.value.desc
  }

  getAllUsers(Number(currentPage), sortByValue).then(response => {
    users.value = response.data;

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

  fetchUsers();
}

function onUserSelect(id) {
  router.replace("/dashboard/view-user/" + id);
}

function onPageSelect(page) {
  if(page === props.currentPage) { return; }

  router.replace("/dashboard/all-users/" + page);
}

fetchUsers();

onBeforeRouteUpdate((to, from, next) => {
  fetchUsers(to.params.page);

  next();
})
</script>

<template>
  <div v-if="!isLoading" class="w-100 d-flex flex-column fill-height">
    <v-table class="w-100 fill-height">
      <thead>
      <tr>
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
      <tr v-for="user in users.content" :key="user.id" @click="onUserSelect(user.id)">
        <td>{{ toDateTime(user.createdAt) }}</td>

        <td>{{ user.name }}</td>

        <td>{{ user.email }}</td>

        <td>
          <v-chip :color="getRoleColor(user.role)">
            {{ user.role }}
          </v-chip>
        </td>

        <td>{{ toDateTime(user.updatedAt) }}</td>
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
