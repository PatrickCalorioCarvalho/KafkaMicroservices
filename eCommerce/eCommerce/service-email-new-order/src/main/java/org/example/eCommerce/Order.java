package org.example.eCommerce;

import java.math.BigDecimal;

public class Order {

    private final String OrderId;
    private final BigDecimal Amount;
    private final String Email;

    public Order( String orderId, BigDecimal amount,String email) {
        OrderId = orderId;
        Amount = amount;
        Email = email;
    }

    public BigDecimal getAmount() {
        return Amount;
    }

    public String getEmail() {
        return Email;
    }

    @Override
    public String toString() {
        return "Order{" +
                "OrderId='" + OrderId + '\'' +
                ", Amount=" + Amount +
                ", Email='" + Email + '\'' +
                '}';
    }
}
