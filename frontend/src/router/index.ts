import { createRouter, createWebHistory } from 'vue-router'

import IndexView from "@/views/IndexView.vue";
import AuthBaseView from "@/views/auth/BaseView.vue";
import authRoutes from "@/router/auth";
import KillBaseView from "@/views/kill/BaseView.vue";
import ResumeBaseView from "@/views/resume/BaseView.vue";
import NavBaseView from "@/views/nav/BaseView.vue";
import BlogBaseView from "@/views/blog/BaseView.vue";


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'index',
      component: IndexView
    },
    {
      path: '/auth',
      component: AuthBaseView,
      children: authRoutes,
    },
    {
      path: '/kill',
      component: KillBaseView
    },
    {
      path: '/nav',
      component: NavBaseView
    },
    {
      path: '/blog',
      component: BlogBaseView
    },
    {
      path: '/resume',
      component: ResumeBaseView
    }
  ]
});

export default router
