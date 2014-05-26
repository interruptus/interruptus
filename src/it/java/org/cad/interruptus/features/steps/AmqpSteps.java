package org.cad.interruptus.features.steps;

import com.rabbitmq.client.Channel;
import cucumber.api.java.After;
import cucumber.api.java.en.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.cad.interruptus.features.BaseIntegration;

public class AmqpSteps extends BaseIntegration
{
    final Set<String> exchanges = new HashSet<>();
    
    @Given("^I have the amqp exchange \"(.*?)\"$")
    public void i_have_the_amqp_exchange(final String exchange) throws IOException
    {
        final String type        = "topic";
        final boolean durable    = false;
        final boolean autoDelete = true;

        amqpConnection.createChannel()
            .exchangeDeclare(exchange, type, durable, autoDelete, null);

        exchanges.add(exchange);
    }

    @After
    public void afterScenario() throws IOException 
    {
        if (exchanges.isEmpty()) {
           return;
        }

        final Channel channel = amqpConnection.createChannel();

        for (final String exchange : exchanges) {
            channel.exchangeDelete(exchange);
        }
    }
}