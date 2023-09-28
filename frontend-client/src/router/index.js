// Composables
import { createRouter, createWebHistory } from 'vue-router'
import axios from "axios";

import {useAppStore} from "@/store/app";

const routes = [
  {
    path: '/',
    alias: ['', '/login'],
    component: () => import('@/layouts/default/Default.vue'),
    meta: {
      title: "Login",
      requiresAuthentication: false,
    },
    children: [
      {
        path: '',
        name: 'Login',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import(/* webpackChunkName: "login" */ '@/views/LoginView.vue'),
      },
    ],
  },
  {
    path: '/dashboard',
    component: () => import('@/layouts/dashboard/DashLayout.vue'),
    meta: {
      title: "Dashboard",
      requiresAuthentication: true,
    },
    children: [
      {
        path: '',
        name: 'Overview',
        component: () => import('@/views/MyOverviewView.vue'),
      },
      {
        path: 'my-tickets/:page',
        name: 'Tickets',
        component: () => import('@/views/MyTicketsView.vue'),
        props: true,
      },
      {
        path: 'assigned-tickets/:page',
        name: 'Assigned Tickets',
        component: () => import('@/views/AssignedTicketsView.vue'),
        props: true,
      },
      {
        path: 'all-tickets/:page',
        name: 'All Tickets',
        component: () => import('@/views/AllTicketsView.vue'),
        props: true,
      },
      {
        path: "view-ticket/:ticketId",
        name: 'View Ticket',
        component: () => import('@/views/ViewTicketView.vue'),
        props: true,
      },
      {
        path: 'create-ticket',
        name: 'Create Ticket',
        component: () => import('@/views/CreateTicketView.vue'),
      },
      {
        path: 'all-users/:page',
        name: 'Users',
        component: () => import('@/views/AllUsersView.vue'),
        props: true,
      },
      {
        path: "view-user/:userId",
        name: 'View User',
        component: () => import('@/views/ViewUserView.vue'),
        props: true,
      },
      {
        path: 'my-account',
        name: 'My Account',
        component: () => import('@/views/MyOverviewView.vue'),
      },
    ],
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
})

router.beforeEach((to, from, next) => {
  const appStore = useAppStore();

  const hasAuth = appStore.isAuthenticated;
  const requiresAuth = to.matched.some(r => r.meta.requiresAuthentication);

  if(to.meta.title) {
    document.title = 'Tickets - ' + to.meta.title;
  }

  if(requiresAuth && !hasAuth) {
    next('/');
  }else{
    next();
  }
})

export default router
