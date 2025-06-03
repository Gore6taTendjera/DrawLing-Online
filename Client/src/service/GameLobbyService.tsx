import CreateGameLobbyRequestData from '../Interface/ICreateGameLobbyRequestData';
import usingAxiosPrivate from '../api/usingAxiosPrivate';

const gameLobbyService = () => {
    const axiosInstance = usingAxiosPrivate();
    const createGameLobby = async (request: CreateGameLobbyRequestData): Promise<string> => {
        try {
            const response = await axiosInstance.post('/game-sessions/create-real', request);
            return response.data;
        } catch (error: any) {
            if (error.response && (error.response.status === 401 || error.response.status === 403)) {
                try {
                    const response = await axiosInstance.post('/game-sessions/create-real', request);
                    return response.data;
                } catch (retryError) {
                    throw retryError;
                }
            } else {
                throw error;
            }
        }
    };
    

    return { createGameLobby };
};

export default gameLobbyService;
