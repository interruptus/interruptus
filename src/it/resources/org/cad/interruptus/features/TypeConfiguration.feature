Feature: Configure types

Background:
    Given I clear all data in zookeeper

Scenario Outline: Type CRUD

    Given I have the type "<json>" configured
    When I list all types
    Then the list response should contain "<json>"
    When I get the type configuration for "<name>" the response should be "<json>"

    Examples:
        | name  | json 																							   |
        | type1 | {"name":"type1","properties":[{"name":"name","type":"string"},{"name":"value","type":"double"}]} |
        | type2 | {"name":"type2","properties":[{"name":"key","type":"string"},{"name":"value","type":"string"}]}  |
