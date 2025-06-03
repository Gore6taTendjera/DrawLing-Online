import usingAxiosPrivate from '../api/usingAxiosPrivate';

const userService = () => {
    const axiosInstance = usingAxiosPrivate();
    
    const getDisplayNameById = async (userId: number): Promise<string> => {
        try {
            const response = await axiosInstance.get<string>(`users/${userId}/display-name`);
            return response.data;
        } catch (error) {
            // console.error('Error fetching display name:', error);
            throw error;
        }
    };
    

    const updateDisplayName = async (userId: number, displayName: string): Promise<void> => {
        try {
            const response = await axiosInstance.patch(`users/${userId}/display-name`, { displayName });
            console.log('Display name set successfully:', response.data);
        } catch (error) {
            // console.error('Error setting display name:', error);
            throw error;
        }
    };

    
    const getTotalUserCount = async (): Promise<number> => {
        try {
            const response = await axiosInstance.get<number>('users/total');
            return response.data;
        } catch (error) {
            // console.error('Error fetching total count:', error);
            throw error;
        }
    };

    return { getDisplayNameById, updateDisplayName, getTotalUserCount };
}


export default userService;
