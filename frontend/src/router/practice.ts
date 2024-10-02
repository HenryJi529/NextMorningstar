import type {RouteRecordRaw} from "vue-router";


export default [
    {
        path: '',
        name: 'practice-index',
        component: () => import("@/views/practice/IndexView.vue"),
        meta: {
            verboseName: "案例索引页"
        }
    },
    {
        path: 'html-table-to-excel-workbook',
        name: 'practice-htmlTableToExcelWorkbook',
        component: () => import("@/views/practice/HtmlTableToExcelWorkbookPractice.vue"),
        meta: {
            verboseName: "将Html表格导出为Excel工作簿",
        }
    },
    {
        path: 'echarts-bar',
        name: 'practice-echartsBar',
        component: () => import("@/views/practice/EchartsBarPractice.vue"),
        meta: {
            verboseName: "调用EchartsJS绘制柱状图"
        }
    },
    {
        path: 'file-upload',
        name: 'practice-fileUpload',
        component: () => import("@/views/practice/FileUploadPractice.vue"),
        meta: {
            verboseName: "文件上传【选择、拖拽、复制、筛选、限大】"
        }
    },
    {
        path: 'carousel',
        name: 'practice-carousel',
        component: () => import("@/views/practice/CarouselPractice.vue"),
        meta: {
            verboseName: "手动实现轮播图"
        }
    },
    {
        path: 'dropdown',
        name: 'practice-dropdown',
        component: () => import("@/views/practice/DropdownPractice.vue"),
        meta: {
            verboseName: "手动实现下拉菜单"
        }
    },
    {
        path: 'hyperscript',
        name: 'practice-hyperscript',
        component: () => import("@/views/practice/HyperscriptPractice.vue"),
        meta: {
            verboseName: "Hyperscript基础练习"
        }
    },
    {
        path: 'mock',
        name: 'practice-mock',
        component: () => import("@/views/practice/MockAPIPractice.vue"),
        meta: {
            verboseName: "Mock接口练习"
        }
    },
    {
        path: 'tabs',
        name: 'practice-tabs',
        component: () => import("@/views/practice/TabsPractice.vue"),
        meta: {
            verboseName: "手动实现标签页"
        }
    },
    {
        path: 'parse-excel-workbook',
        name: 'practice-parseExcelWorkbook',
        component: () => import("@/views/practice/ParseExcelWorkbookPractice.vue"),
        meta: {
            verboseName: "解析Excel工作簿"
        }
    }
] as RouteRecordRaw[];