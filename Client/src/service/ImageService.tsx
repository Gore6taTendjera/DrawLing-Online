import usingAxiosPrivate from '../api/usingAxiosPrivate';

const savedImagesService = () => {
    const axiosInstance = usingAxiosPrivate();

    const getSavedImagesByUserId = async (userId: number): Promise<string[]> => {
        try {
            const response = await axiosInstance.get<string[]>(`images/user/${userId}/saved-drawings`);
            return response.data;
        } catch (error: any) {
            if (error.response && error.response.status === 404) {
                return [];
            }
            throw new Error('Failed to load images');
        }
    }

    return { getSavedImagesByUserId };
}


export default savedImagesService;
