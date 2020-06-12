package entities;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "login", "name", "orders"})
public class Customer {

    @XmlElement(name = "customerId")
    private int id;
    @XmlElement(name = "customerName")
    private String name;
    @XmlElement(name = "customerLogin")
    private String login;
    @XmlTransient
    private String password;

    @XmlElementWrapper(name = "orderList")
    @XmlElement(name = "order")
    private List<Order> orders;

    public Customer(String name, String login, String password) {
        this.id = -1; // means virgin ID, it will be changed, when this object will be added to database
        this.name = name;
        this.login = login;
        this.password = password;
        this.orders = new ArrayList<>();
    }

    public Customer() {
        this.id = -1; // means virgin ID, it will be changed, when this object will be added to database
        this.orders = new ArrayList<>();
    }

    // -----------------------------------------------------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    public void addOrder(int index, Order order) {
        this.orders.add(index, order);
    }

    public Order getOrder(int index) {
        return this.orders.get(index);
    }

    public Order deleteOrder(int index) {
        return this.orders.remove(index);
    }

    public int getOrdersCount() {
        return this.orders.size();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    // -----------------------------------------------------JSON--------------------------------------------------------

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        jsonObject.put("name", this.name);
        jsonObject.put("login", this.login);
        jsonObject.put("password", this.password);

        JSONArray jsonArray = new JSONArray();
        for (Order order : this.orders) {
            jsonArray.put(order.toJsonObject());
        }
        jsonObject.put("orders", jsonArray);

        return jsonObject;
    }

    public static Customer fromJsonObject(JSONObject jsonObject) {
        Customer customer = new Customer(
                jsonObject.getString("name"),
                jsonObject.getString("login"),
                jsonObject.getString("password")
        );
        customer.id = jsonObject.getInt("id");

        for (Object o : jsonObject.getJSONArray("orders")) {
            customer.addOrder(Order.fromJsonObject((JSONObject) o));
        }

        return customer;
    }

    // ----------------------------------------------- IO methods ------------------------------------------------------

    /** Writes a Customer object as a JSON to a Writer
     *  Writer won't be closed afterwards
     */
    public void writeCustomer(Writer writer) {
        this.toJsonObject().write(writer, 4, 0);
    }

    /** Reads all from the Reader object to a Customer (expecting JSON architecture)
     *  Reader will be closed afterwards
     */
    public static Customer readCustomer(Reader reader) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)){
            String collect = bufferedReader.lines().collect(Collectors.joining());
            return fromJsonObject(new JSONObject(collect));
        }
    }


    @Override
    public String toString() {
        return this.toJsonObject().toString(4);
    }


}
