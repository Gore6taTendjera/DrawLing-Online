import { useEffect, useState, useRef } from 'react';
import styles from './lobby.module.css';
import gameLobbyService from '../../service/GameLobbyService';
import { GameMode } from '../../enum/GameModeEnum';
import { GameRoundEnum } from '../../enum/GameRoundEnum';
import { GameWordCategoryEnum } from '../../enum/GameWordCategoryEnum';
import CreateGameLobbyRequestData from '../../Interface/ICreateGameLobbyRequestData';

const gameModeNames: Record<GameMode, string> = {
    [GameMode.NORMAL]: 'Normal',
    [GameMode.DUO]: 'Duo',
    [GameMode.TRIO]: 'Trio',
    [GameMode.COMBINED]: 'Combined'
};

const roundTypeNames: Record<GameRoundEnum, string> = {
    [GameRoundEnum.FAST]: 'Fast Round',
    [GameRoundEnum.NORMAL]: 'Normal Round'
};

const Lobby: React.FC = () => {
    const [gameMode, setGameMode] = useState<GameMode | null>(null);
    const [maxPlayers, setMaxPlayers] = useState<number>(5);
    const [roundTime, setRoundTime] = useState<number>(15);
    const [rounds, setRounds] = useState<number>(5);
    const [roundsData, setRoundsData] = useState<{ roundNumber: number; roundName: GameRoundEnum; wordCategory: GameWordCategoryEnum | null }[]>([]);
    const [roomId, setRoomId] = useState<string | null>(null);
    const [showNotification, setShowNotification] = useState<boolean>(false);
    const [clipboard, setClipboard] = useState<boolean>(false);
    const timeoutRef = useRef<NodeJS.Timeout | null>(null);

    const gameModes = Object.values(GameMode);

    const { createGameLobby } = gameLobbyService();

    useEffect(() => {
        setRoundsData(Array.from({ length: rounds }, (_, index) => ({
            roundNumber: index + 1,
            roundName: GameRoundEnum.NORMAL,
            wordCategory: null
        })));
    }, [rounds]);

    const handleGameModeClick = (mode: GameMode) => setGameMode(mode);

    const handleInputChange = (setter: React.Dispatch<React.SetStateAction<number>>, min: number, max: number) => (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = Math.max(min, Math.min(parseInt(e.target.value), max));
        setter(value);
    };

    const handleRoundTypeChange = (roundNumber: number, roundType: GameRoundEnum) => {
        setRoundsData(prevRounds => prevRounds.map(round =>
            round.roundNumber === roundNumber ? { ...round, roundName: roundType } : round
        ));
    };

    const handleWordCategoryChange = (roundNumber: number, category: GameWordCategoryEnum | null) => {
        setRoundsData(prevRounds => prevRounds.map(round =>
            round.roundNumber === roundNumber ? { ...round, wordCategory: category } : round
        ));
    };

    const handleSubmit = async () => {
        const lobbyData: CreateGameLobbyRequestData = {
            maxPlayers,
            gameModeName: gameMode as GameMode,
            gameRounds: roundsData.map(round => ({
                gameRoundEnum: round.roundName,
                duration: roundTime,
                wordCategory: round.wordCategory
            }))
        };

        try {
            const response = await createGameLobby(lobbyData);
            if (response) {
                setRoomId(response);
                setShowNotification(true);
            }
        } catch (error: any) {
            alert('You are not authorized to create a game lobby. Please log in.');
        }
    };

    const handleRedirect = () => {
        if (roomId) {
            window.location.href = `http://localhost:${window.location.port}/play/${roomId}`;
        }
    };


    useEffect(() => {
    }, [showNotification]);

    useEffect(() => {
        if (clipboard) {
            timeoutRef.current = setTimeout(() => setClipboard(false), 5000);
        }
        return () => {
            if (timeoutRef.current) {
                clearTimeout(timeoutRef.current);
            }
        };
    }, [clipboard]);

    return (
        <div className={styles.section1}>
            <h1 className={styles.title}>Create Game Lobby</h1>
            <div className={styles.form} onSubmit={handleSubmit}>

                <div className={styles.inputGroup}>
                    <label htmlFor="maxPlayers">Max Players</label>
                    <input
                        data-cy="input-maxPlayers"
                        type="number"
                        id="maxPlayers"
                        min="2"
                        max="15"
                        value={maxPlayers}
                        onChange={handleInputChange(setMaxPlayers, 2, 15)}
                    />
                </div>

                <div className={styles.inputGroup}>
                    <label htmlFor="roundTime">Round Time (seconds)</label>
                    <input
                        type="number"
                        id="roundTime"
                        min="30"
                        max="300"
                        value={roundTime}
                        onChange={handleInputChange(setRoundTime, 30, 300)}
                    />
                </div>

                <div className={styles.inputGroup}>
                    <label htmlFor="rounds">Number of Rounds</label>
                    <input
                        type="number"
                        id="rounds"
                        min="2"
                        max="10"
                        value={rounds}
                        onChange={handleInputChange(setRounds, 2, 10)}
                    />
                </div>

                <h2 className={styles.subtitle}>Select Game Mode</h2>
                <div className={styles.cardContainer}>
                    {gameModes.map((mode) => (
                        <div
                            data-cy={`gamemode-${mode}`}
                            key={mode}
                            className={`${styles.card} ${gameMode === mode ? styles.selected : ''}`}
                            onClick={() => handleGameModeClick(mode)}
                        >
                            <h3>{gameModeNames[mode]}</h3>
                        </div>
                    ))}
                </div>

                <h2 className={styles.subtitle}>Select Round Types and Categories</h2>
                {roundsData.map((round) => (
                    <div key={round.roundNumber} className={styles.inputGroup}>
                        <label htmlFor={`round-${round.roundNumber}`}>Round {round.roundNumber}</label>
                        <select
                            id={`round-${round.roundNumber}`}
                            onChange={(e) => handleRoundTypeChange(round.roundNumber, e.target.value as GameRoundEnum)}
                            value={round.roundName}
                        >
                            <option value={GameRoundEnum.NORMAL}>{roundTypeNames[GameRoundEnum.NORMAL]}</option>
                            <option value={GameRoundEnum.FAST}>{roundTypeNames[GameRoundEnum.FAST]}</option>
                        </select>

                        <label htmlFor={`wordCategory-${round.roundNumber}`}>Word Category</label>
                        <select
                            id={`wordCategory-${round.roundNumber}`}
                            onChange={(e) => handleWordCategoryChange(round.roundNumber, e.target.value as GameWordCategoryEnum)}
                            value={round.wordCategory || ''}
                        >
                            <option value="">Select Category</option>
                            {Object.values(GameWordCategoryEnum).map((category) => (
                                <option key={category} value={category}>
                                    {category}
                                </option>
                            ))}
                        </select>
                    </div>
                ))}

                <button
                    data-cy="button-create-lobby"
                    type="button"
                    className={styles.submitButton}
                    onClick={handleSubmit}
                    disabled={gameMode === null}
                >
                    Create Lobby
                </button>
            </div>
            {showNotification && roomId && (
                <div className={styles.notificationBackground}>
                    <div className={styles.notification}>
                        <button
                            onClick={() => setShowNotification(false)}
                            className={styles.closeButton}
                            aria-label="Close Notification"
                        >
                            &times;
                        </button>
                        <p className="mtop20">Room created successfully! Your room ID is: <strong>{roomId}</strong></p>
                        <p>
                            <span
                                className={styles.copyLink}
                                onClick={() => {
                                    const url = `http://localhost:${window.location.port}/play/${roomId}`;
                                    navigator.clipboard.writeText(url).then(() => {
                                        setClipboard(true);
                                        if (timeoutRef.current) {
                                            clearTimeout(timeoutRef.current);
                                        }
                                        timeoutRef.current = setTimeout(() => setClipboard(false), 5000);
                                    });
                                }}
                            >
                                {`http://localhost:${window.location.port}/play/${roomId}`}
                            </span>
                        </p>
                        <p>{clipboard ? 'Copied to clipboard!' : ''}</p>
                        <button data-cy="button-go-to-room" onClick={handleRedirect} className={`${styles.redirectButton} mtop10`}>
                            Go to Room
                        </button>
                    </div>
                </div>
            )}


        </div>

    );
};

export default Lobby;
