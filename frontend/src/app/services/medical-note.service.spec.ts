import {TestBed} from '@angular/core/testing';
import {MedicalNotesService} from './medical-note.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {MedicalNote} from '../model/MedicalNote';

describe('MedicalNotesService', () => {
  let service: MedicalNotesService;
  let httpMock: HttpTestingController;
  const apiMedicalNoteBase = `${environment.apiBaseUrl}/api/medical-notes`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MedicalNotesService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(MedicalNotesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getPatientNotes requests GET and returns mapped list of medical notes', (done: DoneFn) => {
    const patientId = 2;

    const expected: MedicalNote[] = [
      { id: 'a123b456', patId: patientId, patientName: 'John Doe', content: 'Note 1 content', createdAt: '2024-01-01T13:32:19+01:00' } as MedicalNote,
      { id: 'c789d012', patId: patientId, patientName: 'John Doe', content: 'Note 2 content', createdAt: '2024-03-12T20:05:32+01:00' } as MedicalNote,
    ];

    service.getPatientNotes(patientId).subscribe({
      next: notes => {
        expect(notes).toEqual(expected);
        done();
      },
      error: err => { fail(err); done(); }
    });

    const req = httpMock.expectOne(`${apiMedicalNoteBase}/patient/${patientId}`);
    expect(req.request.method).toBe('GET');
    req.flush(expected);
  });

  it('getPatientNotes should log error and propagate when backend fails', (done: DoneFn) => {
    spyOn(console, 'error');

    const patientId = 1;

    service.getPatientNotes(patientId).subscribe({
      next: () => { fail('expected error'); done(); },
      error: err => {
        expect(console.error).toHaveBeenCalledWith('Error fetching medical notes', jasmine.anything());
        expect(err).toBeTruthy();
        done();
      }
    });

    const req = httpMock.expectOne(`${apiMedicalNoteBase}/patient/${patientId}`);
    req.flush({ message: 'fail' }, { status: 500, statusText: 'Server Error' });
  });

  it('deleteNoteById should request DELETE and complete', (done: DoneFn) => {
    const noteId = 'a123b456';

    service.deleteNoteById(noteId).subscribe({
      next: res => {
        expect(res).toBeUndefined();
        done();
      },
      error: err => { fail(err); done(); }
    });

    const req = httpMock.expectOne(`${apiMedicalNoteBase}/${noteId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush('');
  });

  it('deleteNoteById should log error and propagate when backend fails', (done: DoneFn) => {
    spyOn(console, 'error');

    const noteId = 'nonexistent';

    service.deleteNoteById(noteId).subscribe({
      next: () => { fail('expected error'); done(); },
      error: err => {
        expect(console.error).toHaveBeenCalledWith('Error deleting medical note', jasmine.anything());
        expect(err).toBeTruthy();
        done();
      }
    });

    const req = httpMock.expectOne(`${apiMedicalNoteBase}/${noteId}`);
    req.flush({ message: 'fail' }, { status: 404, statusText: 'Not Found' });
  });
});
