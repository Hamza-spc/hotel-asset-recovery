import { DatePipe } from '@angular/common';
import { Component, effect, inject, resource } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { OperationsApi } from '../core/operations-api';
import { OperationsRealtime } from '../core/operations-realtime';

@Component({
  selector: 'app-audit',
  imports: [RouterLink, DatePipe],
  templateUrl: './audit.html',
  styleUrl: './audit.scss',
})
export class Audit {
  private readonly api = inject(OperationsApi);
  private readonly realtime = inject(OperationsRealtime);

  protected readonly entries = resource({
    loader: () => firstValueFrom(this.api.recentAudit()),
  });

  constructor() {
    this.realtime.connect();
    effect(() => {
      if (this.realtime.revision() > 0) {
        this.entries.reload();
      }
    });
  }
}
