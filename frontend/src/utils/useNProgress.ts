import NProgress from "nprogress";

NProgress.configure({
    minimum: 0.1,     // 初始最小百分比
    trickle: true,     // 是否自动递增
    trickleSpeed: 100, // 递增速度(ms)
    easing: 'ease',    // 动画曲线
    speed: 200,        // 动画速度(ms)
    showSpinner: false, // 是否显示旋转图标
    parent: 'body'     // 父容器
});

export default NProgress;