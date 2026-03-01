package com.medilabo.riskevaluatormicroservice.utils;

import com.medilabo.riskevaluatormicroservice.beans.MedicalNoteBean;
import com.medilabo.riskevaluatormicroservice.beans.PatientBean;
import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RiskEvaluatorDataTest {
	public static final Map<RiskLevel, PatientBean> patientsList =
			Collections.unmodifiableMap(new LinkedHashMap<>() {{
				put(RiskLevel.NONE,
						PatientBean.builder()
								.id(1L).firstName("Test").lastName("TestNone")
								.birthDate("1966-12-31").gender("F")
								.address("1 Brookside St").phoneNumber("100-222-3333")
								.build());

				put(RiskLevel.BORDERLINE,
						PatientBean.builder()
								.id(2L).firstName("Test").lastName("TestBorderline")
								.birthDate("1945-06-24").gender("M")
								.address("2 High St").phoneNumber("200-333-4444")
								.build());

				put(RiskLevel.IN_DANGER,
						PatientBean.builder()
								.id(3L).firstName("Test").lastName("TestInDanger")
								.birthDate("2004-06-18").gender("M")
								.address("3 Club Road").phoneNumber("300-444-5555")
								.build());

				put(RiskLevel.EARLY_ONSET,
						PatientBean.builder()
								.id(4L).firstName("Test").lastName("TestEarlyOnset")
								.birthDate("2002-06-28").gender("F")
								.address("4 Valley Dr").phoneNumber("400-555-6666")
								.build());
			}});

	public static final List<MedicalNoteBean> medicalNotes = List.of(
			MedicalNoteBean.builder().id("6967ba0850e332d34fcc00dc").patId(1).patient("").note("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé").build(),
			MedicalNoteBean.builder().id("6967ba0850e332d34fcc00dd").patId(2).patient("").note("Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement").build(),
			MedicalNoteBean.builder().id("6967ba0850e332d34fcc00de").patId(2).patient("").note("Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale").build(),
			MedicalNoteBean.builder().id("6967ba0850e332d34fcc00df").patId(3).patient("").note("Le patient déclare qu'il fume depuis peu").build(),
			MedicalNoteBean.builder().id("6967ba0850e332d34fcc00e0").patId(3).patient("").note("Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière Il se plaint également de crises d’apnée respiratoire anormales Tests de laboratoire indiquant un taux de cholestérol LDL élevé").build(),
			MedicalNoteBean.builder().id("6967ba0850e332d34fcc00e1").patId(4).patient("").note("Le patient déclare qu'il lui est devenu difficile de monter les escaliers Il se plaint également d’être essoufflé Tests de laboratoire indiquant que les anticorps sont élevés Réaction aux médicaments").build(),
			MedicalNoteBean.builder().id("6967ba0850e332d34fcc00e2").patId(4).patient("").note("Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps").build(),
			MedicalNoteBean.builder().id("6967ba0850e332d34fcc00e3").patId(4).patient("").note("Le patient déclare avoir commencé à fumer depuis peu Hémoglobine A1C supérieure au niveau recommandé").build(),
			MedicalNoteBean.builder().id("6967ba0850e332d34fcc00e4").patId(4).patient("").note("Taille, Poids, Cholestérol, Vertige et Réaction").build()
	);
}
