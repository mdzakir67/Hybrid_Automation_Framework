# Interview Notes — Hybrid Automation Framework

## 1. Project in One Minute

### What did I build?

> I built an enterprise-style hybrid automation framework that supports both **UI and API automation** within the same BDD test architecture.

The framework is designed around:

* Java 17
* Selenium
* RestAssured
* Cucumber BDD
* TestNG
* PicoContainer
* Maven
* Docker
* Kubernetes
* GitHub Actions
* Allure
* Logback + SLF4J
* GitHub Pages

The important architectural idea is:

```text
                    Cucumber Scenario
                           │
                 ┌─────────┴─────────┐
                 │                   │
              API Steps           UI Steps
                 │                   │
            API Clients          Page Objects
                 │                   │
             RestAssured          Selenium
                 │                   │
                 └─────────┬─────────┘
                           │
                    ScenarioContext
                           │
                    Shared State
```

This allows a single scenario to perform:

```text
API → UI → API
```

For example:

```text
Create event through API
        ↓
Open application through UI
        ↓
Verify event in UI
        ↓
Update event through API
        ↓
Verify updated data
```

---

# 2. Architecture — Big Picture

```text
                         GitHub Actions
                              │
                 ┌────────────┴────────────┐
                 │                         │
          Runner Execution           Kubernetes Execution
                 │                         │
              Maven                      Docker
                 │                         │
          Cucumber + TestNG          Kubernetes Job
                 │                         │
                 └────────────┬────────────┘
                              │
                         Test Framework
                              │
             ┌────────────────┼────────────────┐
             │                │                │
          API Layer        UI Layer       Test State
             │                │                │
        RestAssured        Selenium      ScenarioContext
             │                │
        API Clients       Page Objects
             │                │
             └───────────────┘
                              │
                         EventHub App
```

Infrastructure:

```text
GitHub
  │
  ├── GitHub Actions
  │       │
  │       ├── Runner Tests
  │       ├── Docker Build
  │       ├── Kubernetes Tests
  │       └── GitHub Pages
  │
  ├── GHCR
  │       │
  │       └── Docker Image
  │
  └── GitHub Pages
          │
          ├── Runner Allure Report
          ├── Kubernetes Allure Report
          ├── Runner Scenario Logs
          └── Kubernetes Scenario Logs
```

---

# 3. Why Hybrid Automation?

## Question: Why not only Selenium?

Selenium is excellent for validating UI behavior, but using UI for everything creates several problems:

* slower execution
* more fragile tests
* difficult test-data setup
* difficult backend validation
* unnecessary dependency on UI flows

For example, if I need to create an event before testing it in UI, doing it through the UI may require:

```text
Login
→ navigate
→ open form
→ enter data
→ submit
→ wait
→ verify
```

API can create the same data much faster.

Therefore:

```text
API = fast setup / backend validation
UI  = actual user behavior validation
```

---

# 4. Why Cucumber?

Cucumber provides the BDD layer.

Example:

```gherkin
Scenario: Verify event creation
    Given I create an event through API
    When I open the EventHub application
    Then I should see the event
```

Cucumber gives us:

* readable scenarios
* separation between business behavior and implementation
* reusable step definitions
* hooks
* scenario lifecycle
* integration with reporting

But Cucumber should **not contain framework logic**.

The responsibility should be:

```text
Feature
   ↓
Step Definition
   ↓
Framework component
   ↓
Actual system
```

---

# 5. Why Separate API Clients and API Steps?

This is one of the most important architecture decisions.

## API Client

The API client knows:

> How to communicate with the API.

Example responsibility:

```java
eventsApiClient.getEvents(...)
```

It handles:

* endpoint
* HTTP method
* headers
* request body
* query parameters
* RestAssured execution

It returns:

```java
Response
```

It does **not** know:

* Cucumber
* ScenarioContext
* assertions
* business validation

---

## API Step

The step definition knows:

> What the test wants to do and verify.

Example:

```text
Given I get the events
Then the response status should be 200
And the event should exist
```

Therefore:

```text
API Step
   ↓
API Client
   ↓
RestAssured
   ↓
API
```

### Why this separation?

