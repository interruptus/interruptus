package org.cad.interruptus.features.steps;

import cucumber.api.java.en.*;
import org.apache.zookeeper.data.Stat;
import org.cad.interruptus.features.BaseIntegration;

public class ZookeeperSteps extends BaseIntegration
{
    String path = "/interruptus/configuration";

    @Given("^I clear the zookeeper configuration$")
    public void i_clear_all_data_in_zookeeper() throws Throwable 
    {
        if (curator.checkExists().forPath(path) == null) {
            return;
        }

        curator.delete()
            .guaranteed()
            .forPath(path);
    }
    
    @Given("^the following configuration exist in zookeeper:$")
    public void the_following_configuration_exist_in_zookeeper(String json) throws Exception
    {
        final byte[] data = json.getBytes();
        final Stat status = curator.checkExists().forPath(path);

        if (status != null) {
            curator.setData()
                .compressed()
                .forPath(path, data);

            return;
        }

        curator.create()
            .compressed()
            .creatingParentsIfNeeded()
            .forPath(path, data);
    }
}