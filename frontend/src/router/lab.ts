import type { RouteRecordRaw } from 'vue-router';

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
        },
    },
    {
        path: 'hyperscript',
        name: 'lab-hyperscript',
        component: () => import('@/views/lab/HyperscriptPractice.vue'),
        meta: {
            verboseName: 'Hyperscript基础练习',
        },
    },
    {
        path: 'mock',
        name: 'lab-mock',
        component: () => import('@/views/lab/MockAPIPractice.vue'),
        meta: {
            verboseName: 'Mock接口练习',
        },
    },
    {
        path: 'parse-excel-workbook',
        name: 'lab-parseExcelWorkbook',
        component: () => import('@/views/lab/ParseExcelWorkbookPractice.vue'),
        meta: {
            verboseName: '解析Excel工作簿',
        },
    },
    {
        path: 'abortable-request',
        name: 'lab-abortable-request',
        component: () => import('@/views/lab/AbortableRequestPractice.vue'),
        meta: {
            verboseName: '可取消请求练习',
        },
    },
    {
        path: 'zip',
        name: 'lab-zip',
        component: () => import('@/views/lab/ZipPractice.vue'),
        meta: {
            verboseName: '多层次多类型的文件压缩练习',
        },
    },
    {
        path: 'browser-viewport',
        name: 'lab-browser-viewport',
        component: () => import('@/views/lab/DomSizePractice.vue'),
        meta: {
            verboseName: 'DOM尺寸相关练习',
        },
    },
    {
        path: 'markdown-convert',
        name: 'lab-markdown-convert',
        component: () => import('@/views/lab/MarkdownConvertPractice.vue'),
        meta: {
            verboseName: 'Markdown格式转换练习',
        },
    },
    {
        path: 'streaming-response',
        name: 'lab-streaming-response',
        component: () => import('@/views/lab/StreamingResponsePractice.vue'),
        meta: {
            verboseName: '流式响应发送与接收练习',
        },
    },
    {
        path: 'editable-table',
        name: 'lab-editable-table',
        component: () => import('@/views/lab/EditableTablePractice.vue'),
        meta: {
            verboseName: '可编辑表格练习',
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
        },
    },
    {
        path: 'data-visualization',
        name: 'lab-data-visualization',
        component: () => import('@/views/lab/DataVisualizationPractice.vue'),
        meta: {
            verboseName: '数据可视化练习',
        },
    },
    {
        path: 'ui-component',
        name: 'lab-ui-component',
        component: () => import('@/views/lab/UIComponentPractice.vue'),
        meta: {
            verboseName: '常见UI组件练习',
        },
    },
    {
        path: 'svg',
        name: 'lab-svg',
        component: () => import('@/views/lab/SVGPractice.vue'),
        meta: {
            verboseName: '常用SVG图标实现练习',
        },
    },
    {
        path: 'chat-room',
        name: 'lab-chat-room',
        component: () => import('@/views/lab/ChatRoomPractice.vue'),
        meta: {
            verboseName: '聊天室实现练习',
            requiresAuth: true,
        },
    },
    {
        path: 'screen-awake',
        name: 'lab-screen-awake',
        component: () => import('@/views/lab/ScreenWakePractice.vue'),
        meta: {
            verboseName: '屏幕常亮实现练习',
        },
    },
] as RouteRecordRaw[];
