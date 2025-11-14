import {TestBed} from '@angular/core/testing';
import {RouterOutlet} from '@angular/router';
import {By} from '@angular/platform-browser';
import {App} from './app';

describe('App component Test Suite', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
    }).compileComponents();
  });

  it('should create the App component', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render a main container', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const mainEl = fixture.debugElement.query(By.css('main.container'));
    expect(mainEl).toBeTruthy();
  });

  it('should contain a router-outlet inside main', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const mainEl = fixture.debugElement.query(By.css('main.container'));
    expect(mainEl).toBeTruthy();

    const routerOutlet = mainEl.query(By.directive(RouterOutlet));
    expect(routerOutlet).toBeTruthy();
  });
});
