import {inject, Injectable, signal, WritableSignal} from '@angular/core';
import {HttpClient, HttpHeaders, HttpStatusCode} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError, map, Observable, of, throwError} from 'rxjs';
import {Credentials} from '../model/Credentials';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly AUTH_TOKEN_URL: string = `${environment.apiBaseUrl}${environment.authTokenUri}`;
  private readonly AUTH_LOGOUT_URL: string = `${environment.apiBaseUrl}${environment.authLogoutUri}`;

  private http = inject(HttpClient);

  isAuthenticated: WritableSignal<boolean> = signal<boolean>(false);

  login(credentials: Credentials): Observable<boolean> {
    const basicToken = btoa(`${credentials.username}:${credentials.password}`);
    const headers = new HttpHeaders({
      Authorization: `Basic ${basicToken}`,
    });

    return this.http
      .get(this.AUTH_TOKEN_URL, {
        headers,
        observe: 'response',
        withCredentials: true
      })
      .pipe(
        map(response => {
          const ok = response.status === HttpStatusCode.Ok;
          this.isAuthenticated.set(ok);
          return ok;
        }),
        catchError(err => {
          if (err.status === HttpStatusCode.Unauthorized || err.status === HttpStatusCode.Forbidden) {
            this.isAuthenticated.set(false);
            return of(false);
          }
          return throwError(() => err);
        })
    );
  }

  logout(): Observable<void> {
   return this.http
     .post<void>(this.AUTH_LOGOUT_URL, {},
      {
        withCredentials: true
      })
      .pipe(
        map(() => {
          this.isAuthenticated.set(false);
        })
      );
  }

  isLoggedIn(): boolean {
    return this.isAuthenticated();
  }
}
