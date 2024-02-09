import {ChatService} from "Frontend/generated/endpoints";
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
import {VirtualList} from "@hilla/react-components/VirtualList";
import {Avatar} from "@hilla/react-components/Avatar";

import './LobbyView.css';
import {formatPastDate, hashCode} from "Frontend/util/util";

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
                Notification.show("Channel successfully created", {
                    theme: "success",
                    position: "bottom-end"
                });
            } catch (err) {
                console.error("Error creating channel", err);
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

function truncateMessage(msg: string) {
    if (msg.length > 50) {
        return msg.substring(0, 50) + "...";
    }
    return msg;
}

function ChannelComponent({channel}: { channel: Channel }) {
    const colorIndex = Math.abs(hashCode(channel.id) % 7);
    return (<div className={"flex gap-m p-m rounded-m channel"} key={"channel-" + channel.id}>
        <Avatar name={channel.name} theme={"small"} colorIndex={colorIndex}/>
        <div className={"flex-auto flex flex-col leading-xs gap-xs"}>
            <div className={"flex items-baseline justify-start gap-s"}>
                <Link to={"channel/" + channel.id} className={"text-m font-bold text-body"}>{channel.name}</Link>
                {channel.lastMessage && <div
                    className={"text-s text-secondary"}>{formatPastDate(new Date(channel.lastMessage.timestamp))}</div>}
            </div>
            {channel.lastMessage && <div className={"text-s text-secondary"}><span
                className={"font-bold"}>{channel.lastMessage.author}</span>: {truncateMessage(channel.lastMessage.message)}
            </div>}
            {!channel.lastMessage && <div className={"text-s text-secondary"}>No messages</div>}
        </div>
    </div>);
}

export default function LobbyView() {
    const {hasAccess} = useAuth();
    const isAdmin = hasAccess({rolesAllowed: ["ADMIN"]});

    useEffect(() => {
        (async () => {
            try {
                channels.value = await ChatService.channels();
            } catch (err) {
                console.error("Error loading channels", err);
                Notification.show("Failed to load channels. Please try again later.", {
                    theme: "error",
                    position: "middle"
                });
                channels.value = [];
            }
        })();
    }, []);

    pageTitle.value = "Channels";

    return (<VerticalLayout theme={"spacing padding"} className={"lobby-view h-full w-full"}>
        <VirtualList items={channels.value} className={"flex-grow border p-s"}>
            {({item}) => {
                return <ChannelComponent channel={item}/>
            }}
        </VirtualList>
        {isAdmin && <AddChannelComponent/>}
    </VerticalLayout>);
}