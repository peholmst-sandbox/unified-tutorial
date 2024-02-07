import {useNavigate, useParams} from "react-router-dom";
import {useEffect} from "react";
import {Chat} from "Frontend/generated/endpoints";
import {VerticalLayout} from "@hilla/react-components/VerticalLayout";
import {MessageList} from "@hilla/react-components/MessageList";
import {MessageInput} from "@hilla/react-components/MessageInput";
import {Subscription} from "@hilla/frontend";
import ChatMessage from "Frontend/generated/com/example/application/service/ChatMessage";
import {useAuth} from "Frontend/util/auth";
import {pageTitle} from "Frontend/views/MainLayout";
import {effect, signal} from "@preact/signals-react";
import connectClient from "Frontend/generated/connect-client.default";

const HISTORY_SIZE = 20; // A small number to demonstrate the feature

const roomId = signal<number | undefined>(undefined);
const subscription = signal<Subscription<ChatMessage> | undefined>(undefined);
const messages = signal<ChatMessage[]>([]);
const connectionActive = signal(false);

// TODO This should be a part of the framework
connectClient.fluxConnection.addEventListener('state-changed', (event: CustomEvent<{ active: boolean }>) => {
    connectionActive.value = event.detail.active;
});

const receiveMessage = (message: ChatMessage) => {
    const newMessages = [...messages.value, message];
    if (newMessages.length > HISTORY_SIZE) {
        newMessages.shift();
    }
    messages.value = newMessages;
};

const sendMessage = (message: string) => {
    if (roomId.value !== undefined) {
        Chat.postMessage(roomId.value, message).then();
    }
};

const subscribe = () => {
    unsubscribe();
    if (roomId.value !== undefined) {
        subscription.value = Chat.liveMessages(roomId.value)
            .onNext(receiveMessage);
        // TODO handle errors
    }
};

const fetchMessageHistory = async () => {
    if (roomId.value !== undefined) {
        messages.value = await Chat.messageHistory(roomId.value, HISTORY_SIZE);
    } else {
        messages.value = [];
    }
};

const unsubscribe = () => {
    if (subscription.value) {
        subscription.value.cancel();
        subscription.value = undefined;
    }
};

effect(() => {
    (async () => {
        if (roomId.value !== undefined && connectionActive.value) {
            pageTitle.value = await Chat.roomName(roomId.value);
            await fetchMessageHistory();
            subscribe();
        }
    })();
    return unsubscribe;
});

export default function RoomView() {
    const currentUser = useAuth().state.user?.name;
    const navigate = useNavigate();
    const params = useParams();

    useEffect(() => {
        const id = Number.parseInt(params.roomId!, 10);
        if (Number.isNaN(id)) {
            navigate("/");
        } else {
            roomId.value = id;
        }
    }, [params.roomId]);

    return (
        <VerticalLayout theme={"padding spacing"} className={"w-full h-full"}>
            <MessageList className={"w-full h-full border"} items={messages.value.map(message => ({
                text: message.message,
                userName: message.author,
                time: message.timestamp,
                theme: message.author === currentUser ? "current-user" : undefined,
            }))}/>
            <MessageInput className={"w-full"} onSubmit={e => sendMessage(e.detail.value)}/>
        </VerticalLayout>
    );
}