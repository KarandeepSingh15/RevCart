import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreateProductRequest,
  ProductResponse,
  UpdateProductRequest,
  UpdateProductStatusRequest
} from '../models/product.models';
import { PageResponse } from '../models/page.models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly BASE = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  getActiveProducts(page = 0, size = 10, sortBy = 'id', direction = 'asc'): Observable<PageResponse<ProductResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('direction', direction);
    return this.http.get<PageResponse<ProductResponse>>(this.BASE, { params });
  }

  getProduct(id: number): Observable<ProductResponse> {
    return this.http.get<ProductResponse>(`${this.BASE}/${id}`);
  }

  getMyProducts(page = 0, size = 10, sortBy = 'id', direction = 'desc'): Observable<PageResponse<ProductResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('direction', direction);
    return this.http.get<PageResponse<ProductResponse>>(`${this.BASE}/my-products`, { params });
  }

  createProduct(req: CreateProductRequest): Observable<ProductResponse> {
    return this.http.post<ProductResponse>(this.BASE, req);
  }

  updateProduct(id: number, req: UpdateProductRequest): Observable<ProductResponse> {
    return this.http.put<ProductResponse>(`${this.BASE}/${id}`, req);
  }

  updateStatus(id: number, req: UpdateProductStatusRequest): Observable<ProductResponse> {
    return this.http.patch<ProductResponse>(`${this.BASE}/${id}/status`, req);
  }
}
