export const getFileExtension = (file: File) => {
    // 获取文件名
    const fileName = file.name;

    // 查找最后一个点的位置
    const dotIndex = fileName.lastIndexOf('.');

    // 如果找到了点，并且点不是在最后一个字符，则提取后缀
    if (dotIndex > 0 && dotIndex < fileName.length - 1) {
        return fileName.substring(dotIndex + 1);
    } else {
        // 没有找到点或者点是最后一个字符，认为没有后缀
        return '';
    }
}





