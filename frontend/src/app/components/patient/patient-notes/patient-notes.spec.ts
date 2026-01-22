import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientNotes } from './patient-notes';

describe('PatientNotes', () => {
  let component: PatientNotes;
  let fixture: ComponentFixture<PatientNotes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientNotes]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientNotes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
