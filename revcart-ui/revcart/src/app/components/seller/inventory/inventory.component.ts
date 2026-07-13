import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { InventoryService } from '../../../services/inventory.service';
import { ProductService } from '../../../services/product.service';
import { InventoryResponse } from '../../../models/inventory.models';
import { ProductResponse } from '../../../models/product.models';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html'
})
export class InventoryComponent implements OnInit {
  productId!: number;
  product: ProductResponse | null = null;
  inventory: InventoryResponse | null = null;

  loadingProduct = true;
  loadingInventory = true;
  saving = false;

  // Forms
  setForm  = this.fb.group({ quantity: [0, [Validators.required, Validators.min(0)]] });
  addForm  = this.fb.group({ quantity: [1, [Validators.required, Validators.min(1)]] });

  // Passed via router state for display before API resolves
  productName: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private inventoryService: InventoryService,
    private productService: ProductService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Grab name hint from navigation state (set by seller-products)
    const nav = this.router.getCurrentNavigation();
    const state = nav?.extras?.state ?? history.state;
    this.productName = state?.['productName'] ?? null;

    this.productId = Number(this.route.snapshot.paramMap.get('productId'));

    this.productService.getProduct(this.productId).subscribe({
      next: p => { this.product = p; this.loadingProduct = false; },
      error: () => { this.loadingProduct = false; }
    });

    this.loadInventory();
  }

  loadInventory(): void {
    this.loadingInventory = true;
    this.inventoryService.getInventory(this.productId).subscribe({
      next: inv => { this.inventory = inv; this.loadingInventory = false; },
      error: () => { this.loadingInventory = false; }
    });
  }

  setStock(): void {
    if (this.setForm.invalid) { this.setForm.markAllAsTouched(); return; }
    this.saving = true;
    const qty = this.setForm.value.quantity!;
    this.inventoryService.setInventory(this.productId, { quantity: qty }).subscribe({
      next: inv => {
        this.inventory = inv;
        this.saving = false;
        this.setForm.reset({ quantity: 0 });
        this.snackBar.open(`Stock set to ${inv.availableQuantity}`, '✓', { duration: 2500 });
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Failed to set stock', 'Close', { duration: 3000 });
      }
    });
  }

  addStock(): void {
    if (this.addForm.invalid) { this.addForm.markAllAsTouched(); return; }
    this.saving = true;
    const qty = this.addForm.value.quantity!;
    this.inventoryService.restockInventory(this.productId, { quantity: qty }).subscribe({
      next: inv => {
        this.inventory = inv;
        this.saving = false;
        this.addForm.reset({ quantity: 1 });
        this.snackBar.open(`Added ${qty} units. New stock: ${inv.availableQuantity}`, '✓', { duration: 2500 });
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Failed to restock', 'Close', { duration: 3000 });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/seller/products']);
  }

  get stockStatus(): 'none' | 'low' | 'ok' {
    if (!this.inventory) return 'none';
    if (this.inventory.availableQuantity === 0) return 'none';
    if (this.inventory.availableQuantity <= 5)  return 'low';
    return 'ok';
  }

  get stockStatusColor(): string {
    switch (this.stockStatus) {
      case 'none': return '#f44336';
      case 'low':  return '#ff9800';
      default:     return '#4caf50';
    }
  }

  get stockStatusLabel(): string {
    switch (this.stockStatus) {
      case 'none': return 'Out of Stock';
      case 'low':  return 'Low Stock';
      default:     return 'In Stock';
    }
  }
}
