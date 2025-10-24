import { RestApi } from "../Api.ts";
import { User } from "../../model/User.ts"

export class UserApi extends RestApi {
    verifyEmail(id: string) {
        return this.post<void>("/api/auth/verify", { id })
    }

    logout() {
        return this.get<void>("/api/auth/logout");
    }

    profile() {
        return this.get<User>("/api/auth/me")
    }

    loginBasic(credential: string, password: string) {
        return this.post<User>("/api/auth/basic/login", { credential, password });
    }

    register(name: string, email: string, password: string) {
        return this.post<User>("/api/auth/basic/register", { name, email, password });
    }
}