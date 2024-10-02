import axios from "@/axios/index";

export const parseExcel = (file: File) => {
    const data = new FormData();
    data.append("file", file);
    return axios(
        {
            method: 'POST',
            url: '/api/practice/parse-excel',
            data: data,
        }
    );
}