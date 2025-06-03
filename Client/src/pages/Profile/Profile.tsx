import React, { useEffect, useState } from 'react';
import styles from './Profile.module.css';
import { jwtDecode } from 'jwt-decode';
import useAuth from '../../hooks/useAuth';
import balanceService from '../../service/BalanceService';
import profilePictureService from '../../service/ProfilePictureService';
import experienceLevelService from '../../service/ExperienceLevelService';
import savedImagesService from '../../service/ImageService';
import userService from '../../service/UserService';

const Profile = () => {
    const { auth } = useAuth();
    const jwtDecoded = jwtDecode(auth.accessToken!);

    const { userId } = jwtDecoded as { userId: number };
    const { sub } = jwtDecoded as { sub: string };

    const [displayName, setDisplayName] = useState<string>(sub!);
    const [isEditingName, setIsEditingName] = useState<boolean>(false);
    const [images, setImages] = useState<string[]>([]);
    const [profilePicture, setProfilePicture] = useState<string>('');
    const [experienceLevel, setExperienceLevel] = useState<number>(0);
    const [xpRemaining, setXpRemaining] = useState<number>(0);
    const [minXp, setMinXp] = useState<number>(0);
    const [maxXp, setMaxXp] = useState<number>(0);
    const [balance, setBalance] = useState<number>(0);
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [previewSrc, setPreviewSrc] = useState<string | null>(null);
    const [isSaving, setIsSaving] = useState<boolean>(false);

    const dummyIMG = "https://cdn1.iconfinder.com/data/icons/user-pictures/100/unknown-1024.png";

    const { getBalanceByUserId } = balanceService();
    const { getProfilePictureByUserId, uploadProfilePicture } = profilePictureService();
    const { getExperienceLevelByUserId } = experienceLevelService();
    const { getSavedImagesByUserId } = savedImagesService();
    const { getDisplayNameById, updateDisplayName } = userService();

    useEffect(() => {
        loadProfileData();
    }, []);

    const loadProfileData = async () => {
        await Promise.all([
            handleGetDisplayName(),
            loadImages(),
            loadProfilePicture(),
            loadExperienceLevel(),
            loadUserBalance(),
        ]);
    };

    const handleGetDisplayName = async () => {
        try {
            const fetchedDisplayName = await getDisplayNameById(userId);
            setDisplayName(fetchedDisplayName);
        } catch (err) {
            setDisplayName(sub!);
            // console.error('Error fetching display name:', err);
        }
    };

    const handleEditDisplayName = () => {
        setIsEditingName(true);
    };

    const handleSaveDisplayName = async () => {
        try {
            await updateDisplayName(userId, displayName);
            setIsEditingName(false);
        } catch (error) {
            // console.error('Failed to update display name:', error);
            alert('An error occurred while updating the display name.');
        }
    };

    const handleCancelEdit = () => {
        setIsEditingName(false);
        handleGetDisplayName(); // Optionally reload display name from server
    };

    const loadImages = async () => {
        try {
            const fetchedImages = await getSavedImagesByUserId(userId);
            setImages(fetchedImages);
        } catch (err) {
            // console.error('Failed to fetch images:', err);
        }
    };

    const loadProfilePicture = async () => {
        try {
            const fetchedProfilePicture = await getProfilePictureByUserId(userId);
            setProfilePicture(fetchedProfilePicture);
        } catch (err) {
            // console.error('Failed to fetch profile picture:', err);
        }
    };

    const loadExperienceLevel = async () => {
        try {
            const levelData = await getExperienceLevelByUserId(userId);
            setExperienceLevel(levelData.level);
            setXpRemaining(levelData.xpRemaining);
            setMinXp(levelData.min);
            setMaxXp(levelData.max);
        } catch (err) {
            // console.error('Failed to fetch experience level:', err);
        }
    };

    const loadUserBalance = async () => {
        try {
            const fetchedBalance = await getBalanceByUserId(userId);
            setBalance(fetchedBalance);
        } catch (err) {
            // console.error('Failed to fetch user balance:', err);
        }
    };

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files.length > 0) {
            const file = event.target.files[0];
            setSelectedFile(file);
            setPreviewSrc(URL.createObjectURL(file));
        }
    };

    const handleSave = async () => {
        if (!selectedFile) {
            alert("Please select a file to upload.");
            return;
        }

        setIsSaving(true);

        try {
            const newProfilePictureUrl = await uploadProfilePicture(userId, selectedFile);
            setProfilePicture(newProfilePictureUrl);
            setPreviewSrc(null);
            setSelectedFile(null);
            alert("Profile picture updated successfully!");
        } catch (error) {
            // console.error('Error uploading image:', error);
            alert("Failed to upload image.");
        }

        setIsSaving(false);
    };

    const handleCancel = () => {
        setPreviewSrc(null);
        setSelectedFile(null);
    };

    const currentXp = maxXp - xpRemaining;
    const progressBarWidth = maxXp > minXp ? ((currentXp - minXp) / (maxXp - minXp)) * 100 : 0;

    return (
        <>
            <section id={styles.section1}>
                <div className='top-space'></div>
                <div className='wrapper'>
                    <div className={`row`}>
                        <div className={styles.profile}>
                            <img
                                src={previewSrc || profilePicture}
                                className={styles.profilePicture}
                                alt="Profile"
                                onError={(e) => {
                                    e.currentTarget.src = dummyIMG;
                                }}
                                onClick={() => document.getElementById('fileInput')?.click()}
                            />
                            <input
                                type="file"
                                id="fileInput"
                                style={{ display: 'none' }}
                                accept="image/*"
                                onChange={handleFileChange}
                            />
                            <div className={`row w100 ${styles.levelText}`}>
                                <span className='text-italic font-xlarge'>Level</span>
                                <span className='font-xlarge'>{experienceLevel}</span>
                            </div>
                            <h2 className='text-center'>${balance}</h2>
                        </div>
                        <div className={styles.profileName}>
                            {!isEditingName ? (
                                <h2
                                    className={`${styles.username} font-xxxlarge`}
                                    onMouseEnter={() => document.getElementById('editIcon')?.classList.add(styles.show)}
                                    onMouseLeave={() => document.getElementById('editIcon')?.classList.remove(styles.show)}
                                >
                                    {displayName}
                                    <span
                                        id="editIcon"
                                        className={`${styles.editIcon}`}
                                        onClick={handleEditDisplayName}
                                    >
                                        ✏️
                                    </span>
                                </h2>
                            ) : (
                                <div className={styles.editDisplayName}>
                                    <input
                                        type="text"
                                        value={displayName}
                                        maxLength={25} // Restrict the maximum input length
                                        onChange={(e) => setDisplayName(e.target.value)}
                                        className={styles.editInput} // Use the updated class
                                    />
                                    <button onClick={handleSaveDisplayName} className={styles.btnPrimary}>
                                        Save
                                    </button>
                                    <button onClick={handleCancelEdit} className={styles.btnSecondary}>
                                        Cancel
                                    </button>
                                </div>


                            )}
                        </div>
                    </div>
                    <div className={styles.levelBarContainer}>
                        <div className={styles.levelBar} style={{ width: `${progressBarWidth}%` }}>
                            <span className={styles.xpText}>{currentXp} / {maxXp} XP</span>
                        </div>
                    </div>
                    <p className='text-center'>XP Remaining: {xpRemaining}</p>
                    {selectedFile && (
                        <div className="text-center mt-3">
                            <button onClick={handleSave} className={`${styles.btn} ${styles.btnPrimary}`} disabled={isSaving}>
                                {isSaving ? 'Saving...' : 'Save'}
                            </button>
                            <button onClick={handleCancel} className={`${styles.btn} ${styles.btnSecondary}`} disabled={isSaving}>
                                Cancel
                            </button>
                        </div>
                    )}
                </div>
            </section>
            <section id={styles.section2}>
                <div className='wrapper'>
                    <h2 className='text-center font-xxlarge'>Saved drawings</h2>
                    <div className={styles.drawings}>
                        {images && (
                            images.map((image, index) => (
                                <a key={index} href={image} target="_blank" rel="noopener noreferrer">
                                    <img src={image} alt={`Drawing ${index + 1}`} />
                                </a>
                            ))
                        )}
                    </div>
                </div>
            </section>
        </>
    );
};

export default React.memo(Profile);
