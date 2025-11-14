import {TestBed} from '@angular/core/testing';
import {AuthService} from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  const STORAGE_KEY = 'auth_user';

  beforeEach(() => {
    localStorage.removeItem(STORAGE_KEY);
    TestBed.configureTestingModule({ providers: [AuthService] });
    service = TestBed.inject(AuthService);
  });

  afterEach(() => {
    localStorage.removeItem(STORAGE_KEY);
  });

  it('should be created and isAuthenticated false when no storage', () => {
    expect(service).toBeTruthy();
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should initialize isAuthenticated true when storage present', () => {
    localStorage.setItem(STORAGE_KEY, 'user');
    service = new AuthService();
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('login with correct credentials sets storage and returns true', () => {
    const result = service.login({ username: 'user', password: 'user' });
    expect(result).toBeTrue();
    expect(localStorage.getItem(STORAGE_KEY)).toBe('user');
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('login with wrong credentials returns false and does not set storage', () => {
    const result = service.login({ username: 'bad', password: 'bad' });
    expect(result).toBeFalse();
    expect(localStorage.getItem(STORAGE_KEY)).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('logout clears storage and sets isAuthenticated false', () => {
    // préparer un état connecté
    localStorage.setItem(STORAGE_KEY, 'user');
    service = new AuthService();
    expect(service.isAuthenticated()).toBeTrue();

    service.logout();

    expect(localStorage.getItem(STORAGE_KEY)).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
  });
});
