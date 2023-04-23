package org.example.eCommerce;

import org.example.eCommerce.database.LocalDatabase;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class OrdersDatabase implements Closeable {
    private final LocalDatabase database;

    OrdersDatabase() throws SQLException {
        this.database = new LocalDatabase("orders_database");
        this.database.createIfNotExists("CREATE TABLE IF NOT EXISTS Orders(" +
                "uuid varchar(200) primary key)");
    }

    public boolean saveNew(Order order) throws SQLException {
        if(wasProcessed(order))
        {
            return false;
        }
        this.database.update("insert into Orders (uuid) values (?)",order.getOrderId());
        return true;
    }
    private boolean wasProcessed(Order order) throws SQLException {
        var result = this.database.query("select uuid from Orders " +
                "where uuid = ? limit 1",order.getOrderId());
        return result.next();
    }

    @Override
    public void close() throws IOException {
        try {
            this.database.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
