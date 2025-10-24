import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { UserApi } from "../../logic/auth/UserApi.ts";

export default function Logout() {
    const navigate = useNavigate();

    useEffect(() => {
        new UserApi().logout().then(() => {
            navigate("/", { replace: true });
            window.location.reload();
        });
    }, [navigate]);

    return <>Logging out...</>;
}
