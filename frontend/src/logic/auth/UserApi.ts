import { RestApi } from "../Api.ts";
import { User } from "../../model/User.ts"

export class UserApi extends RestApi {
    login() {
        this.redirect("/auth/discord/login");
    }

    profile() {
        return this.get<User>("/auth/me")
    }
}