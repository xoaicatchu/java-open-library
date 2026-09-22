Feature: Create Product
  As an admin
  I want to create products
  So that they are available in the system

  Background:
    Given the product repository is empty

  Scenario: Create with valid data
    When I request to create a product with name "Laptop", category "Electronics", and price 1500.0
    Then the response status should be 201
    And the product "Laptop" should exist in the database

  Scenario: Create with invalid data
    When I request to create a product with name "", category "Electronics", and price 1500.0
    Then the response status should be 400
