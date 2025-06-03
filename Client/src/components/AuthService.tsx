import useAuth from '../hooks/useAuth';
import usingAxiosPrivate from "../api/usingAxiosPrivate"
import normalAxios from '../api/axios';
import { useRefreshToken } from "../hooks/useRefreshToken";

import { axiosFinal } from '../api/axios';
import finalInterceptor from '../api/finalInterceptor';

const AuthService = () => {
    const { setAuth, auth } = useAuth();
    const axiosInstance = usingAxiosPrivate();
    const refreshToken = useRefreshToken();
    const finalInterceptorAxios = finalInterceptor();


    const register = async (username: string, password: string) => {
        try {
            const response = await normalAxios.post('/authentication/register', { username, password });
            setAuth({ accessToken: response.data.jwtToken });
            return response;
        } catch (error: any) {
            // console.error('Error registering user:', error);
            throw new Error('Failed to register user');
        }
    };

    const login = async (username: string, password: string) => {
        try {
            const response = await finalInterceptorAxios.post('/authentication/login', { username, password }, {withCredentials: true});
            setAuth({ accessToken: response.data.jwtToken });
            return response;
        } catch (error: any) {
            if (error.response && (error.response.status === 401 || error.response.status === 403)) {
                throw new Error('Invalid username or password');
            }
            throw new Error('An error occurred. Please try again later.');
        }
    };

    const getProtected = async () => {
        try {
            const response = await axiosInstance.get('/authentication/profile', {
                headers: { Authorization: `Bearer ${auth.accessToken}` }
            });
            // console.log("AUTH_SERVICE GET_PROTECTED RESPONSE: " + response.data);
            return response.data;
        } catch (error) {
            // console.error('Error fetching protected data:', error);
            throw new Error('Failed to fetch protected data');
        }
    };


    const logOut = async () => {
        try{
            const response = await axiosInstance.post('authentication/logout', {}, { withCredentials: true });
            setAuth({ accessToken: undefined });
            return response;
        } catch (error) {
            // console.error('Error logging out:', error);
            throw new Error('Failed to log out');
        }

    }

    const getCSRFToken = async () => {
        try {
            const response = await axiosFinal.get('/csrf');
            return response.data;
        } catch (error) {
            // console.error('Error fetching CSRF token:', error);
            throw new Error('Failed to fetch CSRF token');
        }
    };

    return { register, login, refreshToken, getProtected, logOut, getCSRFToken };
};

export default AuthService;
