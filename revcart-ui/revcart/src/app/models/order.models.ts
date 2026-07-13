export type OrderStatus = 'CREATED' | 'CONFIRMED' | 'CANCELLED';

export interface OrderItemResponse {
  productId: number;
  productName?: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface OrderResponse {
  orderId: number;
  customerId: number;
  totalAmount: number;
  status: OrderStatus;
  items: OrderItemResponse[];
  cancellationReason?: string;
}

export interface OrderItemRequest {
  productId: number;
  quantity: number;
}

export interface CreateOrderRequest {
  items: OrderItemRequest[];
}