Because if tomorrow I want to call the API from:

* another test framework
* unit test
* utility
* Java code

I can reuse the API client without Cucumber dependencies.

---

# 6. Why Page Objects?

Page Object Model separates:

```text
WHAT test wants to do
```

from:

```text
HOW UI interaction happens
```

Example:

```text
Step:
    When I login

Page Object:
    enter username
    enter password
    click login
```

The step should not contain:

```java
driver.findElement(By.id("username")).sendKeys(...)
```

Instead:

```text
Step
 ↓
Page Object
 ↓
Selenium
```

---

# 7. Page Object Responsibility

A Page Object contains:

* locators
* UI actions
* navigation
* waits
* UI interaction behavior

It should generally **not contain assertions**.

For example:

```java
loginPage.login(username, password);
```

rather than:

```java
loginPage.loginAndVerifyUserIsLoggedIn();
```

The second approach mixes interaction and validation.

Better separation:

```text
Page Object → performs action
Step         → validates result
```

---

# 8. ScenarioContext

This is one of the most important components of the framework.

## Problem

Suppose:

```text
API creates event
        ↓
returns eventId
        ↓
UI needs eventId
```

How does one step communicate with another?

We use:

```text
ScenarioContext
```

Conceptually:

```text
Scenario
   │
   ├── Step 1 → stores token
   │
   ├── Step 2 → reads token
   │
   ├── Step 3 → stores eventId
   │
   └── Step 4 → reads eventId
```

Example:

```java
context.set("eventId", eventId);
```

Later:

```java
String eventId = context.get("eventId", String.class);
```

---

# 9. What Should ScenarioContext Contain?

Scenario/business state:

```text
token
eventId
userId
response
credentials
scenarioId
```

It should **not** contain:

```text
WebDriver
RequestSpecification
Page Objects
```

Why?

Because those belong to different lifecycle responsibilities.

---

# 10. PicoContainer

Cucumber creates objects such as:

```text
Hooks
EventHubSteps
AuthAPISteps
EventsAPISteps
```

Some of these classes require:

```java
ScenarioContext
```

Instead of manually creating:

```java
new ScenarioContext()
```

everywhere, PicoContainer performs dependency injection.

Example:

```java
public EventHubSteps(ScenarioContext context) {
    this.context = context;
}
```

PicoContainer sees:

```text
EventHubSteps
      ↓
requires ScenarioContext
      ↓
Pico creates ScenarioContext
      ↓
injects it
```

If multiple step classes require `ScenarioContext`, they receive the scenario-scoped instance.

Conceptually:

```text
Cucumber
   ↓
PicoContainer
   ↓
ScenarioContext
   ├── AuthAPISteps
   ├── EventsAPISteps
   ├── EventHubSteps
   └── Hooks
```

---

# 11. PicoContainer vs ScenarioContext

Very important interview distinction.

### PicoContainer

Responsible for:

> Object creation and dependency injection.

### ScenarioContext

Responsible for:

> Sharing scenario-specific business/test state.

They solve different problems.

```text
PicoContainer
    = How objects are created

ScenarioContext
    = What state objects share
```

---

# 12. Scenario Lifecycle

A typical scenario lifecycle is:

```text
Maven
  ↓
TestNG
  ↓
Cucumber Runner
  ↓
Feature
  ↓
Scenario
  ↓
PicoContainer / Glue objects
  ↓
@Before
  ↓
Step definitions
  ↓
API/UI actions
  ↓
@After
```

Our hook performs:

```text
@Before
   ↓
create scenario ID
   ↓
put scenario ID in context
   ↓
put scenario name in MDC
   ↓
initialize WebDriver
```

After scenario:

```text
@After
   ↓
check scenario result
   ↓
quit WebDriver
   ↓
ThreadLocal.remove()
   ↓
remove MDC
```

---

# 13. ThreadLocal — Why?

Suppose tests run in parallel.

```text
Thread 1 → ChromeDriver A
Thread 2 → ChromeDriver B
Thread 3 → ChromeDriver C
```

If we store driver in a normal static variable:

```java
private static WebDriver driver;
```

threads can overwrite each other's driver.

