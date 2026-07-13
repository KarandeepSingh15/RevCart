import { Component, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { ProductResponse, CATEGORY_LABELS } from '../../../models/product.models';
import { PageResponse } from '../../../models/page.models';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductDialogComponent } from '../product-dialog/product-dialog.component';

@Component({
  selector: 'app-seller-products',
  templateUrl: './seller-products.component.html'
})
export class SellerProductsComponent implements OnInit {
  pageData: PageResponse<ProductResponse> | null = null;
  loading = true;
  displayedColumns = ['name', 'category', 'price', 'status', 'actions'];

  currentPage = 0;
  pageSize = 10;
  sortBy = 'id';
  direction = 'desc';

  constructor(
    private productService: ProductService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.productService.getMyProducts(this.currentPage, this.pageSize, this.sortBy, this.direction)
      .subscribe({
        next: page => { this.pageData = page; this.loading = false; },
        error: () => {
          this.loading = false;
          this.snackBar.open('Failed to load products', 'Close', { duration: 3000 });
        }
      });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.load();
  }

  onSortChange(): void {
    this.currentPage = 0;
    this.load();
  }

  openCreate(): void {
    const ref = this.dialog.open(ProductDialogComponent, {
      width: '520px', data: { mode: 'create' }
    });
    ref.afterClosed().subscribe(result => { if (result) { this.currentPage = 0; this.load(); } });
  }

  openEdit(product: ProductResponse): void {
    const ref = this.dialog.open(ProductDialogComponent, {
      width: '520px', data: { mode: 'edit', product }
    });
    ref.afterClosed().subscribe(result => { if (result) this.load(); });
  }

  toggleStatus(product: ProductResponse): void {
    const newStatus = product.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    this.productService.updateStatus(product.id, { status: newStatus }).subscribe({
      next: () => {
        this.snackBar.open(`Product set to ${newStatus}`, '✓', { duration: 2000 });
        this.load();
      },
      error: () => this.snackBar.open('Failed to update status', 'Close', { duration: 3000 })
    });
  }

  activateProduct(product: ProductResponse): void {
    this.productService.updateStatus(product.id, { status: 'ACTIVE' }).subscribe({
      next: () => {
        this.snackBar.open('Product activated', '✓', { duration: 2000 });
        this.load();
      },
      error: () => this.snackBar.open('Failed to activate', 'Close', { duration: 3000 })
    });
  }

  getCategoryLabel(cat: string): string {
    return (CATEGORY_LABELS as any)[cat] ?? cat;
  }

  manageInventory(product: ProductResponse): void {
    this.router.navigate(['/seller/inventory', product.id], {
      state: { productName: product.name, productStatus: product.status }
    });
  }

  // Stats computed from the current page (approximate unless all pages loaded)
  get stats() {
    const items = this.pageData?.content ?? [];
    return {
      total:    this.pageData?.totalElements ?? 0,
      active:   items.filter(p => p.status === 'ACTIVE').length,
      draft:    items.filter(p => p.status === 'DRAFT').length,
      inactive: items.filter(p => p.status === 'INACTIVE').length
    };
  }
}
