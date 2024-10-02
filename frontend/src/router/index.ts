import { createRouter, createWebHistory } from 'vue-router'

import IndexView from "@/views/IndexView.vue";
import authRoutes from "@/router/auth";
import killRoutes from "@/router/kill";
import blogRoutes from "@/router/blog";
import proxyRoutes from "@/router/proxy";
import adminRoutes from "@/router/admin";
import {setPreRoute} from "@/utils/handleRoute";
import {useUserStore} from "@/stores/users";
import {hasAnyPermission} from "@/utils/handlePermission";
import {ROLE_PROXY_ADMIN} from "@/constants/RoleConstant";
import {isDesktop} from "@/utils/handleClient";


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'index',
      component: IndexView
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import("@/views/admin/BaseView.vue"),
      children: adminRoutes,
      meta: {
        permissions: [
          ROLE_PROXY_ADMIN
        ]
      }
    },
    {
      path: '/auth',
      component: () => import("@/views/auth/BaseView.vue"),
      children: authRoutes
    },
    {
      path: '/blog',
      component: () => import('@/views/blog/BaseView.vue'),
      children: blogRoutes
    },
    {
      path: '/kill',
      component: () => import("@/views/kill/BaseView.vue"),
      children:  killRoutes,
      meta: {
        requiresAuth: true,
        isMaintaining: true,
        requireDesktop: true
      }
    },
    {
      path: '/love',
      component: () => import('@/views/love/BaseView.vue')
    },
    {
      path: '/nav',
      component: () => import('@/views/nav/BaseView.vue')
    },
    {
      path: '/pic',
      component: () => import('@/views/pic/BaseView.vue'),
      meta: {
        isMaintaining: true,
      }
    },
    {
      path: '/proxy',
      name: 'proxy',
      component: () => import('@/views/proxy/BaseView.vue'),
      children: proxyRoutes,
      meta: {
        permissions: [
          ROLE_PROXY_ADMIN
        ]
      }
    },
    {
      path: '/resume',
      component: () => import('@/views/resume/BaseView.vue'),
    },
    {
      path: '/rss',
      component: () => import('@/views/rss/BaseView.vue'),
      meta: {
        isMaintaining: true,
      }
    },
    {
      path: '/403',
      component: () => import('@/views/ForbiddenView.vue')
    },
    {
      path: '/404',
      component: () => import('@/views/NotFoundView.vue')
    },
    {
      path: '/maintenance',
      component: () => import('@/views/MaintenanceView.vue')
    },
    {
      path: '/require-desktop',
      component: () => import('@/views/RequireDesktopView.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      component: () => import('@/views/NotFoundView.vue')
    },
  ]
});
router.beforeEach(async (to, from, next) => {

  if(to.meta.requireDesktop && to.meta.requireDesktop == true){
    if(!isDesktop()){
      next({path: '/require-desktop'});
    }
  }

  if(to.meta.isMaintaining && to.meta.isMaintaining == true){
    next({path: '/maintenance'});
  }

  const userStore = useUserStore();
  if(!userStore.isAuthenticated){
    await userStore.loadUser();
  }

  if(to.name == "auth-login" || to.name == "auth-register") {
    if(!(typeof from.name === "string" && from.name.startsWith("auth"))){
      setPreRoute(from.fullPath);
    }
  }

  if(to.meta.requiresAuth && !userStore.isAuthenticated){
    next({name: 'auth-login'});
  }
  if(to.meta.permissions){
    if(!hasAnyPermission(to.meta.permissions as string[])){
      if(!userStore.isAuthenticated){
        next({name: 'auth-login'})
      }
      next({ path: '/403' });
    }
  }

  next();
})

export default router
