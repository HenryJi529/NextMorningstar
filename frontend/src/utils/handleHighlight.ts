import hljs from 'highlight.js/lib/core';
import bash from 'highlight.js/lib/languages/bash';
import css from 'highlight.js/lib/languages/css';
import dockerfile from 'highlight.js/lib/languages/dockerfile';
import django from 'highlight.js/lib/languages/django';
import ini from 'highlight.js/lib/languages/ini';
import java from 'highlight.js/lib/languages/java';
import javascript from 'highlight.js/lib/languages/javascript';
import json from 'highlight.js/lib/languages/json';
import latex from 'highlight.js/lib/languages/latex';
import plaintext from 'highlight.js/lib/languages/plaintext';
import gradle from 'highlight.js/lib/languages/gradle';
import makefile from 'highlight.js/lib/languages/makefile';
import shell from 'highlight.js/lib/languages/shell';
import scss from 'highlight.js/lib/languages/scss';
import properties from 'highlight.js/lib/languages/properties';
import markdown from 'highlight.js/lib/languages/markdown';
import protobuf from 'highlight.js/lib/languages/protobuf';
import xml from 'highlight.js/lib/languages/xml';
import yaml from 'highlight.js/lib/languages/yaml';
import typescript from 'highlight.js/lib/languages/typescript';
import python from 'highlight.js/lib/languages/python';
import nginx from 'highlight.js/lib/languages/nginx';
import sql from 'highlight.js/lib/languages/sql';
import http from 'highlight.js/lib/languages/http';
import applescript from 'highlight.js/lib/languages/applescript';


export const getHljs = () => {
    /* 注册语言 */
    hljs.registerLanguage('plaintext', plaintext);
    // 构建
    hljs.registerLanguage('makefile', makefile);
    hljs.registerLanguage('gradle', gradle);
    // 常见
    hljs.registerLanguage('bash', bash);
    hljs.registerLanguage('applescript', applescript);
    hljs.registerLanguage('shell', shell);
    hljs.registerLanguage('java', java);
    hljs.registerLanguage('css', css);
    hljs.registerLanguage('scss', scss);
    hljs.registerLanguage('javascript', javascript);
    hljs.registerLanguage('typescript', typescript);
    hljs.registerLanguage('python', python);
    hljs.registerLanguage('sql', sql);
    // 配置
    hljs.registerLanguage('dockerfile', dockerfile);
    hljs.registerLanguage('ini', ini);
    hljs.registerLanguage('properties', properties);
    hljs.registerLanguage('xml', xml);
    hljs.registerLanguage('yaml', yaml);
    hljs.registerLanguage('nginx', nginx);
    // 标记
    hljs.registerLanguage('latex', latex);
    hljs.registerLanguage('markdown', markdown);
    // 模版
    hljs.registerLanguage('django', django);
    // 协议
    hljs.registerLanguage('json', json);
    hljs.registerLanguage('protobuf', protobuf);
    hljs.registerLanguage('http', http);

    return hljs;
}