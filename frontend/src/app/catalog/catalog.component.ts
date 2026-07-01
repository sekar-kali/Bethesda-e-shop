import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CatalogService, Product } from '../core/catalog.service';
import { CartService } from '../core/cart.service';

const CATEGORY_LABELS: Record<string, string> = {
  EPICES: 'Épices',
  THE: 'Thés',
  TEXTILE: 'Textiles',
  ARTISANAT: 'Artisanat',
  EPICERIE: 'Épicerie',
};

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css',
})
export class CatalogComponent implements OnInit {
  products = signal<Product[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  categoryLabels = CATEGORY_LABELS;

  constructor(private catalogService: CatalogService, private cartService: CartService) {}

  ngOnInit(): void {
    this.catalogService.list().subscribe({
      next: (products) => {
        this.products.set(products);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(
          "Impossible de charger le catalogue. Vérifiez que catalog-service tourne sur le port 8081."
        );
        this.loading.set(false);
      },
    });
  }

  addToCart(product: Product): void {
    this.cartService.add(product, 1);
  }

  categories(): string[] {
    return [...new Set(this.products().map((p) => p.category))];
  }

  productsFor(category: string): Product[] {
    return this.products().filter((p) => p.category === category);
  }
}
