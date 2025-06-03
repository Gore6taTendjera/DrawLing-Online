import React, { createContext, useState, ReactNode, useEffect } from "react";

// Define the shape of the auth state
type AuthState = {
    user?: string;
    userId?: number;
    accessToken?: string; // Add accessToken property
};

// Define the shape of the context value
type AuthContextType = {
    auth: AuthState;
    setAuth: React.Dispatch<React.SetStateAction<AuthState>>;
};

// Create the context with a default value
const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [auth, setAuth] = useState<AuthState>({});

    useEffect(() => {
        console.log("Auth state updated/used:", auth);
    }, [auth, setAuth]);

    return (
        <AuthContext.Provider value={{ auth, setAuth }}>
            {children}
        </AuthContext.Provider>
    );
}
export default AuthContext;
