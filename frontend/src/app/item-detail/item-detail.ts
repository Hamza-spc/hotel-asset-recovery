import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Auth } from '../auth/auth';
import { FoundItem, OperationsApi } from '../core/operations-api';
import { FloorMap, FloorPin } from '../floor-map/floor-map';

@Component({
  selector: 'app-item-detail',
  imports: [RouterLink, DatePipe, FormsModule, FloorMap],
  templateUrl: './item-detail.html',
  styleUrl: './item-detail.scss',
})
export class ItemDetail {
  private readonly api = inject(OperationsApi);
  private readonly route = inject(ActivatedRoute);
  protected readonly auth = inject(Auth);

  private readonly id = this.route.snapshot.paramMap.get('id') ?? '';
  protected readonly item = signal<FoundItem | null>(null);
  protected storageLocation = 'Shelf A1';
  protected readonly error = signal<string | null>(null);
  protected readonly photoUrl = signal<string | null>(null);
  protected readonly pins = signal<FloorPin[]>([]);

  constructor() {
    this.refresh();
  }

  protected readonly canStore = computed(() => {
    const current = this.item();
    return (
      !!current &&
      current.status === 'LOGGED' &&
      (this.auth.hasRole('HOUSEKEEPING') || this.auth.hasRole('DUTY_MANAGER'))
    );
  });
  protected readonly canClaim = computed(() => {
    const current = this.item();
    return (
      !!current &&
      (current.status === 'STORED' || current.status === 'UNCLAIMED') &&
      (this.auth.hasRole('FRONT_DESK') || this.auth.hasRole('DUTY_MANAGER'))
    );
  });
  protected readonly canReclaim = computed(
    () => this.item()?.status === 'CLAIM_PENDING' && this.auth.hasRole('DUTY_MANAGER'),
  );
  protected readonly canUnclaim = computed(
    () => this.item()?.status === 'STORED' && this.auth.hasRole('DUTY_MANAGER'),
  );
  protected readonly canDispose = computed(
    () => this.item()?.status === 'UNCLAIMED' && this.auth.hasRole('DUTY_MANAGER'),
  );

  store() {
    this.api.storeItem(this.id, this.storageLocation).subscribe({
      next: (item) => this.apply(item),
      error: (err) => this.error.set(err.error?.message ?? 'Action failed.'),
    });
  }

  openClaim() {
    this.api.openClaim(this.id).subscribe({
      next: (item) => this.apply(item),
      error: (err) => this.error.set(err.error?.message ?? 'Action failed.'),
    });
  }

  reclaim() {
    this.api.reclaimItem(this.id).subscribe({
      next: (item) => this.apply(item),
      error: (err) => this.error.set(err.error?.message ?? 'Action failed.'),
    });
  }

  unclaimed() {
    this.api.markUnclaimed(this.id).subscribe({
      next: (item) => this.apply(item),
      error: (err) => this.error.set(err.error?.message ?? 'Action failed.'),
    });
  }

  dispose() {
    this.api.disposeItem(this.id).subscribe({
      next: (item) => this.apply(item),
      error: (err) => this.error.set(err.error?.message ?? 'Action failed.'),
    });
  }

  private refresh() {
    this.api.getItem(this.id).subscribe({
      next: (item) => this.apply(item),
      error: (err) => this.error.set(err.error?.message ?? 'Item not found.'),
    });
  }

  private apply(item: FoundItem) {
    this.error.set(null);
    this.item.set(item);
    this.pins.set(
      item.mapX != null && item.mapY != null
        ? [{ x: item.mapX, y: item.mapY, label: item.zoneName, kind: 'item' }]
        : [],
    );
    if (!item.hasPhoto) {
      this.photoUrl.set(null);
      return;
    }
    this.api.itemPhoto(item.id).subscribe({
      next: (blob) => this.photoUrl.set(URL.createObjectURL(blob)),
      error: () => this.photoUrl.set(null),
    });
  }
}
