import useAuth from './useAuth';
import axios from "../api/axios";

let isRefreshing = false;
let subscribers: ((token: string | null) => void)[] = [];

const onRefreshed = (token: string | null) => {
    subscribers.forEach(callback => callback(token));
    subscribers = [];
};

export const useRefreshToken = () => {
    const { setAuth } = useAuth();

    const refreshToken = async () => {
        if (isRefreshing) {
            return new Promise<string | null>((resolve) => {
                subscribers.push(resolve);
            });
        }

        isRefreshing = true;

        try {
            const response = await axios.post('/authentication/refresh-token');
            const newAccessToken = response.data.jwtToken;

            setAuth({ accessToken: newAccessToken });
            onRefreshed(newAccessToken);
            return newAccessToken;
        } catch (error) {
            onRefreshed(null);
            
            // console.error("REFRESH TOKEN: JWT token is missing: ", error);
            throw error;
        } finally {
            isRefreshing = false;
        }
    };

    return refreshToken;
};
