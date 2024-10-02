export const isDateStrValid = (dateStr: string) => {
    // 检查是否符合 YYYY-MM-DD 格式
    if (!/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) return false;

    // 尝试解析为 Date 对象
    const date = new Date(dateStr);
    return !isNaN(date.getTime());
}
