

<script setup>
import {onMounted, onUpdated, ref} from "vue";
  import { useRouter } from "vue-router";
  import axios from "axios";
  import {useAppStore} from "@/store/app";

  const appStore = useAppStore()
  const router = useRouter()

  function onGoogleLogin() {
    window.location.href = "http://localhost:8080/user/login";
  }

  function onFacebookLogin() {
    alert("Not implemented yet.");
  }

  function onHomeSelect() {
    router.push('/dashboard')
  }

  onMounted(() => {
    if(!appStore.currentUser) {
      axios.get("http://localhost:8080/user/me", {withCredentials: true}).then(response => {
        const user = response.data;

        appStore.setCurrentUser(user)
        console.log(appStore.currentUser)
      }).catch(e => {
        console.log(e);
      })
    }
  })
</script>

<template>
  <v-container class="fill-height" fluid>
    <div class="w-100 fill-height d-flex justify-center align-center">
      <div class="w-33 h-50 border rounded d-flex flex-column">
        <div class="w-100 pa-4 d-flex flex-column justify-center align-center" style="height: 25%">
          <h3>Tickets - Login</h3>
          <p>A simple help desk solution</p>
        </div>

        <v-divider/>

        <div v-if="!appStore.isAuthenticated" class="w-100 d-flex flex-column justify-space-evenly align-center" style="height: 50%">
          <v-btn @click="onGoogleLogin" prepend-icon="mdi-google" color="red" width="300">
            Sign in with Google
          </v-btn>
          <v-btn @click="onFacebookLogin" prepend-icon="mdi-facebook" color="indigo" width="300">
            Sign in with Facebook
          </v-btn>
        </div>
        <div v-else class="w-100 d-flex flex-column justify-space-evenly align-center" style="height: 50%">
          <p v-if="appStore.isAuthenticated">You are signed in, click below to continue.</p>
          <v-btn @click="onHomeSelect" prepend-icon="mdi-home" color="blue" width="300">
            Continue to Home
          </v-btn>
        </div>

        <!-- <v-divider/> -->

        <div class="w-100 d-flex justify-center align-center" style="height: 25%">
        </div>
      </div>
    </div>
  </v-container>
</template>
