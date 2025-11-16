# Medilabo Frontend Application

## Overview

This _module_ contains the **Medilabo** frontend application.  
It provides the user interface to interact with the business backend microservices.  
It is built with **Angular**.

## Prerequisites

- Node.js **20.x** (or higher)
- Angular CLI **20.3.9**
- Bootstrap **5.3.8**

## Installation

To set up the project locally, follow these steps:

1. Clone the repository:

```bash
git clone https://github.com/Steezycoding/medilabo.git
 ```

_or_

```bash
git clone git@github.com:Steezycoding/medilabo.git
 ```

2. Navigate to the `frontend` directory:

```bash
cd frontend/
```

3. Install the required dependencies using npm:

```bash
npm install
```

## Development server

To start a local development server, run:

```bash
npm start
```

Once the server is running, open your browser and navigate to `http://localhost:8080/`.  
_The application will automatically reload whenever you modify any of the source files._

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
npm run build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
