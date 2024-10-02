
export const getSingleParam = (param: string[]| string) => {
    if (Array.isArray(param)) {
        return param[0];
    } else {
        return param;
    }
}