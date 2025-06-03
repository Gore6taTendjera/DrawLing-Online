import usingAxiosPrivate from '../api/usingAxiosPrivate';

import IExperienceLevelResponse from "../Interface/IExperienceLevelResponse";

const experienceLevelService = () => {
    const axiosInstance = usingAxiosPrivate();

    
    const getExperienceLevelByUserId = async (userId: number): Promise<IExperienceLevelResponse> => {
        try {
            const response = await axiosInstance.get<IExperienceLevelResponse>(`experience-level/${userId}/xp-lvl`);
            return response.data;
        } catch (error) {
            // console.error('Error fetching experience level:', error);
            throw error;
        }
    }

    return { getExperienceLevelByUserId };
}


export default experienceLevelService;
