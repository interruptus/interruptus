Feature: Configure types

Background:
    Given I clear the zookeeper configuration

Scenario Outline: Configure types

    Given I have the type "<json>" configured
    When I list all types
    Then the type list response should contain "<json>"
    When I get the type configuration for "<name>" the response should be "<json>"

    Examples:
        | name  | json                                                             |
        | type1 | {"name":"type1","properties":{"name":"string","value":"double"}} |
        | type2 | {"name":"type2","properties":{"key":"string","value":"string"}}  |
        | type1 | {"name":"type1","properties":{"key":"string"}}                   |
