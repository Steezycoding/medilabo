import {HttpHandlerFn, HttpRequest} from '@angular/common/http';

export function AuthInterceptor(req: HttpRequest<any>, next: HttpHandlerFn) {
  const cloned = req.clone({
    withCredentials: true,
  });

  return next(cloned);
}
