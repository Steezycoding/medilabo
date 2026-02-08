import {TestBed} from '@angular/core/testing';
import {RiskEvaluatorService} from './risk-evaluator.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {RiskLevelResponse} from '../model/RiskLevel';

describe('RiskEvaluatorService', () => {
  let service: RiskEvaluatorService;
  let httpMock: HttpTestingController;
  const apiRiskEvaluatorBase = `${environment.apiBaseUrl}/api/risk-evaluator`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RiskEvaluatorService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(RiskEvaluatorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getRiskLevel requests GET and returns response', (done: DoneFn) => {
    const patientId = 2;

    const backendResponse = {
      riskLevel : "BORDERLINE"
    } as RiskLevelResponse;

    service.getRiskLevel(patientId).subscribe({
      next: res => {
        expect(res).toEqual(backendResponse);
        done();
      },
      error: err => { fail(err); done(); }
    });

    const req = httpMock.expectOne(`${apiRiskEvaluatorBase}/patient/${patientId}`);
    expect(req.request.method).toBe('GET');
    req.flush(backendResponse);
  });

  it('getRiskLevel should log error and propagate when backend fails', (done: DoneFn) => {
    spyOn(console, 'error');

    const patientId = 7;

    service.getRiskLevel(patientId).subscribe({
      next: () => { fail('expected error'); done(); },
      error: err => {
        expect(console.error).toHaveBeenCalledWith('Error fetching risk level', jasmine.anything());
        expect(err).toBeTruthy();
        done();
      }
    });

    const req = httpMock.expectOne(`${apiRiskEvaluatorBase}/patient/${patientId}`);
    req.flush({ message: 'fail' }, { status: 500, statusText: 'Server Error' });
  });
});
