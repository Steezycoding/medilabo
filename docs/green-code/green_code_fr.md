# 🍃 Green Code — Comprendre les enjeux et les principes

## 🔎 1. Définition : c’est quoi le Green Code ?

Le _Green Code_ (aussi appelé _green coding_ ou _green software_) désigne l’ensemble des pratiques de développement
logiciel
visant à minimiser l’impact environnemental des applications et systèmes en réduisant la consommation de ressources
(énergie, CPU, mémoire, réseau, stockage) tout au long de leur cycle de vie, sans compromettre la valeur fonctionnelle
pour l’utilisateur.

Contrairement au Green IT, qui englobe l’ensemble des actions écologiques autour de l’informatique (matériel, data
centers, usage), le _Green Code_ se concentre spécifiquement sur la qualité et l’efficacité du code lui-même.

## 📏 2. Comment mesurer l’impact du code ?

> Ce qui n’est pas mesuré ne peut être amélioré.

### 🔹 Quelques métriques clés

| **Type de métrique**                   | **Objectif**                           |
|:---------------------------------------|:---------------------------------------|
| **Consommation énergétique**           | énergie consommée à l’exécution        |
| **Intensité carbone logicielle (SCI)** | CO₂ émis par unité de travail logiciel |
| **Usage CPU / mémoire / I/O réseau**   | charge système réelle                  |
| **Temps d’exécution total**            | indicateur de performance et d’énergie |

La **[Software Carbon Intensity (SCI)](https://sci.greensoftware.foundation/#methodology-summary)** définit une unité de
mesure standardisée pour quantifier l’impact carbone d’un logiciel, exprimée en grammes de CO₂.

### 🧰 Quelques pistes de méthodes et outils pour évaluer l’impact environnemental du code :

- **Profiling runtime _(CPU, mémoire)_** : mesure l’usage des ressources par ligne de code ou module.
- **Outils de mesure énergétique**
    - [PowerAPI](https://powerapi.org/), [Scaphandre](https://github.com/hubblo-org/scaphandre), [JoularJX](https://github.com/joular/joularjx) :
      quantifient l’énergie électrique consommée par un processus ou une application.
- **Outils d'analyse carbone**
    - [SCER](https://github.com/Green-Software-Foundation/scer) : modèles d’évaluation structurés qui
      intègrent énergie et émissions.
    - [GreenFrame](https://docs.greenframe.io/) : évalue l’impact environnemental d’une application web en mesurant les
    - ressources utilisées et en estimant les émissions associées.

## 🧭 3. Bonnes pratiques _Green Code_

> Il ne s’agit pas de coder plus vert à tout prix, mais de coder plus juste, à chaque décision.

### A. Optimiser l’efficacité du code

- **Écrire des algorithmes efficaces** : éviter les boucles et calculs inutiles ; privilégier des algorithmes à
  complexité plus faible lorsque c'est possible.
- **Limiter les allocations mémoire inutiles** : réduire les fuites mémoire et objets temporaires.
- **Minimiser les accès et transferts réseaux** : regrouper les requêtes, compresser les données.
- **Éviter les calculs redondants** : caching, lazy loading, batching.

### B. Au niveau du cycle logiciel

- **Mesurer avant et après** optimisation pour quantifier les gains écologiques.
- **Automatiser les mesures** dans la CI/CD (analyse énergie / carbone à chaque build).
- **Suivi longitudinal** des gains → suivi des indicateurs au fil des versions.

### C. Choix technologiques

- Choisir des **langages et librairies efficaces et pérennes** selon les usages.
- Favoriser des **dépendances légères** plutôt que des frameworks lourds.
- **Optimiser le stack technique** (compilateurs, runtime, versions).

## 🤖 4. _Green Code_ et IA : enjeux spécifiques

L’essor de l’IA pose des défis et des opportunités dans le contexte _Green Code_.

### ⚠️ Enjeux environnementaux

- **Entraînement des modèles IA** consomme beaucoup d’énergie — plusieurs études estiment que les gros modèles peuvent
  émettre autant de CO₂ que plusieurs voitures en une année.
- **Inférence en production** (réponses générées) peut être coûteuse selon la fréquence et la taille des modèles.

### ✔️ Opportunités d’amélioration

**IA pour écrire un code plus vert :**  
[Des études](https://arxiv.org/pdf/2403.03344) explorent par exemple la capacité des modèles à générer du code plus
“vert” (code généré avec des métriques d’efficacité
énergétique en tête) et à mesurer la durabilité de code produit automatiquement.
> Il n’est pas démontré que les LLM surpassent systématiquement les humains en termes de durabilité

Mais cela permettrait de :

- analyser automatiquement les impacts potentiels
- proposer des refactorings pour réduire la consommation
- assister au profiling intelligent de sections critiques d'un programme

### ✳️ Bonnes pratiques IA & performance écologique

- Choisir des modèles adaptés à l’usage (taille, budget énergétique).
- Limiter la fréquence de génération inutile.
- Utiliser des métriques _Green Code_ dans la génération automatique (par ex. SCI).
- Faire des échantillons de comparaison _code humain ↔ code généré_ sur les mêmes métriques d’impact.

→ L’IA peut devenir un outil puissant d’aide à la sobriété logicielle, à condition d’intégrer des métriques écologiques
dans son usage.

### 🔎 Recherches Web manuelles vs IA générative

**Modèle classique (moteur de recherche)**

- indexation optimisée
- faible coût par requête
- mais possible navigation multiple (plusieurs pages)

**Recherche via IA**

- exécution d’un modèle d’IA de grande taille pour générer une réponse
- calcul plus intense par requête
- synthèse pouvant réduire les interactions utilisateur

> L’impact dépend fortement du comportement utilisateur et du nombre total d’opérations.

## 📌 À retenir

- **Le _Green Code_ vise l’efficacité logicielle responsable**, pas seulement une optimisation de performance pure.
- **Mesurer est le préalable indispensable** à toute action.
- Les solutions concrètes incluent **optimisation du code, choix des technos, suivi des métriques et intégration dans le
  pipeline de développement**.
- **L’IA occupe une place croissante**, tant comme facteur d’impact que comme outil facilitateur de code plus durable.