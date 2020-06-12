package entities_jpa;


import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@org.hibernate.annotations.NamedQueries({
        @org.hibernate.annotations.NamedQuery(name = "DeleteOrderProductByOrderIdAndName",
                query = "delete from OrderProduct where product_name = :name and order_id = :orderId")
})
@Table(name = "order_products")
@XmlRootElement(name = "orderProduct")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderProduct {
    private int quantity;

    @EmbeddedId
    private OrderProductId orderProductId;

    public OrderProduct() {

    }

    public OrderProduct(Product product, Order order, int quantity) {
        this.orderProductId = new OrderProductId(product, order);
        this.quantity = quantity;
    }

    public Product getProduct() {
        return orderProductId.getProduct();
    }

    public void setProduct(Product product) {
        this.orderProductId.setProduct(product);
    }

    @Column(name = "quantity")
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderProductId getOrderProductId() {
        return orderProductId;
    }

    public void setOrderProductId(OrderProductId orderProductId) {
        this.orderProductId = orderProductId;
    }

    public double getTotal() {
        return orderProductId.getProduct().getPrice() * quantity;
    }


    @Override
    public String toString() {
        return "OrderProduct{" +
                    "quantity=" + quantity + ", " +
                    "product=" + getProduct().toString() +
                '}';
    }



}
