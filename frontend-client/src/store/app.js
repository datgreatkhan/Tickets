// Utilities
import { defineStore } from 'pinia'
import {testEmailMapping} from "@/util/mapping";

export const useAppStore = defineStore('app', {
  state: () => ({
    currentUser: null,
    currentNotifications: [

    ]
  }),
  getters: {
    isAuthenticated: (state) => state.currentUser !== null,
    user: (state) => state.currentUser,
    isSupport: (state) => (state.currentUser.role === "SUPPORT"),
    isAdminOrSupport: (state) => (state.currentUser.role === "ADMIN" || state.currentUser.role === "SUPPORT"),
    notifications: (state) => state.currentNotifications
  },
  actions: {
    setCurrentUser(user) {
      const userInfo = {
        //name: user.principalData.fullName,
        name: user.userData.name,
        //email: user.principalData.email,
        email: testEmailMapping(user.userData.role),
        avatar: user.principalData.picture,
        role: user.userData.role
      }

      this.currentUser = userInfo;
    }
  }
})
