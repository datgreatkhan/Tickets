<script setup>
const props = defineProps({
  title: String,
  data: Object
})
</script>


<template>
  <v-card class="pa-4">
    <template v-slot:title>
      {{ props.title }}
    </template>
    <v-row class="d-flex justify-center align-center">
      <v-col class="d-flex flex-column justify-center align-center">
        <p>{{ data.primary.header }}</p>
        <p>{{ data.primary.metric }}</p>
      </v-col>

      <v-col class="d-flex flex-column justify-center align-center">
        <p>{{ data.secondary.header }}</p>
        <p>{{ data.secondary.metric }}</p>
      </v-col>
    </v-row>

    <v-row v-if="data.tertiary" class="d-flex justify-center align-center">
      <v-col class="d-flex flex-column justify-center align-center">
        <p>{{ data.tertiary.header }}</p>
        <p>{{ data.tertiary.metric }}</p>
      </v-col>
    </v-row>
  </v-card>
</template>
