import {ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {ActivatedRoute, convertToParamMap, Router, RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';

import {LoginFormComponent} from './login';
import {AuthService} from '../../services/auth.service';
import {of, throwError} from 'rxjs';

describe('LoginForm component Test Suite', () => {
  let fixture: ComponentFixture<LoginFormComponent>;
  let component: LoginFormComponent;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  const createActivatedRouteMock = (params: Record<string, string> = {}) => ({
    snapshot: {
      queryParamMap: convertToParamMap(params),
    },
  });

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [
        FormsModule,
        RouterModule.forRoot([]),
        LoginFormComponent,
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: createActivatedRouteMock(),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginFormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  function setInputValue(selector: string, value: string) {
    const input = fixture.debugElement.query(By.css(selector))
      .nativeElement as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
  }

  function submitForm() {
    const formDebug = fixture.debugElement.query(By.css('form'));
    formDebug.triggerEventHandler('ngSubmit', {});
  }

  it('should create Login component', () => {
    expect(component).toBeTruthy();
  });

  it('should not display error message on component init', () => {
    const alert = fixture.debugElement.query(By.css('.alert.alert-danger'));
    expect(component.error).toBeFalse();
    expect(alert).toBeNull();
  });

  it('should deactivate "Sign In" button if form is invalid', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    const button = fixture.debugElement.query(By.css('button[type="submit"]'))
      .nativeElement as HTMLButtonElement;

    expect(button.disabled).toBeTrue();
  });

  it('should activate "Sign In" button if form is valid', async () => {
    setInputValue('input[name="username"]', 'user');
    setInputValue('input[name="password"]', 'user');

    fixture.detectChanges();

    const button = fixture.debugElement.query(By.css('button[type="submit"]'))
      .nativeElement as HTMLButtonElement;

    expect(button.disabled).toBeFalse();
  });

  it('should display error message if auth failed', () => {
    authServiceSpy.login.and.returnValue(of(false));

    setInputValue('input[name="username"]', 'wrong');
    setInputValue('input[name="password"]', 'wrong');

    fixture.detectChanges();

    submitForm();
    fixture.detectChanges();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      username: 'wrong',
      password: 'wrong',
    });
    expect(component.error).toBeTrue();

    const alert = fixture.debugElement.query(By.css('.alert.alert-danger'));
    expect(alert).not.toBeNull();
    expect(alert.nativeElement.textContent.trim())
      .toContain('Username and/or password are incorrect');
  });

  it('should not navigate if auth failed', () => {
    authServiceSpy.login.and.returnValue(of(false));
    const navigateSpy = spyOn(router, 'navigateByUrl');

    setInputValue('input[name="username"]', 'wrong');
    setInputValue('input[name="password"]', 'wrong');

    fixture.detectChanges();

    submitForm();
    fixture.detectChanges();

    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should authenticate and redirect to "/dashboard" if "returnUrl" is absent', () => {
    authServiceSpy.login.and.returnValue(of(true));
    const navigateSpy = spyOn(router, 'navigateByUrl');

    setInputValue('input[name="username"]', 'user');
    setInputValue('input[name="password"]', 'user');

    fixture.detectChanges();

    submitForm();
    fixture.detectChanges();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      username: 'user',
      password: 'user',
    });
    expect(component.error).toBeFalse();
    expect(navigateSpy).toHaveBeenCalledOnceWith('/dashboard');
  });

  it('should redirect to "returnUrl" if present within query params', async () => {
    const activatedRoute = TestBed.inject(ActivatedRoute) as any;
    activatedRoute.snapshot.queryParamMap = convertToParamMap({
      returnUrl: '/dashboard',
    });

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    authServiceSpy.login.and.returnValue(of(true));

    fixture.detectChanges();

    const navigateSpy = spyOn(router, 'navigateByUrl');

    setInputValue('input[name="username"]', 'user');
    setInputValue('input[name="password"]', 'user');

    fixture.detectChanges();

    submitForm();
    fixture.detectChanges();

    expect(authServiceSpy.login).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledOnceWith('/dashboard');
  });

  it('should set error to true when auth service errors and not navigate', () => {
    authServiceSpy.login.and.returnValue(throwError(() => new Error('network')));
    const navigateSpy = spyOn(router, 'navigateByUrl');

    setInputValue('input[name="username"]', 'user');
    setInputValue('input[name="password"]', 'user');

    fixture.detectChanges();

    submitForm();
    fixture.detectChanges();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      username: 'user',
      password: 'user',
    });
    expect(component.error).toBeTrue();
    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
