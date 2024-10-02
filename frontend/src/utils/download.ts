export const downloadUrl = (url: string, filename: string) => {
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = url;
    a.download = filename; // a.setAttribute(...)
    document.body.appendChild(a);
    a.click(); //执行下载
    setTimeout(() => {
        document.body.removeChild(a); //释放标签
    }, 3000);
};

export const downloadBlob = (blob: Blob, filename: string) => {
    const url = URL.createObjectURL(blob);
    downloadUrl(url, filename);
    setTimeout(() => {
        URL.revokeObjectURL(url); // 释放url
    }, 3000);
};

export const downloadText = (text: string, filename: string, type: string = 'text/plain') => {
    const blob = new Blob([text], { type: type });
    downloadBlob(blob, filename);
};
