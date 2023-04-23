package org.example.eCommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.eCommerce.consumer.ConsumerService;
import org.example.eCommerce.consumer.KafkaService;
import org.example.eCommerce.consumer.ServiceRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReadingReportService implements ConsumerService<User> {
    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) {
        new ServiceRunner(ReadingReportService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        System.out.println("-------------------------------------------------");
        System.out.println("Processing report for " + record.value());
        var message = record.value();
        var user = message.getPayload();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE,target);
        IO.append(target,"Create for "+user.getUuid());
        System.out.println("File Create " + target.getAbsolutePath());
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_USER_GENERATE_READING_REPORT";
    }

    @Override
    public String getConsumerGroup() {
        return ReadingReportService.class.getSimpleName();
    }
}
