package org.example.eCommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {
    private final Connection connection;

    BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        connection = DriverManager.getConnection(url);
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS Users(" +
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
            userKafkaDispatcher.sendAsync(message.getPayload(),user.getUuid(),
                    message.getId().continueWith(BatchSendMessageService.class.getSimpleName()),
                    user);

        }
    }

    private List<User> getAllUsers() throws SQLException {
        var results = connection.prepareStatement("select uuid from Users ").executeQuery();
        List<User> users = new ArrayList<>();
        while(results.next())
        {
            users.add(new User(results.getString(1)));
        }
        return users;
    }

}
