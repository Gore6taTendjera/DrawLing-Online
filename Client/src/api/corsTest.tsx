import React, { useState } from 'react';
import AuthService from '../components/AuthService';

const CorsTest: React.FC = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const { login } = AuthService();

    const handleLogin = async () => {
        try {
            // Step 1: Get CSRF token

            // const csrf = await getCSRFToken();
            // console.log(csrf);


            // Step 2: Login with CSRF token
            // await inter.post('http://localhost:8080/api/authentication/login', { username, password }, {withCredentials: true});

            const jwtToken = await login(username, password)
            console.log("JWT CORS: " + jwtToken.data.jwtToken);
            

            // Handle successful login
            setSuccess('Login successful!');
            setError('');
        } catch (err) {
            // Handle error
            setError('Login failed. Please check your credentials.');
            setSuccess('');
            console.error(err);
        }
    };

    return (
        <div style={{ marginTop: '100px' }}>
            <h2>Login</h2>
            <div>
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>
            <div>
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            <button onClick={handleLogin}>Login</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {success && <p style={{ color: 'green' }}>{success}</p>}
        </div>
    );
};

export default CorsTest;

