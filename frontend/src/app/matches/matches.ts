import { DatePipe, PercentPipe } from '@angular/common';
import { Component, effect, inject, resource, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { MatchSuggestion, OperationsApi } from '../core/operations-api';
import { OperationsRealtime } from '../core/operations-realtime';

@Component({
  selector: 'app-matches',
  imports: [RouterLink, DatePipe, PercentPipe],
  templateUrl: './matches.html',
  styleUrl: './matches.scss',
})
export class Matches {
  private readonly api = inject(OperationsApi);
  private readonly realtime = inject(OperationsRealtime);
  protected readonly error = signal<string | null>(null);

  protected readonly matches = resource({
    loader: () => firstValueFrom(this.api.listMatches()),
  });

  constructor() {
    this.realtime.connect();
    effect(() => {
      if (this.realtime.revision() > 0) {
        this.matches.reload();
      }
    });
  }

  accept(match: MatchSuggestion) {
    this.api.acceptMatch(match.id).subscribe({
      next: () => this.matches.reload(),
      error: (err) => this.error.set(err.error?.message ?? 'Could not accept the match.'),
    });
  }

  reject(match: MatchSuggestion) {
    this.api.rejectMatch(match.id).subscribe({
      next: () => this.matches.reload(),
      error: (err) => this.error.set(err.error?.message ?? 'Could not reject the match.'),
    });
  }
}
