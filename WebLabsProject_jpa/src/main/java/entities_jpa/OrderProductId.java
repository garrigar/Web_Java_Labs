package entities_jpa;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;


@XmlAccessorType(XmlAccessType.FIELD)
@Embeddable
public class OrderProductId implements Serializable {

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_name")
    private Product product;

    public OrderProductId(Product product, Order order) {
        this.product = product;
        this.order = order;
    }

    public OrderProductId() {

    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
