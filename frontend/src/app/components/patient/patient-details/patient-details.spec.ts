import {ComponentFixture, TestBed} from '@angular/core/testing';
import {PatientDetailsComponent} from './patient-details';
import {PatientService} from '../../../services/patient.service';
import {ActivatedRoute, convertToParamMap} from '@angular/router';
import {Patient} from '../../../model/Patient';
import {of, throwError} from 'rxjs';

describe('PatientDetails', () => {
  let component: PatientDetailsComponent;
  let fixture: ComponentFixture<PatientDetailsComponent>;
  let patientServiceSpy: jasmine.SpyObj<PatientService>;

  const patientId = '123';
  const mockPatient: Patient = {
    id: patientId,
    firstName: 'John',
    lastName: 'Doe',
    birthDate: '1990-01-01',
    gender: 'M',
    phoneNumber: '123-4567-890',
    address: '123 Main St'
  } as Patient;

  function createComponentWithRoute(routeParams: {[key: string]: string | null}) {
    TestBed.overrideProvider(ActivatedRoute, {
      useValue: { snapshot: { paramMap: convertToParamMap(routeParams) } }
    });
    fixture = TestBed.createComponent(PatientDetailsComponent);
    component = fixture.componentInstance;
    component.ngOnInit();
    fixture.detectChanges();
  }

  beforeEach(async () => {
    patientServiceSpy = jasmine.createSpyObj('PatientService', [
      'getPatientById',
      'updatePatient',
      'createPatient'
    ]);

    await TestBed.configureTestingModule({
      imports: [PatientDetailsComponent],
      providers: [
        { provide: PatientService, useValue: patientServiceSpy },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({}) } } }
      ]
    })
    .compileComponents();
  });

  afterEach(() => {
    if (fixture) {
      fixture.destroy();
    }
  });

  it('should create PatientDetails component', () => {
    createComponentWithRoute({});

    expect(component).toBeTruthy();
  });

  it('should be in "view" mode onInit when route contains id', () => {
    patientServiceSpy.getPatientById.and.returnValue(of(mockPatient));

    createComponentWithRoute({ id: patientId });

    const title = fixture.nativeElement.querySelector('h2') as HTMLHeadingElement;
    const editBtn = fixture.nativeElement.querySelector('button#editToggle.btn.btn-primary') as HTMLButtonElement;
    const closeEditBtn = fixture.nativeElement.querySelector('button#editToggle.btn-outline-danger') as HTMLButtonElement;
    const submitBtn = fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement;

    expect(component.patientId).toBe(patientId);
    expect(component.isEditMode).toBeFalse();
    expect(title.textContent?.trim()).toBe('Patient');
    expect(editBtn).toBeTruthy();
    expect(closeEditBtn).toBeNull();
    expect(submitBtn).toBeFalsy();
  });

  it('should switch to "edit" mode when in "view" mode and "editToggle" button is clicked', async () => {
    patientServiceSpy.getPatientById.and.returnValue(of(mockPatient));

    createComponentWithRoute({ id: patientId });
    await fixture.whenStable();

    const editBtn = fixture.nativeElement.querySelector('button#editToggle.btn-primary') as HTMLButtonElement;

    // Switch to "edit" mode
    expect(editBtn).toBeTruthy();
    editBtn.click();
    fixture.detectChanges();

    const title = fixture.nativeElement.querySelector('h2') as HTMLHeadingElement;
    const closeEditBtn = fixture.nativeElement.querySelector('button#editToggle.btn-outline-danger') as HTMLButtonElement;
    const submitBtn = fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement;

    expect(component.isEditMode).toBeTrue();
    expect(title.textContent?.trim()).toBe('Patient (edit)');
    expect(closeEditBtn).toBeTruthy();
    expect(submitBtn).toBeTruthy();
    expect(submitBtn.textContent?.trim()).toBe('Update patient');
    expect(submitBtn.disabled).toBeFalse();
  });

  it('should switch to "view" mode when in "edit" mode and "editToggle" button is clicked', async () => {
    patientServiceSpy.getPatientById.and.returnValue(of(mockPatient));

    createComponentWithRoute({ id: patientId });
    await fixture.whenStable();

    // Manually set to "edit" mode
    component.isEditMode = true;
    fixture.detectChanges();

    const closeEditBtn = fixture.nativeElement.querySelector('button#editToggle.btn-outline-danger') as HTMLButtonElement;

    // Switch back to "view" mode
    expect(closeEditBtn).toBeTruthy();
    closeEditBtn.click();
    fixture.detectChanges();

    const title = fixture.nativeElement.querySelector('h2') as HTMLHeadingElement;
    const submitBtn = fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement;
    const editBtn = fixture.nativeElement.querySelector('button#editToggle.btn-primary') as HTMLButtonElement;

    expect(component.isEditMode).toBeFalse();
    expect(editBtn).toBeTruthy();
    expect(title.textContent?.trim()).toBe('Patient');
    expect(submitBtn).toBeFalsy();
  });

  it('should load patient and bind data to form fields onInit', async () => {
    patientServiceSpy.getPatientById.and.returnValue(of(mockPatient));

    createComponentWithRoute({ id: patientId });
    await fixture.whenStable();

    expect(component.patient).toBeDefined();
    expect(component.patient).toEqual(mockPatient);

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
    expect(genderSelect.value).toBe(component.plainTextGender(mockPatient.gender));
    expect(phoneInput.value).toBe(mockPatient.phoneNumber);
    expect(addressInput.value).toBe(mockPatient.address);

    expect(patientServiceSpy.getPatientById).toHaveBeenCalledWith(patientId);
  });

  it('should log error when "patientService.getPatientById" fails', () => {
    const nonExistingId = '-1';
    const error = new Error('Error fetching patient by ID');
    const consoleErrorSpy = spyOn(console, 'error');
    patientServiceSpy.getPatientById.and.returnValue(throwError(() => error));

    createComponentWithRoute({ id: nonExistingId });

    expect(component.errorFetching).toBeTrue();
    expect(component.errorMessage).toBe(`The patient with ID ${nonExistingId} could not be found.`);

    expect(consoleErrorSpy).toHaveBeenCalledWith(error);
  });

  it('should update patient in "edit" mode', () => {
    patientServiceSpy.getPatientById.and.returnValue(of(mockPatient));
    patientServiceSpy.updatePatient.and.returnValue(of(mockPatient));

    createComponentWithRoute({ id: patientId });

    component.patient.firstName = 'Jane';
    component.patient.gender = 'F';
    fixture.detectChanges();

    component.onSubmitPatientForm();

    expect(component.loading).toBeFalse();
    expect(patientServiceSpy.updatePatient).toHaveBeenCalledWith(patientId, component.patient);
  });

  it('should log error when "patientService.updatePatient" fails on submit', () => {
    const consoleErrorSpy = spyOn(console, 'error');
    patientServiceSpy.getPatientById.and.returnValue(of(mockPatient));
    patientServiceSpy.updatePatient.and.returnValue(throwError(() => jasmine.anything()));

    createComponentWithRoute({ id: patientId });

    component.onSubmitPatientForm();

    expect(consoleErrorSpy).toHaveBeenCalledWith('Patient update failed.', jasmine.anything());
  });

  it('should be in "create" mode when route has no id', async () => {
    createComponentWithRoute({});
    await fixture.whenStable();

    expect(component.patientId).toBeNull();
    expect(component.patient).toBeDefined();

    const title = fixture.nativeElement.querySelector('h2') as HTMLHeadingElement;
    expect(title.textContent?.trim()).toBe('New Patient');

    const submitBtn = fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(submitBtn).toBeTruthy();
    expect(submitBtn.textContent?.trim()).toBe('Add new patient');

    expect(patientServiceSpy.getPatientById).not.toHaveBeenCalled();
  });

  it('should submit patient in "create" mode', () => {
    createComponentWithRoute({});

    const newPatient: Patient = {
      firstName: 'Alice',
      lastName: 'Smith',
      birthDate: '1985-05-15',
      gender: 'F',
      phoneNumber: '987-654-2103',
      address: '456 Elm St'
    } as Patient;

    patientServiceSpy.createPatient.and.returnValue(of(component.patient));
    component.patient = newPatient;

    component.onSubmitPatientForm();

    expect(component.loading).toBeFalse();
    expect(patientServiceSpy.createPatient).toHaveBeenCalledWith(component.patient);
  });

  it('should log error when "patientService.createPatient" fails on submit', () => {
    const consoleErrorSpy = spyOn(console, 'error');
    patientServiceSpy.createPatient.and.returnValue(throwError(() => jasmine.anything()));

    createComponentWithRoute({});

    component.onSubmitPatientForm();

    expect(consoleErrorSpy).toHaveBeenCalledWith('Patient creation failed.', jasmine.anything());
  });

  it('should return and display "Unknown" for unsupported or missing gender (default case)', async () => {
    patientServiceSpy.getPatientById.and.returnValue(of({ ...mockPatient, gender: 'X' }));

    createComponentWithRoute({ id: patientId });
    await fixture.whenStable();

    expect(component.plainTextGender('X')).toBe('Unknown');

    const genderInput = fixture.nativeElement.querySelector('#gender') as HTMLInputElement;
    expect(genderInput).toBeTruthy();
    expect(genderInput.value).toBe('Unknown');

    expect(component.plainTextGender(undefined as any)).toBe('Unknown');
  });
});
