# 🍃 Green Code --- Understanding the Challenges and Principles

## 🔎 1. Definition: What is Green Code?

*Green Code* (also referred to as *green coding* or *green software*)
encompasses software development practices aimed at minimizing the
environmental impact of applications and systems by reducing resource
consumption (energy, CPU, memory, network, storage) throughout their
lifecycle, without compromising functional value for the user.

Unlike Green IT, which covers the broader environmental impact of IT
(hardware, data centers, usage), *Green Code* specifically focuses on
the quality and efficiency of the code itself.

------------------------------------------------------------------------

## 📏 2. How to Measure the Impact of Code?

> What is not measured cannot be improved.

### 🔹 Key Metrics

| **Metric Type**                      | **Objective**                         |
|:-------------------------------------|:--------------------------------------|
| **Energy Consumption**               | Energy used during execution          |
| **Software Carbon Intensity (SCI)**  | CO₂ emitted per unit of software work |
| **CPU / Memory / Network I/O Usage** | Actual system load                    |
| **Total Execution Time**             | Performance and energy indicator      |

The **[Software Carbon Intensity (SCI)](https://sci.greensoftware.foundation/#methodology-summary)** defines a
standardized
measurement unit to quantify the carbon impact of software, expressed in
grams of CO₂.

### 🧰 Methods and Tools to Evaluate Environmental Impact

- **Runtime profiling (CPU, memory)**: Measures resource usage per
  line of code or module.
- **Energy measurement tools**
    - [PowerAPI](https://powerapi.org/), [Scaphandre](https://github.com/hubblo-org/scaphandre), [JoularJX](https://github.com/joular/joularjx) :
      These quantify the electrical energy consumed by a process or
      application.
- **Carbon analysis tools**
    - [SCER](https://github.com/Green-Software-Foundation/scer): Structured evaluation models integrating energy and
      emissions.
    - [GreenFrame](https://docs.greenframe.io/): Evaluates the environmental impact of a web
      application by measuring resource usage and estimating
      associated emissions.

------------------------------------------------------------------------

## 🧭 3. Green Code Best Practices

> It's not about coding "greener" at all costs, but about coding more
> responsibly with every decision.

### A. Optimize Code Efficiency

- **Write efficient algorithms**: Avoid unnecessary loops and
  computations; prefer lower-complexity algorithms when possible.
- **Limit unnecessary memory allocations**: Reduce memory leaks and
  temporary objects.
- **Minimize network access and transfers**: Batch requests, compress
  data.
- **Avoid redundant computations**: Use caching, lazy loading,
  batching strategies.

------------------------------------------------------------------------

### B. Throughout the Software Lifecycle

- **Measure before and after** optimization to quantify environmental
  gains.
- **Automate measurements** in CI/CD pipelines (energy/carbon analysis
  at each build).
- **Track progress over time** by monitoring environmental indicators
  across versions.

------------------------------------------------------------------------

### C. Technology Choices

- Choose **efficient and sustainable languages and libraries**
  according to usage.
- Favor **lightweight dependencies** instead of heavy frameworks when
  unnecessary.
- **Optimize the technical stack** (compilers, runtime, versions).

------------------------------------------------------------------------

## 🤖 4. Green Code and AI: Specific Challenges

The rise of AI introduces both challenges and opportunities in the Green
Code context.

### ⚠️ Environmental Challenges

- **AI model training** consumes significant energy --- several
  studies estimate that large models can emit as much CO₂ as multiple
  cars over a year.
- **Production inference** (generated responses) can be costly
  depending on frequency and model size.

------------------------------------------------------------------------

### ✔️ Opportunities for Improvement

**AI to produce greener code:**\
[Studies](https://arxiv.org/pdf/2403.03344) explore the ability of models to generate more energy-efficient
code and assess the sustainability of automatically produced code.

> It is not proven that LLMs systematically outperform humans in terms
> of sustainability.

However, AI could:

- Automatically analyze potential environmental impacts
- Suggest refactorings to reduce consumption
- Assist with intelligent profiling of critical program sections

------------------------------------------------------------------------

### ✳️ AI & Ecological Performance Best Practices

- Choose models appropriate to the use case (size, energy budget).
- Limit unnecessary generation frequency.
- Use Green Code metrics in automated generation (e.g., SCI).
- Compare *human-written code ↔ generated code* using the same impact
  metrics.

→ AI can become a powerful tool for software sobriety, provided
ecological metrics are integrated into its usage.

------------------------------------------------------------------------

## 🔎 Manual Web Searches vs Generative AI

### Traditional Search Engine Model

- Optimized indexing
- Low cost per query
- But may require multiple page navigations

### AI-Based Search

- Execution of a large AI model to generate a response
- More intensive computation per query
- A synthesized response may reduce user interactions

> The environmental impact strongly depends on user behavior and the
> total number of operations performed.

------------------------------------------------------------------------

## 📌 Key Takeaways

- **Green Code promotes responsible software efficiency**, not just
  pure performance optimization.
- **Measurement is the essential prerequisite** for any improvement.
- Practical solutions include **code optimization, technology choices,
  metric tracking, and CI/CD integration**.
- **AI plays a growing role**, both as a source of impact and as a
  facilitator of more sustainable code.
