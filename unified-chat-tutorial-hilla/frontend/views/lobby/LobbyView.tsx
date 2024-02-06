import ChatRoom from "Frontend/generated/com/example/application/service/ChatRoom";
import {Chat} from "Frontend/generated/endpoints";
import {Grid} from "@hilla/react-components/Grid";
import {GridColumn} from "@hilla/react-components/GridColumn";
import {VerticalLayout} from "@hilla/react-components/VerticalLayout";
import {useAuth} from "Frontend/util/auth";
import {HorizontalLayout} from "@hilla/react-components/HorizontalLayout";
import {TextField} from "@hilla/react-components/TextField";
import {Button} from "@hilla/react-components/Button";
import {Link} from "react-router-dom";

import {pageTitle} from "Frontend/views/MainLayout";
import {signal, useSignal} from "@preact/signals-react";
import {useEffect} from "react";

pageTitle.value = "Lobby";

const rooms = signal<ChatRoom[]>([]);
const refreshRooms = async () => {
    rooms.value = await Chat.rooms();
}

function AddRoomComponent() {
    const name = useSignal('');
    const buttonDisabled = useSignal(false);

    const addRoom = async () => {
        if (name.value !== '') {
            buttonDisabled.value = true;
            try {
                await Chat.createRoom(name.value);
                name.value = '';
                await refreshRooms();
            } finally {
                buttonDisabled.value = false;
            }
        }
    };

    // TODO Assign ENTER as shortcut key to button
    return <HorizontalLayout theme={"spacing"} className={"w-full"}>
        <TextField placeholder={"New chat room name"} className={"flex-grow"} value={name.value}
                   onChange={e => name.value = e.target.value}/>
        <Button theme={"primary"} onClick={addRoom} disabled={buttonDisabled.value}>Add room</Button>
    </HorizontalLayout>
}

export default function LobbyView() {
    const {hasAccess} = useAuth();
    const isAdmin = hasAccess({rolesAllowed: ["ROLE_ADMIN"]});

    useEffect(() => {
        (async () => await refreshRooms())();
    }, []);

    return <VerticalLayout theme={"spacing padding"} className={"h-full w-full"}>
        <Grid className={"h-full w-full"} items={rooms.value}>
            <GridColumn header={"Room Name"}>
                {({item}) => {
                    return <Link to={"chatroom/" + item.id}>{item.name}</Link>
                }}
            </GridColumn>
            <GridColumn header={"Last Message"}>
                {({item}) => {
                    return (item as ChatRoom).lastMessage || "Never"
                }}
            </GridColumn>
        </Grid>
        {isAdmin && <AddRoomComponent/>}
    </VerticalLayout>;
}