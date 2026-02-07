package com.medilabo.riskevaluatormicroservice.domain.enums;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public enum TriggerTerm {
	HEMOGLOBINE_A1C("hemoglobine", "a1c"),
	MICROALBUMINE("microalbumine"),
	TAILLE("taille"),
	POIDS("poids"),
	FUMEUR("fumeur", "fumeuse", "fume", "fumer"),
	ANORMAL("anormal", "anormale", "anormaux", "anormales"),
	CHOLESTEROL("cholesterol"),
	VERTIGES("vertige", "vertiges"),
	RECHUTE("rechute"),
	REACTION("reaction"),
	ANTICORPS("anticorps");

	private final Set<String> normalizedVariants;

	TriggerTerm(String... variants) {
		this.normalizedVariants = Arrays.stream(variants)
				.map(TriggerTerm::normalize)
				.collect(Collectors.toSet());
	}

	/**
	 * Indicates if the given raw word matches any of the trigger term variants.
	 */
	public boolean matches(String rawWord) {
		String normalizedWord = normalize(rawWord);
		return normalizedVariants.contains(normalizedWord);
	}

	/**
	 * Normalizes a string by:</br>
	 * - lowercase</br>
	 * - accent removal
	 */
	private static String normalize(String input) {
		if (input == null) {
			return "";
		}

		String lower = input.toLowerCase(Locale.ROOT);
		return Normalizer.normalize(lower, Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "");
	}
}
