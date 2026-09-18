import { DatePipe } from '@angular/common';
import { Component, inject, resource } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { OperationsApi } from '../core/operations-api';

@Component({
  selector: 'app-board',
  imports: [RouterLink, DatePipe],
  templateUrl: './board.html',
  styleUrl: './board.scss',
})
export class Board {
  private readonly api = inject(OperationsApi);

  protected readonly items = resource({
    loader: () => firstValueFrom(this.api.listItems()),
  });
  protected readonly reports = resource({
    loader: () => firstValueFrom(this.api.listReports()),
  });
}
