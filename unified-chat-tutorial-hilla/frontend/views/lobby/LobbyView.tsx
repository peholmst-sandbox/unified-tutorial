import {ChatService} from "Frontend/generated/endpoints";
import {Grid} from "@hilla/react-components/Grid";
import {GridColumn} from "@hilla/react-components/GridColumn";
import {VerticalLayout} from "@hilla/react-components/VerticalLayout";
import {useAuth} from "Frontend/util/auth";
import {HorizontalLayout} from "@hilla/react-components/HorizontalLayout";
import {TextField} from "@hilla/react-components/TextField";
import {Button} from "@hilla/react-components/Button";
import {Notification} from "@hilla/react-components/Notification";
import {Link} from "react-router-dom";

import {pageTitle} from "Frontend/views/MainLayout";
import {signal, useSignal} from "@preact/signals-react";
import {useEffect} from "react";
import Channel from "Frontend/generated/com/example/application/chat/Channel";

const channels = signal<Channel[]>([]);

function AddChannelComponent() {
    const name = useSignal('');
    const buttonDisabled = useSignal(false);

    const addChannel = async () => {
        if (name.value !== '') {
            buttonDisabled.value = true;
            try {
                const newChannel = await ChatService.createChannel(name.value);
                name.value = '';
                channels.value = [...channels.value, newChannel];
            } catch (error) {
                Notification.show("Failed to create channel. Please try again later.", {
                    theme: "error",
                    position: "bottom-end"
                });
            } finally {
                buttonDisabled.value = false;
            }
        }
    };

    // TODO Assign ENTER as shortcut key to button
    return <HorizontalLayout theme={"spacing"} className={"w-full"}>
        <TextField placeholder={"New channel name"} className={"flex-grow"} value={name.value}
                   onChange={e => name.value = e.target.value}/>
        <Button theme={"primary"} onClick={addChannel} disabled={buttonDisabled.value}>Add channel</Button>
    </HorizontalLayout>
}

export default function LobbyView() {
    const {hasAccess} = useAuth();
    const isAdmin = hasAccess({rolesAllowed: ["ADMIN"]});

    useEffect(() => {
        (async () => {
            try {
                channels.value = await ChatService.channels();
            } catch (error) {
                Notification.show("Failed to load channels. Please try again later.", {
                    theme: "error",
                    position: "middle"
                });
                channels.value = [];
            }
        })();
    }, []);

    pageTitle.value = "Channels";

    return <VerticalLayout theme={"spacing padding"} className={"h-full w-full"}>
        <Grid className={"h-full w-full"} items={channels.value}>
            <GridColumn header={"Channel Name"}>
                {({item}) => {
                    return <Link to={"channel/" + item.id}>{item.name}</Link>
                }}
            </GridColumn>
            <GridColumn header={"Last Message"}>
                {({item}) => {
                    return (item as Channel).lastMessage?.timestamp || "Never"
                }}
            </GridColumn>
        </Grid>
        {isAdmin && <AddChannelComponent/>}
    </VerticalLayout>;
}