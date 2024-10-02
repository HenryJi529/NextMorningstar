<script setup lang="ts">
import { convertMarkdownToDocx } from "@mohtasham/md-to-docx";
import download from "downloadjs";
import html2pdf from "html2pdf.js";
import {getMarked, removeLineBreakInBlockFormulas} from "@/utils/handleMarked";

const markdown = `
# 一级标题
## 二级标题
This is a paragraph with **bold** and *italic* text.

- Bullet point with **bold text** inside
- Another point with *italic* and \`code\`
  **Bold text on next line**

1. Numbered item with **bold** formatting
2. Another item with mixed **bold** and *italic*

> This is a blockquote

| Header 1 | Header 2 |
|----------|----------|
| Cell 1   | Cell 2   |
| Cell 3   | Cell 4   |

\`\`\`typescript
function hello(name: string): string {
  return \`Hello, \${name}!\`;
}
\`\`\`

![Test Image](https://picsum.photos/200/200)

COMMENT: This is a comment
`;

const downloadAsDocx = async () => {
    const blob = await convertMarkdownToDocx(markdown);
    download(blob, '测试文件.docx')
}


const downloadAsPdf = async () => {
    const marked = getMarked();
    const html = await marked.parse(removeLineBreakInBlockFormulas(markdown));
    const container = document.createElement('div');
    container.innerHTML = html;
    container.dataset.theme = 'light';
    container.className = 'basic-html';
    setTimeout(()=>{
        html2pdf()
            .set({
                margin: 10,
                filename: `测试文件.pdf`,
                enableLinks: true,
                // @ts-ignore
                pagebreak: {
                    mode: ['css', 'legacy'],
                    avoid: ["table"]
                },
                image: {type: 'jpeg', quality: 0.98},
                html2canvas: {
                    scale: 3, // 分辨率
                    logging: false,
                    useCORS: true,
                    timeout: 60000 // 增加超时时间
                },
                jsPDF: {
                    unit: 'mm',
                    format: 'a4',
                    orientation: 'portrait'
                }
            })
            .from(container)
            .save()
            .then();
    }, 100)
}


</script>

<template>
    <div>
        <div class="flex items-center justify-center space-x-8 mb-3">
            <div class="btn" @click="downloadAsDocx">下载为DOCX</div>
            <div class="btn" @click="downloadAsPdf">下载为PDF</div>
        </div>

        <div class="whitespace-pre border p-2 rounded-2xl">
            {{markdown}}
        </div>
    </div>


</template>

<style lang="scss">
.basic-html {
    h1 {
        @apply text-2xl
    }
    h2 {
        @apply text-xl
    }
}
</style>