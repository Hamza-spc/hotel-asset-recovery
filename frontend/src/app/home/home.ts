import { Component, inject, resource } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { Auth } from '../auth/auth';
import { StaffApi } from '../core/staff-api';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  protected readonly auth = inject(Auth);
  private readonly staffApi = inject(StaffApi);

  protected readonly profile = resource({
    loader: () => firstValueFrom(this.staffApi.me()),
  });
}
