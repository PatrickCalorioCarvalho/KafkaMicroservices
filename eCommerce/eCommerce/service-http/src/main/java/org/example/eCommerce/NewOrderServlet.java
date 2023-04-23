package org.example.eCommerce;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.eCommerce.dispatcher.KafkaDispatcher;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var email =  req.getParameter("email");
            var orderId= UUID.randomUUID().toString();
            var amount = new BigDecimal(req.getParameter("amount"));
            var order = new Order(orderId,amount,email);

            try(var database = new OrdersDatabase()){
                if(database.saveNew(order))
                {
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER",email,
                            new CorrelationId(NewOrderServlet.class.getSimpleName()),
                            order);

                    System.out.println("Novo Pedido Processado");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("Novo Pedido Processado");
                }else
                {
                    System.out.println("Antigo Pedido Processado");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("Antigo Pedido Processado");
                }
            }





        } catch (InterruptedException | SQLException | ExecutionException e) {
            throw new ServletException(e);
        }
    }
}
