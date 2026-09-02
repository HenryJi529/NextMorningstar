import type { RouteRecordRaw } from 'vue-router';

export interface LabRouteMeta {
    verboseName?: string;
    description?: string;
    tags?: string[];
}

export default [
    {
        path: '',
        name: 'lab-index',
        component: () => import('@/views/lab/IndexView.vue'),
        meta: {
            verboseName: '案例索引页',
        },
    },
    {
        path: 'html-table-to-excel-workbook',
        name: 'lab-htmlTableToExcelWorkbook',
        component: () => import('@/views/lab/HtmlTableToExcelWorkbookPractice.vue'),
        meta: {
            verboseName: '将Html表格导出为Excel工作簿',
            description: '把 Html 表格导出为 Excel 工作簿，并对比两种实现方案。',
            tags: ['ExcelJS', 'SheetJS'],
        },
    },
    {
        path: 'hyperscript',
        name: 'lab-hyperscript',
        component: () => import('@/views/lab/HyperscriptPractice.vue'),
        meta: {
            verboseName: 'Hyperscript基础练习',
            description: '抛开模板语法，用 h 函数手写渲染逻辑。',
            tags: ['Vue', '渲染函数'],
        },
    },
    {
        path: 'mock',
        name: 'lab-mock',
        component: () => import('@/views/lab/MockAPIPractice.vue'),
        meta: {
            verboseName: 'Mock接口练习',
            description: '在前端生成以假乱真的模拟数据并请求展示。',
            tags: ['Faker.js', 'Axios'],
        },
    },
    {
        path: 'parse-excel-workbook',
        name: 'lab-parseExcelWorkbook',
        component: () => import('@/views/lab/ParseExcelWorkbookPractice.vue'),
        meta: {
            verboseName: '解析Excel工作簿',
            description: '上传 Excel 文件，对比前端解析与后端解析的耗时。',
            tags: ['SheetJS', 'FileReader'],
        },
    },
    {
        path: 'abortable-request',
        name: 'lab-abortable-request',
        component: () => import('@/views/lab/AbortableRequestPractice.vue'),
        meta: {
            verboseName: '可取消请求练习',
            description: '用 AbortController 随时取消进行中的 HTTP 请求。',
            tags: ['AbortController', 'Axios'],
        },
    },
    {
        path: 'zip',
        name: 'lab-zip',
        component: () => import('@/views/lab/ZipPractice.vue'),
        meta: {
            verboseName: '多层次多类型的文件压缩练习',
            description: '在浏览器里抓取图片并打包成 zip 下载。',
            tags: ['JSZip', 'FileSaver'],
        },
    },
    {
        path: 'browser-viewport',
        name: 'lab-browser-viewport',
        component: () => import('@/views/lab/DomSizePractice.vue'),
        meta: {
            verboseName: 'DOM尺寸相关练习',
            description: '搞清 offset/client/scroll 等各种 DOM 尺寸的区别。',
            tags: ['DOM API'],
        },
    },
    {
        path: 'markdown-convert',
        name: 'lab-markdown-convert',
        component: () => import('@/views/lab/MarkdownConvertPractice.vue'),
        meta: {
            verboseName: 'Markdown格式转换练习',
            description: '把 Markdown 转换为 docx、pdf 等格式并下载。',
            tags: ['Marked', 'html2pdf', 'md-to-docx'],
        },
    },
    {
        path: 'editable-table',
        name: 'lab-editable-table',
        component: () => import('@/views/lab/EditableTablePractice.vue'),
        meta: {
            verboseName: '可编辑表格练习',
            description: '支持行内编辑、校验与提交的表格。',
            tags: ['Ant Design Vue', 'Day.js'],
        },
    },
    {
        path: 'iframe-communication',
        component: () => import('@/views/lab/IframeCommunicationPractice.vue'),
        children: [
            {
                path: '',
                name: 'lab-iframe-communication',
                component: () => import('@/views/lab/iframe/IndexView.vue'),
                meta: {
                    verboseName: 'iframe通信练习',
                    description: '跨窗口通信的三种姿势对比。',
                    tags: ['postMessage', 'MessageChannel', 'BroadcastChannel'],
                },
            },
            {
                path: 'post-message',
                name: 'lab-sub-iframe-communication-by-post-message',
                component: () => import('@/views/lab/iframe/ByPostMessage.vue'),
                meta: {
                    verboseName: 'PostMessage方式',
                },
            },
            {
                path: 'message-channel',
                name: 'lab-sub-iframe-communication-by-message-channel',
                component: () => import('@/views/lab/iframe/ByMessageChannel.vue'),
                meta: {
                    verboseName: 'MessageChannel方式',
                },
            },
            {
                path: 'broadcast-channel-a',
                name: 'lab-sub-iframe-communication-by-broadcast-channel-a',
                component: () => import('@/views/lab/iframe/ByBroadcastChannelA.vue'),
                meta: {
                    verboseName: 'BroadcastChannel方式[A页面]',
                },
            },
            {
                path: 'broadcast-channel-b',
                name: 'lab-sub-iframe-communication-by-broadcast-channel-b',
                component: () => import('@/views/lab/iframe/ByBroadcastChannelB.vue'),
                meta: {
                    verboseName: 'BroadcastChannel方式[B页面]',
                },
            },
        ],
    },
    {
        path: 'small-world-network',
        name: 'lab-small-world-network',
        component: () => import('@/views/lab/SmallWorldNetworkPractice.vue'),
        meta: {
            verboseName: '电影网络六度分隔理论验证',
            description: '用图可视化验证电影合作网络的六度分隔理论。',
            tags: ['AntV G6', 'SVG'],
        },
    },
    {
        path: 'data-visualization',
        name: 'lab-data-visualization',
        component: () => import('@/views/lab/DataVisualizationPractice.vue'),
        meta: {
            verboseName: '数据可视化练习',
            description: '柱状图、折线图、饼图、玫瑰图、漏斗图，混用多种实现方式。',
            tags: ['ECharts', 'Three.js', '手写SVG'],
        },
    },
    {
        path: 'ui-component',
        name: 'lab-ui-component',
        component: () => import('@/views/lab/UIComponentPractice.vue'),
        meta: {
            verboseName: '常见UI组件练习',
            description: '手写下拉、轮播、日期选择器等常见 UI 组件。',
            tags: ['Vue', '原生实现'],
        },
    },
    {
        path: 'svg',
        name: 'lab-svg',
        component: () => import('@/views/lab/SVGPractice.vue'),
        meta: {
            verboseName: '常用SVG图标实现练习',
            description: '手写常用 SVG 图标。',
            tags: ['SVG'],
        },
    },
    {
        path: 'chat-room',
        name: 'lab-chat-room',
        component: () => import('@/views/lab/ChatRoomPractice.vue'),
        meta: {
            verboseName: '聊天室实现练习',
            description: '基于 WebSocket 的在线聊天室。',
            tags: ['WebSocket', 'SockJS', 'STOMP'],
            requiresAuth: true,
        },
    },
    {
        path: 'screen-awake',
        name: 'lab-screen-awake',
        component: () => import('@/views/lab/ScreenWakePractice.vue'),
        meta: {
            verboseName: '屏幕常亮实现练习',
            description: '调用 Wake Lock API 阻止屏幕休眠。',
            tags: ['Wake Lock API'],
        },
    },
] as RouteRecordRaw[];
