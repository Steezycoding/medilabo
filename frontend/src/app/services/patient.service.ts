import {inject, Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Patient} from '../model/Patient';

@Injectable({ providedIn: 'root' })
export class PatientService {
  private apiPatientUrl = `${environment.apiBaseUrl}/api/patients`;

  http = inject(HttpClient);

  getPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(this.apiPatientUrl);
  }
}
