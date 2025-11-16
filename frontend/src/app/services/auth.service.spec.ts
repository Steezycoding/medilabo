import {TestBed} from '@angular/core/testing';
import {AuthService} from './auth.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const STORAGE_KEY_AUTH = 'auth_user';
  const STORAGE_KEY_BASIC = 'auth_basic';

  beforeEach(() => {
    sessionStorage.removeItem(STORAGE_KEY_AUTH);
    sessionStorage.removeItem(STORAGE_KEY_BASIC);

    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    sessionStorage.removeItem(STORAGE_KEY_AUTH);
    sessionStorage.removeItem(STORAGE_KEY_BASIC);
  });

  it('should be created and isAuthenticated false when no storage', () => {
    expect(service).toBeTruthy();
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('login with correct credentials sets storage and returns true', (done: DoneFn) => {
    const credentials = { username: 'user', password: 'user' };
    const expectedBasic = btoa('user:user');

    service.login(credentials).subscribe({
      next: result => {
        expect(result).toBeTrue();
        expect(sessionStorage.getItem(STORAGE_KEY_AUTH)).toBe('user');
        expect(sessionStorage.getItem(STORAGE_KEY_BASIC)).toBe(expectedBasic);
        expect(service.isAuthenticated()).toBeTrue();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url.endsWith('/auth/check') && r.method === 'GET');
    expect(req.request.headers.get('Authorization')).toBe(`Basic ${expectedBasic}`);

    req.flush({ authenticated: true, principal: 'user' }, { status: 200, statusText: 'OK' });
  });

  it('login with wrong credentials returns false and does not set storage', (done: DoneFn) => {
    const credentials = { username: 'bad', password: 'bad' };

    service.login(credentials).subscribe({
      next: result => {
        expect(result).toBeFalse();
        expect(sessionStorage.getItem(STORAGE_KEY_AUTH)).toBeNull();
        expect(sessionStorage.getItem(STORAGE_KEY_BASIC)).toBeNull();
        expect(service.isAuthenticated()).toBeFalse();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url.endsWith('/auth/check') && r.method === 'GET');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });

  it('logout clears storage and sets isAuthenticated false', () => {
    sessionStorage.setItem(STORAGE_KEY_AUTH, 'user');
    sessionStorage.setItem(STORAGE_KEY_BASIC, btoa('user:user'));
    service.isAuthenticated.set(true);

    service.logout();

    expect(sessionStorage.getItem(STORAGE_KEY_AUTH)).toBeNull();
    expect(sessionStorage.getItem(STORAGE_KEY_BASIC)).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
  });
});
