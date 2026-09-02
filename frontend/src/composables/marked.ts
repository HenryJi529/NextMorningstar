import { marked, Marked } from 'marked';
import markedKatex from 'marked-katex-extension';
import { getHljs } from '@/utils/highlight';
import { createApp, nextTick } from 'vue';
import CopyButton from '@/views/blog/components/CopyButton.vue';

const removeLineBreakInBlockFormulas = (markdownText: string) => {
    // 匹配Markdown中的块公式
    const blockFormulaRegex = /\$\$[^$]*\$\$/g;

    const replacer = (match: string) => {
        // 删除所有换行符
        return match.replace(/\n+/g, '');
    };
    return markdownText.replace(blockFormulaRegex, replacer);
};

let markedInstance: Marked | null = null;
const getMarked = () => {
    if (!markedInstance) {
        const hljs = getHljs();
        markedInstance = new Marked()
            .use({
                breaks: true, // 开启单次回车转<br>
                gfm: true     // 启用 GFM (GitHub Flavored Markdown)
            })
            .use(
                markedKatex({
                    throwOnError: false,
                    output: 'mathml',
                    nonStandard: true, // 允许公式的 前/后 $ 前/后没有空格
                })
            )
            .use({
                renderer: {
                    link({ href, title, tokens }) {
                        // 1. 解析链接内部的可见文本 (支持 **加粗**、*斜体* 等)
                        const text = this.parser.parseInline(tokens);

                        // 2. 仅对 http/https 外链补充新标签页属性
                        const targetAttr = /^https?:\/\//i.test(href) ? ' target="_blank"' : '';

                        // 3. title 存在才拼属性
                        const titleAttr = title ? ` title="${title}"` : '';

                        return `<a href="${href || ''}"${titleAttr}${targetAttr}>${text}</a>`;
                    },
                    code({ text: code = '', lang }) {
                        // 1. 提取语言名（防御 "js {1-3}" 等元信息导致高亮失效），并过滤非法特殊字符（防御 class 属性注入）
                        const rawLang = (lang || '').trim().split(/\s+/)[0];
                        const cleanLang = /^[a-zA-Z0-9_\-+.]+$/.test(rawLang) ? rawLang : '';
                        const language =
                            cleanLang && hljs.getLanguage(cleanLang) ? cleanLang : 'plaintext';

                        // 2. 着色（内置已自动完成 HTML 转义）
                        const highlighted = hljs.highlight(code, {
                            language,
                            ignoreIllegals: true,
                        }).value;

                        // 3. 语言类名兜底
                        const langClass = cleanLang ? `hljs language-${cleanLang}` : 'hljs';

                        // 4. 纯净源码放入 data-code，高亮代码放入 <code>
                        return `<pre><code class="${langClass}" data-code="${encodeURIComponent(code)}">${highlighted}</code></pre>`;
                    },
                },
            });
    }

    return markedInstance;
};

export const generateCodeBlockCopyButton = async (containerClassName: string) => {
    // 等待 Vue 完成 DOM 渲染
    await nextTick();
    const COPY_BUTTON_CLASS_NAME = 'hljs-copy-button';
    const codeBlocks = document.querySelectorAll(`${containerClassName} pre`);
    for (const codeBlock of codeBlocks) {
        if (codeBlock.querySelector(`.${COPY_BUTTON_CLASS_NAME}`)) {
            continue;
        }

        const encodedCode = codeBlock.querySelector('code')?.getAttribute('data-code');
        if (encodedCode) {
            // 动态创建CopyButton元素，挂载到pre内
            const mountEle = Object.assign(document.createElement('div'), {
                className: COPY_BUTTON_CLASS_NAME,
            });
            codeBlock.appendChild(mountEle);
            const copyButton = createApp(CopyButton, { source: decodeURIComponent(encodedCode) });
            copyButton.mount(mountEle);
        }
    }
};

interface heading {
    anchor: string;
    level: number;
    text: string;
}

export interface TocTreeNode extends heading {
    children: TocTreeNode[];
}

class TocHandler {
    headings: heading[];
    index: number;

    constructor() {
        this.headings = [];
        this.index = 0;
    }

    add(text: string, level: number) {
        const anchor = `toc-${level}-${++this.index}`;
        if (level > 1) {
            this.headings.push({ anchor, level, text });
        }
        return anchor;
    }

    getTree() {
        const tree: TocTreeNode[] = [];
        const stack: TocTreeNode[] = [];
        const minContentLevel = Math.min(...this.headings.map(h => h.level));
        this.headings.forEach(heading => {
            const node: TocTreeNode = {
                ...heading,
                children: [],
            };
            if (heading.level === minContentLevel) {
                stack.length = 0;
                tree.push(node);
            } else {
                while (stack.length > 0 && stack[stack.length - 1].level >= node.level) {
                    // 栈不为空 且 栈顶的节点level大于等于当前节点
                    stack.pop();
                }
                if (stack.length > 0) {
                    stack[stack.length - 1].children.push(node);
                }
            }
            stack.push(node);
        });
        return tree;
    }
}

const getTocRender = (tocObj: TocHandler) => {
    const renderer = new marked.Renderer({});
    renderer.heading = function ({ tokens, depth }) {
        const text = this.parser.parseInline(tokens);
        const anchor = tocObj.add(text, depth);
        return `<div id=${anchor} class="invisible target:pt-8"></div><h${depth}>${text}</h${depth}>`;
    };
    return renderer;
};

export const useMarked = (toc: boolean = false) => {
    const marked = getMarked();
    const tocHandler = new TocHandler();
    if (toc) {
        marked.use({ renderer: getTocRender(tocHandler) });
    }

    const convertMarkdownToHtml = (markdownText: string) => {
        return marked.parse(removeLineBreakInBlockFormulas(markdownText));
    };

    return {
        convertMarkdownToHtml,
        tocHandler,
    };
};
