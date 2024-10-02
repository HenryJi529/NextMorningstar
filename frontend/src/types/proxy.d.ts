export interface Node {
    id: number;
    link: string;
    subId: number;
    updateTime: string;
}

export interface NodeDetail extends Node {
    name: string;
    protocol: string;
}

export interface Sub {
    id: number;
    name: string;
    link: string;
    updateTime: string;
}

export interface Token {
    id: number;
    name: string;
    value: string;
    updateTime: string;
}

export interface CreateNodeRequestVo {
    link: string;
}

export interface CreateSubRequestVo {
    name: string;
    link: string;
}

export interface CreateTokenRequestVo {
    name: string;
    value: string;
}

export interface UpdateNodeRequestVo {
    id: number;
    name: string;
    link: string;
}

export interface UpdateSubRequestVo {
    id: number;
    name: string;
    link: string;
}

export interface UpdateTokenRequestVo {
    id: number;
    name: string;
    value: string;
}