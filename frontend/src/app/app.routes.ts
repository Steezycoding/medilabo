import {Routes} from '@angular/router';
import {HomeComponent} from './components/home/home';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: '**', redirectTo: '' }
];
