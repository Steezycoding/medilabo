import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, throwError} from 'rxjs';

import {PatientNotesComponent} from './patient-notes';
import {MedicalNotesService} from '../../../services/medical-note.service';
import {MedicalNote} from '../../../model/MedicalNote';
import {Patient} from '../../../model/Patient';

describe('PatientNotes', () => {
  let fixture: ComponentFixture<PatientNotesComponent>;
  let component: PatientNotesComponent;
  let notesServiceSpy: jasmine.SpyObj<MedicalNotesService>;

  const mockPatient = {
    id: '2',
    firstName: 'John',
    lastName: 'Doe',
    birthDate: '1990-06-24',
    gender: 'M',
    address: '2 High St',
    phoneNumber: '200-333-4444'
  } as Patient;

  const mockNewNote = {
    id: 'e345f678',
    patId: 2,
    content: 'New note content',
    createdAt: '2020-03-01'
  } as MedicalNote;

  const mockNotes = [
    { id: 'a123b456', patId: 2, content: 'Note 1 content', createdAt: '2020-01-01' },
    { id: 'c789d012', patId: 2, content: 'Note 2 content', createdAt: '2020-02-01' }
  ] as MedicalNote[];

  beforeEach(() => {
    notesServiceSpy = jasmine.createSpyObj('MedicalNotesService', [
      'getPatientNotes',
      'deleteNoteById',
      'createNote'
    ]);
    notesServiceSpy.getPatientNotes.and.returnValue(of([]));

    TestBed.configureTestingModule({
      imports: [PatientNotesComponent],
      providers: [{ provide: MedicalNotesService, useValue: notesServiceSpy }]
    });

    fixture = TestBed.createComponent(PatientNotesComponent);
    component = fixture.componentInstance;
  });

  it('should create PatientNotes component', () => {
    expect(component).toBeTruthy();
  });

  it('should clear notes and not call service when patientId is null', () => {
    component.notes = mockNotes;

    fixture.detectChanges();

    expect(notesServiceSpy.getPatientNotes).not.toHaveBeenCalled();
    expect(component.notes).toEqual([]);
    expect(component.error).toBeFalse();
  });

  it('should load notes when patientId is provided', () => {
    component.patient = mockPatient;
    notesServiceSpy.getPatientNotes.and.returnValue(of(mockNotes));

    fixture.detectChanges();

    expect(notesServiceSpy.getPatientNotes).toHaveBeenCalledWith(2);
    expect(component.notes).toEqual(mockNotes);
    expect(component.error).toBeFalse();
    expect(component.loading).toBeFalse();
  });

  it('should set error true and log when notes loading fails', () => {
    component.patient = mockPatient;
    const err = new Error('Load failed');
    notesServiceSpy.getPatientNotes.and.returnValue(throwError(() => err));
    const consoleErrorSpy = spyOn(console, 'error');

    fixture.detectChanges();

    expect(notesServiceSpy.getPatientNotes).toHaveBeenCalledWith(2);
    expect(component.error).toBeTrue();
    expect(component.loading).toBeFalse();
    expect(consoleErrorSpy).toHaveBeenCalledWith('Failed to load patient notes', err);
  });

  it('should delete note and update notes list', () => {
    component.patient = mockPatient;
    notesServiceSpy.getPatientNotes.and.returnValue(of(mockNotes));

    fixture.detectChanges();

    notesServiceSpy.deleteNoteById = jasmine.createSpy().and.returnValue(of(undefined));

    component.deleteNote('a123b456');

    expect(notesServiceSpy.deleteNoteById).toHaveBeenCalledWith('a123b456');
    expect(component.notes.length).toBe(1);
    expect(component.notes.find(n => n.id === 'a123b456')).toBeUndefined();
  });

  it('should log error when delete note fails', () => {
    component.patient = mockPatient;
    notesServiceSpy.getPatientNotes.and.returnValue(of(mockNotes));

    const err = new Error('Delete failed');
    notesServiceSpy.deleteNoteById = jasmine.createSpy().and.returnValue(throwError(() => err));
    const consoleErrorSpy = spyOn(console, 'error');

    component.deleteNote('a123b456');

    fixture.detectChanges();

    expect(notesServiceSpy.deleteNoteById).toHaveBeenCalledWith('a123b456');
    expect(consoleErrorSpy).toHaveBeenCalledWith('Failed to delete note', err);
    expect(component.notes.length).toBe(2);
  });

  it('should create a note if form is valid', () => {
    component.patient = mockPatient;
    fixture.detectChanges();

    notesServiceSpy.createNote.and.returnValue(of(mockNewNote));
    notesServiceSpy.getPatientNotes.and.returnValue(of([mockNewNote, ...mockNotes]));

    const mockForm = {
      invalid: false,
      resetForm: jasmine.createSpy('resetForm')
    } as any;

    component.newNoteContent = 'New note content';

    component.createNote(mockForm);

    fixture.detectChanges();

    expect(notesServiceSpy.createNote).toHaveBeenCalledWith(2, 'Doe', 'New note content');
    expect(notesServiceSpy.getPatientNotes).toHaveBeenCalledWith(2);
    expect(component.notes[0]).toEqual(mockNewNote);
    expect(mockForm.resetForm).toHaveBeenCalled();
  });

  it('should not create a note if form is invalid', () => {
    component.patient = mockPatient;
    fixture.detectChanges();

    const mockForm = {
      invalid: true,
      resetForm: jasmine.createSpy('resetForm')
    } as any;

    component.createNote(mockForm);

    fixture.detectChanges();

    expect(notesServiceSpy.createNote).not.toHaveBeenCalled();
    expect(notesServiceSpy.getPatientNotes).toHaveBeenCalledOnceWith(2);
    expect(mockForm.resetForm).not.toHaveBeenCalled();
  });

  it('should not create a note if content is empty', () => {
    component.patient = mockPatient;
    fixture.detectChanges();

    const mockForm = {
      invalid: false,
      resetForm: jasmine.createSpy('resetForm')
    } as any;

    component.newNoteContent = '';

    component.createNote(mockForm);

    fixture.detectChanges();

    expect(notesServiceSpy.createNote).not.toHaveBeenCalled();
    expect(notesServiceSpy.getPatientNotes).toHaveBeenCalledOnceWith(2);
    expect(mockForm.resetForm).not.toHaveBeenCalled();
  });

  it('should log error when create note fails', () => {
    component.patient = mockPatient;
    notesServiceSpy.getPatientNotes.and.returnValue(of(mockNotes));

    fixture.detectChanges();

    const err = new Error('Create failed');
    notesServiceSpy.createNote = jasmine.createSpy().and.returnValue(throwError(() => err));
    const consoleErrorSpy = spyOn(console, 'error');

    const mockForm = {
      invalid: false,
      resetForm: jasmine.createSpy('resetForm')
    } as any;

    component.newNoteContent = 'New note content';

    component.createNote(mockForm);

    fixture.detectChanges();

    expect(notesServiceSpy.createNote).toHaveBeenCalledWith(2, 'Doe', 'New note content');
    expect(consoleErrorSpy).toHaveBeenCalledWith('Failed to create note', err);
    expect(component.notes.length).toBe(2);
  });
});
