package org.cad.interruptus.features;

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

    @Given("^I have these types configured:$")
    public void i_have_these_types_configured(DataTable table) throws Throwable
    {
        for (String input : table.asList(String.class)) {
            this.post("type", input);
        }
    }

    @When("^I list all types$")
    public void i_list_all_types() throws Throwable
    {
        listResponse = this.get("type");
    }

    @Then("^the list response should contain:$")
    public void the_list_response_should_contain(DataTable table) throws Throwable
    {
        final String response                   = listResponse.getEntity(String.class);
        final JSONArray jsonArray               = new JSONArray(response);
        final Map<String, JSONObject> actualMap = new HashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            final JSONObject object = jsonArray.getJSONObject(i);
            final String  name      = String.valueOf(object.get("name"));

            actualMap.put(name, object);
        }

        for (String data : table.asList(String.class)) {
            final JSONObject expectedJson = new JSONObject(data);
            final String  name            = String.valueOf(expectedJson.get("name"));
            final JSONObject actualJson   = actualMap.containsKey(name) ? actualMap.get(name): null;

            JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
        }
    }

    @When("^I get the type configuration for \"(.*?)\"$")
    public void i_get_the_type_configuration_for(String name) throws Throwable
    {
        getResponse = this.get("type/" + name);
    }

    @Then("^the get response should be \"(.*?)\"$")
    public void the_get_response_should_be(String data) throws Throwable
    {
        final JSONObject expectedJson = new JSONObject(data);
        final JSONObject actualJson   = new JSONObject(getResponse.getEntity(String.class));

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }
}
