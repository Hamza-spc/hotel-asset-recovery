import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';

export interface CurrentStaff {
  subject: string;
  username: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class StaffApi {
  private readonly http = inject(HttpClient);

  me() {
    return this.http.get<CurrentStaff>(`${environment.apiUrl}/api/me`);
  }
}
