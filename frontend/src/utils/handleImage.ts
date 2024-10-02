export const setMaxWidthToNaturalWidth = (className: string) => {
    const imgs = document.querySelectorAll<HTMLImageElement>(`${className} img`);
    imgs.forEach(img => {
        if (img.complete && img.naturalHeight !== 0) {
            img.style.maxWidth = `${img.naturalWidth}px`;
        } else {
            img.addEventListener('load', () => {
                img.style.maxWidth = `${img.naturalWidth}px`;
            })
        }
    })
}

export const convertImgToBase64 = (img: HTMLImageElement) => {
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

export const createImageFromFile = (file: File) => {
    return new Promise((resolve, reject) => {
        if (!file.type.startsWith('image/')) {
            return reject(new Error('请选择图片文件'));
        }
        createImageFromBlob(file)
            .then((image) => {
                const img = image as HTMLImageElement;
                img.alt = file.name;
                resolve(img);
            })
            .catch(error => reject(error));
    });
}

export const createImageFromBlob = (blob: Blob) => {
    return new Promise((resolve, reject) => {
        const objectUrl = URL.createObjectURL(blob);
        const image = new Image();
        image.src = objectUrl;

        image.onload = () => {
            URL.revokeObjectURL(objectUrl);
            resolve(image);
        };
        image.onerror = () => {
            URL.revokeObjectURL(objectUrl);
            reject(new Error('图片加载失败'));
        };
    });
}

export const isSupportWebp = () => {
    return !![].map && document.createElement('canvas').toDataURL('image/webp').indexOf('data:image/webp') == 0;
}

export const convertBlobToUrl = (blob: Blob) => {
    return URL.createObjectURL(blob);
}

export const convertBlobToBase64 = (blob: Blob) => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(blob);
    });
}