Instead:

```java
ThreadLocal<WebDriver>
```

provides separate storage per thread.

Conceptually:

```text
ThreadLocal

Thread 1 → Driver A
Thread 2 → Driver B
Thread 3 → Driver C
```

---

# 14. Important ThreadLocal Interview Point

ThreadLocal is **not automatically scenario-scoped**.

It is:

> Thread-scoped storage.

Our framework explicitly controls its lifecycle:

```text
@Before
   ↓
DriverFactory creates driver
   ↓
DriverManager.set()

Scenario runs

@After
   ↓
driver.quit()
   ↓
ThreadLocal.remove()
```

So:

```text
ThreadLocal → thread isolation
Cucumber → scenario lifecycle
```

---

# 15. DriverFactory vs DriverManager

These are intentionally separate.

## DriverFactory

Responsible for:

> Creating a WebDriver.

Example:

```text
chrome → ChromeDriver
firefox → FirefoxDriver
edge → EdgeDriver
```

It uses a map of browser names to suppliers.

---

## DriverManager

Responsible for:

> Managing the current driver's lifecycle.

It provides:

```text
initializeDriver()
getDriver()
tearDown()
```

Therefore:

```text
DriverFactory
    = creation

DriverManager
    = lifecycle/storage
```

---

# 16. Why Selenium Manager?

We don't use WebDriverManager.

Modern Selenium includes Selenium Manager.

Therefore:

```java
new ChromeDriver();
```

can allow Selenium to resolve the required browser driver automatically.

This reduces:

* dependency
* manual driver management
* driver-version configuration

---

# 17. API Request Specification

We centralized common API configuration.

Example:

```text
Base URL
Content-Type
Logging filter
Authorization
```

Instead of repeating:

```java
given()
    .baseUri(...)
    .contentType(...)
```

in every client.

We have:

```text
APIRequestSpec
```

Conceptually:

```text
APIRequestSpec
      │
      ├── defaultSpec()
      │
      └── authenticatedSpec(token)
```

This gives consistency across API clients.

---

# 18. Authentication Flow

Typical flow:

```text
Register/Login
      ↓
AuthApiClient
      ↓
RestAssured
      ↓
AuthResponse
      ↓
Extract token
      ↓
ScenarioContext
```

Later:

```text
ScenarioContext
      ↓
token
      ↓
authenticatedSpec(token)
      ↓
EventsApiClient
```

This allows the same scenario to perform authenticated API operations.

---

# 19. DTOs / Models

We don't want raw JSON strings everywhere.

For example:

```json
{
  "token": "...",
  "userId": "123"
}
```

can become:

```java
AuthResponse
```

RestAssured/Jackson can deserialize:

```java
response.as(AuthResponse.class)
```

Important architecture decision:

### API client

Returns:

```java
Response
```

### API step

Converts:

```java
Response → DTO
```

This keeps the API client generic.

---

# 20. Why Not Deserialize Inside API Client?

Because the client should focus on:

> HTTP communication.

The step should decide:

> What representation it needs.

For example:

```text
API Client
    ↓
Response
```

Then:

```text
Step
    ↓
AuthResponse
```

This keeps the client reusable.

---

# 21. Authentication Models

We separated models based on responsibility.

For example:

```text
auth/
 └── model/
      ├── request/
      └── response/
```

Successful response and failure response are not necessarily the same structure.

This avoids one giant DTO containing unrelated optional fields.

---

# 22. Configuration Architecture

Configuration is separated into:

```text
EnvironmentConfig
ConfigReader
ApiConfig
UIConfig
```

Example:

```text
-Denv=qa
```

causes:

```text
config/qa.properties
```

to be loaded.

For:

```text
-Denv=dev
```

we load:

```text
config/dev.properties
```

This allows the same test code to run against different environments.

---

# 23. Why Externalize Configuration?

We don't want:

```java
String baseUrl = "https://...";
```

hardcoded throughout the framework.

Instead:

```text
Environment
   ↓
Properties
   ↓
ConfigReader
   ↓
ApiConfig / UIConfig
   ↓
Framework
```

Benefits:

