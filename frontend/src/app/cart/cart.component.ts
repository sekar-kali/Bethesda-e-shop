import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../core/cart.service';
import { OrderService } from '../core/order.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css',
})
export class CartComponent {
  email = '';
  submitting = false;
  errorMessage: string | null = null;

  constructor(
    public cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {}

  checkout(): void {
    if (!this.email || this.cartService.lines().length === 0) {
      return;
    }
    this.submitting = true;
    this.errorMessage = null;

    this.orderService.create(this.email, this.cartService.lines()).subscribe({
      next: (order) => {
        this.cartService.clear();
        this.submitting = false;
        this.router.navigate(['/order', order.id]);
      },
      error: () => {
        this.submitting = false;
        this.errorMessage =
          "Impossible de créer la commande. Vérifiez que order-service tourne sur le port 8082.";
      },
    });
  }
}
