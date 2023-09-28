<template>
  <v-container class="fill-height" fluid>
    <div class="w-100 fill-height d-flex justify-center align-center">
      <view-user :userId="props.userId" />
    </div>
  </v-container>
</template>

<script setup>
import ViewUser from "@/components/view_user/ViewUser.vue";

const props = defineProps({
  userId: String
})
</script>
