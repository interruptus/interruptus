package org.cad.interruptus.features.steps;

import com.sun.jersey.api.client.ClientResponse;
import cucumber.api.DataTable;
import cucumber.api.java.en.*;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class TypeConfigStepDef extends BaseResourceSteps
{
    ClientResponse listResponse;
    ClientResponse getResponse;
    
    @Given("^the following types exist:$")
    public void the_following_types_exist(DataTable table) throws Throwable 
    {
        for (String data : table.asList(String.class)) {
            this.postResource("type", data);
        }
    }

    @Given("^I have the type \"(.*?)\" configured$")
    public void i_have_the_type_configured$(String data) throws Throwable
    {
        this.postResource("type", data);
    }

    @When("^I list all types$")
    public void i_list_all_types() throws Throwable
    {
        listResponse = this.getResource("type");
    }

    @Then("^the list response should contain \"(.*?)\"$")
    public void the_list_response_should_contain(String data) throws Throwable
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @When("^I get the type configuration for \"(.*?)\" the response should be \"(.*?)\"$$")
    public void i_get_the_type_configuration_for(String name, String data) throws Throwable
    {
        getResponse = this.getResource("type/" + name);

        final JSONObject expectedJson = new JSONObject(data);
        final JSONObject actualJson   = new JSONObject(getResponse.getEntity(String.class));

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }
}
