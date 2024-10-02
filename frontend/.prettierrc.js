export default {
    useTabs: false, // 禁用Tab，统一用空格缩进
    tabWidth: 4, // 缩进4个空格
    endOfLine: 'lf', // 换行符用 LF
    printWidth: 100, // 每行代码最大字符数
    semi: true, // 语句末尾加分号
    singleQuote: true, // 字符串使用单引号(避免和HTML属性双引号冲突)
    trailingComma: 'es5', // 对象/数组加尾逗号，函数参数不加
    bracketSpacing: true, // 括号两内侧留空格
    bracketSameLine: true, // 标签闭合括号不换行
    arrowParens: 'avoid', // 箭头函数单个参数省略括号
    quoteProps: 'as-needed', // 对象属性仅在必要时加引号，(e.g., {name: 'xxx', 'user-name': 'xxx' })
    htmlWhitespaceSensitivity: 'css', // HTML空白符敏感度遵循CSS规则
    proseWrap: 'preserve', // Markdown文本保留手动换行
    singleAttributePerLine: false, // 多个属性不强制独占一行
    embeddedLanguageFormatting: 'auto', // 自动格式化嵌入在 HTML/MD 中的 JS/CSS 代码
};
