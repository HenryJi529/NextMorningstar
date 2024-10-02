import {marked, Marked} from "marked";
import markedKatex from "marked-katex-extension";
import {markedHighlight} from "marked-highlight";
import {getHljs} from "@/utils/handleHighlight";
import {createApp} from "vue";
import CopyButton from "@/components/blog/CopyButton.vue";

export const removeLineBreakInBlockFormulas = (markdownText: string) => {
    // 匹配Markdown中的块公式
    const blockFormulaRegex = /\$\$[^$]*\$\$/g;

    const replacer = (match: string) => {
        // 删除所有换行符
        return match.replace(/\n+/g, '');
    };
    return markdownText.replace(blockFormulaRegex, replacer);
}

export const getMarked = () => {
    const hljs = getHljs();

    const marked = new Marked(
        markedHighlight({
            emptyLangClass: 'hljs',
            langPrefix: 'hljs language-',
            highlight: (code, lang) => {
                const language = hljs.getLanguage(lang) ? lang : 'plaintext';
                const codeString = hljs.highlight(code, {language}).value;

                const docBody = new DOMParser().parseFromString(codeString, 'text/html').body;
                const newElement = Object.assign(document.createElement("p"), {
                    textContent: code,
                    className: "source hidden",
                });
                docBody.insertBefore(newElement, null);

                return new XMLSerializer().serializeToString(docBody);
            }
        })
    );
    marked.setOptions({
        gfm: true
    })

    marked.use(markedKatex({
        throwOnError: false,
        output: "mathml",
        nonStandard: true // 允许公式的 前/后 $ 前/后没有空格
    }));

    return marked;
}


export const generateCodeBlockCopyButton = (className: string) => {
    setTimeout(() => {
        const codeBlocks = document.querySelectorAll(`${className} pre`);
        for (const codeBlock of codeBlocks) {
            const sourceEle = codeBlock.querySelector('code .source')
            if (sourceEle) {
                // 动态创建CopyButton元素，挂载到pre内
                const source = sourceEle.textContent;
                const mountEle = Object.assign(document.createElement("div"), {
                    className: "hljs-copy-button",
                });
                codeBlock.appendChild(mountEle);

                const copyButton = createApp(CopyButton, {source: source});
                copyButton.mount(mountEle);
            }
        }
    }, 100)
}


interface heading {
    anchor: string;
    level: number;
    text: string;
}

export interface TocTreeNode extends heading {
    children: TocTreeNode[];
}

export class TocHandler {
    headings: heading[];
    index: number;

    constructor() {
        this.headings = [];
        this.index = 0;
    }

    add(text: string, level: number) {
        const anchor = `toc-${level}-${++this.index}`;
        this.headings.push({anchor, level, text});
        return anchor;
    }

    getTree() {
        const tree: TocTreeNode[] = [];
        const stack: TocTreeNode[] = [];

        this.headings.forEach(heading => {
            const node: TocTreeNode = {
                ...heading,
                children: [],
            }
            if (heading.level === 2) {
                tree.push(node);
            } else {
                while (stack.length > 0 && stack[stack.length - 1].level >= node.level) {
                    // 栈不为空 且 栈顶的节点level大于等于当前节点
                    stack.pop();
                }
                if (stack.length > 0) {
                    stack[stack.length - 1].children.push(node);
                } else {
                    tree.push(node);
                }
            }
            stack.push(node);
        })
        return tree;
    }
}

export const getTocRender = (tocObj: TocHandler) => {
    const renderer = new marked.Renderer({});
    renderer.heading = function ({tokens, depth}) {
        const text = this.parser.parseInline(tokens);
        const anchor = tocObj.add(text, depth);
        return `<div id=${anchor} class="invisible target:pt-8"></div><h${depth}>${text}</h${depth}>\n`;
    }
    return renderer;
}