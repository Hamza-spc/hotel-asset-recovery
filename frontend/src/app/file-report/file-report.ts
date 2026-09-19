import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OperationsApi } from '../core/operations-api';
import { FloorMap, FloorPin, MapPick } from '../floor-map/floor-map';

@Component({
  selector: 'app-file-report',
  imports: [FormsModule, RouterLink, FloorMap],
  templateUrl: './file-report.html',
  styleUrl: './file-report.scss',
})
export class FileReport {
  private readonly api = inject(OperationsApi);
  private readonly router = inject(Router);

  protected guestName = '';
  protected roomNumber = '';
  protected contact = '';
  protected description = '';
  protected zoneName = '';
  protected mapX: number | null = null;
  protected mapY: number | null = null;
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
      this.error.set('Tap the floor plan where the guest last saw the item.');
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    this.api
      .fileReport({
        guestName: this.guestName,
        roomNumber: this.roomNumber,
        contact: this.contact,
        description: this.description,
        zoneName: this.zoneName,
        mapX: String(this.mapX),
        mapY: String(this.mapY),
      })
      .subscribe({
        next: () => this.router.navigate(['/board']),
        error: (err) => {
          this.saving.set(false);
          this.error.set(err.error?.message ?? 'Could not file the loss report.');
        },
      });
  }
}
