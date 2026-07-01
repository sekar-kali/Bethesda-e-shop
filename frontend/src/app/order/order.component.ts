import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { OrderResponse, OrderService } from '../core/order.service';

const STATUS_LABELS: Record<string, string> = {
  PENDING: 'En attente',
  CONFIRMED: 'Confirmée — stock disponible',
  OUT_OF_STOCK: 'Rupture de stock',
  PAID: 'Payée',
  CANCELLED: 'Annulée',
};

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order.component.html',
  styleUrl: './order.component.css',
})
export class OrderComponent implements OnInit, OnDestroy {
  order = signal<OrderResponse | null>(null);
  statusLabels = STATUS_LABELS;
  orderId!: number;

  constructor(private route: ActivatedRoute, private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderId = Number(this.route.snapshot.paramMap.get('id'));

    // Recupere l'etat initial via REST...
    this.orderService.get(this.orderId).subscribe((initial) => this.order.set(initial));

    // ...puis s'abonne au flux temps reel via WebSocket pour les mises a jour futures.
    this.orderService.trackOrder(this.orderId).subscribe((update) => this.order.set(update));
  }

  pay(): void {
    if (!this.order()) return;
    this.orderService.pay(this.orderId).subscribe();
  }

  ngOnDestroy(): void {
    this.orderService.disconnect();
  }
}
