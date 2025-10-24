import { BrowserRouter } from 'react-router-dom';
import Router from './Router';
import { UserProvider } from './logic/context/UserContext.tsx';
import './global.css';

export default function App() {
    return (
        <UserProvider>
            <BrowserRouter>
                <Router />
            </BrowserRouter>
        </UserProvider>
    );
};
