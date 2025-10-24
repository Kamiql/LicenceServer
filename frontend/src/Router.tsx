import { Routes, Route, Navigate } from 'react-router-dom';
import Home from './components/home/Home';
import Login from './components/auth/Login';
import Logout from "./components/auth/Logout.tsx";
import VerifyEmail from "./components/auth/VerifyEmail.tsx";
import {useUser} from "./logic/context/UserContext.tsx";

export default function Router() {
    const { user } = useUser();

    return (
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={user ? <Navigate to="/" /> : <Login />} />
            <Route path="/logout" element={<Logout />} />
            <Route path="/verify" element={<VerifyEmail />} />
        </Routes>
    );
}