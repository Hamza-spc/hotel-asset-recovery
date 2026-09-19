import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';

export type ItemCategory =
  | 'WALLET'
  | 'PHONE'
  | 'KEYS'
  | 'JEWELRY'
  | 'BAG'
  | 'CLOTHING'
  | 'DOCUMENT'
  | 'OTHER';

export type ItemStatus =
  | 'LOGGED'
  | 'STORED'
  | 'MATCH_SUGGESTED'
  | 'CLAIM_PENDING'
  | 'RECLAIMED'
  | 'UNCLAIMED'
  | 'DISPOSED';

export type LossReportStatus = 'OPEN' | 'MATCHED' | 'RESOLVED' | 'CLOSED';

export interface FoundItem {
  id: string;
  trackingCode: string;
  description: string;
  category: ItemCategory;
  status: ItemStatus;
  zoneName: string;
  mapX: number | null;
  mapY: number | null;
  storageLocation: string | null;
  hasPhoto: boolean;
  foundBy: string;
  foundAt: string;
}

export interface LossReport {
  id: string;
  guestName: string;
  roomNumber: string;
  contact: string;
  description: string;
  zoneName: string;
  mapX: number | null;
  mapY: number | null;
  status: LossReportStatus;
  filedBy: string;
  filedAt: string;
}

export interface ZoneResolved {
  name: string;
  floorCode: string;
}

export interface HotelZoneCollection {
  type: 'FeatureCollection';
  features: Array<{
    type: 'Feature';
    properties: { id: string; name: string; floorCode: string };
    geometry: { type: string; coordinates: unknown };
  }>;
}

@Injectable({ providedIn: 'root' })
export class OperationsApi {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  listItems() {
    return this.http.get<FoundItem[]>(`${this.base}/api/items`);
  }

  getItem(id: string) {
    return this.http.get<FoundItem>(`${this.base}/api/items/${id}`);
  }

  itemPhoto(id: string) {
    return this.http.get(`${this.base}/api/items/${id}/photo`, { responseType: 'blob' });
  }

  logItem(payload: FormData) {
    return this.http.post<FoundItem>(`${this.base}/api/items`, payload);
  }

  storeItem(id: string, location: string) {
    return this.http.post<FoundItem>(`${this.base}/api/items/${id}/store`, null, { params: { location } });
  }

  openClaim(id: string) {
    return this.http.post<FoundItem>(`${this.base}/api/items/${id}/claim`, null);
  }

  reclaimItem(id: string) {
    return this.http.post<FoundItem>(`${this.base}/api/items/${id}/reclaim`, null);
  }

  markUnclaimed(id: string) {
    return this.http.post<FoundItem>(`${this.base}/api/items/${id}/unclaimed`, null);
  }

  disposeItem(id: string) {
    return this.http.post<FoundItem>(`${this.base}/api/items/${id}/dispose`, null);
  }

  listReports() {
    return this.http.get<LossReport[]>(`${this.base}/api/loss-reports`);
  }

  fileReport(payload: Record<string, string>) {
    const params = { ...payload };
    return this.http.post<LossReport>(`${this.base}/api/loss-reports`, null, { params });
  }

  closeReport(id: string) {
    return this.http.post<LossReport>(`${this.base}/api/loss-reports/${id}/close`, null);
  }

  zones() {
    return this.http.get<HotelZoneCollection>(`${this.base}/api/map/zones`);
  }

  resolveZone(x: number, y: number) {
    return this.http.get<ZoneResolved>(`${this.base}/api/map/resolve`, { params: { x, y } });
  }
}
