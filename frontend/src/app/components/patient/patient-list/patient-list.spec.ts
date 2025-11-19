import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, throwError} from 'rxjs';
import {PatientListComponent} from './patient-list';
import {PatientService} from '../../../services/patient.service';

describe('PatientList', () => {
  let fixture: ComponentFixture<PatientListComponent>;
  let component: PatientListComponent;
  let patientServiceSpy: jasmine.SpyObj<PatientService>;

  beforeEach(() => {
    patientServiceSpy = jasmine.createSpyObj('PatientService', ['getPatients']);

    TestBed.configureTestingModule({
      imports: [PatientListComponent],
      providers: [{ provide: PatientService, useValue: patientServiceSpy }]
    });

    fixture = TestBed.createComponent(PatientListComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    TestBed.resetTestingModule();
    (console.error as any).and?.callThrough?.();
  });

  it('should create PatientList component', () => {
    expect(component).toBeTruthy();
  });

  it('should load patients on component init', () => {
    const mockPatients = [
      { firstName: 'John', lastName: 'Doe', gender: 'M', birthDate: '1990-01-01', address: '1 Brookside St', phoneNumber: '100-222-3333' },
      { firstName: 'Jane', lastName: 'Smith', gender: 'F', birthDate: '2001-11-20', address: '3 Club Road', phoneNumber: '300-444-5555' }
    ];
    patientServiceSpy.getPatients.and.returnValue(of(mockPatients));

    fixture.detectChanges();

    expect(patientServiceSpy.getPatients).toHaveBeenCalled();
    expect((component as any).patients).toEqual(mockPatients);
  });

  it('should log error if service fails', () => {
    patientServiceSpy.getPatients.and.returnValue(throwError(() => new Error('fail')));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(patientServiceSpy.getPatients).toHaveBeenCalled();
    expect((component as any).patients).toEqual([]);
    expect(console.error).toHaveBeenCalledWith('Error fetching patient data');
  });
});
