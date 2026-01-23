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
        patientName: item.patient ?? item.patientName ?? '',
        content: item.note ?? item.content ?? '',
        createdAt: item.createdAt ?? ''
      } as MedicalNote))),
      catchError (err => {
        console.error("Error fetching medical notes", err);
        throw err;
      })
    );
  }
}
