package org.example.eCommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.eCommerce.consumer.KafkaService;
import org.example.eCommerce.database.LocalDatabase;
import org.example.eCommerce.dispatcher.KafkaDispatcher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {
    private final LocalDatabase database;

    BatchSendMessageService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExists("CREATE TABLE IF NOT EXISTS Users(" +
                "uuid varchar(200) primary key," +
                "email varchar(200))");
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException,SQLException {
        var batchSendMessageService = new BatchSendMessageService();
        try(var service = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",
                batchSendMessageService::parse,
                Map.of())) {
            service.run();
        }
    }

    private final KafkaDispatcher<User> userKafkaDispatcher = new KafkaDispatcher<User>();
    private void parse(ConsumerRecord<String, Message<String>> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("-------------------------------------------------");
        System.out.println("Processing new Batch");
        var message = record.value();
        System.out.println("Topic: " + message.getPayload());
        for(User user : getAllUsers()){
            System.out.println("Enviando para Usuario: " + user.getUuid());
            userKafkaDispatcher.sendAsync(message.getPayload(),user.getUuid(),
                    message.getId().continueWith(BatchSendMessageService.class.getSimpleName()),
                    user);

        }
    }

    private List<User> getAllUsers() throws SQLException {
        var results = this.database.query("select uuid from Users");
        List<User> users = new ArrayList<>();
        while(results.next())
        {
            users.add(new User(results.getString(1)));
        }
        return users;
    }

}
