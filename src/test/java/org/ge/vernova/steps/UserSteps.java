package org.ge.vernova.steps;

import io.cucumber.java.en.Then;
import org.ge.vernova.context.ScenarioContext;

public class UserSteps {

    private final ScenarioContext context;

    public UserSteps(ScenarioContext context){
        this.context=context;
    }

    @Then("Scenario Context Verification Step")
    public void verify(){
        System.out.println(this.context.get("title",String.class));
    }
}
