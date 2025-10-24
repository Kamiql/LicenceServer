import { useState, useRef, useEffect } from 'react';
import {
    Box,
    Card,
    CardContent,
    TextField,
    Button,
    Typography,
    Alert,
    Slide
} from '@mui/material';
import { UserApi } from "../../logic/auth/UserApi.ts";

interface LoginFormProps {
    onSwitchToRegister: () => void;
    loading: boolean;
    setLoading: (loading: boolean) => void;
    error: string;
    setError: (error: string) => void;
    onHeightChange: (height: number) => void;
}

interface RegisterFormProps {
    onSwitchToLogin: () => void;
    loading: boolean;
    setLoading: (loading: boolean) => void;
    error: string;
    setError: (error: string) => void;
    onHeightChange: (height: number) => void;
    onRegistrationSuccess: () => void;
}

const extractErrorMessage = (error: any): string => {
    if (typeof error === 'string') {
        return error;
    }
    if (error?.response?.data) {
        return error.response.data;
    }
    if (error?.message) {
        return error.message;
    }
    return 'An unexpected error occurred. Please try again.';
};

function LoginForm(props: LoginFormProps) {
    const [loginData, setLoginData] = useState({
        credential: '',
        password: ''
    });
    const formRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (formRef.current) {
            props.onHeightChange(formRef.current.scrollHeight);
        }
    }, [loginData, props.error, props.loading, props]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        props.setLoading(true);
        props.setError('');

        try {
            await new UserApi().loginBasic(
                loginData.credential.toLowerCase(),
                loginData.password
            );
            window.location.href = '/';
        } catch (error: any) {
            const errorMessage = extractErrorMessage(error);
            props.setError(errorMessage);
        } finally {
            props.setLoading(false);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setLoginData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    return (
        <Box ref={formRef}>
            <Typography
                variant="h4"
                component="h1"
                gutterBottom
                align="center"
                sx={{ color: 'var(--text-primary)', mb: 3 }}
            >
                Login
            </Typography>

            {props.error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {props.error}
                </Alert>
            )}

            <form onSubmit={handleSubmit}>
                <TextField
                    fullWidth
                    label="Username or Email"
                    name="credential"
                    value={loginData.credential}
                    onChange={handleChange}
                    margin="normal"
                    required
                    disabled={props.loading}
                    sx={{
                        '& .MuiInputLabel-root': { color: 'var(--text-secondary)' },
                        '& .MuiOutlinedInput-root': {
                            color: 'var(--text-primary)',
                            '& fieldset': { borderColor: 'var(--border)' },
                            '&:hover fieldset': { borderColor: 'var(--accent)' },
                            '&.Mui-disabled fieldset': { borderColor: 'var(--border)' }
                        }
                    }}
                />

                <TextField
                    fullWidth
                    label="Password"
                    name="password"
                    type="password"
                    value={loginData.password}
                    onChange={handleChange}
                    margin="normal"
                    required
                    disabled={props.loading}
                    sx={{
                        '& .MuiInputLabel-root': { color: 'var(--text-secondary)' },
                        '& .MuiOutlinedInput-root': {
                            color: 'var(--text-primary)',
                            '& fieldset': { borderColor: 'var(--border)' },
                            '&:hover fieldset': { borderColor: 'var(--accent)' },
                            '&.Mui-disabled fieldset': { borderColor: 'var(--border)' }
                        }
                    }}
                />

                <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    disabled={props.loading}
                    sx={{
                        mt: 3,
                        mb: 2,
                        backgroundColor: 'var(--accent)',
                        '&:hover': {
                            backgroundColor: 'var(--accent)',
                            filter: 'brightness(1.2)'
                        },
                        '&.Mui-disabled': {
                            backgroundColor: 'var(--border)',
                            color: 'var(--text-secondary)'
                        }
                    }}
                >
                    {props.loading ? 'Loading...' : 'Login'}
                </Button>
            </form>

            <Box sx={{ textAlign: 'center', mb: 2 }}>
                <Typography
                    variant="body2"
                    sx={{
                        color: 'var(--text-secondary)',
                        cursor: 'pointer',
                        '&:hover': {
                            color: 'var(--accent)'
                        }
                    }}
                    onClick={props.onSwitchToRegister}
                >
                    Don't have an account? Register
                </Typography>
            </Box>
        </Box>
    );
}

