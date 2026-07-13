import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName:  ['', Validators.required],
    email:     ['', [Validators.required, Validators.email]],
    password:  ['', [Validators.required, Validators.minLength(6)]],
    role:      ['CUSTOMER', Validators.required]
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

    const { firstName, lastName, email, password, role } = this.form.value;
    const req = { firstName: firstName!, lastName: lastName!, email: email!, password: password! };
    const register$ = role === 'SELLER'
      ? this.authService.registerSeller(req)
      : this.authService.registerCustomer(req);

    register$.subscribe({
      next: () => {
        const route = role === 'SELLER' ? '/seller' : '/customer';
        this.router.navigate([route]);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const msg = err.status === 409
          ? 'An account with this email already exists.'
          : 'Registration failed. Please try again.';
        this.snackBar.open(msg, 'Close', { duration: 4000 });
      }
    });
  }
}
