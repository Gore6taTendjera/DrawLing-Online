import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PlayCheckDisplayName from './PlayCheckDisplayName';
import axios from 'axios';

interface PlayCheckRoomFullProps {
    id: string | undefined;
}

export default function PlayCheckRoomFull({ id }: PlayCheckRoomFullProps) {
    const navigate = useNavigate();
    const [isRoomFull, setIsRoomFull] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (id) {
            axios.get(`http://localhost:8080/api/room/full/${id}`)
                .then((response) => {
                    if (response.status === 200) {
                        navigate(`/play/${id}`);
                    }
                })
                .catch((error) => {
                    if (error.response && error.response.status === 409) {
                        setIsRoomFull(true);
                    }
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    }, [id, navigate]);

    if (loading) {
        return <div style={{ marginTop: '300px', color: 'black' }}>LOADING</div>;
    }

    if (isRoomFull) {
        return (
            <div style={{ marginTop: '300px', color: 'black' }}>
                The room is full.
            </div>
        );
    }

    return <PlayCheckDisplayName roomId={id} />;
}