* environment independence
* easier CI/CD
* no source-code modification
* easier Docker/Kubernetes execution

---

# 24. Logging Architecture

We use:

```text
SLF4J
   ↓
Logback
```

SLF4J is the logging abstraction.

Logback is the actual logging implementation.

This allows application code to use:

```java
Logger
```

without being tightly coupled to Logback APIs.

---

# 25. Scenario-Specific Logging

We use MDC:

```text
Mapped Diagnostic Context
```

Example:

```text
scenarioName = Verify_Event_Creation
```

Logback can then use:

```text
%X{scenarioName}
```

This lets us associate log output with the scenario.

Conceptually:

```text
Scenario
   ↓
MDC
   ↓
Logback
   ↓
Scenario-specific log file
```

---

# 26. Why SiftingAppender?

Logback's `SiftingAppender` allows us to dynamically separate logs based on a discriminator.

We use:

```text
scenarioName
```

Therefore:

```text
target/logs/
    Scenario_A.log
    Scenario_B.log
    Scenario_C.log
```

This is useful when debugging a failed scenario.

---

# 27. API Logging Filter

RestAssured supports filters.

We created a centralized API logging filter.

Instead of adding logging manually to every API call:

```text
Request
   ↓
RestAssured Filter
   ↓
Log request
   ↓
API
   ↓
Log response
```

This gives consistent API logging.

---

# 28. Why Sanitize API Logs?

API requests may contain:

```text
password
token
accessToken
refreshToken
authorization
```

Logging these directly is a security risk.

Therefore we introduced:

```text
ApiLogSanitizer
```

which masks sensitive fields.

Example:

```text
password = ******
token = ******
```

The important interview statement:

> Logging is useful for debugging, but observability must not compromise credential security.

---

# 29. Allure Reporting

There are two separate concepts:

### Allure adapter

Example:

```text
allure-cucumber7-jvm
allure-testng
```

These collect test execution information and generate raw result files.

Usually:

```text
allure-results/
```

contains files such as:

```text
*.json
*.txt
```

---

### Allure Maven Plugin

This consumes:

```text
allure-results
```

and generates the HTML report.

Therefore:

```text
Test execution
    ↓
Allure adapter
    ↓
Raw results
    ↓
Allure Maven plugin
    ↓
HTML report
```

---

# 30. Why Generate Allure Report in Pages Job?

This was an important architecture decision.

Docker/Kubernetes should primarily produce:

```text
raw test results
```

GitHub Pages should produce:

```text
HTML report
```

Therefore:

```text
Kubernetes
   ↓
Allure raw results
   ↓
Artifact
   ↓
Build Pages job
   ↓
Allure HTML
   ↓
GitHub Pages
```

This separates:

```text
test execution
```

from:

```text
report publishing
```

---

# 31. Important Allure Path Issue

We encountered:

```text
target/target/allure-results
```

The reason was that the Allure Maven plugin interprets relative result paths relative to Maven's build directory.

For example:

```bash
-Dallure.results.directory=runner-allure-results
```

is resolved under:

```text
target/
```

whereas:

```bash
-Dallure.results.directory=target/runner-allure-results
```

can result in:

```text
target/target/runner-allure-results
```

Therefore the Pages job uses the appropriate relative path.

**Interview takeaway:**

> When configuring Maven plugins, I verified how the plugin resolves relative paths instead of assuming command-line property paths behave like normal filesystem paths.

---

# 32. Docker Architecture

Docker image has two stages.

```text
Stage 1
Maven Builder
      ↓
compile/package

Stage 2
Runtime
      ↓
Maven + Java + Chrome
      ↓
Test execution
```

Why multi-stage?

Because:

```text
build dependencies
```

and:

```text
runtime environment
```

can be separated.

---

# 33. Docker Entrypoint

The container starts through:

```text
docker-entrypoint.sh
```

Its job is to:

```text
Read result directory
       ↓
Run Maven tests
       ↓
Return test exit code
```

The important environment variable is:

```text
ALLURE_RESULTS_DIR
```

This allows Kubernetes to control where raw Allure results are written.

---

# 34. Kubernetes Architecture

Each Kubernetes test execution creates:

