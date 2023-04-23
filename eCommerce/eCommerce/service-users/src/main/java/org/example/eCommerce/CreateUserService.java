package org.example.eCommerce;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.eCommerce.consumer.ConsumerService;
import org.example.eCommerce.consumer.ServiceRunner;
import org.example.eCommerce.database.LocalDatabase;

import java.sql.SQLException;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {

    private final LocalDatabase database;

    CreateUserService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExists("CREATE TABLE IF NOT EXISTS Users(" +
                "uuid varchar(200) primary key," +
                "email varchar(200))");

    }

    public static void main(String[] args) {
            new ServiceRunner<>(CreateUserService::new).start(1);

    }
    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("-------------------------------------------------");
        System.out.println("Processing new Order, Checking for new user");
        System.out.println(record.value());
        var order  = record.value().getPayload();
        if(isNewUsers(order.getEmail())){
            insertNewUser(order.getEmail());
        }
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    private void insertNewUser(String email) throws SQLException {
        this.database.update("insert into Users(uuid,email) values(?,?)",UUID.randomUUID().toString(),email);
        System.out.println("Usuario Adicionado com email: "+email);
    }

    private boolean isNewUsers(String email) throws SQLException {
        var result = this.database.query("select uuid from Users " +
                        "where email = ? limit 1",email);
        return !result.next();
    }
}
