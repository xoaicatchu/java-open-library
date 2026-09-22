package com.example.cucumber.stepdefs;

import com.example.cucumber.dto.ProductDto;
import com.example.cucumber.entity.Product;
import com.example.cucumber.repository.ProductRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    private ResponseEntity<?> latestResponse;

    @Given("the product repository is empty")
    public void theProductRepositoryIsEmpty() {
        productRepository.deleteAll();
    }

    @Given("the following products exist in the database:")
    public void theFollowingProductsExistInTheDatabase(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            Product p = new Product(null, row.get("name"), row.get("category"), Double.parseDouble(row.get("price")));
            productRepository.save(p);
        }
    }

    @When("I request to create a product with name {string}, category {string}, and price {double}")
    public void iRequestToCreateAProductWithNameCategoryAndPrice(String name, String category, Double price) {
        ProductDto dto = new ProductDto(name, category, price);
        latestResponse = restTemplate.postForEntity("/api/products", dto, Object.class);
    }

    @When("I search for products with category {string}")
    public void iSearchForProductsWithCategory(String category) {
        latestResponse = restTemplate.getForEntity("/api/products?category=" + category, Product[].class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertThat(latestResponse.getStatusCode().value()).isEqualTo(status);
    }

    @And("the product {string} should exist in the database")
    public void theProductShouldExistInTheDatabase(String name) {
        boolean exists = productRepository.findAll().stream().anyMatch(p -> p.getName().equals(name));
        assertThat(exists).isTrue();
    }

    @And("the response should contain {int} products")
    public void theResponseShouldContainProducts(int count) {
        Product[] products = (Product[]) latestResponse.getBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(count);
    }
}
