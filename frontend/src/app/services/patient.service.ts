import {inject, Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {Patient} from '../model/Patient';

@Injectable({ providedIn: 'root' })
export class PatientService {
  private apiPatientUrl = `${environment.apiBaseUrl}/api/patients`;

  http = inject(HttpClient);

  getPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(this.apiPatientUrl).pipe(
      catchError (err => {
        console.error("Error fetching patients", err);
        throw err;
      })
    );
  }

  getPatientById(id: string): Observable<Patient> {
    return this.http.get<Patient>(this.apiPatientUrl + `/${id}`)
      .pipe(
        catchError (err => {
          console.error("Error fetching patient by ID", err);
          throw err;
        })
    );
  }

  updatePatient(id: string, patient: Patient): Observable<Patient> {
   return this.http.put<Patient>(this.apiPatientUrl + `/${id}`, patient)
      .pipe(
        catchError (err => {
          console.error("Error updating patient", err);
          throw err;
        })
      );
  }

  createPatient(patient: Patient): Observable<Patient> {
    return this.http.post<Patient>(this.apiPatientUrl, patient)
      .pipe(
        catchError (err => {
          console.error("Error creating patient", err);
          throw err;
        })
    );
  }
}