```text
PVC
+
Job
```

Conceptually:

```text
GitHub Actions
      ↓
Create PVC
      ↓
Create Job
      ↓
Job starts Pod
      ↓
Docker image
      ↓
Maven tests
      ↓
/results
      ↓
PVC
```

---

# 35. Why Kubernetes Job?

A Kubernetes Job represents:

> A finite workload that should run to completion.

Automation tests are exactly that.

We don't need a long-running Deployment.

```text
Deployment → long-running application

Job → finite execution
```

Therefore Job is appropriate for test execution.

---

# 36. Why PVC?

The test container is temporary.

When the Pod terminates, its container filesystem should not be considered the long-term storage location.

We need results after the Pod exits.

Therefore:

```text
Pod
 ↓
PVC
 ↓
results survive Pod lifecycle
```

---

# 37. Important PVC Mount Decision

We initially mounted the PVC under:

```text
/workspace/target
```

That caused:

```text
mvn clean
```

to fail.

Why?

Maven clean wants to delete:

```text
/workspace/target
```

but Kubernetes had mounted a volume inside that directory.

Correct architecture:

```text
/workspace/target
    ↓
Maven-owned temporary build directory

/results
    ↓
PVC-owned persistent directory
```

Current structure:

```text
/results
├── allure
└── logs
```

This is an important troubleshooting lesson.

---

# 38. Why One PVC Mount?

We use:

```yaml
mountPath: /results
```

instead of mounting the same PVC multiple times.

Inside:

```text
/results
├── allure/
└── logs/
```

Advantages:

* simpler
* clearer
* fewer volume mount definitions
* easier collector logic
* clean separation from Maven target

---

# 39. Kubernetes Collector

After the test Job finishes, we need to retrieve data from the PVC.

We create a temporary collector Pod.

```text
Test Job
   ↓
PVC
   ↓
Collector Pod
   ↓
/output
   ↓
kubectl cp
   ↓
GitHub runner
```

The collector runs:

```bash
cp -R /results/. /output/
```

Then:

```bash
kubectl cp
```

copies the data back to GitHub Actions.

---

# 40. Why Collector Pod?

The GitHub Actions runner cannot directly access the filesystem of the Kubernetes PVC.

Therefore we create a Pod that mounts the same PVC.

```text
PVC
 ↑
 │
Test Pod

PVC
 ↑
 │
Collector Pod
```

The collector can access the data and expose it through a normal container filesystem.

Then:

```text
kubectl cp
```

brings it back to the runner.

---

# 41. GitHub Actions Pipeline

Pipeline flow:

```text
Push / Manual Trigger
        ↓
Runner Tests
        ↓
Build Docker Image
        ↓
Resolve Image
        ↓
Kubernetes Tests
        ↓
Collect Results
        ↓
Build Pages
        ↓
Deploy Pages
```

---

# 42. Runner Tests

Runner execution:

```bash
mvn clean test
```

produces:

```text
target/
├── allure-results/
├── cucumber-report.html
└── scenario-logs/
```

These are uploaded as GitHub Actions artifacts.

---

# 43. Docker Build

After runner tests pass:

```text
Dockerfile
    ↓
Docker image
    ↓
GHCR
```

The image is tagged using:

```text
GitHub SHA
```

This gives immutable identification.

Example conceptually:

```text
hybrid_automation_framework:<commit-sha>
```

---

# 44. Why SHA-Based Image Tag?

If we use:

```text
latest
```

it can be ambiguous.

A SHA tag gives:

```text
Git commit
     ↓
Docker image
```

So we know exactly which source version produced the image.

This improves traceability and reproducibility.

---

# 45. Kubernetes Image Pull Secret

The image is stored in:

```text
GHCR
```

If the repository/image is private, Kubernetes needs authentication.

Therefore:

```text
GHCR credentials
       ↓
Kubernetes Secret
       ↓
imagePullSecrets
       ↓
Pod pulls image
```

---

# 46. GitHub Pages

Pages contains four main outputs:

```text
Automation Test Reports

├── Runner Allure Report
├── Kubernetes Allure Report
├── Runner Scenario Logs
└── Kubernetes Scenario Logs
```

