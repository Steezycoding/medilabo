import {HttpHandlerFn, HttpRequest} from '@angular/common/http';

export function AuthInterceptor(req: HttpRequest<any>, next: HttpHandlerFn) {
  const token = sessionStorage.getItem('auth_basic');

  if (token) {
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Basic ${token}`)
    });

    return next(cloned);
  } else {
    return next(req);
  }
}
