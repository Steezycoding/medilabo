import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HomeComponent} from './home';
import {By} from '@angular/platform-browser';

describe('Home component Test Suite', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create Home component', () => {
    expect(component).toBeTruthy();
  });

  it('should render a section with class "hero"', () => {
    const sectionEl = fixture.debugElement.query(By.css('section.hero'));
    expect(sectionEl).toBeTruthy();
  });

  it('should display the correct title', () => {
    const h1El = fixture.debugElement.query(By.css('section.hero h1'));
    expect(h1El).toBeTruthy();
    expect(h1El.nativeElement.textContent.trim()).toBe('Welcome on MediLabo');
  });

  it('should display the correct subtitle/lead text', () => {
    const pEl = fixture.debugElement.query(By.css('section.hero p.lead'));
    expect(pEl).toBeTruthy();
    expect(pEl.nativeElement.textContent.trim())
      .toBe(`You must login to reach the Diabete's Risk Evalutor dashboard`);
  });

  it('should have a login button with bootstrap classes', () => {
    const btnEl = fixture.debugElement.query(By.css('section.hero button'));
    expect(btnEl).toBeTruthy();

    const classList = btnEl.nativeElement.classList;
    expect(classList).toContain('btn');
    expect(classList).toContain('btn-primary');
    expect(classList).toContain('btn-md');
  });

  it('should have modal trigger attributes on the login button', () => {
    const btnEl = fixture.debugElement.query(By.css('section.hero button'));
    expect(btnEl).toBeTruthy();

    const toggleAttr = btnEl.nativeElement.getAttribute('data-bs-toggle');
    const targetAttr = btnEl.nativeElement.getAttribute('data-bs-target');

    expect(toggleAttr).toBe('modal');
    expect(targetAttr).toBe('#');
  });
});
