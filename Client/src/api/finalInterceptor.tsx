import { useEffect } from 'react';
import { axiosFinal } from './axios';
import Cookies from 'js-cookie';

const finalInterceptor = () => {
    useEffect(() => {
        const requestInterceptor = axiosFinal.interceptors.request.use(
            config => {
                const xsrfToken = Cookies.get('XSRF-TOKEN');
                console.log('final interceptor request xsrfToken: ', xsrfToken);
                
                if (xsrfToken) {
                    config.headers['X-XSRF-TOKEN'] = xsrfToken;
                }
                return config;
            },
            error => Promise.reject(error)
        );

        const responseInterceptor = axiosFinal.interceptors.response.use(
            response => response,
            async (error) => {
                const originalRequest = error.config;
        
                if (error.response) {
                    console.log('Response error status:', error.response.status);
                } else {
                    console.log('Response error status: No response');
                    console.log('Error message:', error.message);
                }
        
                if (!originalRequest._retry) {
                    originalRequest._retry = true;
        
                    try {
                        const response = await axiosFinal.get('/csrf', { withCredentials: true });
                        console.log('CSRF token response:', response);
                        
                        const xsrfToken = response.data.token;
                        if (xsrfToken) {
                            originalRequest.headers['X-XSRF-TOKEN'] = xsrfToken;
                        }
                        
                        return axiosFinal(originalRequest);
                    } catch (refreshError) {
                        // console.error('Error fetching CSRF token:', refreshError);
                        return Promise.reject(refreshError);
                    }
                }
                return Promise.reject(error);
            }
        );
        

        return () => {
            axiosFinal.interceptors.request.eject(requestInterceptor);
            axiosFinal.interceptors.response.eject(responseInterceptor);
        };

    }, []);

    return axiosFinal;
};

export default finalInterceptor;
