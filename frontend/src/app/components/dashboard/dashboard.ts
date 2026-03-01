import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {PatientListComponent} from '../patient/patient-list/patient-list';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  imports: [
    PatientListComponent,
    FormsModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class DashboardComponent {
  private router = inject(Router);
  private authService = inject(AuthService);

  onSubmitLogoutForm() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigateByUrl('/login');
      }
    });
  }
}
