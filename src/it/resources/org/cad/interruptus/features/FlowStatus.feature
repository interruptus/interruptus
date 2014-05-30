Feature: Configure flows

Background:
    Given I clear the zookeeper configuration
    And I have the amqp exchange "test_collectd_metrics"

Scenario Outline: Start and Stop flows

    Given the following types exist:
        | {"name":"EventLog","properties":{"timestamp":"long","message":"string","eventType":"string"}} |

    And I have the flow "<json>" configured
    When I check the flow status for "<name>" the response should be:
        """
        {"name": "<name>", "status":"STOPPED"}
        """

    When I start the flow "<name>" the response should be "true"
    When I check the flow status for "<name>" the response should be:
        """
        {"name": "<name>", "status":"RUNNING"}
        """

    When I stop the flow "<name>" the response should be "true"
    When I check the flow status for "<name>" the response should be:
        """
        {"name": "<name>", "status":"STOPPED"}
        """

    Examples:
        | name      | json                                                                                                                                                                                                                                                                                                                        |
        | FlowIn1  | {"name": "FlowIn1", "query":"create dataflow FlowIn1 AMQPSource -> FlowIn1<EventLog> {  host: 'localhost',  exchange: 'test_collectd_metrics', port: 5672, username: 'guest',  password: 'guest',  routingKey: '#', collector: {class: 'org.cad.interruptus.AMQPJsonToMap'}, logMessages: true  } EventBusSink(FlowIn1){}"}  |
