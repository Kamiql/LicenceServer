import { useState } from 'react';
import {
    Box,
    Typography,
    Card,
    CardContent,
    Drawer,
    List,
    ListItem,
    ListItemIcon,
    ListItemText
} from '@mui/material';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faChartBar,
    faFolder,
    faProjectDiagram,
    faBuilding,
    faStar,
    faCog,
} from '@fortawesome/free-solid-svg-icons';
import Header from '../layout/Header.tsx';
import {useUser} from "../../logic/context/UserContext.tsx";

const drawerWidth = 240;

export default function Home() {
    const { user } = useUser();

    const [sidebarOpen, setSidebarOpen] = useState(true);

    const menuItems = [
        { text: 'Dashboard', icon: faChartBar, href: '/dashboard' },
        { text: 'Repositories', icon: faFolder, href: '/repositories' },
        { text: 'Projects', icon: faProjectDiagram, href: '/projects' },
        { text: 'Organizations', icon: faBuilding, href: '/organizations' },
        { text: 'Stars', icon: faStar, href: '/stars' },
        { text: 'Settings', icon: faCog, href: '/settings' },
    ];

    return (
        <Box sx={{ display: 'flex' }}>
            <Header user={user} onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} />

            {/* Sidebar */}
            <Drawer
                variant="persistent"
                open={sidebarOpen}
                sx={{
                    width: drawerWidth,
                    flexShrink: 0,
                    '& .MuiDrawer-paper': {
                        width: drawerWidth,
                        boxSizing: 'border-box',
                        backgroundColor: 'var(--secondary-bg)',
                        borderRight: '1px solid var(--border)',
                        color: 'var(--text-primary)',
                        marginTop: '64px',
                    },
                }}
            >
                <List sx={{ marginTop: '16px' }}>
                    {menuItems.map((item) => (
                        <ListItem
                            key={item.text}
                            component="a"
                            href={item.href}
                            sx={{
                                color: 'var(--text-secondary)',
                                textDecoration: 'none',
                                '&:hover': {
                                    backgroundColor: 'rgba(255, 255, 255, 0.1)',
                                    color: 'var(--text-primary)',
                                },
                            }}
                        >
                            <ListItemIcon sx={{ color: 'inherit', minWidth: 40 }}>
                                <FontAwesomeIcon icon={item.icon} />
                            </ListItemIcon>
                            <ListItemText primary={item.text} />
                        </ListItem>
                    ))}
                </List>
            </Drawer>

            {/* Main Content */}
            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    p: 3,
                    marginLeft: sidebarOpen ? 0 : `-${drawerWidth}px`,
                    transition: (theme) => theme.transitions.create('margin', {
                        easing: theme.transitions.easing.sharp,
                        duration: theme.transitions.duration.leavingScreen,
                    }),
                    marginTop: '64px',
                }}
            >
                <Box sx={{ maxWidth: '800px' }}>
                    <Typography variant="h4" gutterBottom sx={{ color: 'var(--text-primary)' }}>
                        Welcome to GitBucket
                    </Typography>
                    <Typography
                        variant="body1"
                        gutterBottom
                        sx={{ color: 'var(--text-secondary)', mb: 3 }}
                    >
                        {user
                            ? `Hello ${user.username}! Welcome back to your dashboard.`
                            : 'Please log in to access all features and manage your projects.'
                        }
                    </Typography>

                    {user && (
                        <Card
                            sx={{
                                backgroundColor: 'var(--secondary-bg)',
                                border: '1px solid var(--border)'
                            }}
                        >
                            <CardContent>
                                <Typography variant="h5" gutterBottom sx={{ color: 'var(--text-primary)' }}>
                                    Quick Stats
                                </Typography>
                            </CardContent>
                        </Card>
                    )}
                </Box>
            </Box>
        </Box>
    );
}