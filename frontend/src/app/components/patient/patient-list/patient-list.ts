import {Component, inject, OnInit} from '@angular/core';
import {PatientService} from '../../../services/patient.service';
import {Patient} from '../../../model/Patient';


@Component({
  selector: 'app-patient-list',
  imports: [],
  templateUrl: './patient-list.html',
  styleUrl: './patient-list.scss',
})
export class PatientListComponent implements OnInit {
  protected patients: Patient[] = [];

  patientService = inject(PatientService);

  ngOnInit(): void {
    this.patientService.getPatients().subscribe({
      next: data => {
        this.patients = data;
      },
      error: err => {
        console.error("Error fetching patient data")
      }
    });
  }
}