// Separate Register-Komponente
function RegisterForm(props: RegisterFormProps) {
    const [registerData, setRegisterData] = useState({
        name: '',
        email: '',
        password: ''
    });
    const [registrationSuccess, setRegistrationSuccess] = useState(false);
    const formRef = useRef<HTMLDivElement>(null);

    // Höhe an Parent melden wenn sich etwas ändert
    useEffect(() => {
        if (formRef.current) {
            props.onHeightChange(formRef.current.scrollHeight);
        }
    }, [registerData, props.error, props.loading, props, registrationSuccess]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        props.setLoading(true);
        props.setError('');

        try {
            await new UserApi().register(
                registerData.name.toLowerCase(),
                registerData.email.toLowerCase(),
                registerData.password
            );
            setRegistrationSuccess(true);
            props.onRegistrationSuccess();
        } catch (error: any) {
            const errorMessage = extractErrorMessage(error);
            props.setError(errorMessage);
        } finally {
            props.setLoading(false);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setRegisterData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const handleBackToLogin = () => {
        setRegistrationSuccess(false);
        setRegisterData({ name: '', email: '', password: '' });
        props.onSwitchToLogin();
    };

    if (registrationSuccess) {
        return (
            <Box ref={formRef}>
                <Typography
                    variant="h4"
                    component="h1"
                    gutterBottom
                    align="center"
                    sx={{ color: 'var(--text-primary)', mb: 3 }}
                >
                    Registration Successful
                </Typography>

                <Alert severity="success" sx={{ mb: 3 }}>
                    A confirmation email has been sent to your email address.
                    Please check your inbox and click the verification link to activate your account.
                </Alert>

                <Typography
                    variant="body2"
                    sx={{
                        color: 'var(--text-secondary)',
                        mb: 3,
                        textAlign: 'center'
                    }}
                >
                    Once you've verified your email, you can log in to your account.
                </Typography>

                <Button
                    fullWidth
                    variant="contained"
                    onClick={handleBackToLogin}
                    sx={{
                        mt: 2,
                        backgroundColor: 'var(--accent)',
                        '&:hover': {
                            backgroundColor: 'var(--accent)',
                            filter: 'brightness(1.2)'
                        }
                    }}
                >
                    Back to Login
                </Button>
            </Box>
        );
    }

    return (
        <Box ref={formRef}>
            <Typography
                variant="h4"
                component="h1"
                gutterBottom
                align="center"
                sx={{ color: 'var(--text-primary)', mb: 3 }}
            >
                Register
            </Typography>

            {props.error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {props.error}
                </Alert>
            )}

            <form onSubmit={handleSubmit}>
                <TextField
                    fullWidth
                    label="Username"
                    name="name"
                    value={registerData.name}
                    onChange={handleChange}
                    margin="normal"
                    required
                    disabled={props.loading}
                    sx={{
                        '& .MuiInputLabel-root': { color: 'var(--text-secondary)' },
                        '& .MuiOutlinedInput-root': {
                            color: 'var(--text-primary)',
                            '& fieldset': { borderColor: 'var(--border)' },
                            '&:hover fieldset': { borderColor: 'var(--accent)' },
                            '&.Mui-disabled fieldset': { borderColor: 'var(--border)' }
                        }
                    }}
                />

                <TextField
                    fullWidth
                    label="Email"
                    name="email"
                    type="email"
                    value={registerData.email}
                    onChange={handleChange}
                    margin="normal"
                    required
                    disabled={props.loading}
                    sx={{
                        '& .MuiInputLabel-root': { color: 'var(--text-secondary)' },
                        '& .MuiOutlinedInput-root': {
                            color: 'var(--text-primary)',
                            '& fieldset': { borderColor: 'var(--border)' },
                            '&:hover fieldset': { borderColor: 'var(--accent)' },
                            '&.Mui-disabled fieldset': { borderColor: 'var(--border)' }
                        }
                    }}
                />

                <TextField
                    fullWidth
                    label="Password"
                    name="password"
                    type="password"
                    value={registerData.password}
                    onChange={handleChange}
                    margin="normal"
                    required
                    disabled={props.loading}
                    sx={{
                        '& .MuiInputLabel-root': { color: 'var(--text-secondary)' },
                        '& .MuiOutlinedInput-root': {
                            color: 'var(--text-primary)',
                            '& fieldset': { borderColor: 'var(--border)' },
                            '&:hover fieldset': { borderColor: 'var(--accent)' },
                            '&.Mui-disabled fieldset': { borderColor: 'var(--border)' }
                        }
                    }}
                />

                <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    disabled={props.loading}
                    sx={{
                        mt: 3,
                        mb: 2,
                        backgroundColor: 'var(--accent)',
                        '&:hover': {
                            backgroundColor: 'var(--accent)',
                            filter: 'brightness(1.2)'
                        },
                        '&.Mui-disabled': {
                            backgroundColor: 'var(--border)',
                            color: 'var(--text-secondary)'
                        }
                    }}
                >
                    {props.loading ? 'Loading...' : 'Register'}
                </Button>
            </form>

            <Box sx={{ textAlign: 'center', mb: 2 }}>
                <Typography
                    variant="body2"
                    sx={{
                        color: 'var(--text-secondary)',
                        cursor: 'pointer',
                        '&:hover': {
                            color: 'var(--accent)'
                        }
                    }}
                    onClick={props.onSwitchToLogin}
                >
                    Already have an account? Login
                </Typography>
            </Box>
        </Box>
    );
}

export default function Auth() {
    const [currentView, setCurrentView] = useState<'login' | 'register'>('login');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [slideDirection, setSlideDirection] = useState<'left' | 'right'>('right');
    const [formHeight, setFormHeight] = useState(400);

    const switchToRegister = () => {
        if (loading) return;
        setSlideDirection('left');
        setCurrentView('register');
        setError('');
    };

    const switchToLogin = () => {
        if (loading) return;
        setSlideDirection('right');
        setCurrentView('login');
        setError('');
    };

    const handleRegistrationSuccess = () => {
        // Optional: Hier könntest du zusätzliche Aktionen nach erfolgreicher Registrierung durchführen
        console.log('Registration successful - email confirmation sent');
    };

    const handleLoginHeightChange = (height: number) => {
        if (currentView === 'login') {
            setFormHeight(height);
        }
    };

    const handleRegisterHeightChange = (height: number) => {
        if (currentView === 'register') {
            setFormHeight(height);
        }
    };

    return (
        <Box
            sx={{
                minHeight: '100vh',
                background: 'var(--primary-bg)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                p: 2
            }}
        >
            <Card
                sx={{
                    maxWidth: 400,
                    width: '100%',
                    backgroundColor: 'var(--secondary-bg)',
                    border: '1px solid var(--border)',
                    position: 'relative',
                    overflow: 'visible'
                }}
            >
                <CardContent sx={{ p: 4, position: 'relative' }}>
                    {/* Container für die animierten Formulare mit dynamischer Höhe */}
                    <Box sx={{
                        position: 'relative',
                        height: formHeight,
                        transition: 'height 0.3s ease-in-out',
                        mb: 3
                    }}>
                        {/* Slide Animation für Login */}
                        <Slide
                            direction={slideDirection}
                            in={currentView === 'login'}
                            mountOnEnter
                            unmountOnExit
                            timeout={300}
                        >
                            <Box sx={{
                                position: 'absolute',
                                width: '100%',
                                top: 0,
                                left: 0
                            }}>
                                <LoginForm
                                    onSwitchToRegister={switchToRegister}
                                    loading={loading}
                                    setLoading={setLoading}
                                    error={error}
                                    setError={setError}
                                    onHeightChange={handleLoginHeightChange}
                                />
                            </Box>
                        </Slide>

                        {/* Slide Animation für Register */}
                        <Slide
                            direction={slideDirection === 'left' ? 'right' : 'left'}
                            in={currentView === 'register'}
                            mountOnEnter
                            unmountOnExit
                            timeout={300}
                        >
                            <Box sx={{
                                position: 'absolute',
                                width: '100%',
                                top: 0,
                                left: 0
                            }}>
                                <RegisterForm
                                    onSwitchToLogin={switchToLogin}
                                    loading={loading}
                                    setLoading={setLoading}
                                    error={error}
                                    setError={setError}
                                    onHeightChange={handleRegisterHeightChange}
                                    onRegistrationSuccess={handleRegistrationSuccess}
                                />
                            </Box>
                        </Slide>
                    </Box>
                </CardContent>
            </Card>
        </Box>
    );
}