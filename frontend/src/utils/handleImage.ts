export const setMaxWidthToNaturalWidth = () => {
    const imgs = document.querySelectorAll<HTMLImageElement>('img');
    imgs.forEach(img => {
        if(img.complete && img.naturalHeight !== 0){
            img.style.maxWidth = `${img.naturalWidth}px`;
        }else{
            img.addEventListener('load', () => {
                img.style.maxWidth = `${img.naturalWidth}px`;
            })
        }
    })
}

export const convertToBase64 = (img: HTMLImageElement) => {
    img.crossOrigin = 'Anonymous';
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d') as CanvasRenderingContext2D;
    // 设置canvas尺寸与图片相同
    canvas.width = img.naturalWidth;
    canvas.height = img.naturalHeight;
    // 绘制图片到canvas
    ctx.drawImage(img, 0, 0);
    // 转换为Base64
    return canvas.toDataURL('image/png');
}