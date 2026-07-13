export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignupRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
}

export interface JwtPayload {
  sub: string;       // email
  userId: number;
  role: string;      // CUSTOMER | SELLER | ADMIN
  ver: number;
  exp: number;
  iat: number;
}
