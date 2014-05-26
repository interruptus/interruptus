Feature: Configuration listener

Background:
    Given I clear the zookeeper configuration

Scenario: Listener for configuration changes

    Given the following configuration exist in zookeeper:
        """
        {"types":{"type1":{"properties":[{"name":"timestamp","type":"long"},{"name":"message","type":"string"}],"name":"type1"}},"flows":{},"statements":{}}
        """

    When I get the type configuration for "type1" the response should be:
        """
        {"name":"type1","properties":[{"name":"timestamp","type":"long"},{"name":"message","type":"string"}]}
        """