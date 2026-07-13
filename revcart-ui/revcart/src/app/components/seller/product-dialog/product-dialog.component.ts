import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ProductService } from '../../../services/product.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductResponse, PRODUCT_CATEGORIES, CATEGORY_LABELS } from '../../../models/product.models';

export interface ProductDialogData {
  mode: 'create' | 'edit';
  product?: ProductResponse;
}

@Component({
  selector: 'app-product-dialog',
  templateUrl: './product-dialog.component.html'
})
export class ProductDialogComponent implements OnInit {
  categories = PRODUCT_CATEGORIES;
  categoryLabels = CATEGORY_LABELS;
  loading = false;

  form = this.fb.group({
    name:        ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    price:       [null as number | null, [Validators.required, Validators.min(0.01)]],
    category:    ['', Validators.required]
  });

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<ProductDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProductDialogData
  ) {}

  ngOnInit(): void {
    if (this.data.mode === 'edit' && this.data.product) {
      const p = this.data.product;
      this.form.patchValue({
        name: p.name,
        description: p.description,
        price: p.price,
        category: p.category
      });
    }
  }

  get title(): string {
    return this.data.mode === 'create' ? 'Add New Product' : 'Edit Product';
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;

    const { name, description, price, category } = this.form.value;
    const req = { name: name!, description: description ?? '', price: price!, category: category! };

    const request$ = this.data.mode === 'create'
      ? this.productService.createProduct(req)
      : this.productService.updateProduct(this.data.product!.id, req);

    request$.subscribe({
      next: () => {
        this.loading = false;
        const msg = this.data.mode === 'create' ? 'Product created' : 'Product updated';
        this.snackBar.open(msg, '✓', { duration: 2000 });
        this.dialogRef.close(true);
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Failed to save product', 'Close', { duration: 3000 });
      }
    });
  }

  getCategoryLabel(cat: string): string {
    return (CATEGORY_LABELS as any)[cat] ?? cat;
  }
}
