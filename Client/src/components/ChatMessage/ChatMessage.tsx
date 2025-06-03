import styles from './ChatMessage.module.css';
import IChatMessage from '../../Interface/IChatMessage';



export default function ChatMessage({ playerName, profilePicture, text }: IChatMessage) {
    return (
        <div className={`${styles.chatMessage} row`}>
            <img src={profilePicture} alt="Profile Picture" />
            <div className={`${styles.message} column`}>
                <span className='bold'>{playerName}</span>
                <p>{text}</p>
            </div>
        </div>
    );
}