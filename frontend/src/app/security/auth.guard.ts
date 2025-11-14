import {inject} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {CanActivateFn, Router, UrlTree} from '@angular/router';

export const authGuard: CanActivateFn = (route, state): boolean | UrlTree => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.isAuthenticated()
    ? true
    : router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};
