<template>
  <v-navigation-drawer rail permanent rail-width="350">

    <!-- TODO: Make this into own component (profile + bell) -->
    <v-list>
      <v-btn
        v-if="appStore.notifications.length > 0"
        class="float-end mr-4"
        density="compact"
        variant="plain"
        rounded
        size="32">

        <v-menu activator="parent">
          <v-list>
            <v-list-item v-for="notification in appStore.notifications">
              <a href="">{{ notification.message }}</a>
            </v-list-item>

            <v-divider />

            <v-list-item class="justify-center">
              <v-btn variant="text">
                Clear All
              </v-btn>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-badge
          dot
          color="red">
          <v-icon
            icon="mdi-bell"
            size="24"
            variant="flat"
          />
        </v-badge>
      </v-btn>

      <v-list-item
        :prepend-avatar="appStore.currentUser.avatar"
        :title="appStore.currentUser.name"
        :subtitle="appStore.currentUser.email"
      />
    </v-list>

    <v-divider />

    <v-list>
      <v-list-item
        prepend-icon="mdi-file"
        title="My Tickets"
        to="/dashboard/my-tickets/1"
      />

      <v-list-item
        prepend-icon="mdi-file-account"
        title="Assigned Tickets"
        to="/dashboard/assigned-tickets/1"
      />
      <!-- to="/dashboard/my-tickets/1" -->

      <v-list-item
        prepend-icon="mdi-file-plus"
        title="Create Ticket"
        to="/dashboard/create-ticket"
      />
    </v-list>

    <v-divider />

    <v-list v-if="appStore.isAdminOrSupport">
      <v-list-item
        prepend-icon="mdi-file-multiple"
        title="All Tickets"
        to="/dashboard/all-tickets/1"
      />
      <v-list-item v-if="!appStore.isSupport"
        prepend-icon="mdi-account-multiple"
        title="All Users"
        to="/dashboard/all-users/1"
      />
    </v-list>

    <v-divider v-if="appStore.isAdminOrSupport"/>

    <v-list>
      <v-list-item
        prepend-icon="mdi-account"
        title="My Account"
        to="/dashboard/my-account"
      />
    </v-list>

  </v-navigation-drawer>
</template>

<script setup>
  import {useAppStore} from "@/store/app";

  const appStore = useAppStore()
</script>
