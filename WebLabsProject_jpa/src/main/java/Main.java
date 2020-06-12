import database.DatabaseManager;
import entities.Customer;
import entities.Order;
import entities.Product;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

    }

    public static void main1(String[] args) {

        try (DatabaseManager databaseManager = new DatabaseManager()) {

            databaseManager.dropEverything();

            databaseManager.createTables();

            List<Customer> customerList = loadCustomers();

            System.out.println("First customer which we a loading:\n" + customerList.get(0));

            databaseManager.initTablesWithCustomers(customerList);

            databaseManager.addNewProduct(new Product("wakaka", 123.234, 10));

            databaseManager.deleteProductByName("wakaka");


            System.out.println("Is there a person with login 'gilmour' and password 'moon1973'? -- " +
                    databaseManager.customerExistsByLoginPassword(new Customer("", "gilmour", "moon1973")));

            System.out.println("Orders of first customer downloaded from DB:");
            for (Order order : databaseManager.getOrdersByCustomerId(1)) {
                System.out.println(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static List<Customer> loadCustomers() {
        List<Customer> list = new ArrayList<>();

        File folder = new File("txtCustomers/");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {

                    try (FileReader fileReader = new FileReader(file)) {
                        list.add(Customer.readCustomer(fileReader));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        return list;
    }

}