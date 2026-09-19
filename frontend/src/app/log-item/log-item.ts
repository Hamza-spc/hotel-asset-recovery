import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ItemCategory, OperationsApi } from '../core/operations-api';
import { FloorMap, FloorPin, MapPick } from '../floor-map/floor-map';

@Component({
  selector: 'app-log-item',
  imports: [FormsModule, RouterLink, FloorMap],
  templateUrl: './log-item.html',
  styleUrl: './log-item.scss',
})
export class LogItem {
  private readonly api = inject(OperationsApi);
  private readonly router = inject(Router);

  protected readonly categories: ItemCategory[] = [
    'WALLET',
    'PHONE',
    'KEYS',
    'JEWELRY',
    'BAG',
    'CLOTHING',
    'DOCUMENT',
    'OTHER',
  ];
  protected description = '';
  protected category: ItemCategory = 'WALLET';
  protected zoneName = '';
  protected mapX: number | null = null;
  protected mapY: number | null = null;
  protected photo: File | null = null;
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly pins = signal<FloorPin[]>([]);

  onFile(event: Event) {
    const input = event.target as HTMLInputElement;
    this.photo = input.files?.[0] ?? null;
  }

  onPin(pick: MapPick) {
    this.mapX = pick.x;
    this.mapY = pick.y;
    this.zoneName = pick.zoneName;
    this.pins.set([{ x: pick.x, y: pick.y, label: pick.zoneName, kind: 'pick' }]);
  }

  submit() {
    if (this.mapX == null || this.mapY == null || !this.zoneName) {
      this.error.set('Tap the floor plan to mark where you found it.');
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    const payload = new FormData();
    payload.append('description', this.description);
    payload.append('category', this.category);
    payload.append('zoneName', this.zoneName);
    payload.append('mapX', String(this.mapX));
    payload.append('mapY', String(this.mapY));
    if (this.photo) {
      payload.append('photo', this.photo);
    }
    this.api.logItem(payload).subscribe({
      next: (item) => this.router.navigate(['/items', item.id]),
      error: (err) => {
        this.saving.set(false);
        this.error.set(err.error?.message ?? 'Could not log the item.');
      },
    });
  }
}
