import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Credentials} from '../../model/Credentials';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class LoginFormComponent {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);

  error = false;
  credentials: Credentials = { username: '', password: '' };

  onSubmitLoginForm(): void {
    this.error = false;

    this.authService.login(this.credentials).subscribe({
      next: success => {
        if (success) {
          const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || '/dashboard';
          this.router.navigateByUrl(returnUrl);
        } else {
          this.error = true;
        }
      },
      error: () => {
        this.error = true;
      }
    });
  }
}
