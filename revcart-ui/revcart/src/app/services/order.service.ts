import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateOrderRequest, OrderResponse } from '../models/order.models';
import { PageResponse } from '../models/page.models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly BASE = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) {}

  createOrder(req: CreateOrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(this.BASE, req);
  }

  getOrder(id: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.BASE}/${id}`);
  }

  getMyOrders(page = 0, size = 10, sortBy = 'id', direction = 'desc'): Observable<PageResponse<OrderResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('direction', direction);
    return this.http.get<PageResponse<OrderResponse>>(`${this.BASE}/my-orders`, { params });
  }
}
