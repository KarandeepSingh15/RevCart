import { Component, OnInit, ViewChild } from '@angular/core';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { ProductService } from '../../../services/product.service';
import { CartService } from '../../../services/cart.service';
import { ProductResponse, CATEGORY_LABELS } from '../../../models/product.models';
import { PageResponse } from '../../../models/page.models';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html'
})
export class ProductListComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  pageData: PageResponse<ProductResponse> | null = null;
  loading = true;

  // Pagination state
  currentPage = 0;
  pageSize = 10;

  // Filter state
  searchTerm = '';
  selectedCategory = '';
  sortBy = 'id';
  direction = 'asc';

  // Track all loaded items for client-side category/search filtering
  allProducts: ProductResponse[] = [];

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.productService.getActiveProducts(this.currentPage, this.pageSize, this.sortBy, this.direction)
      .subscribe({
        next: page => {
          this.pageData = page;
          this.allProducts = page.content;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          this.snackBar.open('Failed to load products', 'Close', { duration: 3000 });
        }
      });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.searchTerm = '';
    this.selectedCategory = '';
    this.load();
  }

  onSortChange(): void {
    this.currentPage = 0;
    this.load();
  }

  // Client-side filter on the current page's content only
  get filteredProducts(): ProductResponse[] {
    return this.allProducts.filter(p => {
      const matchSearch = !this.searchTerm ||
        p.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (p.description ?? '').toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchCat = !this.selectedCategory || p.category === this.selectedCategory;
      return matchSearch && matchCat;
    });
  }

  get categories(): string[] {
    return [...new Set(this.allProducts.map(p => p.category))];
  }

  clearFilter(): void {
    this.searchTerm = '';
    this.selectedCategory = '';
  }

  addToCart(product: ProductResponse): void {
    this.cartService.addItem({ productId: product.id, quantity: 1 }).subscribe({
      next: () => this.snackBar.open(`${product.name} added to cart`, '✓', { duration: 2000 }),
      error: () => this.snackBar.open('Could not add to cart', 'Close', { duration: 3000 })
    });
  }

  getCategoryLabel(cat: string): string {
    return (CATEGORY_LABELS as any)[cat] ?? cat;
  }
}
