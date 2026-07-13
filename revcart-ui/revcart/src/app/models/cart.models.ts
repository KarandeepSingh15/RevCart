export interface CartItemResponse {
  id: number;
  productId: number;
  productName: string;
  productPrice: number;
  quantity: number;
}

export interface CartResponse {
  cartId: number;
  userId: number;
  items: CartItemResponse[];
}

export interface AddCartItemRequest {
  productId: number;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}
