import {Routes} from '@angular/router';
import {authGuard} from './security/auth.guard';
import {HomeComponent} from './components/home/home';
import {DashboardComponent} from './components/dashboard/dashboard';
import {LoginFormComponent} from './components/login/login';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'login', component: LoginFormComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
