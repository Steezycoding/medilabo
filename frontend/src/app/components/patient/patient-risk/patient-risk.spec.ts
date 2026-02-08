import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PatientRiskComponent} from './patient-risk';

describe('PatientRisk', () => {
  let component: PatientRiskComponent;
  let fixture: ComponentFixture<PatientRiskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientRiskComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientRiskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
