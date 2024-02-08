import {useNavigate, useParams} from "react-router-dom";
import {useEffect} from "react";
import {ChatService} from "Frontend/generated/endpoints";
import {VerticalLayout} from "@hilla/react-components/VerticalLayout";
import {MessageList} from "@hilla/react-components/MessageList";
import {MessageInput} from "@hilla/react-components/MessageInput";
import {Subscription} from "@hilla/frontend";
import {useAuth} from "Frontend/util/auth";
import {pageTitle} from "Frontend/views/MainLayout";
import {useSignal} from "@preact/signals-react";
import Message from "Frontend/generated/com/example/application/chat/Message";
import {connectionActive} from "Frontend/util/workarounds";
import {Notification} from "@hilla/react-components/Notification";
import {Button} from "@hilla/react-components/Button";
import Channel from "Frontend/generated/com/example/application/chat/Channel";

const HISTORY_SIZE = 20; // A small number to demonstrate the feature

type ActiveSubscription<T> = { state: "active", subscription: Subscription<T> };
type InactiveSubscription = { state: "inactive" };
type PendingSubscription = { state: "pending" };
type SubscriptionState<T> = ActiveSubscription<T> | InactiveSubscription | PendingSubscription;

export default function ChannelView() {
    const currentUserName = useAuth().state.user?.name;
    const navigate = useNavigate();
    const params = useParams();
    const channel = useSignal<Channel | undefined>(undefined);
    const subscription = useSignal<SubscriptionState<Message[]>>({state: "pending"});
    const messages = useSignal<Message[]>([]);

    const connect = () => {
        disconnect();
        (async () => {
            if (!channel.value) {
                return;
            }
            messages.value = await ChatService.messageHistory(channel.value.id, HISTORY_SIZE);
            // There is a risk of missing messages that arrive after fetching the message history,
            // but before subscribing to the channel. Dealing with this situation is outside the scope of this tutorial.
            subscription.value = {
                state: "active",
                subscription: ChatService.liveMessages(channel.value.id).onNext(receiveMessages).onError(disconnect)
            };
        })();
    };

    const receiveMessages = (incoming: Message[]) => {
        const newMessages = [...messages.value, ...incoming];
        if (newMessages.length > HISTORY_SIZE) {
            newMessages.splice(0, newMessages.length - HISTORY_SIZE);
        }
        messages.value = newMessages;
    };

    const sendMessage = async (message: string) => {
        if (!channel.value) {
            return;
        }
        try {
            await ChatService.postMessage(channel.value.id, message);
        } catch (error) {
            Notification.show("Failed to send the message. Please try again later.", {
                theme: "error",
                position: "bottom-end"
            });
        }
    };

    const disconnect = () => {
        if (subscription.value.state === "active") {
            subscription.value.subscription.cancel();
            subscription.value = {state: "inactive"};
        }
    };

    useEffect(() => {
        (async () => {
            channel.value = params.channelId ? await ChatService.channel(params.channelId) : undefined;
            if (!channel.value) {
                navigate("/");
            } else {
                pageTitle.value = channel.value.name;
            }
        })();
    }, [params.channelId]);

    useEffect(() => {
        if (connectionActive.value) {
            connect();
        } else {
            disconnect();
        }
        return disconnect;
    }, [channel.value, connectionActive.value]);

    return (
        <VerticalLayout theme={"padding spacing"} className={"w-full h-full"}>
            <MessageList className={"w-full h-full border"} items={messages.value.map(message => ({
                text: message.message,
                userName: message.author,
                time: message.timestamp,
                theme: message.author === currentUserName ? "current-user" : undefined,
            }))}/>
            <MessageInput className={"w-full"} onSubmit={e => sendMessage(e.detail.value)}/>
            <Notification opened={subscription.value.state === "inactive"} theme={"error"} duration={0}>
                <span>The connection to the chat service is currently down. If the problem persists, try reloading the page.</span>
                <Button onClick={_ => window.location.reload()}>Reload</Button>
            </Notification>
        </VerticalLayout>
    );
}