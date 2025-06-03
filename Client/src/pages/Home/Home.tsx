import styles from './Home.module.css';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import userService from '../../service/UserService';
import { useEffect } from 'react';

type GameMode = 'Normal' | 'Duo' | 'Trio' | 'Combine';

interface ColorConfig {
    [key: string]: {
        sectionColor: string;
        buttonColor: string;
    };
}

export default function HomePage() {
    const [selectedMode, setSelectedMode] = useState<GameMode>('Normal');
    const [totalUserCount, setTotalUserCount] = useState(0);

    const { getTotalUserCount } = userService();

    const handleUserCount = async () => {
        try{
            getTotalUserCount().then((count) => {
                setTotalUserCount(count);
            })
        } catch (error) {
            console.error('Error fetching user count:', error);
        }
    }

    useEffect(() => {
        handleUserCount();
    }, []);

    console.log("HOME RENDER");

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSelectedMode(event.target.value as GameMode);
    };

    const descriptions: { [key in GameMode]: string } = {
        Normal: 'Standard gameplay mode. \n Enjoy a classic experience with friends!',
        Duo: 'Play with a partner. \n Team up and strategize together to win!',
        Trio: 'Team up with two friends. \n Work together to outsmart your opponents.',
        Combine: 'Draw with your friend at the same time. \n After time is up, the drawings are combined into 1!',
    };

    const colorConfig: ColorConfig = {
        Normal: { sectionColor: '#3f48cc', buttonColor: '#515cff' },
        Duo: { sectionColor: '#3cae45', buttonColor: '#57eb63' },
        Trio: { sectionColor: '#a51f1f', buttonColor: '#dd2b2b' },
        Combine: { sectionColor: '#ac246f', buttonColor: '#dd2b8d' },
    };

    const getImageSrc = (mode: GameMode) => {
        const imageLinks: { [key in GameMode]: string } = {
            Normal: "/normalGM.png",
            Duo: 'https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftootlebootle.files.wordpress.com%2F2015%2F06%2Fblank-canvas.jpg&f=1&nofb=1&ipt=40ab4874f89c5993e24ebd008e6552961f8741ccf433f8bc7afd057c99f25758&ipo=images',
            Trio: 'https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftootlebootle.files.wordpress.com%2F2015%2F06%2Fblank-canvas.jpg&f=1&nofb=1&ipt=40ab4874f89c5993e24ebd008e6552961f8741ccf433f8bc7afd057c99f25758&ipo=images',
            Combine: 'https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftootlebootle.files.wordpress.com%2F2015%2F06%2Fblank-canvas.jpg&f=1&nofb=1&ipt=40ab4874f89c5993e24ebd008e6552961f8741ccf433f8bc7afd057c99f25758&ipo=images',
        };
        return imageLinks[mode];
    };

    return (
        <>
            <section id={styles.section1} style={{ backgroundColor: colorConfig[selectedMode].sectionColor }}>
                <span className={styles.totalUserCount}>Registered players: {totalUserCount}</span>
                <div className={styles.topSpace}></div>
                <div className={`wrapper`}>
                    <h1 className='text-center text-italic'>DrawLing Online</h1>

                    <div className={styles.gameModeSelector}>
                        <h2 className='font-xxxlarge'>Select Game Mode</h2>
                        <div className={styles.radioGroup}>
                            {['Normal', 'Duo', 'Trio', 'Combine'].map((mode) => (
                                <label key={mode}
                                    className={`${styles.radioLabel} ${selectedMode === mode ? styles.selected : ''}`}
                                    style={{
                                        '--button-color': selectedMode === mode ? colorConfig[mode as GameMode].buttonColor : '#ccc'
                                    } as React.CSSProperties}>
                                    <input
                                        type="radio"
                                        value={mode}
                                        checked={selectedMode === mode}
                                        onChange={handleChange}
                                        className={styles.radioInput}
                                    />
                                    <span className='font-large bold'>{mode}</span>
                                </label>
                            ))}
                        </div>
                    </div>
                    
                    <div className={styles.playBtnDiv}>
                        <button className={`${styles.playBtn} text-italic font-xxlarge bold`}>PLAY</button>
                    </div>
                    <h3 className='font-xxlarge text-center text-italic'>OR</h3>
                    <div className={styles.playBtnDiv}>
                        <Link to="/lobby">
                            <button className={`${styles.playBtn} text-italic font-xxlarge bold`}>CREATE ROOM</button>
                        </Link>
                    </div>

                    <div className={styles.descriptionDiv}>
                        <h2 className='font-xxlarge'>Mode Description</h2>
                        {selectedMode && (
                            <span className={`${styles.description} font-large`}>
                                {descriptions[selectedMode].split('\n').map((line, index) => (
                                    <p key={index}>{line}</p>
                                ))}
                            </span>
                        )}
                        <img src={getImageSrc(selectedMode)} alt={selectedMode} />
                    </div>
                </div>
            </section>
        </>
    );
}
