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

export const routes: Routes = [
  { path: '', component: Home, canActivate: [authGuard] },
  { path: 'log-item', component: LogItem, canActivate: [authGuard, roleGuard(['HOUSEKEEPING'])] },
  {
    path: 'file-report',
    component: FileReport,
    canActivate: [authGuard, roleGuard(['FRONT_DESK'])],
  },
  { path: 'board', component: Board, canActivate: [authGuard] },
  { path: 'audit', component: Audit, canActivate: [authGuard] },
  { path: 'map', component: MapPage, canActivate: [authGuard] },
  { path: 'items/:id', component: ItemDetail, canActivate: [authGuard] },
  { path: '**', redirectTo: '' },
];
