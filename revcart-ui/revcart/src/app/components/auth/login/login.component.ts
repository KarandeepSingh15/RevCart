import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  loading = false;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;

    const { email, password } = this.form.value;
    this.authService.login({ email: email!, password: password! }).subscribe({
      next: () => this.redirectAfterLogin(),
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const msg = err.status === 401 ? 'Invalid email or password.' : 'Login failed. Please try again.';
        this.snackBar.open(msg, 'Close', { duration: 4000 });
      }
    });
  }

  private redirectAfterLogin(): void {
    const role = this.authService.role;
    if (role === 'SELLER') this.router.navigate(['/seller']);
    else this.router.navigate(['/customer']);
  }
}
