import { Component, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { OrderService } from '../../../services/order.service';
import { OrderResponse } from '../../../models/order.models';
import { PageResponse } from '../../../models/page.models';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html'
})
export class OrdersComponent implements OnInit {
  pageData: PageResponse<OrderResponse> | null = null;
  loading = true;
  expandedOrderId: number | null = null;

  currentPage = 0;
  pageSize = 10;

  constructor(
    private orderService: OrderService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.orderService.getMyOrders(this.currentPage, this.pageSize).subscribe({
      next: page => {
        this.pageData = page;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Failed to load orders', 'Close', { duration: 3000 });
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.expandedOrderId = null;
    this.load();
  }

  toggleExpand(orderId: number): void {
    this.expandedOrderId = this.expandedOrderId === orderId ? null : orderId;
  }

  getStatusClass(status: string): string {
    return `status-${status.toLowerCase()}`;
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'CONFIRMED': return 'check_circle';
      case 'CANCELLED': return 'cancel';
      default:          return 'hourglass_top';
    }
  }
}
