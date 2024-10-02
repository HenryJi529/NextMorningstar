import { SessionStorageKey } from '@/constants/storage';

export const setPreRoute = (preRouteFullPath: string) => {
    if (preRouteFullPath.startsWith('/auth') && preRouteFullPath !== '/auth/profile') {
        return;
    }
    sessionStorage.setItem(SessionStorageKey.PRE_ROUTE, preRouteFullPath);
};

export const getPreRoute = () => {
    return sessionStorage.getItem(SessionStorageKey.PRE_ROUTE);
};

export const removePreRoute = () => {
    sessionStorage.removeItem(SessionStorageKey.PRE_ROUTE);
};
