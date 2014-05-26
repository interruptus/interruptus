Feature: Configure statements

Background:
    Given I clear the zookeeper configuration

Scenario Outline: Configure a simple statement

    Given the following types exist:
        | {"name":"EventLog","properties":[{"name":"timestamp","type":"long"},{"name":"message","type":"string"},{"name":"eventType","type":"string"}]} |

    Given I have the statement "<json>" configured
    When I list all statements
    Then the statement list should contain "<json>"
    When I get the statement configuration for "<name>" the response should be "<json>"

    When I check the statement status for "<name>" the response should be:
        """
        {"name": "<name>", "status":"STOPPED"}
        """

    Examples:
        | name          | json 	                                                                     |
        | eventlog      | {"name": "eventlog", "query":"SELECT * FROM EventLog", "debug": false}     |
        | eventlogdebug | {"name": "eventlogdebug", "query":"SELECT * FROM EventLog", "debug": true} |
