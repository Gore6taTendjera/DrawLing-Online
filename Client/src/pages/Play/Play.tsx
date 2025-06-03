import React, { useState, useRef, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import styles from './Play.module.css';
import ChatMessage from '../../components/ChatMessage/ChatMessage';
import IChatMessage from '../../Interface/IChatMessage';
import { useParams } from 'react-router-dom';
// import { Navigate } from 'react-router-dom';
import GameNotification from './GameNotification';
import CanvasData from '../../Interface/ICanvasData';
import IActivePlayer from '../../Interface/IActivePlayer';
import { RoleEnum } from '../../enum/RoleEnum';
import { GameStateEnum } from '../../enum/GameStateEnum';



export default function Play(props: { displayNameProp: string; userId?: number }) {
    // console.log("PROPS: " + props?.displayNameProp);

    const dummyIMG = "https://cdn1.iconfinder.com/data/icons/user-pictures/100/unknown-1024.png";

    const params = useParams();
    const roomId = params.roomId;
    const [displayName, setDisplayName] = useState("");
    const [userLoggedId, setUserLoggedId] = useState(0);
    const [messages, setMessages] = useState<IChatMessage[]>([]);
    const [inputValue, setInputValue] = useState<string>('');
    const canvasRef = useRef<HTMLCanvasElement | null>(null);
    const [isDrawing, setIsDrawing] = useState(false);
    const [lastX, setLastX] = useState(0);
    const [lastY, setLastY] = useState(0);
    const [ctx, setCtx] = useState<CanvasRenderingContext2D | null>(null);
    const [lineColor, setLineColor] = useState<string>('#000000');
    const [lineWidth, setLineWidth] = useState<number>(5);
    const [stompClient, setStompClient] = useState<Client | null>(null);
    const [countdown, setCountdown] = useState("");
    const [connectionStatus, setConnectionStatus] = useState<string>('Disconnected');

    const [isInactive, setIsInactive] = useState(false);
    const inactivityTimeout = useRef<NodeJS.Timeout | null>(null);

    const [sessionId, setSessionId] = useState<string>('');

    const [activePlayers, setActivePlayers] = useState<IActivePlayer[]>([]);

    const [playerRole, setPlayerRole] = useState<RoleEnum | null>(null);

    const [canSendMessage, setCanSendMessage] = useState(false);

    const [roundNumber, setRoundNumber] = useState(0);

    const [guessingWord, setGuessingWord] = useState("");


    const [notifications, setNotifications] = useState<{ id: number; message: string; countdown: number }[]>([]);
    const notificationIdRef = useRef(0);

    const [gameState, setGameState] = useState<GameStateEnum>(GameStateEnum.STANDBY);

    const [theWordWas, setTheWordWas] = useState("");

    // console.log(`displayName: ${displayName}`)
    // console.log('activePlayers: ', activePlayers);

    useEffect(() => {
        if (props?.displayNameProp) {
            setDisplayName(props.displayNameProp);
        }
    }, [props?.displayNameProp]);

    useEffect(() => {
        if (props?.userId) {
            setUserLoggedId(props.userId);
        }
    }, [props?.userId]);


    // const redirectToPage = () => {
    //     return <Navigate to="/lobby" replace />;
    // };

    const client = new Client({
        brokerURL: `http://localhost:8080/ws`,
        webSocketFactory: () => new SockJS(`http://localhost:8080/ws`),
        onConnect: () => {
            console.log('Connected to WebSocket');
            setConnectionStatus('Connected');

            console.log("displayName: " + displayName);
            console.log("connectionStatus: " + connectionStatus);

            if (displayName && connectionStatus === "Connected") {

                client.subscribe(`/user/${displayName}/topic/sendSession`, function (message) {
                    console.log("USER/${USERNAME}/TOPIC/SENDSESSION : " + message.body); // receive sessionId
                    setSessionId(message.body);
                });


                if (userLoggedId) {
                    client.publish({
                        destination: `/app/player/join/${roomId}`, // ask for sessionId logged user
                        body: JSON.stringify({ displayName: displayName, userId: userLoggedId }),
                    });
                } else {
                    client.publish({
                        destination: `/app/player/join/${roomId}`, // ask for sessionId
                        body: JSON.stringify({ displayName: displayName }),
                    });
                }

                // saved
                console.log("id: " + sessionId);

                console.log("subscribed to user{displayName}" + displayName);
                console.log("🚀sending player join");
            }

            // real time
            client.subscribe(`/topic/canvas/receive/draw/${roomId}`, (message) => {
                const updateData = JSON.parse(message.body);
                // console.log(updateData);
                drawOnCanvas(updateData.startX, updateData.startY, updateData.endX, updateData.endY, updateData.color, updateData.lineWidth);
                console.log("DRAWN");
            });





            client.subscribe(`/topic/room/${roomId}/notifications/receive/gameState`, (message) => {
                const state = JSON.parse(message.body);
                console.log("Received game state: ", state);
                setGameState(state);
                // addNotification(`GAME STATE RECEIVED: ${state}`, 300000);
            });



            client.subscribe(`/topic/room/${roomId}/notifications/receive/playerJoined`, (message) => {
                const playerName = message.body;
                addNotification(`${playerName} has joined the game!`, 3000);
            });

            client.subscribe(`/topic/room/${roomId}/notifications/receive/playerLeft`, (message) => {
                const playerName = message.body;
                addNotification(`${playerName} has left the game!`, 3000);
            });

            client.subscribe(`/topic/room/${roomId}/notifications/receive/playerGuessed`, (message) => {
                const playerName = message.body;
                addNotification(`Player ${playerName} has guessed the word!`, 3000);
            });

            client.subscribe(`/topic/room/${roomId}/notifications/receive/wordWas`, (message) => {
                const word = message.body;
                setTheWordWas(word);
                // addNotification(`The word was: ${word}`, 300000);
            });

            client.subscribe(`/topic/room/${roomId}/notifications/receive/gameFinished`, () => {
                addNotification("Game finished!", 3000);
            });




            client.subscribe(`/topic/room/${roomId}/receive/roundNumber`, (message) => {
                const roundNumber = JSON.parse(message.body);
                setRoundNumber(roundNumber);
                console.log('RECEIVED ROUND NUMBER: ', roundNumber);
            });

            client.subscribe(`/topic/chat/receive/room/${roomId}`, (message) => {
                const chatMessage = JSON.parse(message.body);
                setMessages((prevMessages) => [
                    {
                        playerName: chatMessage.playerName,
                        text: chatMessage.text,
                        profilePicture: chatMessage.profilePicture
                    },
                    ...prevMessages
                ]);
                console.log('RECEIVED CHAT MESSAGE: ', chatMessage);

            });






            client.subscribe(`/topic/room/receive/activePlayers/${roomId}`, (message) => {
                const activePlayers = JSON.parse(message.body);
                setActivePlayers(activePlayers);
                console.log('RECEIVED ACTIVE PLAYERS: ', activePlayers);
            })



            // real time
            client.subscribe(`/topic/canvas/receive/clear/${roomId}`, (message) => {
                console.log("CLEARING CANVAS: " + message.body);
                clearCanvas();
            });



            client.subscribe(`/topic/timer/${roomId}`, (message) => {
                const timerData = message.body;
                setCountdown(timerData);
                // console.log('Received timer data: ' + timerData);
            });

        },
        onDisconnect: () => {
            console.log('Disconnected from WebSocket');
            setConnectionStatus('Disconnected');
            // alert('You have been disconnected from the game due to inactivity.');
        },
    });


    useEffect(() => {
        if (gameState === GameStateEnum.ROUND_FINISHED) {
            // addNotification("ROUND FINISHED!", 300000);
            setCanSendMessage(false);
        }

        if (gameState === GameStateEnum.ENDED) {
            // addNotification("GAME ENDED!", 300000);
            setCanSendMessage(false);
        }

        console.log("GAME STATE: " + gameState);
    }, [gameState]);

    const addNotification = (message: string, duration: number = 3000) => {
        const newNotification = { id: notificationIdRef.current++, message, countdown: duration };
        setNotifications((prev) => [...prev, newNotification]);

        const timer = setInterval(() => {
            setNotifications((prev) => {
                const updatedNotifications = prev.map(notification => {
                    if (notification.id === newNotification.id) {
                        return { ...notification, countdown: notification.countdown - 1000 };
                    }
                    return notification;
                });

                return updatedNotifications.filter(notification => notification.countdown > 0);
            });
        }, 1000)

        return () => clearInterval(timer);
    };


    const removeNotification = (id: number) => {
        setNotifications((prev) => prev.filter(notification => notification.id !== id));
    };

    const getActivePlayers = () => {
        stompClient?.publish({
            destination: `/app/room/send/getPlayers/${roomId}`,
        });
        console.log("🎈GET PLAYERS");
    }

    const getPlayerRole = () => {
        stompClient?.publish({
            destination: `/app/room/${roomId}/send/getPlayerRole`
        })
        console.log("🍥GET ROLE");
    }

    const getGuessingWord = () => {
        stompClient?.publish({
            destination: `/app/room/send/word`,
        })
        console.log("🥬GET WORD");
    }

    const sendGuessWord = (guess: string) => {
        stompClient?.publish({
            destination: `/app/room/send/word/guess`,
            body: guess
        })
        console.log("📤SEND GUESSING WORD");
    }

    const getRoundNumber = () => {
        stompClient?.publish({
            destination: `/app/room/send/roundNumber`
        })
    }

    // triggered after sessionId is received >
    useEffect(() => {
        if (sessionId && connectionStatus === "Connected") {

            stompClient?.subscribe(`/user/${sessionId}/topic/canvas/receive/draw`, (message) => {
                const updateData = JSON.parse(message.body);
                // console.log(updateData);
                drawOnCanvas(updateData.startX, updateData.startY, updateData.endX, updateData.endY, updateData.color, updateData.lineWidth);
                console.log("RECEIVED DRAW PRIVATE CANVAS DATA: " + message.body);
            });

            stompClient?.subscribe(`/user/${sessionId}/topic/canvas/receive/saved`, (message) => {
                const updateData = JSON.parse(message.body);
                drawOnCanvas(updateData.startX, updateData.startY, updateData.endX, updateData.endY, updateData.color, updateData.lineWidth);
                console.log('Processed saved data.');
            });
            console.log(`Subscribed to saved data for session ${sessionId}`);

            console.log("sesId: " + sessionId);
            console.log("roomId: " + roomId);
            stompClient?.subscribe(`/user/${sessionId}/topic/room/${roomId}/kicked`, (msg) => {
                stompClient.deactivate();
                console.log(msg.body);
                console.log("KICKED");
                alert("You are kicked from the room due to inactivity.");
                console.log("Disconnected from WebSocket");
                window.location.href = '/';
            });

            stompClient?.subscribe(`/user/${sessionId}/topic/player/role`, (msg) => {
                console.log("ROLE RECEIVED:" + msg.body);
                const role = msg.body === '"DRAWING"' ? RoleEnum.DRAWING : RoleEnum.GUESSING;
                console.log("ROLE: " + role);
                setPlayerRole(role);

                console.log("SET ROLE: " + playerRole?.toString());
                console.log("SET ROLE: " + playerRole);
            })

            stompClient?.subscribe(`/user/${sessionId}/topic/room/receive/word`, (message) => {
                console.log("😎😍WORD RECEIVED: " + message.body);
                setGuessingWord(message.body);
            })



            stompClient?.subscribe(`/user/${sessionId}/topic/room/receive/word/guess`, (message) => {
                console.log("GUESSING WORD RECEIVED: " + message.body);
                if (message.body === 'true') {
                    setCanSendMessage(false);
                    console.log("WORD IS Correct!");
                }
            })

            stompClient?.subscribe(`/user/${sessionId}/topic/room/receive/roundNumber`, (message) => {
                console.log("ROUND NUMBER RECEIVED: " + message.body);
                setRoundNumber(parseInt(message.body));
            })


            stompClient?.subscribe(`/user/${sessionId}/topic/canvas/receive/saved`, (message) => {
                console.log("ROUND NUMBER RECEIVED: " + message.body);
                setRoundNumber(parseInt(message.body));
            })

            stompClient?.publish({
                destination: `/app/canvas/send/saved`,
            });
            console.log('Published request for saved canvas data');


            getActivePlayers();
            getPlayerRole();
            getGuessingWord();
            getRoundNumber();

        }
    }, [sessionId, connectionStatus]);

    useEffect(() => {
        setCanSendMessage(playerRole === RoleEnum.GUESSING);

    }, [playerRole]);


    useEffect(() => {
        if (roundNumber >= 1 && guessingWord !== "") {
            getGuessingWord();
        }

    }, []);

    useEffect(() => {
        if (connectionStatus === "Connected") {
            const heartbeatInterval = setInterval(() => {
                if (!isInactive && client && client.connected) {
                    client.publish({
                        destination: `/app/player/heartbeat/${roomId}`,
                    });
                    console.log("Heartbeat sent to the server");
                } else if (isInactive) {
                    console.log('Skipping heartbeat due to inactivity.');
                }
            }, 5000);

            return () => {
                clearInterval(heartbeatInterval);
            };
        }
    }, [connectionStatus, ctx, isInactive]);


    const resetInactivityTimeout = () => {
        if (inactivityTimeout.current) {
            clearTimeout(inactivityTimeout.current);
        }
        inactivityTimeout.current = setTimeout(() => {
            setIsInactive(true);
            console.log('Player inactive.');
        }, 20000000000000);
        setIsInactive(false);
    };


    useEffect(() => {
        const handleMouseMove = () => {
            resetInactivityTimeout();
        };

        window.addEventListener('mousemove', handleMouseMove);
        return () => {
            window.removeEventListener('mousemove', handleMouseMove);
            if (inactivityTimeout.current) {
                clearTimeout(inactivityTimeout.current);
            }
        };
    }, []);

    useEffect(() => {
        client.activate();
        setConnectionStatus('Connected');
        setStompClient(client);

        return () => {
            client.deactivate();
        };

    }, [ctx]);


    const handleMouseMoveGlobal = (event: MouseEvent) => {
        if (gameState === GameStateEnum.ENDED || gameState === GameStateEnum.ROUND_FINISHED || playerRole === RoleEnum.GUESSING) return;
        if (!isDrawing || !ctx) return;

        const rect = canvasRef.current?.getBoundingClientRect();
        const x = event.clientX - (rect?.left || 0);
        const y = event.clientY - (rect?.top || 0);

        ctx.strokeStyle = lineColor;
        ctx.lineWidth = lineWidth;
        ctx.beginPath();
        ctx.moveTo(lastX, lastY);
        ctx.lineTo(x, y);
        ctx.stroke();

        const canvasData: CanvasData = {
            startX: lastX,
            startY: lastY,
            endX: x,
            endY: y,
            color: lineColor,
            lineWidth: lineWidth,
        };

        if (stompClient && stompClient.connected) {
            setConnectionStatus('Connected');
            try {
                stompClient.publish({
                    destination: `/app/canvas/send/draw`,
                    body: JSON.stringify(canvasData),
                });
                console.log('sent canvas draw to server');
            } catch (error) {
                // console.error('Failed to publish message to the server', error);
            }
        } else {
            // console.error('Stomp client is not connected');
            setConnectionStatus('Disconnected');
        }

        setLastX(x);
        setLastY(y);
        console.log("sessionId: " + sessionId);
    };

    const handleMouseUpGlobal = () => {
        setIsDrawing(false);
    };

    useEffect(() => {
        const canvas = canvasRef.current;
        if (canvas) {
            const context = canvas.getContext('2d');
            if (context) {
                context.lineJoin = 'round';
                context.lineCap = 'round';
                context.lineWidth = 5;
                setCtx(context);
                // console.log("CONTEXT SET");
            } else {
                // console.error('Failed to get 2D context for the canvas');
            }
        } else {
            // console.error('Canvas is not available');
        }



        window.addEventListener('mousemove', handleMouseMoveGlobal);
        window.addEventListener('mouseup', handleMouseUpGlobal);

        return () => {
            window.removeEventListener('mousemove', handleMouseMoveGlobal);
            window.removeEventListener('mouseup', handleMouseUpGlobal);
        };
    }, [isDrawing, ctx, lastX, lastY, lineColor]);


    const sendChatMessage = (msg: string) => {
        if (stompClient && stompClient.connected) {
            stompClient.publish({
                destination: `/app/chat/send/${roomId}`,
                body: msg
            })
            // console.log("sent chat message to server" + msg);

        }
    }

    const drawOnCanvas = (startX: number, startY: number, endX: number, endY: number, color: string, lineWidth: number) => {
        const canvas = canvasRef.current;
        if (canvas) {
            const context = canvas.getContext('2d');
            if (context) {
                context.strokeStyle = color;
                context.lineWidth = lineWidth;
                context.beginPath();
                context.moveTo(startX, startY);
                context.lineTo(endX, endY);
                context.stroke();
                // console.log("DRAWINGGGGGG ON CANVAS");
            }
        }
    };

    const handleMouseDown = (event: React.MouseEvent<HTMLCanvasElement>) => {
        if (ctx) {
            setIsDrawing(true);
            const rect = canvasRef.current?.getBoundingClientRect();
            setLastX(event.clientX - (rect?.left || 0));
            setLastY(event.clientY - (rect?.top || 0));
        }
    };

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setInputValue(event.target.value);
    };

    const handleColorChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setLineColor(event.target.value);
    };

    const handleButtonClick = () => {
        if (inputValue.trim() === '') return;
        sendChatMessage(inputValue);
        if (playerRole === RoleEnum.GUESSING) {
            sendGuessWord(inputValue);
        }
        setInputValue('');
    };

    const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            handleButtonClick();
        }
    };


    const handleClearCanvas = () => {
        const canvas = canvasRef.current;
        if (canvas && ctx) {
            if (stompClient && stompClient.connected) {
                stompClient.publish({
                    destination: `/app/canvas/send/clear/${roomId}`,
                });
                console.log('Clear canvas message sent');
                ctx.clearRect(0, 0, canvas.width, canvas.height);
            } else {
                // console.error('STOMP client is not connected. Cannot send clear canvas message.');
            }
        }
    }

    const clearCanvas = () => {
        const canvas = canvasRef.current;
        if (canvas && ctx) {
            console.log("Clearing canvas...");
            ctx.clearRect(0, 0, canvas.width, canvas.height);
        } else {
            // console.error('Canvas or context is not available');
        }
    };


    const handleSaveImage = () => {
        try {
            if (stompClient && stompClient.connected) {
                stompClient.publish({
                    destination: `/app/canvas/send/saveImage`,
                })
            }

        } catch (error) {
            // console.error('Error saving image:', error);
        }
    }

    const handleLineWidthChange = (width: number) => {
        setLineWidth(width);
        console.log(`Line width set to: ${width}`);
    };

    return (
        <>
            <section id={styles.section1}>
                <div id={styles.canvas}>
                    <div className='row' style={{ justifyContent: 'space-between' }}>
                        <h1>Your name: {displayName}</h1>
                        {/* <span className={`${styles.guessWord} font-xxlarge white text-center`}>
                            {guessingWord}
                            <span className='font-medium' style={{ verticalAlign: 'super', marginLeft: '5px' }}>
                                {guessingWord?.replace(/\s/g, '')?.length || ''}
                            </span>
                        </span> */}
                        <div className='column'>
                            <span className='text-right'>Round: {roundNumber}</span>
                            <span className={`${styles.timer} font-xxlarge white text-right`}>{countdown}</span>
                        </div>
                    </div>

                    <div className={`${styles.row} row`}>
                        <div className={styles.playerCards}>
                            {activePlayers.map((player) => (
                                <div key={player.playerName} className={`${styles.playerCard} ${player.playerName === displayName ? styles.personalCard : ''}`}>
                                    <div className={`${styles.playerInfo} row`}>
                                        <img
                                            className={styles.playerImage}
                                            src={player.profilePicture ? player.profilePicture : dummyIMG}
                                            alt="Player"
                                        />
                                        <div className='column'>
                                            <span className={`${styles.playerName} font-large bold`}>
                                                {player.playerName.length > 7 ? `${player.playerName.slice(0, 7)}...` : player.playerName}
                                            </span>

                                            <span className={`${styles.playerBalance} font-medium`}>
                                                ${player.balance}
                                            </span>
                                        </div>
                                    </div>
                                    {player.role === "DRAWING" && (
                                        <img
                                            src="/pen.gif"
                                            alt="Pencil Icon"
                                            className={styles.pencilIcon}
                                        />
                                    )}
                                </div>
                            ))}
                        </div>

                        <div id={styles.palette} style={{ display: playerRole === RoleEnum.DRAWING ? 'flex' : 'none' }}>
                            <svg
                                viewBox="0 0 512 512"
                                onClick={() => {
                                    const colorPicker = document.getElementById('colorPicker');
                                    if (colorPicker) {
                                        colorPicker.click();
                                    }
                                }}
                                xmlns="http://www.w3.org/2000/svg"
                                width="24"
                                height="24"
                                className={styles.icon_palette}
                            >
                                <path d="m204.3 5c-99.4 19.4-179.5 99.3-199.1 198.4-37 187 131.7 326.4 258.8 306.7 41.2-6.4 61.4-54.6 42.5-91.7-23.1-45.4 9.9-98.4 60.9-98.4h79.7c35.8 0 64.8-29.6 64.9-65.3-.5-157.6-143.9-281.6-307.7-249.7zm-108.3 315c-17.7 0-32-14.3-32-32s14.3-32 32-32 32 14.3 32 32-14.3 32-32 32zm32-128c-17.7 0-32-14.3-32-32s14.3-32 32-32 32 14.3 32 32-14.3 32-32 32zm128-64c-17.7 0-32-14.3-32-32s14.3-32 32-32 32 14.3 32 32-14.3 32-32 32zm128 64c-17.7 0-32-14.3-32-32s14.3-32 32-32 32 14.3 32 32-14.3 32-32 32z" fill={lineColor} />
                            </svg>
                            <input
                                type="color"
                                id="colorPicker"
                                value={lineColor}
                                onChange={handleColorChange}
                                style={{ display: 'none' }}
                                disabled={gameState === GameStateEnum.ENDED || gameState === GameStateEnum.ROUND_FINISHED || playerRole !== RoleEnum.DRAWING}
                            />





                            <button
                                disabled={gameState === GameStateEnum.ENDED || gameState === GameStateEnum.ROUND_FINISHED || playerRole !== RoleEnum.DRAWING}
                                onClick={handleClearCanvas}
                                style={{ display: 'flex', alignItems: 'center', background: 'none', border: 'none', cursor: gameState === GameStateEnum.ENDED || gameState === GameStateEnum.ROUND_FINISHED || playerRole !== RoleEnum.DRAWING ? 'not-allowed' : 'pointer' }}
                            >
                                <svg
                                    height="40"
                                    width="40"
                                    viewBox="0 0 20 20"
                                    xmlns="http://www.w3.org/2000/svg"
                                    style={{ pointerEvents: 'none' }}
                                >
                                    <path d="m15.5 2h-3.5v-.5c0-.827-.673-1.5-1.5-1.5h-2c-.827 0-1.5.673-1.5 1.5v.5h-3.5c-.827 0-1.5.673-1.5 1.5v1c0 .652.418 1.208 1 1.414v12.586c0 .827.673 1.5 1.5 1.5h10c.827 0 1.5-.673 1.5-1.5v-12.586c.582-.206 1-.762 1-1.414v-1c0-.827-.673-1.5-1.5-1.5zm-7.5-.5c0-.276.224-.5.5-.5h2c.276 0 .5.224.5.5v.5h-3zm6.5 17.5h-10c-.276 0-.5-.224-.5-.5v-12.5h11v12.5c0 .276-.224.5-.5.5zm1.5-14.5c0 .276-.224.5-.5.5h-12c-.276 0-.5-.224-.5-.5v-1c0-.276.224-.5.5-.5h12c.276 0 .5.224.5.5z" fill='white' />
                                    <path d="m12.5 7c-.276 0-.5.224-.5.5v10c0 .276.224.5.5.5s.5-.224.5-.5v-10c0-.276-.224-.5-.5-.5z" fill='white' />
                                    <path d="m9.5 7c-.276 0-.5.224-.5.5v10c0 .276.224.5.5.5s.5-.224.5-.5v-10c0-.276-.224-.5-.5-.5z" fill='white' />
                                    <path d="m6.5 7c-.276 0-.5.224-.5.5v10c0 .276.224.5.5.5s.5-.224.5-.5v-10c0-.276-.224-.5-.5-.5z" fill='white' />
                                </svg>
                            </button>





                            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                                {[1, 2, 3, 4, 5].map((width) => (
                                    <button
                                        key={width}
                                        onClick={() => handleLineWidthChange(width)}
                                        style={{
                                            padding: '10px',
                                            backgroundColor: lineWidth === width ? 'lightblue' : 'white',
                                            border: '1px solid black',
                                            cursor: 'pointer',
                                            display: 'flex',
                                            justifyContent: 'center',
                                            alignItems: 'center',
                                            width: '40px',
                                            height: '40px',
                                            borderRadius: '50%',
                                        }}
                                    >
                                        <div
                                            style={{
                                                width: `${width * 3}px`,
                                                height: `${width * 3}px`,
                                                borderRadius: '50%',
                                                backgroundColor: lineColor,
                                            }}
                                        />
                                    </button>
                                ))}
                            </div>
                        </div>

                        <div style={{ position: 'relative' }} className='column'>
                            <span className={`${styles.guessWord} font-xxlarge white text-center`}>{guessingWord}
                                <span className={`font-medium text-center`} style={{ verticalAlign: 'super', marginLeft: '5px' }}>
                                    {guessingWord?.replace(/\s/g, '')?.length || ''}
                                </span>
                            </span>

                            <canvas
                                ref={canvasRef}
                                width={720}
                                height={700}
                                onMouseDown={handleMouseDown}
                            ></canvas>
                        </div>

                        {/* <span className={`${styles.guessWord} font-xxlarge white text-center`}>
                            {guessingWord}
                            <span className='font-medium' style={{ verticalAlign: 'super', marginLeft: '5px' }}>
                                {guessingWord?.replace(/\s/g, '')?.length || ''}
                            </span>
                        </span> */}

                        <div id={styles.chat}>
                            <div className={`${styles.chatMessages} column`}>
                                {messages.map((msg, index) => (
                                    <ChatMessage
                                        key={index}
                                        playerName={msg.playerName}
                                        profilePicture={msg.profilePicture || dummyIMG}
                                        text={msg.text}
                                    />
                                ))}
                            </div>
                            <div id={styles.inputBox} className={`row ${!canSendMessage ? styles.disabled : ''}`}>
                                <input
                                    type="text"
                                    value={inputValue}
                                    onChange={handleInputChange}
                                    onKeyDown={handleKeyDown}
                                    disabled={!canSendMessage}
                                />
                                <span className={styles.letterCounter}>
                                    {inputValue.length === 0 ? '' : inputValue.length}
                                </span>
                                <button
                                    onClick={handleButtonClick}
                                    disabled={!canSendMessage}
                                    className={!canSendMessage ? styles.disabled : ''}
                                >
                                    <svg fill="none" height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg">
                                        <g stroke="#fff" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5">
                                            <path d="m7.39999 6.32003 8.49001-2.83c3.81-1.27 5.88.81 4.62 4.62l-2.83 8.48997c-1.9 5.71-5.02 5.71-6.92 0l-.84001-2.52-2.52-.84c-5.71-1.9-5.71-5.00997 0-6.91997z" />
                                            <path d="m10.11 13.6501 3.58-3.59" />
                                        </g>
                                    </svg>
                                </button>

                            </div>
                        </div>
                    </div>
                    {userLoggedId !== 0 && (
                        <div>
                            <button className={styles.buttonDrawingSave} onClick={handleSaveImage}>save drawing</button>
                        </div>

                    )}
                    <div style={{ color: connectionStatus === 'Connected' ? 'green' : 'red' }}>
                        Status: {connectionStatus}
                    </div>
                </div>


                <div className={styles.notificationContainer}>
                    {notifications.map(notification => (
                        <GameNotification
                            key={notification.id}
                            message={notification.message}
                            onClose={() => removeNotification(notification.id)}
                        />
                    ))}
                </div>

                {gameState === GameStateEnum.ENDED && (
                    <div className={styles.modal}>
                        <button className={styles.closeButton}>
                            &times;
                        </button>
                        <div style={{ fontSize: '2rem' }}>
                            Game Finished.
                        </div>
                    </div>
                )}

                {gameState === GameStateEnum.ROUND_FINISHED && (
                    <div className={styles.modal}>
                        <button className={styles.closeButton}>
                            &times;
                        </button>
                        <div style={{ fontSize: '2rem' }}>
                            Round finished the word is: {theWordWas}
                        </div>
                    </div>
                )}
            </section>
        </>
    );
}    