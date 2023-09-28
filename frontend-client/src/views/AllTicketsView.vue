<template>
  <v-container class="fill-height" fluid>
    <div class="w-100 fill-height d-flex justify-center align-center">
      <tickets-table
        :currentPage="props.page"
        :adminMode="appStore.isAdminOrSupport"
      />
    </div>
  </v-container>
</template>

<script setup>
import {useAppStore} from "@/store/app";

import TicketsTable from '@/components/TicketsTable.vue'

const props = defineProps({
  page: String
})

const appStore = useAppStore()
</script>
