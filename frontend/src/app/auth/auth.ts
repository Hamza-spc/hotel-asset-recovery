import { Injectable, computed, signal } from '@angular/core';
import { User, UserManager } from 'oidc-client-ts';
import { createUserManager } from './auth.config';

export type StaffRole = 'HOUSEKEEPING' | 'FRONT_DESK' | 'DUTY_MANAGER';

@Injectable({ providedIn: 'root' })
export class Auth {
  private readonly manager: UserManager = createUserManager();
  private readonly user = signal<User | null>(null);
  private readonly ready = signal(false);
  private readonly platformError = signal<string | null>(null);

  readonly isAuthenticated = computed(() => this.user() !== null);
  readonly username = computed(
    () => this.user()?.profile.preferred_username ?? this.user()?.profile.sub ?? '',
  );
  readonly roles = computed(() => readRoles(this.user()));
  readonly error = this.platformError.asReadonly();
  readonly initialized = this.ready.asReadonly();

  async initialize(): Promise<void> {
    try {
      if (window.location.search.includes('code=')) {
        const signedIn = await this.manager.signinRedirectCallback();
        this.user.set(signedIn);
        window.history.replaceState({}, document.title, window.location.pathname);
      } else {
        this.user.set(await this.manager.getUser());
      }
      this.manager.events.addUserLoaded((next) => this.user.set(next));
      this.manager.events.addUserUnloaded(() => this.user.set(null));
    } catch (error) {
      this.platformError.set(
        error instanceof Error ? error.message : 'Could not reach Keycloak. Start infra first.',
      );
    } finally {
      this.ready.set(true);
    }
  }

  login(): Promise<void> {
    return this.manager.signinRedirect();
  }

  logout(): Promise<void> {
    return this.manager.signoutRedirect();
  }

  accessToken(): string | undefined {
    return this.user()?.access_token;
  }

  hasRole(role: StaffRole): boolean {
    return this.roles().includes(role);
  }
}

function readRoles(user: User | null): StaffRole[] {
  if (!user) {
    return [];
  }
  const profile = user.profile as Record<string, unknown>;
  const tokenClaims = decodeJwt(user.access_token);
  const roles = new Set<string>([
    ...asStringArray(profile['roles']),
    ...asStringArray(tokenClaims['roles']),
    ...realmRoles(profile['realm_access']),
    ...realmRoles(tokenClaims['realm_access']),
  ]);
  const known: StaffRole[] = ['HOUSEKEEPING', 'FRONT_DESK', 'DUTY_MANAGER'];
  return known.filter((role) => roles.has(role));
}

function realmRoles(value: unknown): string[] {
  if (value && typeof value === 'object') {
    return asStringArray((value as { roles?: unknown }).roles);
  }
  return [];
}

function decodeJwt(token: string | undefined): Record<string, unknown> {
  if (!token) {
    return {};
  }
  try {
    const payload = token.split('.')[1] ?? '';
    const padded = payload.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(padded)) as Record<string, unknown>;
  } catch {
    return {};
  }
}

function asStringArray(value: unknown): string[] {
  return Array.isArray(value) ? value.filter((item): item is string => typeof item === 'string') : [];
}
