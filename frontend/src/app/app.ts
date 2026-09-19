import { Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { filter, map, startWith } from 'rxjs';
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
  private readonly router = inject(Router);

  protected readonly publicDesk = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map(() => this.isPublic(this.router.url)),
      startWith(this.isPublic(this.router.url)),
    ),
    { initialValue: this.isPublic(this.router.url) },
  );

  constructor() {
    effect(() => {
      if (this.auth.isAuthenticated() && !this.publicDesk()) {
        this.realtime.connect();
      } else {
        this.realtime.disconnect();
      }
    });
  }

  private isPublic(url: string) {
    return url.startsWith('/guest') || url.startsWith('/how-it-works');
  }
}