Structure:

```text
pages/
├── index.html
├── runner/
├── kubernetes/
├── runner-scenario-logs/
└── k8s-scenario-logs/
```

---

# 47. Why Scenario Log Index Pages?

GitHub Pages doesn't automatically provide a directory listing like a traditional web server.

If we link:

```text
./runner-scenario-logs/
```

there must be:

```text
runner-scenario-logs/index.html
```

Otherwise we get:

```text
404
```

So we generate an index page listing every `.log` file.

---

# 48. GitHub Actions Artifacts vs GitHub Pages

Important distinction.

### GitHub Actions artifact

Used for:

> temporary build/test output associated with a workflow run.

Example:

```text
runner-run-results
k8s-run-results
```

### GitHub Pages

Used for:

> human-accessible published reports.

Therefore:

```text
Test execution
    ↓
Artifact
    ↓
Pages build
    ↓
Published report
```

---

# 49. Why Not Put Logs Inside Allure?

Allure is for:

```text
test execution reporting
```

Scenario logs are:

```text
raw diagnostic information
```

Keeping them separate gives:

```text
Allure → test results
Logs   → detailed debugging
```

This makes troubleshooting easier.

---

# 50. Maven

Maven handles:

* dependency management
* compilation
* test execution
* plugins
* lifecycle

Important lifecycle:

```text
validate
compile
test
package
...
```

For our framework:

```text
mvn clean test
```

means:

```text
clean
 ↓
remove target
 ↓
compile
 ↓
test
```

---

# 51. Why Maven?

Benefits:

* dependency management
* standard Java project structure
* plugin ecosystem
* CI/CD compatibility
* reproducible builds

Our project follows:

```text
src/main/java
src/test/java
src/test/resources
```

---

# 52. TestNG + Cucumber

Cucumber provides:

```text
BDD
Feature
Scenario
Steps
Hooks
```

TestNG provides:

```text
test execution integration
parallel execution capabilities
CI integration
```

Our runner extends:

```java
AbstractTestNGCucumberTests
```

Therefore:

```text
TestNG
   ↓
Cucumber
   ↓
Feature execution
```

---

# 53. Why Not JUnit?

Cucumber supports multiple runners.

We selected TestNG because it integrates well with:

* enterprise test execution
* parallel execution
* CI/CD
* suite management

The important point is consistency with the framework's execution model.

---

# 54. Feature → Step Mapping

Suppose:

```gherkin
Given I create an event through API
```

Cucumber matches:

```java
@Given("I create an event through API")
```

Then:

```text
Step Definition
      ↓
EventsApiClient
      ↓
RestAssured
      ↓
API
```

The feature file never knows how RestAssured works.

---

# 55. Generic Response Validation

Instead of writing:

```text
verify login response status
verify event response status
verify booking response status
```

separately, a generic step can validate:

```gherkin
Then the API response status should be 200
```

This works because:

```text
ScenarioContext
     ↓
lastApiResponse
```

stores the latest response.

This reduces duplicate step definitions.

---

# 56. Business Validation vs Technical Validation

There are two kinds of API validation.

### Technical

```text
status = 200
Content-Type = application/json
```

### Business

```text
event exists
event ID matches
event name is correct
```

Both are useful.

The framework separates:

```text
HTTP-level validation
```

from:

```text
business-level validation
```

---

# 57. Why API + UI in Same Scenario?

This is the main value proposition of the framework.

Example:

```text
Given I create an event through API
      ↓
eventId stored in ScenarioContext

When I open EventHub
      ↓
Page Object

Then I should see the event
      ↓
UI validation

When I update the event through API
      ↓
API Client

Then the updated event should be visible
```

The shared state is:

```text
ScenarioContext
```

---

# 58. Framework Responsibility Map

Memorize this table.

