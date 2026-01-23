import {Component, inject, Input, SimpleChanges} from '@angular/core';
import {MedicalNote} from '../../../model/MedicalNote';
import {MedicalNotesService} from '../../../services/medical-note.service';

@Component({
  selector: 'app-patient-notes',
  imports: [],
  templateUrl: './patient-notes.html',
  styleUrl: './patient-notes.scss',
})
export class PatientNotesComponent {
  @Input() patientId: string | null = null;

  notes: MedicalNote[] = [];
  loading = false;
  error = false;

  private notesService = inject(MedicalNotesService);


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['patientId']) {
      this.loadNotes();
    }
  }

  private loadNotes(): void {
    if (!this.patientId) {
      this.notes = [];
      return;
    }

    const id = Number(this.patientId);

    this.loading = true;
    this.error = false;

    this.notesService.getPatientNotes(id).subscribe({
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
}
