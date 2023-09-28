<template>
  <v-container class="fill-height" fluid>
    <div class="w-100 fill-height d-flex justify-center align-center">
      <tickets-table
        :currentPage="props.page"
      />
    </div>
  </v-container>
</template>

<script setup>
import TicketsTable from '@/components/TicketsTable.vue'

const props = defineProps({
  page: String
})
</script>
