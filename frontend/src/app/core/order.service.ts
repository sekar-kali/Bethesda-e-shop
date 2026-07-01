import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { CartLine } from './cart.service';

export interface OrderResponse {
  id: number;
  customerEmail: string;
  status: 'PENDING' | 'CONFIRMED' | 'OUT_OF_STOCK' | 'PAID' | 'CANCELLED';
  totalAmount: number;
  createdAt: string;
  items: { productId: number; productName: string; quantity: number; unitPrice: number }[];
}

const ORDER_API_URL = 'http://localhost:8082/api/orders';
const WS_URL = 'http://localhost:8082/ws-orders';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private stompClient?: Client;

  constructor(private http: HttpClient) {}

  create(customerEmail: string, items: CartLine[]): Observable<OrderResponse> {
    const payload = {
      customerEmail,
      items: items.map((i) => ({
        productId: i.productId,
        productName: i.productName,
        quantity: i.quantity,
        unitPrice: i.unitPrice,
      })),
    };
    return this.http.post<OrderResponse>(ORDER_API_URL, payload);
  }

  get(orderId: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${ORDER_API_URL}/${orderId}`);
  }


  pay(orderId: number): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${ORDER_API_URL}/${orderId}/pay`, {});
  }

  /**
   * S'abonne aux mises a jour temps reel de la commande via WebSocket (STOMP/SockJS).
   * Chaque changement de statut publie cote backend (order.events) est repercute
   * immediatement ici, sans polling HTTP.
   */
  trackOrder(orderId: number): Observable<OrderResponse> {
    const subject = new Subject<OrderResponse>();

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(WS_URL) as unknown as WebSocket,
      reconnectDelay: 5000,
      onConnect: () => {
        this.stompClient?.subscribe(`/topic/orders/${orderId}`, (message: IMessage) => {
          subject.next(JSON.parse(message.body));
        });
      },
    });
    this.stompClient.activate();

    return subject.asObservable();
  }

  disconnect(): void {
    this.stompClient?.deactivate();
  }
}
