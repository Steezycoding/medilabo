import {HttpHandlerFn, HttpRequest} from '@angular/common/http';
import {inject} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {catchError, switchMap, throwError} from 'rxjs';

export function AuthInterceptor(req: HttpRequest<any>, next: HttpHandlerFn) {
  const authService = inject(AuthService);

  const isAuthRequest = req.url.includes('/auth/');
  const isApiRequest  = req.url.includes('/api/');

  if (isAuthRequest) {
    const authReq = req.clone({ withCredentials: true });
    return next(authReq);
  }

  const cloned = req.clone({
    withCredentials: true,
  });

  return next(cloned).pipe(
    catchError(error => {
      console.log('HTTP Error intercepted:', error);
      const alreadyRetried = cloned.headers.has('X-Retry');

      if (error.status === 401 && isApiRequest && !alreadyRetried) {
        return authService.refreshToken().pipe(
          switchMap(success => {
            if (success) {
              const retryReq = cloned.clone({
                withCredentials: true,
                headers: cloned.headers.set('X-Retry', 'true')
              });
              return next(retryReq);
            } else {
              authService.logout();
              return throwError(() => error);
            }
          }),
          catchError(refreshError => {
            authService.logout();
            return throwError(() => refreshError);
          })
        );
      }

      return throwError(() => error);
    })
  );
}
