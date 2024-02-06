import ChatRoom from "Frontend/generated/com/example/application/service/ChatRoom";
import {Chat} from "Frontend/generated/endpoints";
import {Grid} from "@hilla/react-components/Grid";
import {GridColumn} from "@hilla/react-components/GridColumn";
import {VerticalLayout} from "@hilla/react-components/VerticalLayout";
import {useEffect, useState} from "react";
import {useAuth} from "Frontend/util/auth";
import {HorizontalLayout} from "@hilla/react-components/HorizontalLayout";
import {TextField} from "@hilla/react-components/TextField";
import {Button} from "@hilla/react-components/Button";
import {Link} from "react-router-dom";

import {pageTitle} from "Frontend/views/MainLayout";

type AddRoomProps = {
    onRoomAddedCallback?: () => void;
}

function AddRoom(props: AddRoomProps) {
    const [name, setName] = useState('');
    const [buttonDisabled, setButtonDisabled] = useState(false);

    const addRoom = () => {
        if (name !== '') {
            setButtonDisabled(true);
            Chat.createRoom(name).then(() => {
                if (props.onRoomAddedCallback) {
                    props.onRoomAddedCallback();
                }
                setName('');
                setButtonDisabled(false);
            });
        }
    };

    // TODO Assign ENTER as shortcut key to button

    return <HorizontalLayout theme={"spacing"} className={"w-full"}>
        <TextField placeholder={"New chat room name"} className={"flex-grow"} value={name}
                   onChange={e => setName(e.target.value)}/>
        <Button theme={"primary"} onClick={addRoom} disabled={buttonDisabled}>Add room</Button>
    </HorizontalLayout>
}

export default function LobbyView() {
    const {hasAccess} = useAuth();
    pageTitle.value = "Lobby";

    const [rooms, setRooms] = useState<ChatRoom[]>([]);

    const refreshRooms = () => {
        Chat.rooms().then(result => setRooms(result));
    }

    useEffect(() => {
        refreshRooms();
    }, []);

    const isAdmin = hasAccess({rolesAllowed: ["ROLE_ADMIN"]});

    return <VerticalLayout theme={"spacing padding"} className={"h-full w-full"}>
        <Grid className={"h-full w-full"} items={rooms}>
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
        {isAdmin && <AddRoom onRoomAddedCallback={refreshRooms}/>}
    </VerticalLayout>;
}