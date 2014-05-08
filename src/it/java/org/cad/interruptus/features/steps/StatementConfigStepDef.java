package org.cad.interruptus.features.steps;

import com.sun.jersey.api.client.ClientResponse;
import cucumber.api.DataTable;
import cucumber.api.java.en.*;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.Assert.fail;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class StatementConfigStepDef extends BaseResourceSteps
{
    ClientResponse listResponse;
    ClientResponse getResponse;

    @Given("^the following statements exist:$")
    public void the_following_statements_exist(DataTable table) throws Throwable 
    {
        for (String data : table.asList(String.class)) {
            this.postResource("statement", data);
        }
    }

    @Given("^I have the statement \"(.*?)\" configured$")
    public void i_have_the_statement_configured$(String data) throws Throwable
    {
        this.postResource("statement", data);
    }

    @When("^I list all statements$")
    public void i_list_all_statements() throws Throwable
    {
        listResponse = this.getResource("statement");
    }

    @Then("^the statement list should contain \"(.*?)\"$")
    public void the_statement_list_should_contain(String data) throws Throwable
    {
        final String response                   = listResponse.getEntity(String.class);
        final JSONArray jsonArray               = new JSONArray(response);
        final Map<String, JSONObject> actualMap = new HashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            final JSONObject object = jsonArray.getJSONObject(i);
            final String  name      = String.valueOf(object.get("name"));

            actualMap.put(name, object);
        }
        
        final JSONObject expectedJson = new JSONObject(data);
        final String  name            = String.valueOf(expectedJson.get("name"));
        final JSONObject actualJson   = actualMap.containsKey(name) ? actualMap.get(name): null;

        if (actualJson == null) {
            fail(String.format("Failed to assert that statement '%s' exists", expectedJson.get("name")));

            return;
        }

        JSONAssert.assertEquals(expectedJson, actualMap.get(name), JSONCompareMode.LENIENT);
    }

    @When("^I get the statement configuration for \"(.*?)\" the response should be \"(.*?)\"$$")
    public void i_get_the_statement_configuration_for(String name, String data) throws Throwable
    {
        getResponse = this.getResource("statement/" + name);

        final JSONObject expectedJson = new JSONObject(data);
        final JSONObject actualJson   = new JSONObject(getResponse.getEntity(String.class));

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }
    
    @When("^I check the statement status for \"(.*?)\" the response should be:$")
    public void i_check_the_statement_status_for_the_response_should_be(String name, String data) throws Throwable 
    {
        final ClientResponse response = this.getResource("statement/" + name + "/state");
        final String actualResponse   = response.getEntity(String.class);

        final JSONObject expectedJson = new JSONObject(data);
        final JSONObject actualJson   = new JSONObject(actualResponse);

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }
}
