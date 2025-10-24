import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import {
    Box,
    Card,
    CardContent,
    Typography,
    Alert,
    CircularProgress,
    Button
} from '@mui/material';
import { UserApi } from "../../logic/auth/UserApi.ts";

export default function VerifyEmail() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    const verificationId = searchParams.get('id');

    useEffect(() => {
        const verifyEmail = async () => {
            if (!verificationId) {
                setError('Invalid verification link');
                setLoading(false);
                return;
            }

            try {
                await new UserApi().verifyEmail(verificationId);
                setSuccess(true);

                // Automatically redirect to login after 3 seconds on success
                setTimeout(() => {
                    navigate('/login');
                }, 3000);

            } catch (error: any) {
                if (typeof error === 'string') {
                    setError(error);
                } else if (error?.response?.data) {
                    setError(error.response.data);
                } else if (error?.message) {
                    setError(error.message);
                } else {
                    setError('Email verification failed. Please try again.');
                }
            } finally {
                setLoading(false);
            }
        };

        verifyEmail();
    }, [verificationId, navigate]);

    const handleGoToLogin = () => {
        navigate('/login');
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
                    border: '1px solid var(--border)'
                }}
            >
                <CardContent sx={{ p: 4, textAlign: 'center' }}>
                    <Typography
                        variant="h4"
                        component="h1"
                        gutterBottom
                        sx={{ color: 'var(--text-primary)', mb: 3 }}
                    >
                        Email Verification
                    </Typography>

                    {loading && (
                        <Box sx={{ my: 4 }}>
                            <CircularProgress
                                sx={{
                                    color: 'var(--accent)',
                                    mb: 2
                                }}
                            />
                            <Typography
                                variant="body1"
                                sx={{ color: 'var(--text-secondary)' }}
                            >
                                Verifying your email address...
                            </Typography>
                        </Box>
                    )}

                    {error && !loading && (
                        <Box>
                            <Alert
                                severity="error"
                                sx={{ mb: 3 }}
                            >
                                {error}
                            </Alert>
                            <Button
                                variant="contained"
                                onClick={handleGoToLogin}
                                sx={{
                                    backgroundColor: 'var(--accent)',
                                    '&:hover': {
                                        backgroundColor: 'var(--accent)',
                                        filter: 'brightness(1.2)'
                                    }
                                }}
                            >
                                Go to Login
                            </Button>
                        </Box>
                    )}

                    {success && !loading && (
                        <Box>
                            <Alert
                                severity="success"
                                sx={{ mb: 3 }}
                            >
                                Your email has been successfully verified!
                                <br />
                                Redirecting to login page...
                            </Alert>
                        </Box>
                    )}
                </CardContent>
            </Card>
        </Box>
    );
}