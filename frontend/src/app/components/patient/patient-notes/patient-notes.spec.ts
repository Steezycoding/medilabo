import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SimpleChange} from '@angular/core';
import {of, throwError} from 'rxjs';

import {PatientNotesComponent} from './patient-notes';
import {MedicalNotesService} from '../../../services/medical-note.service';
import {MedicalNote} from '../../../model/MedicalNote';

describe('PatientNotes', () => {
  let component: PatientNotesComponent;
  let fixture: ComponentFixture<PatientNotesComponent>;
  let notesServiceSpy: jasmine.SpyObj<MedicalNotesService>;

  const mockNotes = [
    { id: 'a123b456', patId: 2, content: 'Note 1 content', createdAt: '2020-01-01' },
    { id: 'c789d012', patId: 2, content: 'Note 2 content', createdAt: '2020-02-01' }
  ] as MedicalNote[];

  beforeEach(async () => {
    notesServiceSpy = jasmine.createSpyObj('MedicalNotesService', ['getPatientNotes']);

    await TestBed.configureTestingModule({
      imports: [PatientNotesComponent],
      providers: [{ provide: MedicalNotesService, useValue: notesServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(PatientNotesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  function setPatientIdAndTrigger(id: number | null) {
    const newValue = id === null ? null : String(id);
    const change = new SimpleChange(component.patientId, id, true);
    component.patientId = newValue;
    component.ngOnChanges({ patientId: change });
    fixture.detectChanges();
  }

  it('should create PatientNotes component', () => {
    expect(component).toBeTruthy();
  });

  it('should clear notes and not call service when patientId is null', () => {
    component.notes = mockNotes;
    setPatientIdAndTrigger(null);

    expect(notesServiceSpy.getPatientNotes).not.toHaveBeenCalled();
    expect(component.notes).toEqual([]);
    expect(component.error).toBeFalse();
  });

  it('should load notes when patientId is provided', () => {
    notesServiceSpy.getPatientNotes.and.returnValue(of(mockNotes));

    setPatientIdAndTrigger(2);

    expect(notesServiceSpy.getPatientNotes).toHaveBeenCalledWith(2);
    expect(component.notes).toEqual(mockNotes);
    expect(component.error).toBeFalse();
    expect(component.loading).toBeFalse();
  });

  it('should set error true and log when notes loading fails', () => {
    const err = new Error('Load failed');
    notesServiceSpy.getPatientNotes.and.returnValue(throwError(() => err));
    const consoleErrorSpy = spyOn(console, 'error');

    setPatientIdAndTrigger(2);

    expect(notesServiceSpy.getPatientNotes).toHaveBeenCalledWith(2);
    expect(component.error).toBeTrue();
    expect(component.loading).toBeFalse();
    expect(consoleErrorSpy).toHaveBeenCalledWith('Failed to load patient notes', err);
  });

  it('should delete note and update notes list', () => {
    notesServiceSpy.getPatientNotes.and.returnValue(of(mockNotes));
    setPatientIdAndTrigger(2);

    notesServiceSpy.deleteNoteById = jasmine.createSpy().and.returnValue(of(undefined));

    component.deleteNote('a123b456');

    expect(notesServiceSpy.deleteNoteById).toHaveBeenCalledWith('a123b456');
    expect(component.notes.length).toBe(1);
    expect(component.notes.find(n => n.id === 'a123b456')).toBeUndefined();
  });

  it('should log error when delete note fails', () => {
    notesServiceSpy.getPatientNotes.and.returnValue(of(mockNotes));
    setPatientIdAndTrigger(2);

    const err = new Error('Delete failed');
    notesServiceSpy.deleteNoteById = jasmine.createSpy().and.returnValue(throwError(() => err));
    const consoleErrorSpy = spyOn(console, 'error');

    component.deleteNote('a123b456');

    expect(notesServiceSpy.deleteNoteById).toHaveBeenCalledWith('a123b456');
    expect(consoleErrorSpy).toHaveBeenCalledWith('Failed to delete note', err);
    expect(component.notes.length).toBe(2); // Notes list should remain unchanged
  });
});
