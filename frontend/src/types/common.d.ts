
export interface Response {
    msg: string;
    code: number;
    data: object;
}

export interface CaptchaResponseVo {
    imageData: string;
    sessionId: string;
}
