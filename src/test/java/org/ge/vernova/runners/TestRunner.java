package org.ge.vernova.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
       features = "src/test/resources/features",
       glue = {
               "org.ge.vernova.steps",
               "org.ge.vernova.hooks"
       },
       plugin = {
               "html:target/cucumber-report.html",
               "json:target/cucumber-report.json",
               "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
       },
       monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @DataProvider(parallel = true)
    @Override
    public Object[][] scenarios() {
        return super.scenarios();
    }
}