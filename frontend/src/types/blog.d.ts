export interface Article {
    id: string;
    title: string;
    content: string;
    categoryId: number;
    views: number;
    createTime: string;
    updateTime: string;
}

export interface Category {
    id: number;
    name: string;
}

export interface CategoryDetail extends Category{
    count: number;
}

export interface Tag {
    id: number;
    name: string;
}

export interface TagDetail extends Tag {
    count: number;
}

export interface Comment {
    id: string;
    parentId: string;
    userId: string;
    articleId: string;
    content: string;
    createTime: string;
    updateTime: string;
}

export interface ArticleDetail {
    article: Article;
    category: Category;
    tags: Tag[];
}

export interface CommentDetail extends Comment {
    username: string;
    nickname: string;
    avatar: string;
    thumbsUpUserIds: string[];
    thumbsDownUserIds: string[];
}

export interface CreateCommentRequestVo {
    parentId: string | null;
    articleId: string;
    content: string;
}