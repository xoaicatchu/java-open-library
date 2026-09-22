Feature: Get Products
  As a customer
  I want to search products by category
  So that I can find what I need

  Background:
    Given the product repository is empty
    And the following products exist in the database:
      | name   | category    | price |
      | Laptop | Electronics | 1500  |
      | Phone  | Electronics | 800   |
      | Desk   | Furniture   | 200   |

  Scenario Outline: Search by category
    When I search for products with category "<category>"
    Then the response status should be 200
    And the response should contain <count> products

    Examples:
      | category    | count |
      | Electronics | 2     |
      | Furniture   | 1     |
      | Clothing    | 0     |
