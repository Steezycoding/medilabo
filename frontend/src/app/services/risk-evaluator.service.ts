import {inject, Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {RiskLevelResponse} from '../model/RiskLevel';

@Injectable({ providedIn: 'root' })
export class RiskEvaluatorService {
  private apiRiskEvaluatorUrl = `${environment.apiBaseUrl}/api/risk-evaluator`;

  http = inject(HttpClient);

  getRiskLevel(patientId: number): Observable<RiskLevelResponse> {
    return this.http.get<RiskLevelResponse>(this.apiRiskEvaluatorUrl + `/patient/${patientId}`)
      .pipe(
        catchError (err => {
          console.error("Error fetching risk level", err);
          throw err;
        })
      );
  }
}
