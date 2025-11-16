import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class DashboardComponent {
  private router = inject(Router);
  private auth = inject(AuthService);

  onLogout() {
    this.auth.logout();
    this.router.navigateByUrl('/')
  }
}
