package com.example.datafaker.service;

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
                faker.timeAndDate().past().atZone(ZoneId.systemDefault()).toLocalDate(),
                customers.get(faker.number().numberBetween(0, customers.size()))
        )).len(20).generate();
        orderRepository.saveAll(orders);
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
