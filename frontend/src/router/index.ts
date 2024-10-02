import { createRouter, createWebHistory } from 'vue-router'

import IndexView from "@/views/IndexView.vue";
import AuthBaseView from "@/views/auth/BaseView.vue";
import authRoutes from "@/router/auth";
import killRoutes from "@/router/kill";
import blogRoutes from "@/router/blog";
import KillBaseView from "@/views/kill/BaseView.vue";
import LoveBaseView from "@/views/love/BaseView.vue";
import ResumeBaseView from "@/views/resume/BaseView.vue";
import NavBaseView from "@/views/nav/BaseView.vue";
import BlogBaseView from "@/views/blog/BaseView.vue";
import {setPreRoute} from "@/utils/handleRoute";
import {useUserStore} from "@/stores/users";
import ForbiddenView from "@/views/ForbiddenView.vue";
import NotFoundView from "@/views/NotFoundView.vue";


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
      children: authRoutes
    },
    {
      path: '/kill',
      component: KillBaseView,
      children:  killRoutes,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/love',
      component: LoveBaseView
    },
    {
      path: '/nav',
      component: NavBaseView
    },
    {
      path: '/blog',
      component: BlogBaseView,
      children: blogRoutes
    },
    {
      path: '/resume',
      component: ResumeBaseView
    },
    {
      path: '/403',
      component: ForbiddenView
    },
    {
      path: '/404',
      component: NotFoundView
    },
    {
      path: '/:pathMatch(.*)*',
      component: () => import('@/views/NotFoundView.vue')
    },
  ]
});
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  if(!userStore.isAuthenticated){
    await userStore.loadUser();
  }

  if(to.name == "auth-login" || to.name == "auth-register") {
    setPreRoute(from.fullPath);
  }

  if(to.meta.requiresAuth && !userStore.isAuthenticated){
    return next({name: 'auth-login'});
  }
  if(to.meta.permissions){
    let flag = false;
    for(let permission of to.meta.permissions as string[]){
      if(userStore.permissions.includes(permission)){
          flag = true;
      }
    }
    if(!flag){
      next({ path: '/403' });
    }
  }

  next();
})

export default router
