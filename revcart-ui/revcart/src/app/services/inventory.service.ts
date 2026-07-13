import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InventoryResponse, UpsertInventoryRequest } from '../models/inventory.models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private readonly BASE = `${environment.apiUrl}/inventory`;

  constructor(private http: HttpClient) {}

  getInventory(productId: number): Observable<InventoryResponse> {
    return this.http.get<InventoryResponse>(`${this.BASE}/product/${productId}`);
  }

  setInventory(productId: number, req: UpsertInventoryRequest): Observable<InventoryResponse> {
    return this.http.put<InventoryResponse>(`${this.BASE}/product/${productId}`, req);
  }

  restockInventory(productId: number, req: UpsertInventoryRequest): Observable<InventoryResponse> {
    return this.http.patch<InventoryResponse>(`${this.BASE}/product/${productId}/restock`, req);
  }
}
