import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class GuestGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (!this.authService.isLoggedIn) {
      return true;
    }
    // Already logged in — redirect to correct dashboard
    const role = this.authService.role;
    if (role === 'SELLER') this.router.navigate(['/seller']);
    else this.router.navigate(['/customer']);
    return false;
  }
}
