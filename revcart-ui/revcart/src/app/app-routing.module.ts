import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { ShellComponent } from './components/shell/shell.component';
import { ProductListComponent } from './components/customer/product-list/product-list.component';
import { CartComponent } from './components/customer/cart/cart.component';
import { OrdersComponent } from './components/customer/orders/orders.component';
import { SellerProductsComponent } from './components/seller/seller-products/seller-products.component';
import { InventoryComponent } from './components/seller/inventory/inventory.component';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';
import { GuestGuard } from './guards/guest.guard';

const routes: Routes = [
  { path: 'login',    component: LoginComponent,    canActivate: [GuestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [GuestGuard] },

  // Customer shell
  {
    path: 'customer',
    component: ShellComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'CUSTOMER' },
    children: [
      { path: '',         redirectTo: 'products', pathMatch: 'full' },
      { path: 'products', component: ProductListComponent },
      { path: 'cart',     component: CartComponent },
      { path: 'orders',   component: OrdersComponent }
    ]
  },

  // Seller shell
  {
    path: 'seller',
    component: ShellComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'SELLER' },
    children: [
      { path: '',                       redirectTo: 'products', pathMatch: 'full' },
      { path: 'products',               component: SellerProductsComponent },
      { path: 'inventory/:productId',   component: InventoryComponent }
    ]
  },

  // Default redirect
  { path: '',   redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
