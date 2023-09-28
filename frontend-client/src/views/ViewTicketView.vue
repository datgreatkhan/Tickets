<template>
  <v-container class="fill-height" fluid>
    <div class="w-100 fill-height d-flex justify-center align-center">
      <view-ticket :ticketId="props.ticketId" :adminMode="appStore.isAdminOrSupport"/>
    </div>
  </v-container>
</template>

<script setup>
import {useAppStore} from "@/store/app";

import ViewTicket from "@/components/view_ticket/ViewTicket.vue";

const props = defineProps({
  ticketId: String
})

const appStore = useAppStore()
</script>
