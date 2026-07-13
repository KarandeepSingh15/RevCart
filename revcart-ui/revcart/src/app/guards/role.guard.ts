import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRole: string = route.data['role'];
    if (!this.authService.isLoggedIn) {
      this.router.navigate(['/login']);
      return false;
    }
    if (this.authService.role !== expectedRole) {
      // Redirect to the correct dashboard
      const role = this.authService.role;
      if (role === 'CUSTOMER') this.router.navigate(['/customer']);
      else if (role === 'SELLER') this.router.navigate(['/seller']);
      else this.router.navigate(['/login']);
      return false;
    }
    return true;
  }
}
