<script setup>
import StatCard from "@/components/overview/StatCard.vue";

import {ref} from "vue";
import {getUserSummary} from "@/api/user";
import {toDateTime} from "@/util/date";
import {formatTicketSummary, formatUserSummary} from "@/util/data_helper";

const isLoading = ref(true)

const userSummary = ref(undefined)
const ticketsSummary = ref(undefined)

function fetchStats() {
  getUserSummary().then(response => {
    console.log(response.data)

    const data = response.data

    userSummary.value = formatUserSummary(data);

    ticketsSummary.value = formatTicketSummary(data);

    isLoading.value = false
  }).catch(e => {
    console.log(e)

    isLoading.value = false
  })
}

fetchStats()
</script>

<template>
  <v-container class="fill-height" fluid>
    <div class="w-100 fill-height d-flex justify-center align-center">

      <div v-if="!isLoading" class="w-100 fill-height d-flex flex-column justify-start ">

        <div class="w-100 pa-8 d-flex align-center justify-center" style="height: 10%">
          <h4>My Overview</h4>
        </div>

        <v-divider />

        <div class="w-100 pa-8 d-flex flex-row align-start justify-center">
          <v-row>
            <v-col cols="4">
              <StatCard title="User Info" :data="userSummary"/>
            </v-col>

            <v-col cols="4">
              <StatCard title="Ticket Stats" :data="ticketsSummary"/>
            </v-col>
          </v-row>
        </div>
      </div>

      <v-progress-circular indeterminate size="64" v-else />

    </div>
  </v-container>
</template>
