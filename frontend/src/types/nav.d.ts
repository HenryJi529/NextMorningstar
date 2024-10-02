export interface Entry {
    name: string;
    link: string;
    isSpecial: boolean;
}

export interface Item {
    name: string;
    image: string | undefined;
    desc: string | undefined;
    url: string | undefined;
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