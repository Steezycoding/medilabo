import {inject, Injectable, signal} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError, map, Observable, of, throwError} from 'rxjs';

interface Credentials {
  username: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly STORAGE_KEY_AUTH = 'auth_user';
  private readonly STORAGE_KEY_BASIC = 'auth_basic';
  private readonly AUTH_URL = `${environment.apiBaseUrl}/auth/check`;

  private http = inject(HttpClient);

  isAuthenticated = signal<boolean>(this.hasExistingSession());

  login(credentials: Credentials): Observable<boolean> {
    const basicToken = btoa(`${credentials.username}:${credentials.password}`);
    const headers = new HttpHeaders({
      Authorization: `Basic ${basicToken}`,
    });

    return this.http.get(this.AUTH_URL, { headers, observe: 'response'}).pipe(
      map(response => {
        const principal = (response.body as any)?.principal;
        const matches = principal === credentials.username;

        if (response.status === 200) {
          if (!matches) {
            throw new Error('Principal does not match credentials');
          }
          sessionStorage.setItem(this.STORAGE_KEY_AUTH, credentials.username);
          sessionStorage.setItem(this.STORAGE_KEY_BASIC, basicToken);
          this.isAuthenticated.set(true);
          return true;
        }

        this.isAuthenticated.set(false);
        return false;
      }),
      catchError(err => {
        if (err.status === 401) {
          this.cleanSessionStorage()
          this.isAuthenticated.set(false);
          return of(false);
        }
        return throwError(() => err);
      })
    );
  }

  logout(): void {
    this.cleanSessionStorage()
    this.isAuthenticated.set(false);
  }

  private cleanSessionStorage(): void {
    sessionStorage.removeItem(this.STORAGE_KEY_AUTH);
    sessionStorage.removeItem(this.STORAGE_KEY_BASIC);
  }

  private hasExistingSession(): boolean {
    return !!sessionStorage.getItem(this.STORAGE_KEY_AUTH) && !!sessionStorage.getItem(this.STORAGE_KEY_BASIC);
  }
}
