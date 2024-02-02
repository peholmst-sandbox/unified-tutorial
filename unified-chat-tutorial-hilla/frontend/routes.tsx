import {protectRoutes} from '@hilla/react-auth';
import LoginView from 'Frontend/views/login/LoginView.js';
import MainLayout from 'Frontend/views/MainLayout.js';
import {createBrowserRouter, RouteObject} from 'react-router-dom';
import LobbyView from "Frontend/views/lobby/LobbyView";
import RoomView from "Frontend/views/room/RoomView";

export const routes = protectRoutes([
  {
    element: <MainLayout />,
    handle: { title: 'Main' },
    children: [
        {path: '/', element: <LobbyView/>, handle: {title: 'Lobby', requiresLogin: true}},
        {path: '/chatroom', element: <RoomView/>, handle: {title: 'Chat Room', requiresLogin: true}},
    ],
  },
  { path: '/login', element: <LoginView /> },
]) as RouteObject[];

export default createBrowserRouter(routes);
