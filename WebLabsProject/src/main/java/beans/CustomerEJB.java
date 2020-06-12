package beans;

import database.DatabaseManager;
import entities.Customer;

import javax.ejb.Stateless;
import java.sql.SQLException;

@Stateless
public class CustomerEJB {

    public Customer validateUserLogin(String login, String password) throws SQLException {

        try (DatabaseManager databaseManager = new DatabaseManager()) {
            if (databaseManager.customerExistsByLoginPassword(login, password)) {
                return databaseManager.downloadCustomer(new Customer(null, login, password));
            } else {
                return null;
            }
        }

    }

    public void deleteProduct(String productName) throws SQLException {

        try (DatabaseManager databaseManager = new DatabaseManager()) {
            databaseManager.deleteProductByName(productName);
        }

    }
}
