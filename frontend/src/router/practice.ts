import type {RouteRecordRaw} from "vue-router";
import IndexView from "@/views/practice/IndexView.vue";
import HtmlTableToExcelWorkbookPractice from "@/views/practice/HtmlTableToExcelWorkbookPractice.vue";
import EchartsBarPractice from "@/views/practice/EchartsBarPractice.vue";


export default [
    {
        path: '',
        component: IndexView,
        meta: {
            verboseName: "案例索引页"
        }
    },
    {
        path: 'html-table-to-excel-workbook',
        name: 'practice-htmlTableToExcelWorkbook',
        component: HtmlTableToExcelWorkbookPractice,
        meta: {
            verboseName: "将Html表格导出为Excel工作簿"
        }
    },
    {
        path: 'echarts-bar',
        name: 'practice-echartsBar',
        component: EchartsBarPractice,
        meta: {
            verboseName: "调用EchartsJS绘制柱状图"
        }
    }
] as RouteRecordRaw[];