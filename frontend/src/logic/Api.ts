import axios, { type AxiosInstance } from "axios";

export class RestApi {
    protected client: AxiosInstance;

    constructor(baseURL: string) {
        this.client = axios.create({ baseURL });
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