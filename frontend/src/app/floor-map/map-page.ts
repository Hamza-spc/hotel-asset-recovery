import { Component, computed, effect, inject, resource } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { OperationsApi } from '../core/operations-api';
import { OperationsRealtime } from '../core/operations-realtime';
import { FloorMap, FloorPin } from './floor-map';

@Component({
  selector: 'app-map-page',
  imports: [RouterLink, FloorMap],
  templateUrl: './map-page.html',
  styleUrl: './map-page.scss',
})
export class MapPage {
  private readonly api = inject(OperationsApi);
  private readonly realtime = inject(OperationsRealtime);

  protected readonly items = resource({
    loader: () => firstValueFrom(this.api.listItems()),
  });
  protected readonly reports = resource({
    loader: () => firstValueFrom(this.api.listReports()),
  });

  constructor() {
    this.realtime.connect();
    effect(() => {
      if (this.realtime.revision() > 0) {
        this.items.reload();
        this.reports.reload();
      }
    });
  }

  protected readonly pins = computed<FloorPin[]>(() => {
    const found = this.items.hasValue() ? this.items.value() : [];
    const claims = this.reports.hasValue() ? this.reports.value() : [];
    return [
      ...found
        .filter((item) => item.mapX != null && item.mapY != null)
        .map((item) => ({
          x: item.mapX!,
          y: item.mapY!,
          label: `${item.trackingCode} · ${item.zoneName}`,
          kind: 'item' as const,
          href: `/items/${item.id}`,
        })),
      ...claims
        .filter((report) => report.mapX != null && report.mapY != null)
        .map((report) => ({
          x: report.mapX!,
          y: report.mapY!,
          label: `${report.guestName} · ${report.zoneName}`,
          kind: 'report' as const,
        })),
    ];
  });
}
