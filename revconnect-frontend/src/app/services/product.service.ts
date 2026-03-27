/*
10. **product.service.ts** - Product Management Service

**Purpose:** Handles business products

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `createProduct(product)` | Add new product | POST `/api/products` |
| `getProductsByBusiness(businessId)` | Get business products | GET `/api/products/business/{businessId}` |
| `updateProduct(id, product)` | Update product | PUT `/api/products/{id}` |
| `deleteProduct(id)` | Delete product | DELETE `/api/products/{id}` |

**Used By:** ProfileComponent (for business users)
*/



import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Product {
  id?: number;
  businessUserId?: number;
  productName: string;
  description: string;
  price: number;
  productImagePath?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  addProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  getProductsByBusiness(businessUserId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/business/${businessUserId}`);
  }

  deleteProduct(productId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${productId}`);
  }
}
