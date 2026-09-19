import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { roleGuard } from './auth/role.guard';
import { Audit } from './audit/audit';
import { Board } from './board/board';
import { FileReport } from './file-report/file-report';
import { MapPage } from './floor-map/map-page';
import { Home } from './home/home';
import { ItemDetail } from './item-detail/item-detail';
import { LogItem } from './log-item/log-item';
import { GuestPortal } from './guest/guest-portal';
import { GuestReceipt } from './guest/guest-receipt';
import { HowItWorks } from './how-it-works/how-it-works';
import { Matches } from './matches/matches';

export const routes: Routes = [
  { path: 'guest', component: GuestPortal },
  { path: 'guest/:id', component: GuestReceipt },
  { path: 'how-it-works', component: HowItWorks },
  { path: '', component: Home, canActivate: [authGuard] },
  { path: 'log-item', component: LogItem, canActivate: [authGuard, roleGuard(['HOUSEKEEPING'])] },
  {
    path: 'file-report',
    component: FileReport,
    canActivate: [authGuard, roleGuard(['FRONT_DESK'])],
  },
  { path: 'board', component: Board, canActivate: [authGuard] },
  { path: 'audit', component: Audit, canActivate: [authGuard] },
  { path: 'matches', component: Matches, canActivate: [authGuard, roleGuard(['DUTY_MANAGER'])] },
  { path: 'map', component: MapPage, canActivate: [authGuard] },
  { path: 'items/:id', component: ItemDetail, canActivate: [authGuard] },
  { path: '**', redirectTo: '' },
];
