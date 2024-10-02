export interface Entry {
    name: string;
    link: string;
    class: "special" | undefined;
}

export interface Item {
    name: string;
    image: string | null;
    desc: string | null;
    url: string | null;
    display: "modal" | undefined;
    entries: Entry[];
}

export interface Category {
    name: string;
    slug: string;
    icon: string;
    level: "admin" | undefined;
    items: Item[];
}