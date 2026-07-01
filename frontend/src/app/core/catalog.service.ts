import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  id: number;
  name: string;
  description: string;
  category: 'EPICES' | 'THE' | 'TEXTILE' | 'ARTISANAT' | 'EPICERIE';
  origin: string;
  price: number;
  stockQuantity: number;
}

export const CATALOG_API_URL = 'http://localhost:8081/api/products';

@Injectable({ providedIn: 'root' })
export class CatalogService {
  constructor(private http: HttpClient) {}

  list(category?: string): Observable<Product[]> {
    const url = category ? `${CATALOG_API_URL}?category=${category}` : CATALOG_API_URL;
    return this.http.get<Product[]>(url);
  }
}
