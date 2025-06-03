import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import AuthService from './AuthService';

const Admin = () => {
    console.log("Admin");
    const [message, setMessage] = useState<string>('');
    const { getProtected, logOut } = AuthService();

    const getMessage = async () => {
        try {
            const response = await getProtected();
            setMessage(response);
        } catch (error) {
            console.error(error);
        }
    };

    const handleLogout = async () => {
        try {
            await logOut();
            console.log('Logged out successfully');
        } catch (error) {
            console.error('Error logging out:', error);
        }
    };

    useEffect(() => {
        getMessage();
    }, []);

    return (
        <section style={{ color: "black" }}>
            <br />
            <br />
            <br />
            <h1>Admins Page</h1>
            <br />
            <br />
            <p>{message}</p>
            <div className="flexGrow">
                <Link to="/">Home</Link>
            </div>
            <button onClick={handleLogout}>Logout</button>

            <button onClick={getMessage}>getMSG</button>
        </section>
    );
};

export default Admin;

