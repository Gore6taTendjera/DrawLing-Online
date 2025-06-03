import axios from 'axios';
import Cookies from 'js-cookie';

const BASE_URL = 'http://localhost:8080/api/';

export default axios.create({
    baseURL: BASE_URL,
    withCredentials: true, 
    headers: {
        'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN')
    }
});

export const axiosPrivate = axios.create({
    baseURL: BASE_URL,
    withCredentials: true,
    headers: {
        'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN')
    }
});


export const axiosFinal = axios.create({
    baseURL: BASE_URL,
    withCredentials: true
})