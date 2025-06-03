import axios from 'axios';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import PlayCheckRoomFull from './PlayCheckRoomFull';

const PlayCheck = () => {
    const params = useParams();
    const roomId = params.roomId;
    const [roomNotFound, setRoomNotFound] = useState(false);
    const [checking, setChecking] = useState(true);

    const checkRoomStatus = async () => {
        console.log("Checking room status...");

        try {
            const response = await axios.get(`http://localhost:8080/api/room/full/${roomId}`);
            console.log("RESP: ", response);

            if (response.status === 404) {
                console.log('Room not found');
                setRoomNotFound(true);
            } else if (response.status !== 200) {
                console.log("Not 200 status");
            }
        } catch (error : any) {
            if (error.response) {
                console.log("Error status: ", error.response.status);
                if (error.response.status === 404) {
                    console.log('Room not found');
                    setRoomNotFound(true);
                } else {
                    console.log("Not 200 status");
                }
            } else {
                // console.error("Error: ", error.message);
            }
        } finally {
            setChecking(false);
        }
    };

    useEffect(() => {
        console.log("Checking room status...");
        checkRoomStatus();
    }, [roomId]);

    if (checking) {
        return <div style={{ marginTop: '300px', color: 'black' }}>LOADING</div>;
    }

    if (roomNotFound) {
        return <div style={{ marginTop: '300px', color: 'black' }}>Room not found</div>;
    }

    return roomId ? <PlayCheckRoomFull id={roomId} /> : <div>Room ID is not available</div>;
};

export default PlayCheck;
