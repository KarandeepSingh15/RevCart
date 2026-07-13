import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, JwtPayload, LoginRequest, SignupRequest } from '../models/auth.models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'revcart_token';
  private readonly BASE = `${environment.apiUrl}/auth`;

  private currentUserSubject = new BehaviorSubject<JwtPayload | null>(this.parseStoredToken());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Periodically check token expiry (every 30 seconds)
    setInterval(() => this.checkExpiry(), 30_000);
  }

  get currentUser(): JwtPayload | null {
    return this.currentUserSubject.value;
  }

  get isLoggedIn(): boolean {
    return !!this.currentUser && !this.isExpired(this.currentUser);
  }

  get role(): string | null {
    return this.currentUser?.role ?? null;
  }

  get token(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.BASE}/login`, req).pipe(
      tap(res => this.handleToken(res.token))
    );
  }

  registerCustomer(req: SignupRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.BASE}/register/customer`, req).pipe(
      tap(res => this.handleToken(res.token))
    );
  }

  registerSeller(req: SignupRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.BASE}/register/seller`, req).pipe(
      tap(res => this.handleToken(res.token))
    );
  }

  logout(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  private handleToken(token: string): void {
    sessionStorage.setItem(this.TOKEN_KEY, token);
    const payload = this.decodeToken(token);
    this.currentUserSubject.next(payload);
  }

  private parseStoredToken(): JwtPayload | null {
    const token = sessionStorage.getItem(this.TOKEN_KEY);
    if (!token) return null;
    const payload = this.decodeToken(token);
    if (!payload || this.isExpired(payload)) {
      sessionStorage.removeItem(this.TOKEN_KEY);
      return null;
    }
    return payload;
  }

  private decodeToken(token: string): JwtPayload | null {
    try {
      const base64 = token.split('.')[1];
      const json = atob(base64.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(json) as JwtPayload;
    } catch {
      return null;
    }
  }

  private isExpired(payload: JwtPayload): boolean {
    return Date.now() / 1000 > payload.exp;
  }

  private checkExpiry(): void {
    if (this.currentUser && this.isExpired(this.currentUser)) {
      this.logout();
    }
  }
}
