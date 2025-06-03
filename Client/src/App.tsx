import './App.css';
import Header from './components/Header/Header';
import { Outlet } from 'react-router-dom';
import { Helmet } from 'react-helmet';

function App() {
  return (
    <>
      <Helmet>
        <title>Drawling Online</title>
        <meta name="description" content="Online multiplayer drawing game with interactive controls." />
      </Helmet>
      <Header />
      <Outlet />
    </>
  );
}

export default App;
