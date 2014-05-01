Feature: Configure types

  Scenario: Create and list types
    Given I have these types configured:
      | {"name":"type1","properties":[{"name":"name","type":"string"},{"name":"value","type":"double"}]} |
      | {"name":"type2","properties":[{"name":"key","type":"string"},{"name":"value","type":"string"}]} |

    When I list all types
    Then the list response should contain:
      | {"name":"type1","properties":[{"name":"name","type":"string"},{"name":"value","type":"double"}]} |
      | {"name":"type2","properties":[{"name":"key","type":"string"},{"name":"value","type":"string"}]} |

Scenario Outline: Reading

  When I get the type configuration for "<name>"
  Then the get response should be "<json>"

  Examples:
    | name  | json 																							   |
	| type1 | {"name":"type1","properties":[{"name":"name","type":"string"},{"name":"value","type":"double"}]} |
	| type2 | {"name":"type2","properties":[{"name":"key","type":"string"},{"name":"value","type":"string"}]}  |
