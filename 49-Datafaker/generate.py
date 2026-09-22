import os

base_dir = r'd:\GitHub\java-example\49-Datafaker'

files = {
    'pom.xml': '''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.1</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>49-Datafaker</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>49-Datafaker</name>
    <description>Demo project for Datafaker with Spring Boot</description>
    <properties>
        <java.version>21</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>net.datafaker</groupId>
            <artifactId>datafaker</artifactId>
            <version>2.4.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
''',
    '.mvn/wrapper/maven-wrapper.properties': '''distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
''',
    'src/main/resources/application.yml': '''server:
  port: 8149
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:fakerdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false
''',
    'src/main/java/com/example/datafaker/DatafakerApplication.java': '''package com.example.datafaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DatafakerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatafakerApplication.class, args);
    }
}
''',
    'src/main/java/com/example/datafaker/entity/Product.java': '''package com.example.datafaker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String department;
    private BigDecimal price;

    public Product() {}
    public Product(String name, String department, BigDecimal price) {
        this.name = name; this.department = department; this.price = price;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
''',
    'src/main/java/com/example/datafaker/entity/Customer.java': '''package com.example.datafaker.entity;

import jakarta.persistence.*;

@Entity
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;

    public Customer() {}
    public Customer(String fullName, String email, String phoneNumber, String address) {
        this.fullName = fullName; this.email = email; this.phoneNumber = phoneNumber; this.address = address;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
''',
    'src/main/java/com/example/datafaker/entity/CustomerOrder.java': '''package com.example.datafaker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class CustomerOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private LocalDate orderDate;
    
    @ManyToOne
    private Customer customer;

    public CustomerOrder() {}
    public CustomerOrder(String orderNumber, LocalDate orderDate, Customer customer) {
        this.orderNumber = orderNumber; this.orderDate = orderDate; this.customer = customer;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
''',
    'src/main/java/com/example/datafaker/repository/ProductRepository.java': '''package com.example.datafaker.repository;
import com.example.datafaker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> {}
''',
    'src/main/java/com/example/datafaker/repository/CustomerRepository.java': '''package com.example.datafaker.repository;
import com.example.datafaker.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerRepository extends JpaRepository<Customer, Long> {}
''',
    'src/main/java/com/example/datafaker/repository/CustomerOrderRepository.java': '''package com.example.datafaker.repository;
import com.example.datafaker.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {}
''',
    'src/main/java/com/example/datafaker/faker/ECommerceProvider.java': '''package com.example.datafaker.faker;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class ECommerceProvider extends AbstractProvider<BaseProviders> {

    public ECommerceProvider(BaseProviders faker) {
        super(faker);
    }

    public String orderStatus() {
        return faker.options().option("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");
    }
    
    public String discountCode() {
        return "DISCOUNT" + faker.number().numberBetween(10, 50);
    }
}
''',
    'src/main/java/com/example/datafaker/faker/MyFaker.java': '''package com.example.datafaker.faker;

import net.datafaker.Faker;
import java.util.Locale;
import java.util.Random;

public class MyFaker extends Faker {
    
    public MyFaker() {
        super();
    }
    
    public MyFaker(Locale locale) {
        super(locale);
    }

    public MyFaker(Random random) {
        super(random);
    }

    public ECommerceProvider eCommerce() {
        return getProvider(ECommerceProvider.class, ECommerceProvider::new, this);
    }
}
''',
    'src/main/java/com/example/datafaker/service/FakerService.java': '''package com.example.datafaker.service;

import com.example.datafaker.entity.Customer;
import com.example.datafaker.entity.CustomerOrder;
import com.example.datafaker.entity.Product;
import com.example.datafaker.faker.MyFaker;
import com.example.datafaker.repository.CustomerOrderRepository;
import com.example.datafaker.repository.CustomerRepository;
import com.example.datafaker.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class FakerService {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CustomerOrderRepository orderRepository;

    public FakerService(ProductRepository productRepository, CustomerRepository customerRepository, CustomerOrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    public List<Customer> generateVietnameseCustomers(int count) {
        MyFaker vnFaker = new MyFaker(new Locale("vi", "VN"));
        return vnFaker.collection(() -> new Customer(
                vnFaker.name().fullName(),
                vnFaker.internet().emailAddress(),
                vnFaker.phoneNumber().phoneNumber(),
                vnFaker.address().fullAddress()
        )).len(count).generate();
    }

    public Customer generateDeterministicCustomer(long seed) {
        MyFaker faker = new MyFaker(new Random(seed));
        return new Customer(
                faker.name().fullName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.address().fullAddress()
        );
    }
    
    public List<Product> generateProductsFromExpression(int count) {
        MyFaker faker = new MyFaker();
        return faker.collection(() -> new Product(
                faker.expression("#{Commerce.productName}"),
                faker.expression("#{Commerce.department}"),
                new BigDecimal(faker.commerce().price())
        )).len(count).generate();
    }
    
    public String generateDiscountCode() {
        MyFaker faker = new MyFaker();
        return faker.eCommerce().discountCode();
    }

    @Transactional
    public void seedDatabase() {
        MyFaker faker = new MyFaker();
        
        // Products
        List<Product> products = faker.collection(() -> new Product(
                faker.commerce().productName(),
                faker.commerce().department(),
                new BigDecimal(faker.commerce().price())
        )).len(10).generate();
        productRepository.saveAll(products);

        // Customers
        List<Customer> customers = faker.collection(() -> new Customer(
                faker.name().fullName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.address().fullAddress()
        )).len(5).generate();
        customerRepository.saveAll(customers);

        // Orders
        List<CustomerOrder> orders = faker.collection(() -> new CustomerOrder(
                faker.commerce().promotionCode(6),
                faker.timeAndDate().past().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                customers.get(faker.number().numberBetween(0, customers.size()))
        )).len(20).generate();
        orderRepository.saveAll(orders);
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
''',
    'src/main/java/com/example/datafaker/controller/DatafakerController.java': '''package com.example.datafaker.controller;

import com.example.datafaker.entity.Customer;
import com.example.datafaker.entity.Product;
import com.example.datafaker.service.FakerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faker")
public class DatafakerController {

    private final FakerService fakerService;

    public DatafakerController(FakerService fakerService) {
        this.fakerService = fakerService;
    }

    @PostMapping("/seed")
    public String seed() {
        fakerService.seedDatabase();
        return "Database seeded successfully!";
    }

    @GetMapping("/customers/vn")
    public List<Customer> getVnCustomers() {
        return fakerService.generateVietnameseCustomers(5);
    }
    
    @GetMapping("/products/expression")
    public List<Product> getProductsExpression() {
        return fakerService.generateProductsFromExpression(3);
    }
}
''',
    'src/test/java/com/example/datafaker/DatafakerApplicationTests.java': '''package com.example.datafaker;

import com.example.datafaker.entity.Customer;
import com.example.datafaker.entity.Product;
import com.example.datafaker.faker.MyFaker;
import com.example.datafaker.service.FakerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatafakerApplicationTests {

    @Autowired
    private FakerService fakerService;

    @Test
    void testVietnameseLocale() {
        List<Customer> vnCustomers = fakerService.generateVietnameseCustomers(2);
        assertThat(vnCustomers).hasSize(2);
        assertThat(vnCustomers.get(0).getFullName()).isNotNull();
    }

    @Test
    void testDeterministicSeed() {
        Customer c1 = fakerService.generateDeterministicCustomer(12345L);
        Customer c2 = fakerService.generateDeterministicCustomer(12345L);
        assertThat(c1.getFullName()).isEqualTo(c2.getFullName());
        assertThat(c1.getEmail()).isEqualTo(c2.getEmail());
    }

    @Test
    void testCustomProvider() {
        MyFaker faker = new MyFaker();
        String orderStatus = faker.eCommerce().orderStatus();
        assertThat(orderStatus).isIn("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");
        
        String discountCode = fakerService.generateDiscountCode();
        assertThat(discountCode).startsWith("DISCOUNT");
    }

    @Test
    void testExpressionBasedGeneration() {
        List<Product> products = fakerService.generateProductsFromExpression(3);
        assertThat(products).hasSize(3);
        assertThat(products.get(0).getName()).isNotNull();
        assertThat(products.get(0).getDepartment()).isNotNull();
    }

    @Test
    void testCollectionGeneration() {
        MyFaker faker = new MyFaker();
        List<String> words = faker.collection(() -> faker.lorem().word()).len(5).generate();
        assertThat(words).hasSize(5);
    }

    @Test
    void testDatabaseSeeding() {
        fakerService.seedDatabase();
        List<Customer> allCustomers = fakerService.getAllCustomers();
        assertThat(allCustomers).hasSizeGreaterThanOrEqualTo(5);
    }
    
    @Test
    void testBasicProviders() {
        MyFaker faker = new MyFaker();
        assertThat(faker.name().fullName()).isNotBlank();
        assertThat(faker.address().fullAddress()).isNotBlank();
        assertThat(faker.internet().emailAddress()).isNotBlank();
        assertThat(faker.phoneNumber().phoneNumber()).isNotBlank();
        assertThat(faker.commerce().productName()).isNotBlank();
    }
}
'''
}

for path, content in files.items():
    full_path = os.path.join(base_dir, path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, 'w', encoding='utf-8') as f:
        f.write(content)

print("Project generated successfully!")