| Component        | Responsibility             |
| ---------------- | -------------------------- |
| Cucumber         | BDD execution              |
| TestNG           | Test execution integration |
| Step Definitions | Test orchestration         |
| API Clients      | API communication          |
| Page Objects     | UI interaction             |
| ScenarioContext  | Scenario state             |
| PicoContainer    | Dependency injection       |
| DriverFactory    | Driver creation            |
| DriverManager    | Driver lifecycle           |
| ThreadLocal      | Thread isolation           |
| ConfigReader     | Configuration loading      |
| APIRequestSpec   | API request defaults       |
| RestAssured      | HTTP automation            |
| Selenium         | Browser automation         |
| SLF4J            | Logging abstraction        |
| Logback          | Logging implementation     |
| Allure Adapter   | Raw test results           |
| Allure Maven     | HTML report                |
| Docker           | Environment packaging      |
| Kubernetes Job   | Test execution             |
| PVC              | Persistent test output     |
| Collector Pod    | Retrieve PVC data          |
| GitHub Actions   | CI/CD                      |
| GitHub Pages     | Report publishing          |

---

# 59. Most Important Design Principles

### 1. Single Responsibility

Each component should have one major responsibility.

```text
API Client ≠ assertions
Page Object ≠ assertions
DriverManager ≠ driver creation
PicoContainer ≠ scenario state
```

---

### 2. Separation of Concerns

```text
BDD
↓
Steps
↓
Framework
↓
Tools
```

---

### 3. Reusability

Common behavior is centralized.

Examples:

```text
APIRequestSpec
WaitUtils
ElementActions
BasePage
DriverManager
ScenarioContext
```

---

### 4. Configuration over Hardcoding

Environment-specific values belong in configuration.

---

### 5. Observability

Every test should provide:

```text
Allure report
+
scenario logs
+
API logs
```

---

### 6. CI/CD First

The framework should not depend on:

```text
developer laptop
```

It should run through:

```text
Maven
Docker
Kubernetes
GitHub Actions
```

---

# 60. Common Interview Questions

## Why use ThreadLocal?

> To isolate WebDriver instances between parallel execution threads and prevent one thread from overwriting another thread's driver.

---

## Is ThreadLocal scenario scoped?

> No. ThreadLocal is thread scoped. Scenario lifecycle is managed by Cucumber. Our hooks explicitly create and clean up the driver.

---

## Why PicoContainer?

> To provide dependency injection for Cucumber glue classes and allow scenario-scoped objects such as ScenarioContext to be shared cleanly between step classes.

---

## Why ScenarioContext?

> To share scenario-specific state such as authentication tokens, IDs, credentials and API responses between independent step definition classes.

---

## Why don't API clients assert?

> Because API clients should be responsible for HTTP communication. Assertions belong to the test layer so that clients remain reusable.

---

## Why don't Page Objects assert?

> Page Objects model UI behavior. Assertions belong to the test/step layer, which keeps interaction and validation separate.

---

## Why return Response from API Client?

> It keeps the client generic. Different tests may need different DTOs or raw response information, so deserialization and validation are performed at the test layer.

---

## Why use DTOs?

> DTOs give type-safe access to API data and avoid scattering JSON-path strings throughout the test code.

---

## Why use a RequestSpecification?

> To centralize common API configuration such as base URL, content type, authentication and filters, reducing duplication across API clients.

---

## Why use API logging filters?

> To centralize request/response logging rather than adding logging code to every API method.

---

## Why sanitize logs?

> API logs can contain credentials and tokens. Sensitive information should never be exposed in CI logs or published artifacts.

---

## Why use Kubernetes Job instead of Deployment?

> Tests are finite workloads. A Kubernetes Job represents a workload that should execute and terminate after completion.

---

## Why PVC?

> The test Pod is ephemeral, while test results need to survive after the Pod exits. A PVC provides persistent storage.

---

## Why mount results outside `target`?

> Maven clean owns the target directory and deletes it. Mounting a PVC inside target prevents Maven from cleaning it correctly.

---

## Why Docker?

> Docker provides a consistent execution environment containing Java, Maven, browser dependencies and the framework.

---

## Why Kubernetes?

> It allows the same containerized automation workload to run as an isolated, repeatable Job and provides infrastructure-level orchestration.

---

## Why GitHub Actions?

> It automates the complete lifecycle from test execution through Docker image creation, Kubernetes execution, result collection and report publication.

---

# 61. If Asked to Explain the Complete Flow

