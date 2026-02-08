import {Component, inject, Input} from '@angular/core';
import {Patient} from '../../../model/Patient';
import {RiskLevel} from '../../../model/RiskLevel';
import {RiskEvaluatorService} from '../../../services/risk-evaluator.service';

@Component({
  selector: 'app-patient-risk',
  imports: [],
  templateUrl: './patient-risk.html',
  styleUrl: './patient-risk.scss',
})
export class PatientRiskComponent {
  @Input() patient!: Patient;

  protected loading: boolean = false;
  protected riskLevel: RiskLevel | null = null;

  private riskEvaluatorService: RiskEvaluatorService = inject(RiskEvaluatorService);

  ngOnInit(): void {
    if (this.patient && this.patient.id) {
      this.fetchRisk();
    }
  }

  fetchRisk(): void {
    this.loading = true;
    this.riskLevel = null;

    this.riskEvaluatorService.getRiskLevel(Number(this.patient.id)).subscribe({
      next: (resp) => {
        this.riskLevel = resp['riskLevel'];
        this.loading = false;
      },
      error: (err) => {
        if (err?.status === 404) {
          console.error('Failed to retrieved patient', err);
        } else {
          console.error('Failed to retrieved risk level', err);
        }
      },
    });
  }

  alertClass(level: RiskLevel | null): string {
    switch (level) {
      case RiskLevel.NONE:
        return 'alert-success';
      case RiskLevel.BORDERLINE:
        return 'alert-warning';
      case RiskLevel.IN_DANGER:
        return 'alert-danger';
      case RiskLevel.EARLY_ONSET:
        return 'alert-danger';
      default:
        return 'alert-secondary';
    }
  }

  riskLabel(level: RiskLevel | null): string {
    switch (level) {
      case RiskLevel.NONE:
        return 'None';
      case RiskLevel.BORDERLINE:
        return 'Borderline';
      case RiskLevel.IN_DANGER:
        return 'Danger';
      case RiskLevel.EARLY_ONSET:
        return 'Early onset';
      default:
        return '—';
    }
  }
}
