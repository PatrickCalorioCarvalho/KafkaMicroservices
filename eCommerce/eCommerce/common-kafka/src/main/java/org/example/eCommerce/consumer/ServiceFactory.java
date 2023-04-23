package org.example.eCommerce;

public interface ServiceFactory<T> {
    ConsumerService<T> create();
}
