import {TestBed} from '@angular/core/testing';
import {AuthService} from './auth.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created and isAuthenticated false when no storage', () => {
    expect(service).toBeTruthy();
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('login with correct credentials returns true', (done: DoneFn) => {
    const credentials = { username: 'user', password: 'user' };
    const expectedBasic = btoa('user:user');

    service.login(credentials).subscribe({
      next: result => {
        expect(result).toBeTrue();
        expect(service.isAuthenticated()).toBeTrue();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === service['AUTH_TOKEN_URL'] && r.method === 'GET');
    expect(req.request.headers.get('Authorization')).toBe(`Basic ${expectedBasic}`);

    req.flush({ authenticated: true, principal: 'user' }, { status: 200, statusText: 'OK' });
  });

  it('login with wrong credentials returns false', (done: DoneFn) => {
    const credentials = { username: 'bad', password: 'bad' };

    service.login(credentials).subscribe({
      next: result => {
        expect(result).toBeFalse();
        expect(service.isAuthenticated()).toBeFalse();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === service['AUTH_TOKEN_URL'] && r.method === 'GET');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });

  it('logout sets isAuthenticated false', (done: DoneFn) => {
    service.isAuthenticated.set(true);

    service.logout().subscribe({
      next: () => {
        expect(service.isAuthenticated()).toBeFalse();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === (service as any)['AUTH_LOGOUT_URL'] && r.method === 'POST');
    req.flush({}, { status: 200, statusText: 'OK' });
  });

  it('login should rethrow non-401 backend errors (catchError returns throwError)', (done: DoneFn) => {
    const credentials = { username: 'user', password: 'pass' };

    service.login(credentials).subscribe({
      next: () => { fail('expected error'); done(); },
      error: err => {
        expect(err).toBeTruthy();
        expect((err as any).status).toBe(500);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === (service as any)['AUTH_LOGOUT_URL'] && r.method === 'GET');
    req.flush({}, { status: 500, statusText: 'Server Error' });
  });
});
