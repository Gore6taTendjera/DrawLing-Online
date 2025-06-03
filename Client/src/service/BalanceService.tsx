import usingAxiosPrivate from '../api/usingAxiosPrivate';

const balanceService = () => {
    const axiosInstance = usingAxiosPrivate();


    const getBalanceByUserId = async (userId: number): Promise<number> => {
        try {
            const response = await axiosInstance.get<number>(`balances/${userId}`);
            return response.data;
        } catch (error) {
            // console.error('Error fetching balance:', error);
            throw error;
        }
    };

    return { getBalanceByUserId };
}


export default balanceService;
