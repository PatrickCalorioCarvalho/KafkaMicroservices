package org.example.eCommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.eCommerce.consumer.ConsumerService;
import org.example.eCommerce.consumer.ServiceRunner;

import java.util.concurrent.ExecutionException;

public class EmailService implements ConsumerService<String> {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner(EmailService::new).start(5);
    }

    public String getTopic()
    {
        return "ECOMMERCE_SEND_EMAIL";
    }
    public String getConsumerGroup()
    {
        return EmailService.class.getSimpleName();
    }

    public void parse(ConsumerRecord<String,Message<String>> record) {
        System.out.println("-------------------------------------------------");
        System.out.println("Send Email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println("Email send");
    }

}
