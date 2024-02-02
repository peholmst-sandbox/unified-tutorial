import {useAuth} from 'Frontend/util/auth.js';
import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {LoginForm} from "@hilla/react-components/LoginForm";

export default function LoginView() {
    const {login} = useAuth();
    const [hasError, setError] = useState<boolean>();
    const navigate = useNavigate();

    return (
        <div className={"flex flex-col h-full w-full items-center justify-center gap-m"}>
            <h1>Vaadin Chat</h1>
            <div>You can log in as 'alice', 'bob' or 'admin'. The password for all of them is 'password'.</div>
            <LoginForm error={hasError} onLogin={async ({detail: {username, password}}) => {
                const {defaultUrl, error, redirectUrl} = await login(username, password);
                if (error) {
                    setError(true);
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
