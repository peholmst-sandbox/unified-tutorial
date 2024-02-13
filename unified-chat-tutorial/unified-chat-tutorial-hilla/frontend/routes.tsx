import {protectRoutes} from '@hilla/react-auth';
import LoginView from 'Frontend/views/login/LoginView.js';
import MainLayout from 'Frontend/views/MainLayout.js';
import {createBrowserRouter, RouteObject} from 'react-router-dom';
import LobbyView from "Frontend/views/lobby/LobbyView";
import ChannelView from "Frontend/views/channel/ChannelView";

export const routes = protectRoutes([
    {
        element: <MainLayout/>,
        handle: {title: 'Main'},
        children: [
            {path: '/', element: <LobbyView/>, handle: {requiresLogin: true}},
            {path: '/channel/:channelId', element: <ChannelView/>, handle: {requiresLogin: true}},
        ],
    },
    {path: '/login', element: <LoginView/>},
]) as RouteObject[];

export default createBrowserRouter(routes);
