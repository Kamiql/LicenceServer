export class User {
    id: number;
    username: string;
    email: string | null;
    avatar: string | null;
    groups: string[];

    constructor(
        id: number,
        username: string,
        email: string | null = null,
        avatar: string | null = null,
        groups: string[] = []
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.groups = groups;
    }
}
