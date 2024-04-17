import {useAuth} from "Frontend/util/auth.js";
import {LoginForm} from "@vaadin/react-components/LoginForm";
import {signal} from "@preact/signals-react";
import {useEffect} from "react";

export default function LoginView() {
    const {login} = useAuth();
    const hasError = signal(false);
    //const navigate = useNavigate();

    useEffect(() => {
        document.title = "Login";
    }, []);

    return (
        <div className="flex flex-col h-full w-full items-center justify-center gap-m">
            <h1>Vaadin Chat</h1>
            <div>You can log in as 'alice', 'bob' or 'admin'. The password for all of them is 'password'.</div>
            <LoginForm error={hasError.value} onLogin={async ({detail: {username, password}}) => {
                const {defaultUrl, error, redirectUrl} = await login(username, password);
                if (error) {
                    hasError.value = true;
                } else {
                    const url = redirectUrl ?? defaultUrl ?? "/";
                    const path = new URL(url, document.baseURI).pathname;
                    // navigate(path); // Does not work because of https://github.com/vaadin/hilla/issues/2063
                    document.location = path; // Workaround until the issue has been fixed
                }
            }}
            />
        </div>
    )
}
