import { Link } from 'react-router-dom';
import styles from './Navbar.module.css';

export default function Navbar() {
  return (
    <nav className={styles.navbar}>
      <ul className={styles.navList}>
        <li className={styles.navItem}>
          <Link className='font-large' to="/">Home</Link>
        </li>
        <li className={styles.navItem}>
          <Link className='font-large' to="/lobby">Play</Link>
        </li>
        <li className={styles.navItem}>
          <Link className='font-large' to="/Profile">Profile</Link>
        </li>
        <li className={styles.navItem}>
          <Link className='font-large' to="/Login">Login</Link>
        </li>
      </ul>
    </nav>
  );
}
