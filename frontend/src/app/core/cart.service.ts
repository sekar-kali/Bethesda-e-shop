import { Injectable, signal, computed } from '@angular/core';
import { Product } from './catalog.service';

export interface CartLine {
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly _lines = signal<CartLine[]>([]);

  readonly lines = this._lines.asReadonly();

  readonly total = computed(() =>
    this._lines().reduce((sum, l) => sum + l.unitPrice * l.quantity, 0)
  );

  readonly itemCount = computed(() =>
    this._lines().reduce((count, l) => count + l.quantity, 0)
  );

  add(product: Product, quantity = 1): void {
    const existing = this._lines().find((l) => l.productId === product.id);
    if (existing) {
      this._lines.update((lines) =>
        lines.map((l) =>
          l.productId === product.id ? { ...l, quantity: l.quantity + quantity } : l
        )
      );
    } else {
      this._lines.update((lines) => [
        ...lines,
        { productId: product.id, productName: product.name, unitPrice: product.price, quantity },
      ]);
    }
  }

  remove(productId: number): void {
    this._lines.update((lines) => lines.filter((l) => l.productId !== productId));
  }

  clear(): void {
    this._lines.set([]);
  }
}
