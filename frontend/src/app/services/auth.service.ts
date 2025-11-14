import {Injectable, signal} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly STORAGE_KEY = 'auth_user';
  isAuthenticated = signal<boolean>(!!localStorage.getItem(this.STORAGE_KEY));

  login(credentials: { username: any; password: any; }): boolean {
    const ok = credentials.username === 'user' && credentials.password === 'user';
    if (ok) {
      localStorage.setItem(this.STORAGE_KEY, 'user');
      this.isAuthenticated.set(true);
    }
    return ok;
  }

  logout(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.isAuthenticated.set(false);
  }
}
