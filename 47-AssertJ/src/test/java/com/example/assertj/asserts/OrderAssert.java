package com.example.assertj.asserts;

import com.example.assertj.domain.Order;
import org.assertj.core.api.AbstractAssert;

public class OrderAssert extends AbstractAssert<OrderAssert, Order> {

    public OrderAssert(Order actual) {
        super(actual, OrderAssert.class);
    }

    public static OrderAssert assertThat(Order actual) {
        return new OrderAssert(actual);
    }

    public OrderAssert hasOrderId(String expectedId) {
        isNotNull();
        if (!actual.getOrderId().equals(expectedId)) {
            failWithMessage("Expected order ID to be <%s> but was <%s>", expectedId, actual.getOrderId());
        }
        return this;
    }

    public OrderAssert hasItemsCount(int expectedSize) {
        isNotNull();
        if (actual.getItems().size() != expectedSize) {
            failWithMessage("Expected order to have <%d> items but had <%d>", expectedSize, actual.getItems().size());
        }
        return this;
    }
}
