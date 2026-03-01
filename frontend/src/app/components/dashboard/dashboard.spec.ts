import {ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {Router, RouterModule} from '@angular/router';

import {DashboardComponent} from './dashboard';
import {AuthService} from '../../services/auth.service';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {of} from 'rxjs';

describe('DashboardComponent', () => {
  let fixture: ComponentFixture<DashboardComponent>;
  let component: DashboardComponent;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', [
      'logout',
    ]);

    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        DashboardComponent,
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create Dashboard component', () => {
    expect(component).toBeTruthy();
  });

  it('should display title inside the page', () => {
    const title = fixture.debugElement.query(By.css('h1')).nativeElement;
    expect(title.textContent.trim()).toBe('Dashboard');
  });

  it('should render the patient list component', () => {
    const patientList = fixture.debugElement.query(By.css('app-patient-list'));
    expect(patientList).not.toBeNull();
  });

  it('should display "Logout" button', () => {
    const btn = fixture.debugElement.query(By.css('button[type="submit"]'))
    expect(btn).not.toBeNull();
    expect(btn.nativeElement.textContent.trim()).toBe('Logout');
  });

  it('should call "onSubmitLogoutForm()" function when "Logout" button is clicked', () => {
    const onLogoutSpy = spyOn(component, 'onSubmitLogoutForm');

    const btn = fixture.debugElement.query(By.css('button[type="submit"]'))
    btn.nativeElement.click();
    fixture.detectChanges()

    expect(onLogoutSpy).toHaveBeenCalledTimes(1);
  });

  it('should call "authService.logout" and redirect to "/login" within "onSubmitLogoutForm()" function', () => {
    authServiceSpy.logout.and.returnValue(of(void 0));
    const navigateSpy = spyOn(router, 'navigateByUrl');

    component.onSubmitLogoutForm();

    expect(authServiceSpy.logout).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith('/login');
  });
});
