package org.example.eCommerce;

import java.math.BigDecimal;

public class Order {

    private final String UserId,OrderId;
    private final BigDecimal Amount;

    public Order(String userId, String orderId, BigDecimal amount) {
        UserId = userId;
        OrderId = orderId;
        Amount = amount;
    }
}
