import {Component, inject, Input, OnInit} from '@angular/core';
import {MedicalNote} from '../../../model/MedicalNote';
import {MedicalNotesService} from '../../../services/medical-note.service';
import {Patient} from '../../../model/Patient';
import {FormsModule, NgForm} from '@angular/forms';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-patient-notes',
  imports: [
    FormsModule,
    DatePipe
  ],
  templateUrl: './patient-notes.html',
  styleUrl: './patient-notes.scss',
})
export class PatientNotesComponent implements OnInit {
  @Input() patient: Patient | null = null;

  notes: MedicalNote[] = [];
  loading = false;
  error = false;
  newNoteContent: string = '';

  private notesService = inject(MedicalNotesService);

  private patientId: number = 0;


  ngOnInit(): void {
    if (this.patient && this.patient.id) {
      this.patientId = Number(this.patient.id);
      this.loadNotes();
    } else {
      this.notes = [];
      this.error = false;
    }
  }

  private loadNotes(): void {
    this.loading = true;
    this.error = false;

    this.notesService.getPatientNotes(this.patientId).subscribe({
      next: (data: any[]) => {
        this.notes = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Failed to load patient notes', err);
        this.error = true;
        this.loading = false;
      }
    });
  }

  public createNote(form: NgForm) {
    if (form.invalid) {
      return;
    }

    const content = this.newNoteContent.trim();
    if (!content) {
      return;
    }
    const patientLastName = this.patient?.lastName || '';

    this.notesService.createNote(this.patientId, patientLastName, content).subscribe({
      next: (newNote: MedicalNote) => {
        this.notes.unshift(newNote);
        this.loadNotes();
        this.resetForm(form);
      },
      error: (err: any) => {
        console.error('Failed to create note', err);
      }
    });
  }

  public deleteNote(noteId: string): void {
    this.notesService.deleteNoteById(noteId).subscribe({
      next: () => {
        this.notes = this.notes.filter(n => String(n.id) !== String(noteId));
      },
      error: (err: any) => {
        console.error('Failed to delete note', err);
      }
    });
  }

  public resetForm(form: NgForm): void {
    form.resetForm();
    this.newNoteContent = '';
  }
}
