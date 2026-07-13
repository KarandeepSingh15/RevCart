import { Component, OnInit } from '@angular/core';
import { CartService } from '../../../services/cart.service';
import { CartItemResponse, CartResponse } from '../../../models/cart.models';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { OrderService } from '../../../services/order.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit {
  cart: CartResponse | null = null;
  loading = true;
  placingOrder = false;

  constructor(
    public cartService: CartService,
    private orderService: OrderService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cartService.cart$.subscribe(cart => {
      this.cart = cart;
      this.loading = false;
    });
    this.cartService.loadCart().subscribe({ error: () => (this.loading = false) });
  }

  updateQuantity(item: CartItemResponse, newQty: number): void {
    if (newQty < 1) return;
    this.cartService.updateItem(item.id, { quantity: newQty }).subscribe({
      error: () => this.snackBar.open('Could not update quantity', 'Close', { duration: 3000 })
    });
  }

  removeItem(item: CartItemResponse): void {
    this.cartService.removeItem(item.id).subscribe({
      error: () => this.snackBar.open('Could not remove item', 'Close', { duration: 3000 })
    });
  }

  clearCart(): void {
    this.cartService.clearCart().subscribe({
      next: () => this.snackBar.open('Cart cleared', '✓', { duration: 2000 }),
      error: () => this.snackBar.open('Could not clear cart', 'Close', { duration: 3000 })
    });
  }

  get total(): number {
    return this.cart?.items?.reduce((sum, i) => sum + i.productPrice * i.quantity, 0) ?? 0;
  }

  placeOrder(): void {
    if (!this.cart?.items?.length) return;
    this.placingOrder = true;

    const items = this.cart.items.map(i => ({ productId: i.productId, quantity: i.quantity }));
    this.orderService.createOrder({ items }).subscribe({
      next: order => {
        this.placingOrder = false;
        this.cartService.clearCart().subscribe();
        this.snackBar.open(`Order #${order.orderId} placed! Status: ${order.status}`, '✓', { duration: 4000 });
        this.router.navigate(['/customer/orders']);
      },
      error: () => {
        this.placingOrder = false;
        this.snackBar.open('Failed to place order. Please try again.', 'Close', { duration: 4000 });
      }
    });
  }
}
