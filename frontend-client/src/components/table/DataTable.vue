<script setup>
import {computed, onMounted, ref} from "vue";
import {getStatusColor, getTypeColor} from "@/util/mapping";
import {toDateTime} from "@/util/date";

const props = defineProps({
  endpoint: Function,
  currentPage: String,
  viewRoute: {
    type: String,
    required: true
  },
  headers: {
    type: Array,
    required: true
  },
  withHeaders: {
    type: Boolean,
    default: true
  }
})

const data = ref([])
const isLoading = ref(true)

const totalPages = computed(() => data.value.totalPages)

const sortBy = ref({})

onMounted(() => {
  sortBy.value = props.headers[props.headers.length - 1];

  fetchData();
})

function fetchData() {
  props.endpoint(props.currentPage, sortBy.value).then(response => {
    data.value = response.data;

    if (isLoading.value) {
      isLoading.value = false;
    }
  }).catch(e => console.log(e));
}
</script>

<template>
  <div v-if="!isLoading" class="w-100 d-flex flex-column fill-height">
    <v-table v-if="!isLoading" class="w-100 fill-height">
      <thead>
      <tr v-if="withHeaders">
        <th
          v-for="header in headers"
          :key="header.id"
          @click="onHeaderSelect(header)"
        >
          <div class="d-flex flex-row">
            <h4>{{ header.name }}{{ isSelectedHeader(header.id) ? '*' : '' }}</h4>
            <v-icon v-if="isSelectedHeader(header.id)" :icon="getHeaderIcon(header)" class="ml-2" />
          </div>
        </th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="object in data.content" :key="object.id" @click="onObjectSelect(object.id)">

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
