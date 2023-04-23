package org.example.eCommerce.database;

import java.sql.*;

public class LocalDatabase {

    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/"+name+".db";
        connection = DriverManager.getConnection(url);
    }

    public  void createIfNotExists(String sql)
    {
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public boolean update(String statement, String ... params) throws SQLException {
        return PreparedStatement(statement, params).execute();
    }

    private PreparedStatement PreparedStatement(String statement, String[] params) throws SQLException {
        var prepareStatement = connection.prepareStatement(statement);
        for(int i = 0; i< params.length; i++)
        {
            prepareStatement.setString(i+1, params[i]);
        }
        return prepareStatement;
    }

    public ResultSet query(String query, String ... params) throws SQLException {
        return PreparedStatement(query, params).executeQuery();
    }

    public void close() throws SQLException {
        connection.close();
    }
}
