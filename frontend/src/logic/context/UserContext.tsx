import {createContext, useContext, useState, type ReactNode, useEffect} from "react";
import type {User} from "../../model/User.ts";
import {UserApi} from "../auth/UserApi.ts";

type UserContextType = {
    user: User | null;
    setUser: (user: User | null) => void;
    reloadUser: () => void;
};

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);

    const reloadUser = () => {
        new UserApi().profile()
            .then(setUser)
            .catch(() => setUser(null));
    };

    useEffect(() => {
        reloadUser();
    }, []);

    return (
        <UserContext.Provider value={{ user, setUser, reloadUser }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    const context = useContext(UserContext);
    if (!context) throw new Error("useUser must be used within a UserProvider");
    return context;
};