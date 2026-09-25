# Multi-stage build: compile project, keep Maven runtime for running tests
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY . /workspace
# Pre-download dependencies and build (skip tests here; tests run at container start)
RUN mvn -B -DskipTests package -Dallure.results.directory=/results

FROM maven:3.9.4-eclipse-temurin-17
WORKDIR /workspace
COPY --from=builder /workspace /workspace
COPY docker-entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# Default directory where Allure results will be written (mount a PVC here in k8s)
ENV ALLURE_RESULTS_DIR=/results
VOLUME ["/results"]

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
