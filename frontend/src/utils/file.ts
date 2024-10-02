export const getFileExtension = (file: File) => {
    const fileName = file.name;

    const dotIndex = fileName.lastIndexOf('.');

    if (dotIndex > 0) {
        // 找到了点，并且点不在第一个位置索引
        return fileName.substring(dotIndex + 1);
    } else {
        // 没有找到点
        return '';
    }
};
