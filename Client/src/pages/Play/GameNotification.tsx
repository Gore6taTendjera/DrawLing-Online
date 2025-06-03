import React, { useEffect } from 'react';
import styles from './GameNotification.module.css';

interface GameNotificationProps {
    message: string;
    duration?: number;
    onClose: () => void;
}

const GameNotification: React.FC<GameNotificationProps> = ({ message, duration = 3000, onClose }) => {
    useEffect(() => {
        const timer = setTimeout(() => {
            onClose();
        }, duration);

        return () => clearTimeout(timer);
    }, [duration, onClose]);

    return (
        <div className={styles.notification}>
            {message}
            <button onClick={onClose} className={styles.closeButton}>X</button>
        </div>
    );
};

export default GameNotification;
