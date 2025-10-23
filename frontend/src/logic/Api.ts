import axios, { type AxiosInstance } from "axios";

export class RestApi {
    protected client: AxiosInstance;

    constructor(baseURL: string = import.meta.env.VITE_API_BASE_URL) {
        console.log(baseURL);
        this.client = axios.create({ baseURL });
    }

    protected redirect(url: string) {
        const baseURL = this.client.defaults.baseURL || '';
        window.location.href = `${baseURL}${url}`;
    }

    protected get<T>(url: string, config?: any) {
        return this.client.get<T>(url, config).then(res => res.data);
    }

    protected post<T>(url: string, data?: any, config?: any) {
        return this.client.post<T>(url, data, config).then(res => res.data);
    }

    protected put<T>(url: string, data?: any, config?: any) {
        return this.client.put<T>(url, data, config).then(res => res.data);
    }

    protected delete<T>(url: string, config?: any) {
        return this.client.delete<T>(url, config).then(res => res.data);
    }
}