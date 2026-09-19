import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OperationsApi } from '../core/operations-api';
import { FloorMap, FloorPin, MapPick } from '../floor-map/floor-map';

@Component({
  selector: 'app-guest-portal',
  imports: [FormsModule, RouterLink, FloorMap],
  templateUrl: './guest-portal.html',
  styleUrl: './guest-portal.scss',
})
export class GuestPortal {
  private readonly api = inject(OperationsApi);
  private readonly router = inject(Router);

  protected guestName = '';
  protected roomNumber = '';
  protected contact = '';
  protected description = '';
  protected zoneName = '';
  protected mapX: number | null = null;
  protected mapY: number | null = null;
  protected lookupId = '';
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly pins = signal<FloorPin[]>([]);

  onPin(pick: MapPick) {
    this.mapX = pick.x;
    this.mapY = pick.y;
    this.zoneName = pick.zoneName;
    this.pins.set([{ x: pick.x, y: pick.y, label: pick.zoneName, kind: 'pick' }]);
  }

  submit() {
    if (this.mapX == null || this.mapY == null || !this.zoneName) {
      this.error.set('Tap the floor plan where you last saw the item.');
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    this.api
      .fileGuestReport({
        guestName: this.guestName,
        roomNumber: this.roomNumber,
        contact: this.contact,
        description: this.description,
        zoneName: this.zoneName,
        mapX: this.mapX,
        mapY: this.mapY,
      })
      .subscribe({
        next: (report) => this.router.navigate(['/guest', report.id]),
        error: (err) => {
          this.saving.set(false);
          this.error.set(err.error?.message ?? 'Could not file the report. Ask the front desk.');
        },
      });
  }

  lookup() {
    const id = this.lookupId.trim();
    if (!id) {
      return;
    }
    this.router.navigate(['/guest', id]);
  }
}
