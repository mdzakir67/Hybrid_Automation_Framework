```text
┌─────────────────────────────────────────────────────────────────┐
│                         1. MAVEN                                │
│                                                                 │
│  mvn test                                                       │
│                                                                 │
│  Maven loads dependencies:                                      │
│  • Cucumber                                                     │
│  • Cucumber TestNG                                              │
│  • Cucumber PicoContainer                                       │
│  • Selenium                                                     │
│  • RestAssured                                                  │
│                                                                 │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                         2. TESTNG                               │
│                                                                 │
│  Finds your TestRunner                                          │
│                                                                 │
│  public class TestRunner                                        │
│      extends AbstractTestNGCucumberTests                        │
│                                                                 │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                       3. CUCUMBER                               │
│                                                                 │
│  Reads @CucumberOptions                                         │
│                                                                 │
│  features = ".../features"                                      │
│  glue     = "org.ge.vernova"                                    │
│  plugins  = ...                                                  │
│                                                                 │
│  Finds your .feature files                                      │
│                                                                 │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    4. FEATURE PARSING                           │
│                                                                 │
│  Cucumber reads:                                                │
│                                                                 │
│  Scenario: Login with valid credentials                         │
│      Given I open EventHub                                      │
│      When I login with valid credentials                        │
│      Then I should be logged in                                 │
│                                                                 │
│  Cucumber now knows:                                             │
│                                                                 │
│       "I have one scenario to execute."                         │
│                                                                 │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
                 ┌──────────────────────────────┐
                 │       SCENARIO STARTS        │
                 └──────────────┬───────────────┘
                                │
              ┌─────────────────┴──────────────────┐
              │                                    │
              ▼                                    ▼
       (CONSTRUCTION)                        (EXECUTION)
       OBJECT MANAGEMENT                    SCENARIO LIFECYCLE
              │                                    │
              │                                    │
              ▼                                    ▼
       ObjectFactory                           @Before
              │                                    │
              ▼                                    ▼
       PicoContainer                    DriverManager.initializeDriver()
              │                                    │
              │                                    ▼
              │                            DriverFactory
              │                                    │
              │                                    ▼
              │                              ChromeDriver
              │                                    │
              │                                    ▼
              │                         ThreadLocal<WebDriver>
              │                                    │
              │                                    │
              └─────────────────┬──────────────────┘
                                │
                                ▼
                   FIRST GHERKIN STEP
                                │
                                ▼
                  "Given I open EventHub"
                                │
                                ▼
                    Cucumber finds matching
                       step definition
                                │
                                ▼
                       EventHubSteps
                                │
                                ▼
                    landingPage.open()
                                │
                                ▼
                     EventsHubLandingPage
                                │
                                ▼
                       WebDriver operation
                                │
                                ▼
                         Chrome browser
```

## Allure reporting

This framework is configured to generate Allure reports for Cucumber scenarios.

### Run the suite

```bash
mvn clean test
```

### Generate the HTML report

```bash
mvn allure:report
```

### Open the report locally

```bash
mvn allure:serve
```

The report is generated from the `target/allure-results` folder and includes screenshot attachments for failed scenarios.

### Question

**Why don't we initialize/capture the WebDriver inside the Page Object constructor?**

### Answer

We avoid **capturing the driver during Page Object construction** because the Page Object and WebDriver are managed through **different lifecycles**.

* **PicoContainer** manages the lifecycle and dependency injection of our Step Classes and Page Objects.
* **Cucumber Hooks + DriverManager/ThreadLocal** manage the WebDriver lifecycle.
* If the Page Object constructor calls `DriverManager.getDriver()`, its construction becomes dependent on the WebDriver already being initialized.

So we keep the lifecycles separate and obtain the **current WebDriver when the Page Object performs a UI operation**, rather than capturing it during construction.

> **In short: separate object lifecycle from browser lifecycle.**
