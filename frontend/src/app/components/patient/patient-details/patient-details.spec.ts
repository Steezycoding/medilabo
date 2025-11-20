import {ComponentFixture, TestBed} from '@angular/core/testing';
import {PatientDetailsComponent} from './patient-details';
import {PatientService} from '../../../services/patient.service';
import {ActivatedRoute, convertToParamMap} from '@angular/router';
import {Patient} from '../../../model/Patient';
import {of, throwError} from 'rxjs';
import {By} from '@angular/platform-browser';

describe('PatientDetails', () => {
  let component: PatientDetailsComponent;
  let fixture: ComponentFixture<PatientDetailsComponent>;
  let patientServiceSpy: jasmine.SpyObj<PatientService>;

  const mockPatient: Patient = {
    id: '1',
    firstName: 'John',
    lastName: 'Doe',
    birthDate: '1990-01-01',
    gender: 'M',
    phoneNumber: '123-4567-890',
    address: '123 Main St'
  } as Patient;

  beforeEach(async () => {
    patientServiceSpy = jasmine.createSpyObj('PatientService', ['getPatientById', 'updatePatient']);
    patientServiceSpy.getPatientById.and.returnValue(of(mockPatient));

    await TestBed.configureTestingModule({
      imports: [PatientDetailsComponent],
      providers: [
        { provide: PatientService, useValue: patientServiceSpy },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } } }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create PatientDetails component', () => {
    expect(component).toBeTruthy();
  });

  it('should load patient and bind to form fields onInit', () => {
    const native = fixture.nativeElement as HTMLElement;
    const firstNameInput = native.querySelector('#firstName') as HTMLInputElement;
    const lastNameInput = native.querySelector('#lastName') as HTMLInputElement;
    const birthDateInput = native.querySelector('#birthDate') as HTMLInputElement;
    const genderSelect = native.querySelector('#gender') as HTMLSelectElement;
    const phoneInput = native.querySelector('#phoneNumber') as HTMLInputElement;
    const addressInput = native.querySelector('#address') as HTMLInputElement;

    expect(firstNameInput.value).toBe(mockPatient.firstName);
    expect(lastNameInput.value).toBe(mockPatient.lastName);
    expect(birthDateInput.value).toBe(mockPatient.birthDate);
    expect(genderSelect.value).toBe(mockPatient.gender);
    expect(phoneInput.value).toBe(mockPatient.phoneNumber);
    expect(addressInput.value).toBe(mockPatient.address);
  });

  it('should call updatePatient when the form is submitted', () => {
    component.patient.firstName = 'Jane';
    fixture.detectChanges();

    const formDebug = fixture.debugElement.query(By.css('form'));
    formDebug.triggerEventHandler('ngSubmit', {});
    fixture.detectChanges();

    expect(patientServiceSpy.updatePatient).toHaveBeenCalledWith('1', component.patient);
  });

  it('should log error when getPatientById fails', () => {
    const error = new Error('Not found');
    patientServiceSpy.getPatientById.and.returnValue(throwError(() => error));

    const consoleErrorSpy = spyOn(console, 'error');

    component.ngOnInit();

    expect(consoleErrorSpy).toHaveBeenCalledWith(error);
  });

  it('should handle missing patient ID in route parameters', () => {
    const consoleErrorSpy = spyOn(console, 'error');

    (component as any).route = { snapshot: { paramMap: convertToParamMap({id: null}) } };

    component.ngOnInit();

    expect(component.patientId).toBe('');
    //expect(patientServiceSpy.getPatientById).not.toHaveBeenCalled();
    expect(consoleErrorSpy).toHaveBeenCalledWith('Patient ID is missing in route parameters');
  });
});
