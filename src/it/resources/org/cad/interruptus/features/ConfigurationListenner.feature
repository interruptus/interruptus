Feature: Configuration listener

Background:
    Given I clear the zookeeper configuration
    And the following types exist:
        | {"name":"type1","properties":[{"name":"value1","type":"string"}]} |

Scenario: Listener for configuration changes

    Given the following configuration put in zookeeper:
        """
        {"types":{"type1":{"name":"type1","properties":[{"name":"value1","type":"string"}]},"type2":{"name":"type2","properties":[{"name":"value2","type":"string"}]}},"flows":{},"statements":{}}
        """

    When I get the type configuration for "type1" the response should be:
        """
        {"name":"type1","properties":[{"name":"value1","type":"string"}]}
        """

    When I get the type configuration for "type2" the response should be:
        """
        {"name":"type2","properties":[{"name":"value2","type":"string"}]}
        """