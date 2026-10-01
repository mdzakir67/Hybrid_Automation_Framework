#!/bin/bash

# Entry point for running tests and generating Allure artifacts
ALLURE_DIR=${ALLURE_RESULTS_DIR:-/results}

echo "Allure results directory: ${ALLURE_DIR}"

# Run tests; capture exit code but continue to generate report so artifacts are preserved
echo "Running mvn clean test..."
set +e
mvn -B -Dallure.results.directory=${ALLURE_DIR} clean test
TEST_EXIT=$?
set -e

echo "Generating Allure report (may succeed even if tests failed)"
mvn -B -Dallure.results.directory=${ALLURE_DIR} allure:report || true

echo "Tests finished with exit code ${TEST_EXIT}. Allure results (and report) available at ${ALLURE_DIR}"
exit ${TEST_EXIT}
