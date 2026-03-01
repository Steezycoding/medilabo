import {inject} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {CanActivateFn, Router, UrlTree} from '@angular/router';
import {map, Observable} from 'rxjs';

export const authGuard: CanActivateFn = (route, state): Observable<boolean | UrlTree>  => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.isLoggedIn().pipe(
    map(isAuth => {
      if (isAuth) {
        return true;
      }
      return router.createUrlTree(
        ['/login'],
        { queryParams: { returnUrl: state.url } }
      );
    })
  );
};
