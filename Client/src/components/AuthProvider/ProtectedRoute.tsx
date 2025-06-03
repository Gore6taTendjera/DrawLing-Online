import React, { useEffect, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import { useRefreshToken } from '../../hooks/useRefreshToken';
import { jwtDecode } from 'jwt-decode';

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const { auth } = useAuth();
    const location = useLocation();
    const [loading, setLoading] = useState(true);
    const refreshToken = useRefreshToken();

    useEffect(() => {
        const attemptRefreshToken = async () => {
            try {
                if (!auth.accessToken) {
                    console.log('PROTECTED ROUTE: No access token found. Attempting to refresh token.');
                    await refreshToken();
                } else {
                    const jwtDecoded = jwtDecode(auth.accessToken);

                    if (jwtDecoded && jwtDecoded.exp) {
                        if (jwtDecoded.exp < Date.now() / 1000) {
                            await refreshToken();
                        }
                    }
                }
            } catch (error) {
                // console.error('Failed to refresh token:', error);
            } finally {
                setLoading(false);
            }
        };

        attemptRefreshToken();
    }, [auth.accessToken, refreshToken]);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!auth.accessToken) {
        return <Navigate to="/login" state={{ from: location }} />;
    }

    return <>{children}</>;
};

export default ProtectedRoute;
