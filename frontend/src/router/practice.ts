import type {RouteRecordRaw} from "vue-router";


export default [
    {
        path: '',
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
            requireDesktop: true
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
            verboseName: "使用Multipart上传文件"
        }
    }
] as RouteRecordRaw[];