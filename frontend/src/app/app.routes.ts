import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { Home } from './home/home';

export const routes: Routes = [
  { path: '', component: Home, canActivate: [authGuard] },
  { path: '**', redirectTo: '' },
];
