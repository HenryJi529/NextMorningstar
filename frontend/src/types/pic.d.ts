export interface Image {
    filename: string;
    path: string;
    size: number;
    updateTime: string;
}

export interface Usage {
    usedStorage: number;
    imageCount: number;
    dayCount: number;
}