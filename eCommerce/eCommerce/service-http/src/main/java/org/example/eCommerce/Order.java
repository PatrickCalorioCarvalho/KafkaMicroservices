package org.example.eCommerce;

import java.math.BigDecimal;

public class Order {

    private final String OrderId;
    private final BigDecimal Amount;
    private final String Email;


    public Order(String orderId, BigDecimal amount, String email) {
        OrderId = orderId;
        Amount = amount;
        Email = email;
    }

    public String getOrderId() {
        return OrderId;
    }
}
