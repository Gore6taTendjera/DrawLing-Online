import usingAxiosPrivate from '../api/usingAxiosPrivate';


const profilePictureService = () => {
    const axiosInstance = usingAxiosPrivate();

    
    const getProfilePictureByUserId = async (userId: number): Promise<string> => {
        try {
            const response = await axiosInstance.get<string>(`images/user/${userId}/profile-picture`);
            return response.data;
        } catch (error) {
            // console.error('Error fetching balance:', error);
            throw error;
        }
    };


    const uploadProfilePicture = async (userId: number, file: File): Promise<string> => {
        const formData = new FormData();
        formData.append("file", file);
    
        try {
            const response = await axiosInstance.post(`images/user/${userId}/profile-picture`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            return response.data;
        } catch (error) {
            // console.error('Error uploading profile picture:', error);
            throw new Error('Failed to upload profile picture');
        }
    }

    return { getProfilePictureByUserId, uploadProfilePicture };
}


export default profilePictureService;
