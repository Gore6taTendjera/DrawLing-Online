import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'

import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import ProtectedRoute from './components/AuthProvider/ProtectedRoute.tsx'
import { AuthProvider } from './contexts/AuthProvider.tsx';

import App from './App.tsx'
import HomePage from './pages/Home/Home.tsx'
import Error404 from './pages/404.tsx'
// import PlayPage from './pages/Play/Play.tsx'
import ProfilePage from './pages/Profile/Profile.tsx'
import LoginPage from './pages/Login/Login.tsx'
import TestPage from './components/test.tsx'
import TestPage2 from './components/test2.tsx'
import Lobby from './pages/Lobby/lobby.tsx'


import Admin from './components/testAdmin.tsx'
import PlayCheck from './pages/Play/PlayCheck.tsx'

import CorsTest from './api/corsTest.tsx'


const router = createBrowserRouter([
  {
    element: <App />,
    children: [
      {
        path: "*",
        element: <Error404 />
      },
      {
        path: "/",
        index: true,
        element: <HomePage />,
      },
      {
        path: "/play/:roomId",
        element: <PlayCheck />,
      },
      {
        path: "/play",
        element: <PlayCheck />,
      },
      {
        path: "/profile",
        element:
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>,
      },
      {
        // path: "/lobby/:id",
        path: "/lobby",
        element:
          <Lobby />
      },







      {
        path: "/login",
        element: <LoginPage />,
      },
      {
        path: "/test",
        element: <TestPage />,
      },
      {
        path: "/test2",
        element: <TestPage2 />,
      },
      {
        path: "/testAdmin",
        element:
          <ProtectedRoute>
            <Admin />
          </ProtectedRoute>,
      },
      {
        path: "/cors",
        element: <CorsTest />
      }
    ]
  },

]);


createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  </StrictMode>,
)
