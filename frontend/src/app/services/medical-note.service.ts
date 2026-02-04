import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {MedicalNote} from '../model/MedicalNote';

@Injectable({
  providedIn: 'root'
})
export class MedicalNotesService {
  private apiMedicalNoteUrl = `${environment.apiBaseUrl}/api/medical-notes`;

  http = inject(HttpClient);

  getPatientNotes(patientId: number): Observable<MedicalNote[]> {
    return this.http.get<any[]>(this.apiMedicalNoteUrl + `/patient/${patientId}`).pipe(
      map(items => items.map(item => ({
        id: item.id,
        patId: item.patId,
        patName: item.patient,
        content: item.note,
        createdAt: item.createdAt
      } as MedicalNote))),
      catchError (err => {
        console.error("Error fetching medical notes", err);
        throw err;
      })
    );
  }

  createNote(patientId: number, patientName: string, content: string): Observable<MedicalNote> {
    const payload: any = {
      patId: patientId,
      patient: patientName,
      note: content
    };

    return this.http.post<MedicalNote>(this.apiMedicalNoteUrl, payload).pipe(
      map(item => ({
        patId: item.patId,
        patName: item.patName,
        content: item.content
      } as MedicalNote)),
      catchError(err => {
        console.error("Error creating medical note", err);
        throw err;
      })
    );
  }

  deleteNoteById(noteId: string): Observable<void> {
    return this.http.delete(this.apiMedicalNoteUrl + `/${noteId}`, { responseType: 'text' }).pipe(
      map(() => undefined),
      catchError(err => {
        console.error("Error deleting medical note", err);
        throw err;
      })
    );
  }
}
