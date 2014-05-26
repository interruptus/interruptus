Feature: Configure flows

Background:
    Given I clear the zookeeper configuration
    And I have the amqp exchange "test_collectd_metrics"
    And the following types exist:
        | {"name":"EventLog","properties":[{"name":"timestamp","type":"long"},{"name":"message","type":"string"},{"name":"eventType","type":"string"}]} |
    And the following statements exist:
        | {"name": "eventlog", "query":"SELECT * FROM EventLog", "debug": false} |

Scenario Outline: Configure a simple flow

    Given I have the flow "<json>" configured
    When I list all flows
    Then the flow list response should contain "<json>"
    When I get the flow configuration for "<name>" the response should be "<json>"

    When I check the flow status for "<name>" the response should be:
        """
        {"name": "<name>", "status":"STOPPED"}
        """

    Examples:
        | name  | json                                                                                             |
        | EventsIn | {"name": "EventsIn", "query":"create dataflow EventsIn AMQPSource -> EventsIn<EventLog> {  host: 'localhost',  exchange: 'test_collectd_metrics', port: 5672, username: 'guest',  password: 'guest',  routingKey: '#', collector: {class: 'org.cad.interruptus.AMQPJsonToMap'}, logMessages: true  } EventBusSink(EventsIn){}"} |
