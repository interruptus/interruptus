package org.cad.interruptus.features.steps;

import cucumber.api.java.en.*;
import org.cad.interruptus.features.BaseIntegration;

public class ZookeeperSteps extends BaseIntegration
{
    @Given("^I clear all data in zookeeper$")
    public void i_clear_all_data_in_zookeeper() throws Throwable 
    {
        curator.delete()
            .guaranteed()
            .deletingChildrenIfNeeded()
            .forPath("/interruptus");
    }
}