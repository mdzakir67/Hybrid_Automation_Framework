package org.ge.vernova.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.ge.vernova.context.ScenarioContext;
import org.ge.vernova.driver.DriverManager;
import org.ge.vernova.utils.ScreenshotUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.ByteArrayInputStream;
import java.util.UUID;

public class Hooks {


    private final static Logger log = LoggerFactory.getLogger(Hooks.class);
    private ScenarioContext context;

    public Hooks(ScenarioContext context) {
        this.context = context;
    }

    @Before
    public void setUp(Scenario scenario) {
        String scenarioName = sanitizeScenarioName(scenario.getName());
        String scenarioID = UUID.randomUUID().toString().replaceAll("-", "").substring(0,8);
        MDC.put("scenarioName", scenarioName);
        context.set("scenarioId",scenarioID);
        log.info("Started Scenario %s".formatted(scenarioName));
        DriverManager.initializeDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            WebDriver driver = DriverManager.getDriver();
            String scenarioName = sanitizeScenarioName(scenario.getName());
            if (scenario.isFailed() && driver != null) {
                log.info("Failed Scenario %s".formatted(scenarioName));
                byte[] screenshot = ScreenshotUtils.captureScreenshot();
                if (screenshot.length > 0) {
                    scenario.attach(screenshot, "image/png", scenarioName + ".png");
                }
            } else {
                log.info("Scenario Passed %s".formatted(scenario.getName()));
            }

            log.info("Finished Scenario %s".formatted(scenario.getName()));
            DriverManager.tearDown();
        }
        finally {
            MDC.remove("scenarioName");
        }
    }

    private String sanitizeScenarioName(String scenarioName) {
        return scenarioName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

}
