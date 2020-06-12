package entities_jpa;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "orders")
@XmlRootElement(name = "order")
@XmlAccessorType(XmlAccessType.FIELD)
public class Order {
    private int orderId;
    @XmlElementWrapper(name="orderProductList")
    @XmlElement(name = "orderProduct")
    private List<OrderProduct> orderProductList = new ArrayList<>();

    public Order(int orderId, List<OrderProduct> orderProducts) {
        this.orderId = orderId;
        this.orderProductList.addAll(orderProducts);
    }

    public Order() {}

    @Column(name = "order_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    public List<OrderProduct> getOrderProductList() {
        return orderProductList;
    }

    public void setOrderProductList(List<OrderProduct> orderProductList) {
        this.orderProductList = orderProductList;
    }

    public void setOrderProductList(ArrayList<OrderProduct> orderProductList) {
        this.orderProductList = orderProductList;
    }

    public OrderProduct getOrderProduct(int index) {
        return orderProductList.get(index);
    }

    public void addOrderProduct(OrderProduct orderProduct) {
        orderProductList.add(orderProduct);
    }

    public void removeOrderProduct(int index) {
        orderProductList.remove(index);
    }

    public int orderProductsSize() {
        return orderProductList.size();
    }

    public void sort(Comparator<OrderProduct> comparator) {
        orderProductList.sort(comparator);
    }

    @Override
    public String toString() {
        return "Order{" +
                    "orderId=" + orderId + ", " +
                    "orderProducts=" + orderProductList +
                '}';
    }

}
