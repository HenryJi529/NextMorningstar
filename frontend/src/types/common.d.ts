export interface R<T> {
    msg: string;
    code: number;
    data: T;
}

export interface ImageCaptchaResponseVo {
    imageData: string;
    sessionId: string;
}

export interface PageResult<T> {
    totalRecordNum: number;
    totalPageNum: number;
    pageSize: number;
    currentPageSize: number;
    pageNum: number;
    records: T[];
}

export interface NavItem4Manage {
    cnName: string;
    enName: string;
    listRouteName: string;
    editRouteName: string;
    iconName: string;
}