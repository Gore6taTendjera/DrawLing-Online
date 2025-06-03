import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from './AuthService';

export default function TestLoginComponent() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const { login } = AuthService();

  const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError('');

    try {
      await login(username, password);
      navigate('/testAdmin');
    } catch (err: any) {
      setError(err.message);
      console.error(err.message);
    }
  };

  return (
    <div>
      <br />
      <br />
      <br />
      <br />
      <br />
      <form onSubmit={handleLogin}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button type="submit">Login</button>
      </form>
      {/* Display the error message if it exists */}
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
};
