import {PRE_ROUTE} from "@/constants/LocalStorageConstant";

export const setPreRoute = (fromFullPath: string) => {
    if (fromFullPath === '/auth/register' || fromFullPath === '/auth/login') {
        return;
    }
    localStorage.setItem(PRE_ROUTE, fromFullPath);
}

export const getPreRoute = () => {
    return localStorage.getItem(PRE_ROUTE);
}

export const removePreRoute = () => {
    localStorage.removeItem(PRE_ROUTE);
}