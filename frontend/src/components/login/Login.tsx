import { UserApi } from "../../logic/auth/UserApi.ts";

export default function Login() {
    const handleLogin = () => {
        new UserApi().login();
    };

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '100vh',
            background: 'var(--primary-bg)'
        }}>
            <div style={{
                background: 'var(--secondary-bg)',
                padding: '32px',
                borderRadius: '8px',
                textAlign: 'center',
                border: '1px solid var(--border)'
            }}>
                <h1 style={{ marginBottom: '16px' }}>Login</h1>
                <p style={{ color: 'var(--text-secondary)', marginBottom: '24px' }}>
                    Please log in with Discord to continue
                </p>
                <button onClick={handleLogin}>
                    Login with Discord
                </button>
            </div>
        </div>
    );
}