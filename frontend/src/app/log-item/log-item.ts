import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ItemCategory, OperationsApi } from '../core/operations-api';

@Component({
  selector: 'app-log-item',
  imports: [FormsModule, RouterLink],
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
  protected zoneName = 'Lobby';
  protected photo: File | null = null;
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);

  onFile(event: Event) {
    const input = event.target as HTMLInputElement;
    this.photo = input.files?.[0] ?? null;
  }

  submit() {
    this.saving.set(true);
    this.error.set(null);
    const payload = new FormData();
    payload.append('description', this.description);
    payload.append('category', this.category);
    payload.append('zoneName', this.zoneName);
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
