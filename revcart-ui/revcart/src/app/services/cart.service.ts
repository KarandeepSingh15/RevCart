import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AddCartItemRequest, CartResponse, UpdateCartItemRequest } from '../models/cart.models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly BASE = `${environment.apiUrl}/cart`;

  private cartSubject = new BehaviorSubject<CartResponse | null>(null);
  cart$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient) {}

  get cartItemCount(): number {
    return this.cartSubject.value?.items?.length ?? 0;
  }

  get cartTotal(): number {
    return this.cartSubject.value?.items?.reduce(
      (sum, item) => sum + item.productPrice * item.quantity, 0
    ) ?? 0;
  }

  loadCart(): Observable<CartResponse> {
    return this.http.get<CartResponse>(this.BASE).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  addItem(req: AddCartItemRequest): Observable<CartResponse> {
    return this.http.post<CartResponse>(`${this.BASE}/items`, req).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  updateItem(itemId: number, req: UpdateCartItemRequest): Observable<CartResponse> {
    return this.http.patch<CartResponse>(`${this.BASE}/items/${itemId}`, req).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  removeItem(itemId: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE}/items/${itemId}`).pipe(
      tap(() => this.loadCart().subscribe())
    );
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.BASE).pipe(
      tap(() => this.cartSubject.next(null))
    );
  }

  clearLocal(): void {
    this.cartSubject.next(null);
  }
}
