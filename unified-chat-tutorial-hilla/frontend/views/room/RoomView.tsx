import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {Chat} from "Frontend/generated/endpoints";
import {VerticalLayout} from "@hilla/react-components/VerticalLayout";
import {MessageList} from "@hilla/react-components/MessageList";
import {MessageInput} from "@hilla/react-components/MessageInput";

export default function RoomView() {
    const navigate = useNavigate();
    const {roomId} = useParams();
    const [roomName, setRoomName] = useState("");

    useEffect(() => {
        const id = roomId ? Number.parseInt(roomId, 10) : Number.NaN;
        if (Number.isNaN(id)) {
            navigate("/");
        } else {
            Chat.roomName(id).then(
                result => setRoomName(result),
                () => navigate("/")
            );
        }
    }, [roomId]);

    // TODO Set page title to room name

    return <VerticalLayout theme={"padding spacing"} className={"w-full h-full"}>
        <MessageList className={"w-full h-full border"}/>
        <MessageInput className={"w-full"}/>
    </VerticalLayout>
}