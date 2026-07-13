import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-shell',
  templateUrl: './shell.component.html'
})
export class ShellComponent implements OnInit {
  constructor(
    public authService: AuthService,
    public cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.role === 'CUSTOMER') {
      this.cartService.loadCart().subscribe();
    }
  }

  get isCustomer(): boolean {
    return this.authService.role === 'CUSTOMER';
  }

  get isSeller(): boolean {
    return this.authService.role === 'SELLER';
  }

  get userName(): string {
    const user = this.authService.currentUser;
    return user?.sub ?? 'User';
  }

  logout(): void {
    this.cartService.clearLocal();
    this.authService.logout();
  }
}
