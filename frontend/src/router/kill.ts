import type {RouteRecordRaw} from "vue-router";


export default [
    {
        path: '', // 首页
        component: () => import("@/views/kill/HomeView.vue")
    },
    // {
    //     path: 'room', // 房间(当前)
    // },
    {
        path: 'game', // 游戏(当前)
        component: () => import("@/views/kill/GameView.vue")
    },
    // {
    //     path: 'settings', // 设置
    // },
    // {
    //     path: 'help', // QA和feedback
    // },
    // {
    //     path: 'lobby', // 大厅
    // },
    // {
    //     path: 'rank', // 排名
    // },
] as RouteRecordRaw[]