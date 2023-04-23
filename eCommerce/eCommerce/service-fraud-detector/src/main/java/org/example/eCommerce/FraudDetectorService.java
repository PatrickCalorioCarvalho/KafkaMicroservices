package org.example.eCommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.eCommerce.consumer.ConsumerService;
import org.example.eCommerce.consumer.ServiceRunner;
import org.example.eCommerce.database.LocalDatabase;
import org.example.eCommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerService<Order> {

    private final LocalDatabase database;

    FraudDetectorService() throws SQLException {
        this.database = new LocalDatabase("fraud_database");
        this.database.createIfNotExists("CREATE TABLE IF NOT EXISTS Orders(" +
                "uuid varchar(200) primary key," +
                "is_fraud boolean)");
    }


    public static void main(String[] args) {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    private final KafkaDispatcher<Order> orderKafkaDispatcher = new KafkaDispatcher<>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("-------------------------------------------------");
        System.out.println("Processing new Order, Checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        var message = record.value();
        var order  = message.getPayload();
        if(wasProcessed(order)){
            System.out.println("Order "+order.getOrderId()+" ja foi processada");
            return;
        }

        try {
            Thread.sleep(5000);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if(isFraud(order))
        {
            // Simulando um fraude com valor superior a 4500
            database.update("insert into Orders (uuid,is_fraud) values (?,true)",order.getOrderId());
            System.out.println("Order is a fraud!!!!"+ order);
            orderKafkaDispatcher.send("ECOMMERCE_ORDER_REJECTD",order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()),
                    order);
        }else
        {
            database.update("insert into Orders (uuid,is_fraud) values (?,false)",order.getOrderId());
            System.out.println("Approved: "+ order);
            orderKafkaDispatcher.send("ECOMMERCE_ORDER_APPROVED",order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()),
                    order);
        }
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var result = this.database.query("select uuid from Orders " +
                "where uuid = ? limit 1",order.getOrderId());
        return result.next();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }

    private static boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }
}
