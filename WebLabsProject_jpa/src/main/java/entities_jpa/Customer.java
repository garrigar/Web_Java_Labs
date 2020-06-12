package entities_jpa;



import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@org.hibernate.annotations.NamedQueries({
        @org.hibernate.annotations.NamedQuery(name="GetCustomerByUsernameAndPassword",
                query = "FROM Customer WHERE login = :login AND password = :password")
})
@Table(name = "customers")
@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {
    private int customerId;
    private String name;
    private String login;
    @XmlTransient
    private String password;
    @XmlElementWrapper(name="orderList")
    @XmlElement(name = "order")
    private List<Order> orders = new ArrayList<>();

    public Customer(int customerId, String name, String login, String password, List<Order> orders) {
        this.customerId = customerId;
        this.name = name;
        this.login = login;
        this.password = password;
        this.orders.addAll(orders);
    }

    public Customer() {

    }

    @Column(name = "customer_id")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Column(name = "customer_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String username) {
        this.login = username;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Order getOrder(int index) {
        return orders.get(index);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void removeOrder(int index) {
        orders.remove(index);
    }

    public int orderSize() {
        return orders.size();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", username='" + login + '\'' +
                ", password='" + password + '\'' +
                ", orderList=" + orders +
                '}';
    }

}
