export interface InventoryResponse {
  productId: number;
  availableQuantity: number;
  reservedQuantity: number;
}

export interface UpsertInventoryRequest {
  quantity: number;
}
