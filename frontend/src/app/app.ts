import { Component, effect, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { Auth } from './auth/auth';
import { OperationsRealtime } from './core/operations-realtime';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly auth = inject(Auth);
  private readonly realtime = inject(OperationsRealtime);

  constructor() {
    effect(() => {
      if (this.auth.isAuthenticated()) {
        this.realtime.connect();
      } else {
        this.realtime.disconnect();
      }
    });
  }
}
