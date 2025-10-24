import axios, { type AxiosInstance } from "axios";

export class RestApi {
    protected client: AxiosInstance;

    constructor() {
        this.client = axios.create();
    }

    protected async get<T>(url: string, config?: any) {
        const res = await this.client.get<T>(url, config);
        return res.data;
    }

    protected async post<T>(url: string, data?: any, config?: any) {
        const res = await this.client.post<T>(url, data, config);
        return res.data;
    }

    protected async put<T>(url: string, data?: any, config?: any) {
        let res = await this.client.put<T>(url, data, config);
        return res.data;
    }

    protected async delete<T>(url: string, config?: any) {
        let res = await this.client.delete<T>(url, config);
        return res.data;
    }
}