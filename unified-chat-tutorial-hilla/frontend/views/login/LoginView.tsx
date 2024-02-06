import {useAuth} from 'Frontend/util/auth.js';
import {useNavigate} from 'react-router-dom';
import {LoginForm} from "@hilla/react-components/LoginForm";
import {signal} from "@preact/signals-react";

export default function LoginView() {
    const {login} = useAuth();
    const hasError = signal(false);
    const navigate = useNavigate();
    document.title = "Login";

    return (
        <div className={"flex flex-col h-full w-full items-center justify-center gap-m"}>
            <h1>Vaadin Chat</h1>
            <div>You can log in as 'alice', 'bob' or 'admin'. The password for all of them is 'password'.</div>
            <LoginForm error={hasError.value} onLogin={async ({detail: {username, password}}) => {
                const {defaultUrl, error, redirectUrl} = await login(username, password);
                if (error) {
                    hasError.value = true;
                } else {
                    const url = redirectUrl ?? defaultUrl ?? '/';
                    const path = new URL(url, document.baseURI).pathname;
                    navigate(path);
                }
            }}
            />
        </div>
    )
}
