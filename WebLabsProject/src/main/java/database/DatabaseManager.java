package database;

import entities.Customer;
import entities.Order;
import entities.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements AutoCloseable {

    private static final String jdbcURL = "jdbc:h2:./db/database";
    private static final String jdbcUsername = "sa";
    private static final String jdbcPassword = "";

    private Connection connection;

    // this is necessary for Java 5 or older
    /*
    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    */

    public DatabaseManager() throws SQLException {
        this(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public DatabaseManager(String jdbcURL, String jdbcUsername, String jdbcPassword) throws SQLException {
        this.connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        this.connection.setAutoCommit(false);
    }

    @Override
    public void close() throws SQLException {
        this.connection.close();
    }

    // -------------------------------------------- Tables' methods ----------------------------------------------------

    /**
     * causes an implicit commit before execution
     */
    public void createTables() throws SQLException {

        String sql = "CREATE TABLE CUSTOMERS\n" +
                "(\n" +
                "  CUSTOMER_ID   INTEGER PRIMARY KEY,\n" +
                "  CUSTOMER_NAME VARCHAR,\n" +
                "  LOGIN         VARCHAR NOT NULL,\n" +
                "  PASSWORD      VARCHAR NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE PRODUCTS\n" +
                "(\n" +
                "  PRODUCT_NAME VARCHAR PRIMARY KEY,\n" +
                "  PRICE        NUMBER(20, 2) NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE ORDERS\n" +
                "(\n" +
                "  ORDER_ID    INTEGER PRIMARY KEY,\n" +
                "  CUSTOMER_ID INTEGER,\n" +
                "  FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMERS\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE ORDER_PRODUCTS\n" +
                "(\n" +
                "  ORDER_ID     INTEGER NOT NULL,\n" +
                "  PRODUCT_NAME VARCHAR NOT NULL,\n" +
                "  QUANTITY     INTEGER,\n" +
                "  PRIMARY KEY (ORDER_ID, PRODUCT_NAME),\n" +
                "  FOREIGN KEY (ORDER_ID) REFERENCES ORDERS,\n" +
                "  FOREIGN KEY (PRODUCT_NAME) REFERENCES PRODUCTS\n" +
                ");\n";

        try {

            try (Statement statement = this.connection.createStatement()) {
                statement.executeUpdate(sql);
            }

            this.connection.commit();
        } catch (SQLException sqlEx) {
            System.err.println("SQL error occurred:");
            sqlEx.printStackTrace();
            System.err.println("ROLLING BACK...");
            this.connection.rollback();
        }
    }

    public void initTablesWithCustomers(List<Customer> customers) throws SQLException {
        try {

            for (Customer customer : customers) {
                this.addNewCustomerInterior(customer);
            }

            this.connection.commit();
        } catch (SQLException sqlEx) {
            System.err.println("SQL error occurred:");
            sqlEx.printStackTrace();
            System.err.println("ROLLING BACK...");
            this.connection.rollback();
        }
    }

    /**
     * Drops all existing views, tables, sequences, schemas, function aliases, roles, user-defined aggregate functions,
     * domains, and users (except the current user).
     * <p>
     * Warning: this command can not be rolled back.
     * <p>
     * Admin rights are required to execute this command.
     */
    public void dropEverything() throws SQLException {
        String sql = "DROP ALL OBJECTS;";

        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(sql);
        }

        this.connection.commit();
    }

    // -------------------------------------------- Product methods ----------------------------------------------------

    public boolean productExistsByName(String productName) throws SQLException {

        String sql = "SELECT * FROM PRODUCTS WHERE PRODUCT_NAME = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {

            preparedStatement.setString(1, productName);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        }
    }

    private void addNewProductInterior(Product product) throws SQLException, IllegalArgumentException {

        if (this.productExistsByName(product.getName())) {
            throw new IllegalArgumentException("This product already exists");
        }

        String sql = "INSERT INTO PRODUCTS VALUES (?, ?);";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());

            preparedStatement.executeUpdate();

        }
    }

    public void addNewProduct(Product product) throws SQLException, IllegalArgumentException {
        try {

            this.addNewProductInterior(product);

            this.connection.commit();
        } catch (SQLException sqlEx) {
            System.err.println("SQL error occurred:");
            sqlEx.printStackTrace();
            System.err.println("ROLLING BACK...");
            this.connection.rollback();
        }
    }

    public void addProductToOrder(Product product, int orderId) throws SQLException, IllegalArgumentException {
        try {

            if (!this.orderExistsById(orderId)){
                throw new IllegalArgumentException("This order does not exist");
            }

            String sqlOrderProducts = "INSERT INTO ORDER_PRODUCTS VALUES (?, ?, ?);";

            try {
                this.addNewProductInterior(product);
            } catch (IllegalArgumentException ignored) {
                // product already exists in database
            }
            if (this.productExistsInOrder(orderId, product)) {
                // product already exists in order
                // so there were duplicates of Products (by name) in this order
                // ignore them
                System.err.println("Product with name \"" + product.getName() + "\" already exists in order #"
                        + orderId + ", it is considered as a duplicate of some previous product and thus is ignored");
            } else {
                try (PreparedStatement psOrderProducts = this.connection.prepareStatement(sqlOrderProducts)) {
                    psOrderProducts.setInt(1, orderId);
                    psOrderProducts.setString(2, product.getName());
                    psOrderProducts.setInt(3, product.getCount());

                    psOrderProducts.executeUpdate();
                }
            }

            this.connection.commit();
        } catch (SQLException sqlEx) {
            System.err.println("SQL error occurred:");
            sqlEx.printStackTrace();
            System.err.println("ROLLING BACK...");
            this.connection.rollback();
        }
    }

    public void deleteProductByName(String productName) throws SQLException /*, IllegalArgumentException*/ {

        if (productName == null) {
            throw new IllegalArgumentException("product name is null");
        }

        try {

            // use if you don't want it to be silent
            /*
            if (!this.productExistsByName(productName)) {
                throw new IllegalArgumentException("This product does not exist in database");
            }
            */

            String sqlOrderProducts = "DELETE FROM ORDER_PRODUCTS WHERE PRODUCT_NAME = ?;";

            try (PreparedStatement preparedStatement = this.connection.prepareStatement(sqlOrderProducts)) {

                preparedStatement.setString(1, productName);

                preparedStatement.executeUpdate();
            }

            String sqlProducts = "DELETE FROM PRODUCTS WHERE PRODUCT_NAME = ?;";

            try (PreparedStatement preparedStatement = this.connection.prepareStatement(sqlProducts)) {

                preparedStatement.setString(1, productName);

                preparedStatement.executeUpdate();
            }

            this.connection.commit();
        } catch (SQLException sqlEx) {
            System.err.println("SQL error occurred:");
            sqlEx.printStackTrace();
            System.err.println("ROLLING BACK...");
            this.connection.rollback();
        }

    }

    public void deleteProductByName(Product product) throws SQLException {
        this.deleteProductByName(product.getName());
    }

    // --------------------------------------------- Order methods -----------------------------------------------------

    public boolean orderExistsById(int orderId) throws SQLException {

        String sql = "SELECT * FROM ORDERS WHERE ORDER_ID = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, orderId);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        }
    }

    private int getFreeOrderId() throws SQLException {

        String sql = "SELECT MAX(ORDER_ID) AS CNT FROM ORDERS;";

        try (Statement statement = this.connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getInt("CNT") + 1;
        }
    }

    public boolean productExistsInOrderByName(int orderId, String productName) throws SQLException {

        String sql = "SELECT * FROM ORDER_PRODUCTS WHERE ORDER_ID = ? AND PRODUCT_NAME = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, orderId);
            preparedStatement.setString(2, productName);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        }
    }

    private boolean productExistsInOrder(int orderId, Product product) throws SQLException {
        return this.productExistsInOrderByName(orderId, product.getName());
    }

    /**
     * !!! changes order ID to unique id in DB
     * customer given must exist in database or be null
     */
    private void addNewOrderInterior(Order order, Customer customer) throws SQLException {

        int orderId = this.getFreeOrderId();

        String sqlOrders = "INSERT INTO ORDERS VALUES (?, ?);";

        try (PreparedStatement psOrders = this.connection.prepareStatement(sqlOrders)) {

            psOrders.setInt(1, orderId);
            if (customer == null) {
                psOrders.setNull(2, Types.INTEGER);
            } else {
                psOrders.setInt(2, customer.getId());
            }

            psOrders.executeUpdate();

            order.setId(orderId);
        }

        String sqlOrderProducts = "INSERT INTO ORDER_PRODUCTS VALUES (?, ?, ?);";

        for (int i = 0; i < order.getProductsCount(); i++) {
            Product product = order.getProduct(i);
            try {
                this.addNewProductInterior(product);
            } catch (IllegalArgumentException ignored) {
                // product already exists in database
            }
            if (this.productExistsInOrder(orderId, product)) {
                // product already exists in order
                // so there were duplicates of Products (by name) in this order
                // ignore them
                System.err.println("Product with name \"" + product.getName() + "\" already exists in order #"
                        + orderId + ", it is considered as a duplicate of some previous product and thus is ignored");
                continue;
            }
            try (PreparedStatement psOrderProducts = this.connection.prepareStatement(sqlOrderProducts)) {
                psOrderProducts.setInt(1, orderId);
                psOrderProducts.setString(2, product.getName());
                psOrderProducts.setInt(3, product.getCount());

                psOrderProducts.executeUpdate();
            }
        }


    }

    /**
     * !!! changes order ID to unique id in DB
     */
    public void addNewOrder(Order order, Customer customer) throws SQLException {
        try {

            if (this.customerExistsById(customer.getId())) {
                this.addNewOrderInterior(order, customer);
            } else {
                throw new IllegalArgumentException("Customer specified could not be identified, " +
                        "its id wasn't found in database");
            }

            this.connection.commit();
        } catch (SQLException sqlEx) {
            System.err.println("SQL error occurred:");
            sqlEx.printStackTrace();
            System.err.println("ROLLING BACK...");
            this.connection.rollback();
        }
    }

    /**
     * !!! changes order ID to unique id in DB
     */
    public void addNewOrder(Order order) throws SQLException {
        try {

            this.addNewOrderInterior(order, null);

            this.connection.commit();
        } catch (SQLException sqlEx) {
            System.err.println("SQL error occurred:");
            sqlEx.printStackTrace();
            System.err.println("ROLLING BACK...");
            this.connection.rollback();
        }
    }

    public void deleteOrderById(int orderId) throws SQLException /*, IllegalArgumentException*/ {

        try {

            // use if you don't want it to be silent
            /*
            if (!this.orderExistsById(orderId)) {
                throw new IllegalArgumentException("This order does not exist in database");
            }
            */

            String sqlOrderProducts = "DELETE FROM ORDER_PRODUCTS WHERE ORDER_ID = ?;";

            try (PreparedStatement preparedStatement = this.connection.prepareStatement(sqlOrderProducts)) {

                preparedStatement.setInt(1, orderId);

                preparedStatement.executeUpdate();
            }

            String sqlOrders = "DELETE FROM ORDERS WHERE ORDER_ID = ?;";

            try (PreparedStatement preparedStatement = this.connection.prepareStatement(sqlOrders)) {

                preparedStatement.setInt(1, orderId);

                preparedStatement.executeUpdate();
            }

            this.connection.commit();
        } catch (SQLException sqlEx) {
            System.err.println("SQL error occurred:");
            sqlEx.printStackTrace();
            System.err.println("ROLLING BACK...");
            this.connection.rollback();
        }

    }

    public void deleteOrderById(Order order) throws SQLException {
        this.deleteOrderById(order.getId());
    }


    // ------------------------------------------- Customer methods ----------------------------------------------------

    private int getFreeCustomerId() throws SQLException {

        String sql = "SELECT MAX(CUSTOMER_ID) AS CNT FROM CUSTOMERS;";

        try (Statement statement = this.connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getInt("CNT") + 1;
        }
    }

    /**
     * !!! This method changes ID of given customer to unique id in DB
     */
    private void addNewCustomerInterior(Customer customer) throws SQLException {

        int customerId = this.getFreeCustomerId();

        String sql = "INSERT INTO CUSTOMERS VALUES (?, ?, ?, ?);";

        try (PreparedStatement psCustomers = this.connection.prepareStatement(sql)) {

            psCustomers.setInt(1, customerId);
            psCustomers.setString(2, customer.getName());
            psCustomers.setString(3, customer.getLogin());
            psCustomers.setString(4, customer.getPassword());

            psCustomers.executeUpdate();

            customer.setId(customerId);
        }


        for (int i = 0; i < customer.getOrdersCount(); i++) {
            Order order = customer.getOrder(i);
            this.addNewOrderInterior(order, customer);
        }
    }

    /**
     * !!! This method changes ID of given customer to unique id in DB
     */
    public void addNewCustomer(Customer customer) throws SQLException {
        try {

            this.addNewCustomerInterior(customer);

            this.connection.commit();
        } catch (SQLException sqlEx) {
            System.err.println("SQL error occurred:");
            sqlEx.printStackTrace();
            System.err.println("ROLLING BACK...");
            this.connection.rollback();
        }
    }

    public boolean customerExistsById(int customerId) throws SQLException {

        String sql = "SELECT * FROM CUSTOMERS WHERE CUSTOMER_ID = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, customerId);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        }
    }

    public boolean customerExistsByLoginPassword(String login, String password) throws SQLException {

        String sql = "SELECT * FROM CUSTOMERS WHERE LOGIN = ? AND PASSWORD = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {

            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        }
    }

    public boolean customerExistsByLoginPassword(Customer customer) throws SQLException {
        return this.customerExistsByLoginPassword(customer.getLogin(), customer.getPassword());
    }

    private int getCustomerIdByLoginPassword(String login, String password) throws SQLException, IllegalArgumentException {

        if (!this.customerExistsByLoginPassword(login, password)){
            throw new IllegalArgumentException("This customer does not exist in database");
        }

        String sql = "SELECT CUSTOMER_ID FROM CUSTOMERS WHERE LOGIN = ? AND PASSWORD = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {

            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("CUSTOMER_ID");
        }
    }

    // ------------------------ methods getting instances from prev. lab from DB ---------------------------------------

    public Order getOrderById(int orderId) throws SQLException, IllegalArgumentException {

        if (!this.orderExistsById(orderId)){
            throw new IllegalArgumentException("This order does not exist in database");
        }

        Order order = new Order();
        order.setId(orderId);

        // fill up products

        String sql = "SELECT OP.PRODUCT_NAME, OP.QUANTITY, P.PRICE\n" +
                "FROM ORDER_PRODUCTS OP\n" +
                "       JOIN PRODUCTS P ON OP.PRODUCT_NAME = P.PRODUCT_NAME\n" +
                "WHERE OP.ORDER_ID = ?;";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, orderId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                String name = resultSet.getString("PRODUCT_NAME");
                int count = resultSet.getInt("QUANTITY");
                double price = resultSet.getDouble("PRICE");

                order.addProduct(new Product(name, price, count));
            }
        }

        return order;
    }

    public Customer getCustomerById(int customerId) throws SQLException, IllegalArgumentException {

        if (!this.customerExistsById(customerId)){
            throw new IllegalArgumentException("This customer does not exist in database");
        }

        Customer customer;

        String sqlCustomers = "SELECT * FROM CUSTOMERS WHERE CUSTOMER_ID = ?;";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sqlCustomers)) {

            preparedStatement.setInt(1, customerId);

            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            String name = resultSet.getString("CUSTOMER_NAME");
            String login = resultSet.getString("LOGIN");
            String password = resultSet.getString("PASSWORD");

            customer = new Customer(name, login, password);
        }

        customer.setId(customerId);

        // fill up orders

        String sqlOrders = "SELECT ORDER_ID FROM ORDERS WHERE CUSTOMER_ID = ?;";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sqlOrders)) {

            preparedStatement.setInt(1, customerId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                customer.addOrder(this.getOrderById(resultSet.getInt("ORDER_ID")));
            }
        }

        return customer;
    }

    public Customer downloadCustomer(Customer customer) throws SQLException, IllegalArgumentException {
        return this.getCustomerById(this.getCustomerIdByLoginPassword(customer.getLogin(), customer.getPassword()));
    }

    public List<Product> getProductsByOrderId(int orderId) throws SQLException {
        Order order = this.getOrderById(orderId);
        List<Product> ans = new ArrayList<>();
        for (int i = 0; i < order.getProductsCount(); i++){
            ans.add(order.getProduct(i));
        }
        return ans;
    }

    public List<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        Customer customer = this.getCustomerById(customerId);
        List<Order> ans = new ArrayList<>();
        for (int i = 0; i < customer.getOrdersCount(); i++){
            ans.add(customer.getOrder(i));
        }
        return ans;
    }

    // ниже -- немного бессмысленные методы
    // в задании сказано реализовать запросы:
    //      * выбора всех элементов заданного списка;
    //      * выбора всех списков, соответствующих заданному пользователю;
    // если под "заданным" подразумевается ID, то все логично, эти методы написаны выше
    // а вот если под "заданным" подразумевается сама сущность (instance класса), то это немного бессмысленно,
    //   ибо сущность клиента уже содержит в себе массив списков, а сущность списка уже содержит массив элементов
    // как бы то ни было, этот вариант методов представлен ниже

    public List<Product> getProductsByOrderId(Order order) throws SQLException {
        return this.getProductsByOrderId(order.getId());
    }
    public List<Order> getOrdersByCustomerId(Customer customer) throws SQLException {
        return this.getOrdersByCustomerId(customer.getId());
    }

}
