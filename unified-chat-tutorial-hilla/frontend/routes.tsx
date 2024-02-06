import {protectRoutes} from '@hilla/react-auth';
import LoginView from 'Frontend/views/login/LoginView.js';
import MainLayout from 'Frontend/views/MainLayout.js';
import {createBrowserRouter, RouteObject} from 'react-router-dom';
import LobbyView from "Frontend/views/lobby/LobbyView";
import RoomView from "Frontend/views/room/RoomView";

export const routes = protectRoutes([
    {
        element: <MainLayout/>,
        handle: {title: 'Main'},
        children: [
            {path: '/', element: <LobbyView/>, handle: {requiresLogin: true}},
            {path: '/chatroom/:roomId', element: <RoomView/>, handle: {requiresLogin: true}},
        ],
    },
    {path: '/login', element: <LoginView/>},
]) as RouteObject[];

export default createBrowserRouter(routes);
