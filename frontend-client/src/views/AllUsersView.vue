<template>
  <v-container class="fill-height" fluid>
    <div class="w-100 fill-height d-flex justify-center align-center">
      <users-table :currentPage="props.page" />
    </div>
  </v-container>
</template>

<script setup>
import UsersTable from '@/components/UsersTable.vue'

const props = defineProps({
  page: String
})
</script>
