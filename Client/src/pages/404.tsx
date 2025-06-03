import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import styles from './404.module.css';

export default function Error404() {
    useEffect(() => {
        document.body.classList.add('error404-body');
        return () => {
            document.body.classList.remove('error404-body');
        };
    }, []);

    return (
        <div className={styles.container}>
            <div className={styles.content}>
                <h1 className={styles.title}>404</h1>
                <h2 className={styles.subtitle}>Oops! Page Not Found</h2>
                <p className={styles.message}>
                    Sorry, the page you are looking for does not exist. 
                    It might have been removed or is temporarily unavailable.
                </p>
                <Link to="/" className={styles.button}>Go Back</Link>
            </div>
        </div>
    );
};
