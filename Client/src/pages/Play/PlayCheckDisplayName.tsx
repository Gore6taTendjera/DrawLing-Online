import { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import useAuth from '../../hooks/useAuth';
import { useRefreshToken } from '../../hooks/useRefreshToken';
import Play from "./Play";
import { Link, useNavigate } from "react-router-dom";
import userService from "../../service/UserService";
import axios from 'axios';
import styles from "./PlayCheckDisplayName.module.css";

interface IPlayCheckDisplayName {
    roomId: string | undefined;
}

export default function PlayCheckDisplayName({ roomId }: IPlayCheckDisplayName) {
    const { auth } = useAuth();
    const jwtDecoded = auth.accessToken ? jwtDecode(auth.accessToken) : null;
    const sub = jwtDecoded ? (jwtDecoded as { sub?: string }).sub : undefined;
    const userId = jwtDecoded ? (jwtDecoded as { userId?: number }).userId : undefined;

    const [inputValue, setInputValue] = useState<string>('');
    const [displayName, setDisplayName] = useState<string>('');
    const [isUserIdUsed, setIsUserIdUsed] = useState<boolean>(false);
    const [alreadyJoined, setAlreadyJoined] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [alertShown, setAlertShown] = useState<boolean>(false);
    const navigate = useNavigate();

    const getRefreshToken = useRefreshToken();
    const { getDisplayNameById } = userService();

    useEffect(() => {
        if (!sub) {
            (async () => {
                await getRefreshToken();
            })();
        }
    }, []);

    const checkIfUserIdExistsInRoom = async () => {
        try {
            await axios.get(`http://localhost:8080/api/rooms/${roomId}/users/${userId}`);
            setAlreadyJoined(false);
        } catch {
            setAlreadyJoined(true);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (userId) {
            setIsUserIdUsed(true);
            checkIfUserIdExistsInRoom();
        }
    }, [auth.accessToken]);

    useEffect(() => {
        if (isUserIdUsed && !alreadyJoined && !isLoading) {
            getDisplayNameById2();
        } else if (alreadyJoined && !alertShown) {
            setAlertShown(true);
            alert('You can\'t join 2 times with this account');
            navigate('/lobby', { replace: true });
        }
    }, [isUserIdUsed, alreadyJoined, isLoading, alertShown]);

    const getDisplayNameById2 = async () => {
        let name = '';
        try {
            if (userId !== undefined) {
                name = await getDisplayNameById(userId);
            } else {
                throw new Error('User ID is undefined');
            }
        } catch (error) {
            name = sub!;
        } finally {
            checkGetDisplayNameIfTaken(name);
        }
    };

    const updateDisplayName = async () => {
        const trimmedInputValue = inputValue.trim();
        if (trimmedInputValue && trimmedInputValue !== displayName) {
            await checkGetDisplayNameIfTaken(trimmedInputValue);
            setInputValue('');
        } else {
            // console.error('Display name is taken');
        }
    };

    const checkGetDisplayNameIfTaken = async (trimmedInputValue: string) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/room/${roomId}/check-display-name`, {
                params: { displayName: trimmedInputValue },
            });
            setDisplayName(response.data);
        } catch (error) {
            // console.error('Error checking display name:', error);
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            updateDisplayName();
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setInputValue(e.target.value.trim());
    };

    if (displayName) {
        return <Play displayNameProp={displayName} userId={isUserIdUsed ? userId : undefined} />;
    }

    return (
        <div>
            <div className={styles.noDisplayName}>
                <div className={styles.glassmorphismContainer}>
                    <div className={`${styles.roomContainer} row`}>
                        <h2 className='font-xxlarge'>Room ID: </h2>
                        <span className='text-center font-xlarge'>{roomId}</span>
                    </div>
                    <label className={styles.displayNameLabel}>
                        Set your display name:
                        <input
                            type="text"
                            value={inputValue}
                            onChange={handleChange}
                            required
                            className={styles.displayNameInput}
                            onKeyDown={handleKeyPress}
                        />
                    </label>
                    <button
                        className={styles.setDisplayNameButton}
                        onClick={updateDisplayName}
                    >
                        Set Display Name
                    </button>
                    <p className={styles.infoText}>
                        Lost your score? <br />
                        Have an account? Login from <Link to="/login" className={styles.link}>here</Link>.
                    </p>
                </div>
            </div>
        </div>
    );
}