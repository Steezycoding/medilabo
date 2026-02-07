package com.medilabo.riskevaluatormicroservice.utils;

import com.medilabo.riskevaluatormicroservice.beans.MedicalNoteBean;

import java.util.List;

public class MedicalNotesUtils {
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
