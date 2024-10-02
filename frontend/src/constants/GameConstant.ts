export enum GameMode {
    IDENTITY,
    NATION,
    REVERT,
    SOLO,
    DOUBLES,
    TRIPLES,
}

export interface GameModeInfo {
    name: string;
}

export const GameModeInfoMap: Record<GameMode, GameModeInfo> = {
    [GameMode.IDENTITY]: {
        name: "身份场",
    },
    [GameMode.NATION]: {
        name: "国战",
    },
    [GameMode.REVERT]: {
        name: "斗地主",
    },
    [GameMode.SOLO]: {
        name: "1V1",
    },
    [GameMode.DOUBLES]: {
        name: "2V2",
    },
    [GameMode.TRIPLES]: {
        name: "3V3",
    },
};
