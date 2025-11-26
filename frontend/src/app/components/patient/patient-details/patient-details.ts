import {Component, inject, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Patient} from '../../../model/Patient';
import {PatientService} from '../../../services/patient.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-patient-details',
  imports: [
    FormsModule
  ],
  templateUrl: './patient-details.html',
  styleUrl: './patient-details.scss',
})
export class PatientDetailsComponent implements OnInit {
  patient: Patient = {} as Patient;
  patientId: string = '';
  isEditMode = false;
  errorFetching = false;
  errorMessage: string = '';
  loading = false;
  fallbackRoute: string = '/dashboard';

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private patientService = inject(PatientService);

  ngOnInit(): void {
    this.patientId = this.route.snapshot.paramMap.get('id') || '';
    this.isEditMode = this.patientId !== '';

    if (this.isEditMode) {
      if (this.patientId === '') {
        console.error('Patient ID is missing in route parameters');
        return;
      }

      this.loading = true;
      this.patientService.getPatientById(this.patientId).subscribe({
        next: data => {
          this.patient = data;
          this.loading = false;
        },
        error: err => {
          this.errorFetching = true;
          this.errorMessage = `The patient with ID ${this.patientId} could not be found.`;
          console.error(err)
          this.loading = false;
        }
      });
    } else {
      this.patient = {} as Patient;
    }
  }

  onSubmitPatientForm() {
    this.loading = true;
    if (this.isEditMode) {
      this.patientService.updatePatient(this.patientId, this.patient).subscribe({
        next: (): void => {
          this.loading = false;
          this.router.navigate([this.fallbackRoute]);
        },
        error: (err: any) => {
          console.error('Patient update failed.', err);
          this.loading = false;
        }
      });
    } else {
      this.patientService.createPatient(this.patient).subscribe({
        next: (): void => {
          this.loading = false;
          this.router.navigate([this.fallbackRoute]);
        },
        error: (err: any) => {
          console.error('Patient creation failed.', err);
          this.loading = false;
        }
      });
    }
  }
}
