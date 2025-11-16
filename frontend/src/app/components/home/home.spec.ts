import {ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {Router, RouterModule} from '@angular/router';

import {HomeComponent} from './home';
import {AuthService} from '../../services/auth.service';

describe('HomeComponent', () => {
  let fixture: ComponentFixture<HomeComponent>;
  let component: HomeComponent;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', [
      'isAuthenticated',
    ]);

    authServiceSpy.isAuthenticated.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        HomeComponent,
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create Home component', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct title"', () => {
    const h1: HTMLElement = fixture.debugElement.query(By.css('h1')).nativeElement;
    expect(h1.textContent?.trim()).toBe('Welcome on MediLabo');
  });

  it('should display the explanatory text', () => {
    const p: HTMLElement = fixture.debugElement.query(By.css('p.lead')).nativeElement;
    expect(p.textContent)
      .toContain('You must login to reach the Diabetes Risk Evaluator dashboard');
  });

  it('should display a "Login" button redirectin to "/login"', () => {
    const linkDebug = fixture.debugElement.query(By.css('a.btn.btn-primary'));
    expect(linkDebug).not.toBeNull();

    const link: HTMLAnchorElement = linkDebug.nativeElement;
    expect(link.textContent?.trim()).toBe('Login');
    expect(link.getAttribute('href')).toBe('/login');
  });

  it('should not redirect to "/dashboard" if the user is not authenticated', () => {
    const navigateSpy = spyOn(router, 'navigateByUrl');

    expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should redirect to "/dashboard" if the user is authenticated', () => {
    authServiceSpy.isAuthenticated.and.returnValue(true);
    const navigateSpy = spyOn(router, 'navigateByUrl');

    const localFixture = TestBed.createComponent(HomeComponent);
    localFixture.detectChanges(); // ngOnInit()

    expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledOnceWith('/dashboard');
  });
});
