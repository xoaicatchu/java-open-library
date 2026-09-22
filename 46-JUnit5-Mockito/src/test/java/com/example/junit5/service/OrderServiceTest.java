package com.example.junit5.service;

import com.example.junit5.entity.Order;
import com.example.junit5.entity.OrderStatus;
import com.example.junit5.exception.OrderNotFoundException;
import com.example.junit5.exception.PaymentException;
import com.example.junit5.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private NotificationService notificationService;

    // Use Spy to test real methods on the service itself, though usually we spy dependencies.
    // For demonstration, let's spy OrderService.
    @Spy
    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully")
        void shouldCreateOrderSuccessfully() {
            // Given (BDD Style)
            Order order = new Order(null, "John", "Laptop", 1000.0, null);
            Order savedOrder = new Order(1L, "John", "Laptop", 1000.0, OrderStatus.PENDING);
            
            given(orderRepository.save(any(Order.class))).willReturn(savedOrder);
            given(paymentGateway.processPayment(1000.0)).willReturn(true);

            // When
            Order result = orderService.createOrder(order);

            // Then
            then(orderRepository).should(times(2)).save(any(Order.class));
            then(notificationService).should().sendNotification("Order placed successfully", "John");
            assertEquals(OrderStatus.COMPLETED, result.getStatus());
        }

        @Test
        @DisplayName("Should throw PaymentException when payment fails")
        void shouldThrowPaymentExceptionWhenPaymentFails() {
            // Given
            Order order = new Order(null, "Jane", "Phone", 500.0, null);
            Order savedOrder = new Order(2L, "Jane", "Phone", 500.0, OrderStatus.PENDING);
            
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
            when(paymentGateway.processPayment(500.0)).thenReturn(false);

            // When / Then
            assertThrows(PaymentException.class, () -> orderService.createOrder(order));
            
            verify(orderRepository, times(2)).save(orderCaptor.capture());
            Order finalSavedOrder = orderCaptor.getValue();
            assertEquals(OrderStatus.FAILED, finalSavedOrder.getStatus());
            verifyNoInteractions(notificationService);
        }
    }

    @Nested
    @DisplayName("Cancel Order Tests")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel order successfully")
        void shouldCancelOrderSuccessfully() {
            // Given
            Order existingOrder = new Order(1L, "Alice", "Tablet", 300.0, OrderStatus.COMPLETED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

            // When
            Order result = orderService.cancelOrder(1L);

            // Then
            assertEquals(OrderStatus.CANCELLED, result.getStatus());
            verify(notificationService).sendNotification(eq("Order cancelled"), eq("Alice"));
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when order does not exist")
        void shouldThrowExceptionWhenOrderNotFound() {
            // Given
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            Exception exception = assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(99L));
            assertTrue(exception.getMessage().contains("Order not found"));
            verifyNoInteractions(notificationService);
        }
    }

    @Nested
    @DisplayName("Parameterized Tests")
    class ParameterizedTests {

        @ParameterizedTest
        @ValueSource(doubles = {100.0, 200.0, 300.0})
        @DisplayName("Test discount calculation with valid amounts")
        void testCalculateDiscountWithValueSource(double amount) {
            double result = orderService.calculateDiscount(amount, 10);
            assertEquals(amount * 0.9, result);
        }

        @ParameterizedTest
        @CsvSource({
                "100.0, 10, 90.0",
                "200.0, 20, 160.0",
                "50.0, 50, 25.0"
        })
        @DisplayName("Test discount calculation with CsvSource")
        void testCalculateDiscountWithCsvSource(double amount, int percentage, double expected) {
            double result = orderService.calculateDiscount(amount, percentage);
            assertEquals(expected, result);
        }

        @ParameterizedTest
        @MethodSource("provideAmountsAndPercentages")
        @DisplayName("Test discount calculation with MethodSource")
        void testCalculateDiscountWithMethodSource(double amount, int percentage, double expected) {
            double result = orderService.calculateDiscount(amount, percentage);
            assertEquals(expected, result);
        }

        private static Stream<org.junit.jupiter.params.provider.Arguments> provideAmountsAndPercentages() {
            return Stream.of(
                    org.junit.jupiter.params.provider.Arguments.of(100.0, 10, 90.0),
                    org.junit.jupiter.params.provider.Arguments.of(500.0, 20, 400.0)
            );
        }
    }

    @Test
    @DisplayName("Verify spy method calls")
    void testSpyBehavior() {
        // Since we mocked orderService with @Spy, we can verify its own methods were called
        double result = orderService.calculateDiscount(100.0, 20);
        assertEquals(80.0, result);
        verify(orderService).calculateDiscount(100.0, 20);
    }
}
