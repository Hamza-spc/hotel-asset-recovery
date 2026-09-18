import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OperationsApi } from '../core/operations-api';

@Component({
  selector: 'app-file-report',
  imports: [FormsModule, RouterLink],
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
  protected zoneName = 'Lobby';
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);

  submit() {
    this.saving.set(true);
    this.error.set(null);
    this.api
      .fileReport({
        guestName: this.guestName,
        roomNumber: this.roomNumber,
        contact: this.contact,
        description: this.description,
        zoneName: this.zoneName,
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
