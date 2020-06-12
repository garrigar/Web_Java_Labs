package entities_jpa;



import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "products")
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
public class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        if (price <= 0)
            throw new IllegalArgumentException("price is negative");
        this.name = name;
        this.price = price;
    }

    public Product() {

    }

    @Column(name = "product_name")
    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "price")
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                    "name='" + name + "', " +
                    "price=" + price +
                '}';
    }

}
