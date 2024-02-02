import {configureAuth} from '@hilla/react-auth';
import {CurrentUser} from "Frontend/generated/endpoints";

const auth = configureAuth(CurrentUser.getDetails);

export const useAuth = auth.useAuth;
export const AuthProvider = auth.AuthProvider;
