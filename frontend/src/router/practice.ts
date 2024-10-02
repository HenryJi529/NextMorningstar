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
        path: 'echarts',
        name: 'practice-echarts',
        component: () => import("@/views/practice/EchartsPractice.vue"),
        meta: {
            verboseName: "Echarts图表绘制练习"
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
    },
    {
        path: 'svg',
        name: 'practice-svg',
        component: () => import("@/views/practice/SVGPractice.vue"),
        meta: {
            verboseName: "手动实现常用的SVG图标"
        }
    },
    {
        path: 'abortable-request',
        name: 'practice-abortable-request',
        component: () => import("@/views/practice/AbortableRequestPractice.vue"),
        meta: {
            verboseName: "可取消请求练习"
        }
    },
    {
        path: 'zip',
        name: 'practice-zip',
        component: () => import("@/views/practice/ZipPractice.vue"),
        meta: {
            verboseName: "多层次多类型的文件压缩练习"
        }
    },
    {
        path: 'browser-viewport',
        name: 'practice-browser-viewport',
        component: () => import("@/views/practice/BrowserViewportPractice.vue"),
        meta: {
            verboseName: "浏览器视口相关练习"
        }
    },
    {
        path: 'linear-progress-bar',
        name: 'practice-linear-progress-bar',
        component: () => import("@/views/practice/LinearProgressBarPractice.vue"),
        meta: {
            verboseName: "线性进度条练习"
        }
    },
    {
        path: 'markdown-convert',
        name: 'practice-markdown-convert',
        component: () => import("@/views/practice/MarkdownConvertPractice.vue"),
        meta: {
            verboseName: "Markdown格式转换练习"
        }
    },
    {
        path: 'streaming-response',
        name: 'practice-streaming-response',
        component: () => import("@/views/practice/StreamingResponsePractice.vue"),
        meta: {
            verboseName: "流式响应发送与接收练习"
        }
    },
    {
        path: 'editable-table',
        name: 'practice-editable-table',
        component: () => import("@/views/practice/EditableTablePractice.vue"),
        meta: {
            verboseName: "可编辑表格练习"
        }
    },
    {
        path: 'iframe-communication',
        component: () => import("@/views/practice/IframeCommunicationPractice.vue"),
        children: [
            {
                path: '',
                name: 'practice-iframe-communication',
                component: () => import("@/views/practice/iframe/IndexView.vue"),
                meta: {
                    verboseName: "iframe通信练习"
                }
            },
            {
                path: 'post-message',
                name: 'practice-iframe-communication-by-post-message',
                component: () => import("@/views/practice/iframe/ByPostMessage.vue"),
                meta: {
                    verboseName: "PostMessage方式"
                },
            },
            {
                path: 'message-channel',
                name: 'practice-iframe-communication-by-message-channel',
                component: () => import("@/views/practice/iframe/ByMessageChannel.vue"),
                meta: {
                    verboseName: "MessageChannel方式"
                },
            },
            {
                path: 'broadcast-channel-a',
                name: 'practice-iframe-communication-by-broadcast-channel-a',
                component: () => import("@/views/practice/iframe/ByBroadcastChannelA.vue"),
                meta: {
                    verboseName: "BroadcastChannel方式[A页面]"
                },
            },
            {
                path: 'broadcast-channel-b',
                name: 'practice-iframe-communication-by-broadcast-channel-b',
                component: () => import("@/views/practice/iframe/ByBroadcastChannelB.vue"),
                meta: {
                    verboseName: "BroadcastChannel方式[B页面]"
                },
            },
        ]
    },
    {
        path: 'small-world-network',
        name: 'practice-small-world-network',
        component: () => import("@/views/practice/SmallWorldNetworkPractice.vue"),
        meta: {
            verboseName: "电影网络六度分隔理论验证"
        }
    }
] as RouteRecordRaw[];