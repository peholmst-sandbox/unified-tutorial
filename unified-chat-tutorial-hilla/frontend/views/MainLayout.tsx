import {AppLayout} from '@hilla/react-components/AppLayout.js';
import {DrawerToggle} from '@hilla/react-components/DrawerToggle.js';
import Placeholder from 'Frontend/components/placeholder/Placeholder.js';
import {useAuth} from 'Frontend/util/auth.js';
import {useRouteMetadata} from 'Frontend/util/routing.js';
import {Suspense, useEffect} from 'react';
import {Outlet} from 'react-router-dom';
import {SideNav} from "@hilla/react-components/SideNav";
import {SideNavItem} from "@hilla/react-components/SideNavItem";
import {Icon} from "@hilla/react-components/Icon";
import {Scroller} from "@hilla/react-components/Scroller";
import {Button} from "@hilla/react-components/Button";
import {Tooltip} from "@hilla/react-components/Tooltip";

export default function MainLayout() {
    const currentTitle = useRouteMetadata()?.title ?? 'Vaadin Chat'; // TODO How to get dynamic title?
    useEffect(() => {
        document.title = currentTitle;
    }, [currentTitle]);

    const {state, logout} = useAuth();
    return (
        <AppLayout primarySection="drawer">
            <span className={"items-center flex text-l font-semibold h-xl px-m"} slot={"drawer"}>Vaadin Chat</span>
            <Scroller slot={"drawer"} className={"p-s"}>
                <SideNav>
                    <SideNavItem path="/">
                        <Icon icon={"vaadin:building"} slot={"prefix"}/>
                        Lobby
                    </SideNavItem>
                </SideNav>
            </Scroller>

            <header slot={"navbar"} className={"items-center flex pe-m w-full"}>
                <DrawerToggle aria-label="Menu toggle">
                    <Tooltip slot={"tooltip"} text={"Menu toggle"}/>
                </DrawerToggle>
                <h2 className={"text-l m-0 flex-grow"}>{currentTitle}</h2>
                <Button onClick={logout}>Logout {state.user?.name}</Button>
            </header>

            <Suspense fallback={<Placeholder/>}>
                <Outlet/>
            </Suspense>
        </AppLayout>
    );
}
