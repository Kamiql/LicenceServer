import { useState, useRef, useEffect } from 'react';
import {
    AppBar,
    Toolbar,
    Typography,
    IconButton,
    Avatar,
    Box,
} from '@mui/material';
import {
    FontAwesomeIcon
} from '@fortawesome/react-fontawesome';
import {
    faBars,
    faUser,
    faCog,
    faSignOutAlt,
} from '@fortawesome/free-solid-svg-icons';
import { User } from '../../model/User';

interface HeaderProps {
    user: User | null;
    onToggleSidebar?: () => void;
}

export default function Header({ user, onToggleSidebar }: HeaderProps) {
    const [menuOpen, setMenuOpen] = useState(false);
    const menuRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setMenuOpen(false);
            }
        }

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    return (
        <AppBar
            position="fixed"
            sx={{
                zIndex: (theme) => theme.zIndex.drawer + 1,
                backgroundColor: 'var(--primary-bg)',
                borderBottom: '1px solid var(--border)',
                boxShadow: 'none'
            }}
        >
            <Toolbar sx={{ justifyContent: 'space-between' }}>
                {/* Left Section */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <IconButton
                        color="inherit"
                        onClick={onToggleSidebar}
                        sx={{ color: 'var(--text-primary)' }}
                    >
                        <FontAwesomeIcon icon={faBars} />
                    </IconButton>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <img src="/logo.png" alt="Logo" style={{ height: '32px' }} />
                        <Typography
                            variant="h6"
                            component="div"
                            sx={{
                                color: 'var(--text-primary)',
                                fontWeight: 600
                            }}
                        >
                            GitBucket
                        </Typography>
                    </Box>
                </Box>

                {/* Right Section */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    {user ? (
                        <>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mr: 1 }}>
                                <Avatar
                                    src={user.avatar || '/vite.svg'}
                                    sx={{ width: 32, height: 32 }}
                                />
                                <Typography variant="body2" sx={{ color: 'var(--text-primary)' }}>
                                    {user.username}
                                </Typography>
                            </Box>

                            <div ref={menuRef}>
                                <IconButton
                                    color="inherit"
                                    onClick={() => setMenuOpen(!menuOpen)}
                                    sx={{ color: 'var(--text-primary)' }}
                                >
                                    <FontAwesomeIcon icon={faUser} />
                                </IconButton>

                                {menuOpen && (
                                    <>
                                        <div className="overlay-backdrop" onClick={() => setMenuOpen(false)} />
                                        <div className="popup-menu">
                                            <button className="popup-item" onClick={() => {
                                                window.location.href = "/settings/profile"
                                            }}>
                                                <FontAwesomeIcon icon={faUser} />
                                                Profile
                                            </button>
                                            <button className="popup-item" onClick={() => {
                                                window.location.href = "/settings"
                                            }}>
                                                <FontAwesomeIcon icon={faCog} />
                                                Settings
                                            </button>
                                            <div className="popup-divider" />
                                            <button className="popup-item" onClick={() => {
                                                window.location.href = "/logout"
                                            }}>
                                                <FontAwesomeIcon icon={faSignOutAlt} />
                                                Logout
                                            </button>
                                        </div>
                                    </>
                                )}
                            </div>
                        </>
                    ) : (
                        <IconButton
                            color="inherit"
                            component="a"
                            href="/login"
                            sx={{ color: 'var(--text-primary)' }}
                        >
                            <FontAwesomeIcon icon={faUser} />
                        </IconButton>
                    )}
                </Box>
            </Toolbar>
        </AppBar>
    );
}