import { Injectable, inject, signal } from '@angular/core';
import { Client, type IMessage } from '@stomp/stompjs';
import { environment } from '../../environments/environment';
import { Auth } from '../auth/auth';
import { OperationsNotice } from './operations-api';

@Injectable({ providedIn: 'root' })
export class OperationsRealtime {
  private readonly auth = inject(Auth);
  private client?: Client;
  private readonly latest = signal<OperationsNotice | null>(null);
  private readonly ticks = signal(0);
  private readonly live = signal(false);

  readonly event = this.latest.asReadonly();
  readonly revision = this.ticks.asReadonly();
  readonly connected = this.live.asReadonly();

  connect() {
    if (this.client || !this.auth.isAuthenticated()) {
      return;
    }
    const token = this.auth.accessToken();
    if (!token) {
      return;
    }
    const wsUrl = environment.apiUrl.replace(/^http/, 'ws') + '/ws';
    this.client = new Client({
      brokerURL: wsUrl,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 3000,
      onConnect: () => {
        this.live.set(true);
        this.client?.subscribe('/topic/operations', (message: IMessage) => this.onMessage(message));
      },
      onWebSocketClose: () => this.live.set(false),
      onStompError: () => this.live.set(false),
    });
    this.client.activate();
  }

  disconnect() {
    this.client?.deactivate();
    this.client = undefined;
    this.live.set(false);
  }

  private onMessage(message: IMessage) {
    if (!message.body) {
      return;
    }
    this.latest.set(JSON.parse(message.body) as OperationsNotice);
    this.ticks.update((value) => value + 1);
  }
}
