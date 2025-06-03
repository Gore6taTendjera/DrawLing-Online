import { useEffect } from "react";
import { axiosPrivate } from "./axios";
import Cookies from "js-cookie";

import { useRefreshToken } from "../hooks/useRefreshToken";
import useAuth from "../hooks/useAuth";

const usingAxiosPrivate = () => {
    const refreshToken = useRefreshToken();
    const { auth } = useAuth();

    useEffect(() => {
        const requestInterceptor = axiosPrivate.interceptors.request.use(
            config => {
                const xsrfToken = Cookies.get('XSRF-TOKEN');
                
                if (xsrfToken) {
                    config.headers['X-XSRF-TOKEN'] = xsrfToken;
                }

                console.log('Request Interceptor accessToken:', auth.accessToken);
                if (auth.accessToken) {
                    console.log('🎉AXIOS INTERCEPTOR: JWT PRESENT:', config.url);

                    if (!config.headers['Authorization']) {
                        config.headers['Authorization'] = `Bearer ${auth?.accessToken}`;
                    }
                }
                console.log('Request Interceptor:', config.url);
                return config;
            },
            error => Promise.reject(error)
        );

        const responseInterceptor = axiosPrivate.interceptors.response.use(
            response => response,
            async (error) => {
                const { response } = error;
                if (response && (response.status === 401 || response.status === 403)) {
                    const originalRequest = error.config;

                    if (originalRequest._retry) {
                        console.log('Already retried request:', originalRequest.url);
                        return Promise.reject(error);
                    }
                    originalRequest._retry = true;

                    try {
                        console.log('Refreshing access token...');
                        const newAccessToken = await refreshToken();
                        originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                        return axiosPrivate(originalRequest);
                    } catch (refreshError) {
                        // console.error('Error refreshing token:', refreshError);
                        return Promise.reject(refreshError);
                    }
                }
                return Promise.reject(error);
            }
        );

        return () => {
            axiosPrivate.interceptors.request.eject(requestInterceptor);
            axiosPrivate.interceptors.response.eject(responseInterceptor);
        };
    }, [refreshToken]);

    return axiosPrivate;
};

export default usingAxiosPrivate;
