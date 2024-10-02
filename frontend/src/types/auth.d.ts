export interface UserPrivateInfo {
    // 账号唯一且存在
    id: string;
    username: string;
    // 状态
    isLocked: boolean;
    isAdmin: boolean;
    // 账号唯一
    phone: string | null;
    email: string | null;
    // 权限
    permissions: string[];
    // 其他信息
    nickname: string;
    sex: string;
    avatar: string | null;
    createTime: string;
    updateTime: string;
}

export interface UserPublicInfo {
    isSelf: boolean;
    id: string;
    username: string;
    isLocked: boolean;
    nickname: string;
    sex: string;
    avatar: string | null;
    createTime: string;
    updateTime: string;
}

export interface UserEditableInfo {
    username: string;
    password: string;
    phone: string;
    email: string;
    nickname: string;
    avatar: string | null;
}

export interface RegisterRequestVo {
    sessionId: string;
    code: string;
    username: string;
    password: string;
    confirmPassword: string;
    nickname: string;
    sex: string;
}

export interface RegisterResponseVo {
    username: string;
    token: string;
}

export interface LoginRequestVo {
    sessionId: string;
    code: string;
    username: string;
    password: string;
}

export interface LoginResponseVo {
    username: string;
    token: string;
}

export interface LogoutResponseVo {
    username: string;
}

export interface GeolocationCoords {
    latitude: number;
    longitude: number;
}
