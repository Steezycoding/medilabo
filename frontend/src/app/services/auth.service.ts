import {inject, Injectable, signal, WritableSignal} from '@angular/core';
import {HttpClient, HttpHeaders, HttpStatusCode} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError, firstValueFrom, map, Observable, of, switchMap, throwError} from 'rxjs';
import {Credentials} from '../model/Credentials';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly AUTH_TOKEN_URL: string = `${environment.apiBaseUrl}${environment.authTokenUri}`;
  private readonly AUTH_CHECK_TOKEN_URL: string = `${environment.apiBaseUrl}${environment.authCheckUri}`;
  private readonly AUTH_REFRESH_TOKEN_URL: string = `${environment.apiBaseUrl}${environment.authRefreshUri}`;
  private readonly AUTH_LOGOUT_URL: string = `${environment.apiBaseUrl}${environment.authLogoutUri}`;

  private http = inject(HttpClient);

  isAuthenticated: WritableSignal<boolean> = signal<boolean>(false);

  private hasCheckedAuth = false;

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
          this.hasCheckedAuth = true;
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

  checkToken(): Observable<boolean> {
    return this.http.get(this.AUTH_CHECK_TOKEN_URL, {
      withCredentials: true
    }).pipe(
      map(() => {
        this.isAuthenticated.set(true);
        return true;
      }),
      catchError(err => {
        if (err.status === 401) {
          this.isAuthenticated.set(false);
          this.hasCheckedAuth = true;
          return of(false);
        }
        return throwError(() => err);
      })
    );
  }

  refreshToken(): Observable<boolean> {
    console.log('Refreshing auth token');
    return this.http.post(this.AUTH_REFRESH_TOKEN_URL, {}, {
      withCredentials: true
    }).pipe(
      map(() => true),
      catchError(err => {
        if (err.status === 401) {
          this.isAuthenticated.set(false);
          this.hasCheckedAuth = true;
          return of(false);
        }
        return throwError(() => err);
      })
    );
  }

  logout(): Observable<void> {
    console.log('Logging out user');
    return this.http
     .post<void>(this.AUTH_LOGOUT_URL, {},
      {
        withCredentials: true
      })
      .pipe(
        map(() => {
          this.isAuthenticated.set(false);
          this.hasCheckedAuth = true;
        })
      );
  }

  isLoggedIn(): Observable<boolean> {
    if (this.hasCheckedAuth) {
      return of(this.isAuthenticated());
    }

    return this.checkToken().pipe(
      switchMap(isValid => {
        if (isValid) {
          return of(true);
        }
        return this.refreshToken();
      })
    );
  }
}
