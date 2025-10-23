import { useState, useEffect } from 'react';
import { UserApi } from "../../logic/auth/UserApi.ts";
import { User } from "../../model/User.ts";

export default function Home() {
    const [user, setUser] = useState<User | null>(null);
    const [sidebarOpen, setSidebarOpen] = useState(true);

    useEffect(() => {
        new UserApi().profile()
            .then(setUser)
            .catch(() => setUser(new User(
                15101519100019,
                "kamiql",
                "kilian.aqua@gmail.com"
            )));
    }, []);

    return (
        <>
            {/* Header */}
            <header className="header">
                <div className="header-left">
                    <img src="/logo.png" alt="Logo" className="logo" />
                    <span style={{ fontSize: '20px', fontWeight: '600' }}>ShitCup</span>
                </div>

                <div className="header-right">
                    {user ? (
                        <>
                            <div className="user-info">
                                <img
                                    src={user.avatar || '/vite.svg'}
                                    alt="Avatar"
                                    className="avatar"
                                />
                                <span>{user.username}</span>
                            </div>
                            <button
                                className="burger-menu"
                                onClick={() => setSidebarOpen(!sidebarOpen)}
                            >
                                ☰
                            </button>
                        </>
                    ) : (
                        <a href="/login">
                            <button>Login</button>
                        </a>
                    )}
                </div>
            </header>

            {/* Main Content */}
            <div className="main-container">
                {/* Sidebar */}
                {sidebarOpen && (
                    <aside className="sidebar">
                        <nav className="sidebar-nav">
                            <a href="/dashboard" className="sidebar-item active">
                                <span>📊</span>
                                Dashboard
                            </a>
                            <a href="/repositories" className="sidebar-item">
                                <span>📁</span>
                                Repositories
                            </a>
                            <a href="/projects" className="sidebar-item">
                                <span>🗂️</span>
                                Projects
                            </a>
                            <a href="/organizations" className="sidebar-item">
                                <span>🏢</span>
                                Organizations
                            </a>
                            <a href="/stars" className="sidebar-item">
                                <span>⭐</span>
                                Stars
                            </a>
                            <a href="/settings" className="sidebar-item">
                                <span>⚙️</span>
                                Settings
                            </a>
                        </nav>
                    </aside>
                )}

                {/* Content */}
                <main className="content" style={{
                    marginLeft: sidebarOpen ? 'var(--sidebar-width)' : '0'
                }}>
                    <div style={{ maxWidth: '800px' }}>
                        <h1 style={{ marginBottom: '16px' }}>Welcome to ShitCup</h1>
                        <p style={{ color: 'var(--text-secondary)', marginBottom: '24px' }}>
                            {user
                                ? `Hello ${user.username}! Welcome back to your dashboard.`
                                : 'Please log in to access all features and manage your projects.'
                            }
                        </p>

                        {user && (
                            <div style={{
                                background: 'var(--secondary-bg)',
                                padding: '24px',
                                borderRadius: '8px',
                                border: '1px solid var(--border)'
                            }}>
                                <h2 style={{ marginBottom: '16px' }}>Quick Stats</h2>
                                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
                                    <div>
                                        <h3 style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>Repositories</h3>
                                        <p style={{ fontSize: '24px', fontWeight: 'bold' }}>0</p>
                                    </div>
                                    <div>
                                        <h3 style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>Projects</h3>
                                        <p style={{ fontSize: '24px', fontWeight: 'bold' }}>0</p>
                                    </div>
                                    <div>
                                        <h3 style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>Organizations</h3>
                                        <p style={{ fontSize: '24px', fontWeight: 'bold' }}>0</p>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </main>
            </div>
        </>
    );
}