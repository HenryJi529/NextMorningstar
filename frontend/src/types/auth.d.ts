export interface UserPrivateInfo {
    // 账号唯一且存在
    id: string;
    username: string;
    // 状态
    isLocked: boolean;
    // 账号唯一
    email: string;
    // 权限
    permissions: string[];
    // 其他信息
    nickname: string;
    avatar: string | null;
    createTime: string;
    updateTime: string;
}

export interface UserManageableInfo {
    id: string;
    username: string;
    isLocked: boolean;
    email: string;
    roles: string[];
    nickname: string;
    avatar: string | null;
    created: string;
    updated: string;
}

export interface UserPublicInfo {
    isSelf: boolean;
    id: string;
    username: string;
    isLocked: boolean;
    isOnline: boolean;
    nickname: string;
    avatar: string | null;
    createTime: string;
    updateTime: string;
}

export interface UserEditableInfo {
    username: string;
    password: string;
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
}

export interface RegisterResponseVo {
    username: string;
    token: string;
}

export interface LoginRequestVo {
    sessionId: string;
    code: string;
    account: string;
    password: string;
}

export interface LoginResponseVo {
    username: string;
    token: string;
}

export interface OAuthResponseVo {
    id: string;
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

export interface UpdateEmailRequestVo {
    email: string;
    code: string;
}

export interface UpdatePasswordRequestVo {
    newPassword: string;
    confirmPassword: string;
}

export interface InitiateRecoveryRequestVo {
    email: string;
    code: string;
}

export interface CompleteRecoveryRequestVo {
    id: string;
    token: string;
}

export interface CompleteRecoveryResponseVo {
    username: string;
    token: string;
}