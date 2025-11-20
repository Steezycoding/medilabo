import {Routes} from '@angular/router';
import {authGuard} from './security/auth.guard';
import {HomeComponent} from './components/home/home';
import {DashboardComponent} from './components/dashboard/dashboard';
import {LoginFormComponent} from './components/login/login';
import {PatientDetailsComponent} from './components/patient/patient-details/patient-details';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'login', component: LoginFormComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'patient/:id', component: PatientDetailsComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
