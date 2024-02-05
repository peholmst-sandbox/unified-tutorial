import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {Chat} from "Frontend/generated/endpoints";
import {VerticalLayout} from "@hilla/react-components/VerticalLayout";
import {MessageList, MessageListItem} from "@hilla/react-components/MessageList";
import {MessageInput} from "@hilla/react-components/MessageInput";
import {Subscription} from "@hilla/frontend";
import ChatMessage from "Frontend/generated/com/example/application/service/ChatMessage";
import {useAuth} from "Frontend/util/auth";
import {Notification} from '@hilla/react-components/Notification.js';

const HISTORY_SIZE = 20; // A small number to demonstrate the feature

export default function RoomView() {
    const {state} = useAuth();
    const navigate = useNavigate();
    const params = useParams();
    const [roomId, setRoomId] = useState<number | undefined>()
    const [roomName, setRoomName] = useState("");
    const [subscription, setSubscription] = useState<Subscription<ChatMessage> | undefined>();
    const [messages, setMessages] = useState<MessageListItem[]>([]);

    useEffect(() => {
        const id = params.roomId ? Number.parseInt(params.roomId, 10) : Number.NaN;
        if (Number.isNaN(id)) {
            navigate("/");
        } else {
            setRoomId(id);
        }
    }, [params.roomId]);

    useEffect(() => {
        if (subscription) {
            subscription.cancel();
            setSubscription(undefined);
        }
        if (roomId !== undefined) {
            Chat.roomName(roomId).then(
                result => {
                    setRoomName(result);
                    Chat.messageHistory(roomId, HISTORY_SIZE).then(oldMessages => {
                            setMessages(oldMessages.map(messageToItem));
                            setSubscription(Chat.liveMessages(roomId)
                                .onNext(message => {
                                    receiveMessage(message);
                                })
                                .onError(() => {
                                    // TODO Handle error
                                })
                            );
                        },
                        () => navigate("/")
                    );
                },
                () => navigate("/")
            );
        }
        return () => {
            if (subscription) {
                subscription.cancel();
                setSubscription(undefined);
            }
        };
    }, [roomId]);

    // TODO Set page title to room name

    const messageToItem = (message: ChatMessage): MessageListItem => {
        return {
            text: message.message,
            userName: message.author,
            time: message.timestamp,
            theme: message.author === state.user?.name ? "current-user" : undefined,
        };
    };

    const sendMessage = (message: string) => {
        if (roomId !== undefined) {
            Chat.postMessage(roomId, message).then();
        }
    };

    const receiveMessage = (message: ChatMessage) => {
        setMessages(prev => {
            let newMessages = [...prev, messageToItem(message)];

            if (newMessages.length > HISTORY_SIZE) {
                newMessages = newMessages.slice(newMessages.length - HISTORY_SIZE);
            }

            if (state.user?.name !== message.author) {
                Notification.show(`New message from ${message.author}`);
            }

            return newMessages;
        });
    };

    return <VerticalLayout theme={"padding spacing"} className={"w-full h-full"}>
        <MessageList className={"w-full h-full border"} items={messages}/>
        <MessageInput className={"w-full"} onSubmit={e => sendMessage(e.detail.value)}/>
    </VerticalLayout>
}