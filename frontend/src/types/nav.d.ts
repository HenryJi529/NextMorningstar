export interface Entry {
    name: string;
    link: string;
    isSpecial: boolean;
}

export interface Item {
    name: string;
    image?: string;
    desc?: string;
    url?: string;
    isModal: boolean;
    entries: Entry[];
}

export interface Category {
    name: string;
    slug: string;
    icon: string;
    requireAdmin: boolean;
    items: Item[];
}