Memorize this answer:

> "When a pipeline starts, GitHub Actions first executes the framework on the runner using Maven. Cucumber runs the scenarios through TestNG. API steps communicate through RestAssured API clients, while UI steps use Page Objects and Selenium. ScenarioContext allows API and UI steps to share scenario state, while PicoContainer handles dependency injection and ThreadLocal isolates WebDriver instances.
>
> After the runner execution, the Docker image is built and pushed to GHCR using the Git commit SHA as the image tag. Kubernetes then creates a Job using that image and a PVC for persistent test output. The Job writes Allure results and scenario logs under `/results`. Once execution finishes, a collector Pod mounts the same PVC and copies the results back to the GitHub runner.
>
> Finally, the Pages job combines the runner and Kubernetes results, generates the Allure HTML reports, creates scenario-log index pages, and publishes everything to GitHub Pages."

---

# 62. If Asked "What Was the Hardest Problem?"

Use the Kubernetes/Maven issue.

> "One issue I encountered was mounting the persistent volume under Maven's `target` directory. The test container was running `mvn clean test`, and Maven clean tried to delete `/workspace/target`. Because the PVC was mounted inside that directory, Maven couldn't delete the mounted path and the build failed.
>
> I separated Maven's temporary build directory from persistent test output. Maven owns `/workspace/target`, while Kubernetes mounts the PVC at `/results`, with `/results/allure` and `/results/logs`. This allowed Maven clean to operate normally while preserving test artifacts."

That's a **very good real-world debugging story** because it demonstrates understanding rather than just configuration.

---

# 63. If Asked "What Would You Improve?"

Don't say:

> "Nothing."

Say:

> "The current architecture is intentionally modular, but there are areas I would evolve based on scale."

Possible improvements:

```text
1. Parallel execution
2. Better retry strategy
3. API contract/schema validation
4. More comprehensive environment configuration
5. Secret management
6. Test data management
7. Container image optimization
8. Kubernetes resource limits
9. Better failed-job detection
10. Distributed execution
```

Then explain that you wouldn't add these until the requirement exists.

---

# 64. Anti-Patterns to Avoid Saying

Don't say:

> "ThreadLocal makes each scenario independent."

Better:

> "ThreadLocal isolates driver storage per thread; Cucumber manages scenario lifecycle."

Don't say:

> "PicoContainer stores scenario data."

Better:

> "PicoContainer manages dependency injection; ScenarioContext stores scenario data."

Don't say:

> "Allure generates reports during the test."

Better:

> "The Allure adapter collects raw results during execution; the reporting layer generates the HTML report."

Don't say:

> "PVC is used to store the Pod."

Better:

> "PVC persists test output beyond the lifecycle of the ephemeral test Pod."

Don't say:

> "API client validates the API."

Better:

> "API client executes the request; test steps perform validation."

---

# 65. The Mental Model to Remember Before Interview

If you forget everything else, remember this:

```text
                  ┌──────────────────┐
                  │    Cucumber      │
                  │  BDD / Scenario  │
                  └────────┬─────────┘
                           │
                    Step Definitions
                           │
              ┌────────────┴────────────┐
              │                         │
          API Layer                  UI Layer
              │                         │
        API Clients                 Page Objects
              │                         │
        RestAssured                 Selenium
              │                         │
              └────────────┬────────────┘
                           │
                   ScenarioContext
                           │
                    Shared Test State

        ┌─────────────────────────────────────┐
        │ Infrastructure                      │
        │                                     │
        │ Maven → Docker → Kubernetes         │
        │                    │                │
        │                    PVC              │
        │                    │                │
        │               Test Results          │
        └────────────────────┬────────────────┘
                             │
                       GitHub Actions
                             │
                    ┌────────┴────────┐
                    │                 │
                 Allure            Logs
                    │                 │
                    └────────┬────────┘
                             │
                       GitHub Pages
```

### The five things you absolutely need to be able to explain

```text
1. Why API + UI?
2. Why ScenarioContext + PicoContainer?
3. Why ThreadLocal + DriverManager?
4. Why Docker + Kubernetes + PVC?
5. How test execution becomes a published Allure report?
```