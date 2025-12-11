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


  it('checkToken success sets authenticated true', (done: DoneFn) => {
    service.checkToken().subscribe({
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

    const req = httpMock.expectOne(r => r.url === (service as any)['AUTH_CHECK_TOKEN_URL'] && r.method === 'GET');
    req.flush({}, { status: 200, statusText: 'OK' });
  });

  it('checkToken 401 sets authenticated false and marks checked', (done: DoneFn) => {
    service.isAuthenticated.set(true);

    service.checkToken().subscribe({
      next: result => {
        expect(result).toBeFalse();
        expect(service.isAuthenticated()).toBeFalse();
        expect(service['hasCheckedAuth']).toBeTrue();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === (service as any)['AUTH_CHECK_TOKEN_URL'] && r.method === 'GET');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });

  it('checkToken rethrows unexpected errors', (done: DoneFn) => {
    service.checkToken().subscribe({
      next: () => { fail('expected error'); done(); },
      error: err => {
        expect(err).toBeTruthy();
        expect((err as any).status).toBe(500);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === (service as any)['AUTH_CHECK_TOKEN_URL'] && r.method === 'GET');
    req.flush({}, { status: 500, statusText: 'Server Error' });
  });

  it('refreshToken success returns true', (done: DoneFn) => {
    service.refreshToken().subscribe({
      next: result => {
        expect(result).toBeTrue();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === (service as any)['AUTH_REFRESH_TOKEN_URL'] && r.method === 'POST');
    req.flush({}, { status: 200, statusText: 'OK' });
  });

  it('refreshToken 401 resets authentication and marks checked', (done: DoneFn) => {
    service.isAuthenticated.set(true);

    service.refreshToken().subscribe({
      next: result => {
        expect(result).toBeFalse();
        expect(service.isAuthenticated()).toBeFalse();
        expect(service['hasCheckedAuth']).toBeTrue();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === (service as any)['AUTH_REFRESH_TOKEN_URL'] && r.method === 'POST');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });

  it('refreshToken rethrows unexpected errors', (done: DoneFn) => {
    service.refreshToken().subscribe({
      next: () => { fail('expected error'); done(); },
      error: err => {
        expect(err).toBeTruthy();
        expect((err as any).status).toBe(500);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === (service as any)['AUTH_REFRESH_TOKEN_URL'] && r.method === 'POST');
    req.flush({}, { status: 500, statusText: 'Server Error' });
  });

  it('isLoggedIn returns cached state when already checked', (done: DoneFn) => {
    service.isAuthenticated.set(true);
    service['hasCheckedAuth'] = true;

    service.isLoggedIn().subscribe({
      next: result => {
        expect(result).toBeTrue();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });
  });


  it('isLoggedIn relies on checkToken when not cached and does not refresh on success', (done: DoneFn) => {
    service.isLoggedIn().subscribe({
      next: result => {
        expect(result).toBeTrue();
        expect(service.isAuthenticated()).toBeTrue();
        httpMock.expectNone(r => r.url === (service as any)['AUTH_REFRESH_TOKEN_URL']);
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const req = httpMock.expectOne(r => r.url === (service as any)['AUTH_CHECK_TOKEN_URL'] && r.method === 'GET');
    req.flush({}, { status: 200, statusText: 'OK' });
  });

  it('isLoggedIn falls back to refreshToken when checkToken fails', (done: DoneFn) => {
    service.isLoggedIn().subscribe({
      next: result => {
        expect(result).toBeTrue();
        done();
      },
      error: err => {
        fail(err);
        done();
      }
    });

    const checkReq = httpMock.expectOne(r => r.url === (service as any)['AUTH_CHECK_TOKEN_URL'] && r.method === 'GET');
    checkReq.flush({}, { status: 401, statusText: 'Unauthorized' });

    const refreshReq = httpMock.expectOne(r => r.url === (service as any)['AUTH_REFRESH_TOKEN_URL'] && r.method === 'POST');
    refreshReq.flush({}, { status: 200, statusText: 'OK' });
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
