package org.example.eCommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.eCommerce.consumer.ConsumerService;
import org.example.eCommerce.consumer.KafkaService;
import org.example.eCommerce.consumer.ServiceRunner;
import org.example.eCommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {
    public static void main(String[] args) {
        new ServiceRunner(EmailNewOrderService::new).start(1);
    }

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("-------------------------------------------------");
        System.out.println("Processing new Order, preparing email");
        System.out.println(record.key());
        Order order =  record.value().getPayload();
        var emailCode = "Thank you for your order! We are processing your order!";
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL",order.getEmail(),
                record.value().getId().continueWith(EmailNewOrderService.class.getSimpleName()),
                emailCode);
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}
