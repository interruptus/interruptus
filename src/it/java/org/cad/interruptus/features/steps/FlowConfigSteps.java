package org.cad.interruptus.features.steps;

import com.sun.jersey.api.client.ClientResponse;
import cucumber.api.DataTable;
import cucumber.api.java.en.*;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class FlowConfigSteps extends BaseResourceSteps
{
    ClientResponse listResponse;
    ClientResponse getResponse;

    @Given("^the following flows exist:$")
    public void the_following_flows_exist(DataTable table) throws Throwable 
    {
        for (String data : table.asList(String.class)) {
            this.postResource("flow", data);
        }
    }

    @Given("^I have the flow \"(.*?)\" configured$")
    public void i_have_the_flow_configured$(String data) throws Throwable
    {
        this.postResource("flow", data);
    }

    @When("^I list all flows$")
    public void i_list_all_flows() throws Throwable
    {
        listResponse = this.getResource("flow");
    }

    @Then("^the flow list response should contain \"(.*?)\"$")
    public void the_flow_list_response_should_contain(String data) throws Throwable
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
            fail(String.format("Failed to assert that flow '%s' exists", expectedJson.get("name")));

            return;
        }

        JSONAssert.assertEquals(expectedJson, actualMap.get(name), JSONCompareMode.LENIENT);
    }

    @When("^I get the flow configuration for \"(.*?)\" the response should be \"(.*?)\"$$")
    public void i_get_the_flow_configuration_for(String name, String data) throws Throwable
    {
        getResponse = this.getResource("flow/" + name);

        final JSONObject expectedJson = new JSONObject(data);
        final JSONObject actualJson   = new JSONObject(getResponse.getEntity(String.class));

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @When("^I check the flow status for \"(.*?)\" the response should be:$")
    public void i_check_the_flow_status_for_the_response_should_be(final String name, final String data) throws Throwable 
    {
        final ClientResponse response = this.getResource("flow/" + name + "/state");
        final String actualResponse   = response.getEntity(String.class);

        final JSONObject expectedJson = new JSONObject(data);
        final JSONObject actualJson   = new JSONObject(actualResponse);

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @When("^I start the flow \"(.*?)\" the response should be \"(.*?)\"$")
    public void i_start_the_flow_the_response_should_be(String name, String expectedResponse) throws JSONException
    {
        final ClientResponse response = this.postResource("flow/" + name + "/start");
        final String actualResponse   = response.getEntity(String.class);

        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @When("^I stop the flow \"(.*?)\" the response should be \"(.*?)\"$")
    public void i_stop_the_flow_the_response_should_be(String name, String expectedResponse) throws JSONException
    {
        final ClientResponse response = this.postResource("flow/" + name + "/stop");
        final String actualResponse   = response.getEntity(String.class);

        Assert.assertEquals(expectedResponse, actualResponse);
    }
}
