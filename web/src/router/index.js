import Vue from 'vue'
import Router from 'vue-router'
/* Layout */
import Layout from '@/layout'

Vue.use(Router)

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: '首页',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '首页', icon: 'dashboard' }
    }]
  },
  {
    path: '/app',
    component: Layout,
    redirect: '/app',
    children: [{
      path: '',
      name: '应用',
      component: () => import('@/views/app/index'),
      meta: { title: '应用', icon: 'app' }
    },
    {
      path: 'create',
      name: '创建应用',
      component: () => import('@/views/app/create'),
      meta: { title: '创建应用', icon: 'app' },
      hidden: true
    },
    {
      path: ':id',
      name: 'AppDetail',
      component: () => import('@/views/app/appDetail'),
      hidden: true
    }
    ]
  },
  {
    path: '/identity',
    component: Layout,
    redirect: '/identity/enterprise',
    name: '身份源管理',
    meta: { title: '身份源管理', icon: 'identity' },
    children: [
      {
        path: 'enterprise',
        name: '企业身份源',
        component: () => import('@/views/identity/enterprise/index'),
        meta: { title: '企业身份源', icon: 'enterprise' }
      },
      {
        path: 'create',
        name: '创建身份源',
        component: () => import('@/views/identity/enterprise/create'),
        hidden: true,
        meta: { title: '创建身份源' }
      },
      {
        path: 'social',
        name: '社会身份源',
        component: () => import('@/views/identity/social/index'),
        meta: { title: '社会身份源', icon: 'social' }
      }
    ]
  },
  {
    path: '/userManagement',
    component: Layout,
    redirect: '/userManagement/user',
    name: '用户管理',
    meta: { title: '用户管理', icon: 'user-manage' },
    children: [
      {
        path: 'user',
        name: '用户',
        component: () => import('@/views/userManagement/user'),
        meta: { title: '用户', icon: 'user' }
      },
      {
        path: 'userGroup',
        name: '用户组',
        component: () => import('@/views/userManagement/userGroup'),
        meta: { title: '用户组', icon: 'user-group' }
      },
      {
        path: 'role',
        name: '角色',
        component: () => import('@/views/userManagement/role'),
        meta: { title: '角色', icon: 'role' }
      }
    ]
  },
  {
    path: '/permissionManagement',
    component: Layout,
    redirect: '/permissionManagement/',
    name: '权限管理',
    meta: { title: '权限管理', icon: 'identity' },
    children: [
      {
        path: 'generalResource',
        name: '常规资源',
        component: () => import('@/views/permission/generalResource/index'),
        meta: { title: '常规资源', icon: 'ui' }
      },
      {
        path: 'apiResource',
        name: 'API资源',
        component: () => import('@/views/permission/apiResource/index'),
        meta: { title: 'API资源', icon: 'api' }
      }

    ]
  },
  {
    path: '/security',
    component: Layout,
    redirect: '/security/',
    name: '安全',
    meta: { title: '安全', icon: 'security' },
    children: [
      {
        path: 'attackProtection',
        name: '攻击防护',
        component: () => import('@/views/security/attackProtection/index'),
        meta: { title: '攻击防护', icon: 'attackProtection' }
      },
      {
        path: 'mfa',
        name: '多因素身份认证',
        component: () => import('@/views/security/mfa/index'),
        meta: { title: '多因素身份认证', icon: 'MFA' }
      },
      {
        path: 'securityPolicy',
        name: '登陆验证策略',
        component: () => import('@/views/security/securityPolicy/index'),
        meta: { title: '登陆验证策略', icon: 'securityPolicy' }
      },
      {
        path: 'passwordPolicy',
        name: '密码策略',
        component: () => import('@/views/security/passwordPolicy/index'),
        meta: { title: '密码策略', icon: 'passwordPolicy' }
      }
    ]
  },
  {
    path: '/auditLog',
    component: Layout,
    redirect: '/auditLog',
    children: [{
      path: '',
      name: '审计日志',
      component: () => import('@/views/auditLog/index'),
      meta: { title: '审计日志', icon: 'log' }
    }]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
