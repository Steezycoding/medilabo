import {Component, inject, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Patient} from '../../../model/Patient';
import {PatientService} from '../../../services/patient.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-patient-details',
  imports: [
    FormsModule
  ],
  templateUrl: './patient-details.html',
  styleUrl: './patient-details.scss',
})
export class PatientDetailsComponent implements OnInit {
  patient: Patient = null!;
  patientId: string = '';

  route = inject(ActivatedRoute);
  patientService = inject(PatientService);

  ngOnInit(): void {
    this.patientId = this.route.snapshot.paramMap.get('id') || '';

    if (this.patientId === '') {
      console.error('Patient ID is missing in route parameters');
      return;
    }

    this.patientService.getPatientById(this.patientId).subscribe({
      next: data => {
        this.patient = data;
      },
      error: err => {
        console.error(err)
      }
    });
  }

  onSubmitPatientForm() {
    this.patientService.updatePatient(this.patientId, this.patient);
  }
}
