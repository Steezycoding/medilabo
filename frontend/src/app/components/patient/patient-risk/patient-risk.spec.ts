import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PatientRiskComponent} from './patient-risk';
import {RiskLevel} from '../../../model/RiskLevel';
import {RiskEvaluatorService} from '../../../services/risk-evaluator.service';
import {of, throwError} from 'rxjs';
import {Patient} from '../../../model/Patient';

describe('PatientRisk', () => {
  let fixture: ComponentFixture<PatientRiskComponent>;
  let component: PatientRiskComponent;
  let riskServiceSpy: jasmine.SpyObj<RiskEvaluatorService>;

  const mockPatient = {
    id: '2',
    firstName: 'John',
    lastName: 'Doe',
    birthDate: '1990-06-24',
    gender: 'M',
    address: '2 High St',
    phoneNumber: '200-333-4444'
  } as Patient;

  beforeEach(async () => {
    riskServiceSpy = jasmine.createSpyObj('RiskEvaluatorService', ['getRiskLevel']);
    riskServiceSpy.getRiskLevel.and.returnValue(of({ riskLevel: RiskLevel.NONE }));

    await TestBed.configureTestingModule({
      imports: [PatientRiskComponent],
      providers: [{ provide: RiskEvaluatorService, useValue: riskServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(PatientRiskComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should not call service when patient is null', () => {
    // patient not set
    fixture.detectChanges();

    expect(riskServiceSpy.getRiskLevel).not.toHaveBeenCalled();
    expect(component.riskLevel).toBeNull();
    expect(component['loading']).toBeFalse();
  });

  it('should fetch risk when patient provided and update state on success', () => {
    component.patient = mockPatient;
    riskServiceSpy.getRiskLevel.and.returnValue(of({ riskLevel: RiskLevel.BORDERLINE }));

    fixture.detectChanges();

    expect(riskServiceSpy.getRiskLevel).toHaveBeenCalledWith(2);
    expect(component.riskLevel).toBe(RiskLevel.BORDERLINE);
    expect(component['loading']).toBeFalse();
  });

  it('should set loading true while fetching and clear riskLevel until resolved', () => {
    component.patient = mockPatient;
    riskServiceSpy.getRiskLevel.and.returnValue(of({ riskLevel: RiskLevel.NONE }));

    component.fetchRisk();

    expect(component['loading']).toBeFalse();
    expect(component.riskLevel).toBe(RiskLevel.NONE);
  });

  it('should log specific message when 404 error occurs', () => {
    component.patient = mockPatient;
    const err = { status: 404, message: 'Not Found' };
    riskServiceSpy.getRiskLevel.and.returnValue(throwError(() => err));
    const consoleErrorSpy = spyOn(console, 'error');

    component.fetchRisk();

    expect(riskServiceSpy.getRiskLevel).toHaveBeenCalledWith(2);
    expect(consoleErrorSpy).toHaveBeenCalledWith('Failed to retrieved patient', err);
    expect(component['loading']).toBeFalse();
  });

  it('should log generic error when non-404 error occurs', () => {
    component.patient = mockPatient;
    const err = new Error('Server error');
    riskServiceSpy.getRiskLevel.and.returnValue(throwError(() => err));
    const consoleErrorSpy = spyOn(console, 'error');

    component.fetchRisk();

    expect(riskServiceSpy.getRiskLevel).toHaveBeenCalledWith(2);
    expect(consoleErrorSpy).toHaveBeenCalledWith('Failed to retrieved risk level', err);
  });

  it('alertClass should map levels to bootstrap classes', () => {
    expect(component.alertClass(RiskLevel.NONE)).toBe('alert-success');
    expect(component.alertClass(RiskLevel.BORDERLINE)).toBe('alert-warning');
    expect(component.alertClass(RiskLevel.IN_DANGER)).toBe('alert-danger');
    expect(component.alertClass(RiskLevel.EARLY_ONSET)).toBe('alert-danger');
    expect(component.alertClass(null)).toBe('alert-secondary');
  });

  it('riskLabel should map levels to readable labels', () => {
    expect(component.riskLabel(RiskLevel.NONE)).toBe('None');
    expect(component.riskLabel(RiskLevel.BORDERLINE)).toBe('Borderline');
    expect(component.riskLabel(RiskLevel.IN_DANGER)).toBe('Danger');
    expect(component.riskLabel(RiskLevel.EARLY_ONSET)).toBe('Early onset');
    expect(component.riskLabel(null)).toBe('—');
  });
});
