import { useState, useEffect } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

const MyComponent = () => {
    const [jwtToken, setJwtToken] = useState<string | null>(null);
    const [response, setResponse] = useState<string | null>(null);
    const [protectedResponse, setProtectedResponse] = useState<string | null>(null);
    const [image, setImage] = useState<string | null>(null);
    const [profilePicture, setProfilePicture] = useState<string | null>(null);

    useEffect(() => {
        axios.defaults.baseURL = 'http://localhost:8080/api/';
        axios.defaults.withCredentials = true;

        if (jwtToken) {
            console.log('JWT Token is here');
            // axios.defaults.headers.common['Authorization'] = `Bearer ${jwtToken}`;
            // axios.defaults.headers.common.Authorization = `Bearer ${jwtToken}`;
            // axios.defaults.headers.get.Authorization = `Bearer ${jwtToken}`;
            // axios.defaults.headers.post.Authorization = `Bearer ${jwtToken}`;
        } else {
            console.log('JWT Token is not available');
        }
    }, [jwtToken]);


    async function login() {
        axios.post('authentication/login', {
            username: 'xd',
            password: '123',
        }, { withCredentials: true })

            .then(response => {
                setResponse(JSON.stringify(response.data));
                setJwtToken(response.data.jwtToken);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    async function refreshToken() {
        axios.post('authentication/refresh-token')
            .then(response => {
                const newAccessToken = response.data.jwtToken;
                setJwtToken(newAccessToken);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }


    async function logOut() {
        axios.post('authentication/logout')

    }

    async function getProtected() {
        if (!jwtToken) {
            console.error('JWT Token is not available');
            return;
        }
    
        try {
            const headers = new Headers();
            headers.append('Authorization', `Bearer ${jwtToken}`);
    
            const response = await fetch('http://localhost:8080/api/authentication/profile', { credentials: 'include', headers });
    
            // Log the response status
            console.log('Response Status:', response.status);
    
            // Read the response as text
            const responseText = await response.text();
            console.log('Response Text:', responseText);
    
            if (!response.ok) {
                throw new Error(`Failed to get protected data: ${response.status}`);
            }
    
            // Use the response text directly
            setProtectedResponse(JSON.stringify({ timestamp: new Date().toISOString(), message: responseText }));
        } catch (error) {
            console.error('Error:', error);
        }
    }
    


    async function getProtectedNoJwt() {
        if (!jwtToken) {
            console.error('JWT Token is not available');
            return;
        };

        await axios.get('authentication/profile', { headers: { Authorization: `Bearer ${jwtToken}` }, withCredentials: true, }, )
            .then(response => {
                setProtectedResponse(JSON.stringify({ timestamp: new Date().toISOString(), data: response.data }));
            }).catch(error => {
                console.error('Error:', error);
            })

    }


    async function getImage() {
        try {
            const response = await axios.get("http://localhost:8080/api/images/user/1/profile-picture");
            setImage(response.data);
            console.log(response.data);
        } catch (error) {
            console.error('Error fetching image:', error);
        }
    }






    async function getUserProfilePicture() {
        try {
            const jwtDecoded = jwtDecode(jwtToken!);
            const { userId } = jwtDecoded as { userId: number };
            console.log(userId);
            
            const response = await axios.get(`http://localhost:8080/api/images/user/${userId}/profile-picture`);
            setProfilePicture(response.data);
        } catch (error) {
            console.error('Error fetching image:', error);
        }
    }



return (
    <div style={{ color: 'black' }}>
        <br />
        <br />
        <br />
        <br />

        <button onClick={login}>login</button>

        <button onClick={refreshToken}>refreshToken</button>

        <button onClick={logOut}>logOut</button>
        <br />
        <button onClick={getProtected}>protected with jwt</button>


        <button onClick={getProtectedNoJwt}>protected NO JWT</button>

        <button onClick={getImage}>get image</button>

        <button onClick={getUserProfilePicture}>getUserProfilePicture</button>


        <p>Response: {response}</p>
        <p>JWT TOKEN: {jwtToken}</p>
        <p>protected response: {protectedResponse}</p>



        {image && <img src={image} alt="User Drawing" style={{ maxWidth: '100%', height: 'auto' }} />}
        <br />
        <h1>profile picture:</h1>
        {profilePicture && <img src={profilePicture} alt="User Drawing" style={{ maxWidth: '100%', height: 'auto' }} />}
    </div>
);
};

export default MyComponent;

