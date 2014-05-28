Feature: Configure statements

Background:
    Given I clear the zookeeper configuration

Scenario Outline: Start and Stop statements

    Given the following types exist:
        | {"name":"EventLog","properties":[{"name":"timestamp","type":"long"},{"name":"message","type":"string"},{"name":"eventType","type":"string"}]} |

    And I have the statement "<json>" configured
    When I check the statement status for "<name>" the response should be:
        """
        {"name": "<name>", "status":"STOPPED"}
        """

    When I start the statement "<name>" the response should be "true"
    When I check the statement status for "<name>" the response should be:
        """
        {"name": "<name>", "status":"STARTED"}
        """

    When I stop the statement "<name>" the response should be "true"
    When I check the statement status for "<name>" the response should be:
        """
        {"name": "<name>", "status":"STOPPED"}
        """

    Examples:
        | name  | json                                                 |
        | sttm1 | {"name": "sttm1", "query":"SELECT * FROM EventLog"}  |
