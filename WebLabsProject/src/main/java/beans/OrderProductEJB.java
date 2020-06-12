package beans;

import database.DatabaseManager;
import entities.Product;

import javax.ejb.Stateless;
import java.sql.SQLException;

@Stateless
public class OrderProductEJB {

    public boolean checkProductExistsInOrderByName(int orderId, String productName) throws SQLException {

        try (DatabaseManager databaseManager = new DatabaseManager()) {
            return databaseManager.productExistsInOrderByName(orderId, productName);
        }
    }

    public void addProduct(Product product, int orderId) throws SQLException {

        try (DatabaseManager databaseManager = new DatabaseManager()) {
            databaseManager.addProductToOrder(product, orderId);
        }

    }

}
