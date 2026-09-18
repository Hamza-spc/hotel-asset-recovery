import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth, StaffRole } from './auth';

export function roleGuard(roles: StaffRole[]): CanActivateFn {
  return () => {
    const auth = inject(Auth);
    const router = inject(Router);
    return roles.some((role) => auth.hasRole(role)) ? true : router.parseUrl('/');
  };
}
