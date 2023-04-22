package org.example.eCommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {
    public static void main(String[] args) {
        var fraudDetectorService = new FraudDetectorService();
        try(var service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCER_NEW_ORDER",
                fraudDetectorService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    private final KafkaDispatcher<Order> orderKafkaDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("-------------------------------------------------");
        System.out.println("Processing new Order, Checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        var order  = record.value();
        if(isFraud(order))
        {
            // Simulando um fraude com valor superior a 4500
            System.out.println("Order is a fraud!!!!"+ order);
            orderKafkaDispatcher.send("ECOMMERCE_ORDER_REJECTD",order.getEmail(),order);
        }else
        {
            System.out.println("Approved: "+ order);
            orderKafkaDispatcher.send("ECOMMERCE_ORDER_APPROVED",order.getEmail(),order);
        }
    }

    private static boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }
}
