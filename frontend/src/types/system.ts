export enum Scope {
    PUBLIC = 'PUBLIC',
    INTERNAL = 'INTERNAL',
}

export interface CreateSysParamRequestVo {
    name: string;
    value: string;
    scope: string;
    status: boolean;
    remark: string;
}

export interface UpdateSysParamRequestVo {
    id: string;
    name: string;
    value: string;
    scope: string;
    status: boolean;
    remark: string;
}

export interface SysParam {
    id: string;
    name: string;
    value: string;
    scope: string;
    status: boolean;
    remark: string;
    createTime: string;
    updateTime: string;
}
