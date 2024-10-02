
export interface R<T> {
    msg: string;
    code: number;
    data: T;
}

export interface CaptchaResponseVo {
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