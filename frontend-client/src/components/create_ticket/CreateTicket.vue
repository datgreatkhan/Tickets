<script setup>
import {ref} from "vue";

import {createTicket} from "@/api/ticket";
import {useRouter} from "vue-router";

const router = useRouter()

const title = ref("")
const type = ref("Issue")
const body = ref("")

function goToMyTickets() {
  router.replace("/dashboard/my-tickets/1");
}

function onCreate() {
  const data = {
    title: title.value,
    type: type.value.toUpperCase(),
    body: body.value
  }

  createTicket(data).then(response => {
    goToMyTickets()
  }).catch(e => {
    console.log(e)

    alert("An error has occurred.")
  });
}
</script>

<template>
  <div class="border rounded d-flex flex-column justify-center align-center" style="width: 900px; height: 650px">

    <div class="w-100 pa-4 d-flex align-center justify-center" style="height: 10%">
      <h3>Create Ticket</h3>
    </div>

    <v-divider />

    <v-form class="w-100 pa-4 d-flex flex-column justify-center align-start" style="height: 90%">
      <v-text-field
        v-model="title"
        label="Title"
        style="width: 45%"
      />
      <v-select
        v-model="type"
        label="Type"
        :items="['Issue', 'Inquiry', 'Report', 'Other']"
        style="width: 45%"
      />
      <v-textarea
        no-resize
        v-model="body"
        label="Body"
        style="width: 100%"
      />
      <div class="w-100 d-flex flex-row justify-space-between align-center" style="height: 15%">
        <v-btn @click="goToMyTickets" color="white">
          Cancel
        </v-btn>
        <v-btn @click="onCreate" color="indigo">
          Submit
        </v-btn>
      </div>
    </v-form>
  </div>
</template>
