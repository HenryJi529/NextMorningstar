import type {RouteRecordRaw} from "vue-router";
import HomeView from "@/views/kill/HomeView.vue";
import GameView from "@/views/kill/GameView.vue";


export default [
    {
        path: '', // 首页
        component: HomeView
    },
    // {
    //     path: 'room', // 房间(当前)
    // },
    {
        path: 'game', // 游戏(当前)
        component: GameView
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