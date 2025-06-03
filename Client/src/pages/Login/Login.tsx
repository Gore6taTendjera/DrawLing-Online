import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './Login.module.css';
import AuthService from '../../components/AuthService';


export default function LoginPage() {
    const [isLogin, setIsLogin] = useState(true);
    const [animationClass, setAnimationClass] = useState(styles.fadeIn);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const { register, login } = AuthService();


    const toggleForm = () => {
        setAnimationClass(styles.fadeOut);

        setTimeout(() => {
            setIsLogin(!isLogin);
            setAnimationClass(styles.fadeIn);
        }, 500);
    };

    const handleLogin = async (e: any) => {
        e.preventDefault();
        setError('');
        try {
            await login(username, password);
            navigate('/profile');
        } catch (err: any) {
            setError(err.message);
        }
    };

    const handleRegister = async (e: any) => {
        e.preventDefault();
        setError('');
        try {
            await register(username, password);
            setUsername('');
            setPassword('');
            setError('Registration successful! You can now log in.');
            setTimeout(() => {
                setIsLogin(true);
                setAnimationClass(styles.fadeOut);
                setTimeout(() => {
                    setAnimationClass(styles.fadeIn);
                }, 500);
            }, 2000);
        } catch (err: any) {
            setError(err.message);
        }
    };

    return (
        <div className={styles.loginContainer}>
            <div className={`${styles.loginForm} ${animationClass}`}>
                {isLogin ? (
                    <form onSubmit={handleLogin}>
                        <h2>Login</h2>
                        {error && <p className={styles.error}>{error}</p>}
                        <div className={styles.inputGroup}>
                            <label htmlFor="username">Username</label>
                            <input
                                data-cy="input-username"
                                type="text"
                                id="username"
                                placeholder="Enter your username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                            />
                        </div>
                        <div className={styles.inputGroup}>
                            <label htmlFor="password">Password</label>
                            <input
                                data-cy="input-password"
                                type="password"
                                id="password"
                                placeholder="Enter your password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                        <button data-cy="button-login" type="submit" className={styles.submitButton}>
                            Login
                        </button>
                        <p className={styles.toggleText}>
                            Don't have an account?
                            <button type="button" onClick={toggleForm} className={styles.toggleButton}>
                                Register
                            </button>
                        </p>
                    </form>
                ) : (
                    <form onSubmit={handleRegister}>
                        <h2>Register</h2>
                        {error && <p className={styles.error}>{error}</p>}
                        <div className={styles.inputGroup}>
                            <label htmlFor="username">Username</label>
                            <input
                                type="text"
                                id="username"
                                placeholder="Enter your username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                            />
                        </div>
                        <div className={styles.inputGroup}>
                            <label htmlFor="password">Password</label>
                            <input
                                type="password"
                                id="password"
                                placeholder="Enter your password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                        <button type="submit" className={styles.submitButton}>
                            Register
                        </button>
                        <p className={styles.toggleText}>
                            Already have an account?
                            <button type="button" onClick={toggleForm} className={styles.toggleButton}>
                                Login
                            </button>
                        </p>
                    </form>
                )}
            </div>
        </div>
    );
}
