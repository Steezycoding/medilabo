import {TestBed} from '@angular/core/testing';
import {PatientService} from './patient.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Patient} from '../model/Patient';

describe('PatientService', () => {
  let service: PatientService;
  let httpMock: HttpTestingController;
  const apiPatientBase = `${environment.apiBaseUrl}/api/patients`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PatientService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(PatientService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getPatients requests GET and returns list of patients', (done: DoneFn) => {
    const mock: Patient[] = [{ id: '1', firstName: 'John', lastName: 'Doe' } as Patient];

    service.getPatients().subscribe({
      next: patients => {
        expect(patients).toEqual(mock);
        done();
      },
      error: err => { fail(err); done(); }
    });

    const req = httpMock.expectOne(apiPatientBase);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('getPatientById requests GET on /{id} and returns patient', (done: DoneFn) => {
    const id = '123';
    const mock: Patient = { id, firstName: 'Jane', lastName: 'Roe' } as Patient;

    service.getPatientById(id).subscribe({
      next: p => {
        expect(p).toEqual(mock);
        done();
      },
      error: err => { fail(err); done(); }
    });

    const req = httpMock.expectOne(`${apiPatientBase}/${id}`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('updatePatient requests PUT on /{id} with the patient details', () => {
    const id = '42';
    const patient: Patient = { id, firstName: 'Foo', lastName: 'Bar' } as Patient;

    service.updatePatient(id, patient);

    const req = httpMock.expectOne(`${apiPatientBase}/${id}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(patient);
    req.flush(null, { status: 204, statusText: 'No Content' });
  });

  it('getPatients should log error and propagate when backend fails', (done: DoneFn) => {
    spyOn(console, 'error');

    service.getPatients().subscribe({
      next: () => { fail('expected error'); done(); },
      error: err => {
        expect(console.error).toHaveBeenCalledWith('Error fetching patients', jasmine.anything());
        expect(err).toBeTruthy();
        done();
      }
    });

    const req = httpMock.expectOne(apiPatientBase);
    req.flush({ message: 'fail' }, { status: 500, statusText: 'Server Error' });
  });

  it('getPatientById should log error and propagate when backend fails', (done: DoneFn) => {
    spyOn(console, 'error');
    const id = '999';

    service.getPatientById(id).subscribe({
      next: () => { fail('expected error'); done(); },
      error: err => {
        expect(console.error).toHaveBeenCalledWith('Error fetching patient by ID', jasmine.anything());
        expect(err).toBeTruthy();
        done();
      }
    });

    const req = httpMock.expectOne(`${apiPatientBase}/${id}`);
    req.flush({ message: 'not found' }, { status: 500, statusText: 'Server Error' });
  });

  it('updatePatient should log error when backend fails (service subscribes internally)', () => {
    spyOn(console, 'error');
    const id = '42';
    const patient: Patient = { id, firstName: 'Foo', lastName: 'Bar' } as Patient;

    service.updatePatient(id, patient);

    const req = httpMock.expectOne(`${apiPatientBase}/${id}`);

    try {
      req.flush({ message: 'update failed' }, { status: 500, statusText: 'Server Error' });
    } catch (e) {
      // expected rethrown error from catchError -> subscribe without error handler
    }

    expect(console.error).toHaveBeenCalledWith('Error updating patient', jasmine.anything());
  });
});
