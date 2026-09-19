import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EMPTY } from 'rxjs';
import { catchError, switchMap } from 'rxjs';
import { GuestReport, LossReportStatus, OperationsApi } from '../core/operations-api';

@Component({
  selector: 'app-guest-receipt',
  imports: [RouterLink, DatePipe],
  templateUrl: './guest-receipt.html',
  styleUrl: './guest-receipt.scss',
})
export class GuestReceipt {
  private readonly api = inject(OperationsApi);
  private readonly route = inject(ActivatedRoute);

  protected readonly report = signal<GuestReport | null>(null);
  protected readonly missing = signal(false);

  constructor() {
    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const id = params.get('id');
          this.report.set(null);
          this.missing.set(false);
          if (!id) {
            this.missing.set(true);
            return EMPTY;
          }
          return this.api.guestReport(id).pipe(
            catchError(() => {
              this.missing.set(true);
              return EMPTY;
            }),
          );
        }),
        takeUntilDestroyed(),
      )
      .subscribe((value) => this.report.set(value));
  }

  protected explain(status: LossReportStatus): string {
    switch (status) {
      case 'OPEN':
        return 'The hotel has your report. A duty manager will review any suggested matches.';
      case 'MATCHED':
        return 'A possible match is on the staff inbox. Front desk will contact you if it holds.';
      case 'RESOLVED':
        return 'Staff accepted a match. Ask the front desk to collect the item.';
      case 'CLOSED':
        return 'This report is closed. File a new one if something else is missing.';
    }
  }
}
