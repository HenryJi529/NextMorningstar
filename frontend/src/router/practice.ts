import type {RouteRecordRaw} from "vue-router";
import IndexView from "@/views/practice/IndexView.vue";
import HtmlTableToExcelWorkbookPractice from "@/views/practice/HtmlTableToExcelWorkbookPractice.vue";


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
    }
] as RouteRecordRaw[];