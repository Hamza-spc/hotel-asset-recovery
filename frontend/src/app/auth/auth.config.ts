import { UserManager, WebStorageStateStore } from 'oidc-client-ts';
import { environment } from '../../environments/environment';

export function createUserManager(): UserManager {
  return new UserManager({
    authority: environment.keycloakIssuer,
    client_id: environment.clientId,
    redirect_uri: window.location.origin + '/',
    post_logout_redirect_uri: window.location.origin + '/',
    response_type: 'code',
    scope: 'openid profile email',
    automaticSilentRenew: true,
    userStore: new WebStorageStateStore({ store: window.sessionStorage }),
  });
}
