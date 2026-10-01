# Multi-stage build: compile project, keep Maven runtime for running tests
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY . /workspace
# Pre-download dependencies and build (skip tests here; tests run at container start)
RUN mvn -B -DskipTests package -Dallure.results.directory=/results

FROM maven:3.9.4-eclipse-temurin-17
WORKDIR /workspace

# Install Google Chrome
RUN apt-get update \
    && apt-get install -y wget gnupg ca-certificates \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub \
        | gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
        > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace /workspace
COPY docker-entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
