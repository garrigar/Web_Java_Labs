package entities;

import java.io.*;
import java.util.stream.Collectors;

import org.json.JSONObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /** product name */
    private String name;
    /** product price for single */
    private double price;
    /** product count */
    private int count;
    /** product total price */
    private double total;

    // ----------------------------------------------------------------------------------------------------------------

    public Product(String name, double price, int count) {
        checkName(name);
        checkPrice(price);
        checkCount(count);

        this.name = name;
        this.price = price;
        this.count = count;
        this.total = this.price * this.count;
    }

    // ----------------------------------------------------------------------------------------------------------------

    private void checkName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
    }

    private void checkPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("price is negative: " + price);
        }
    }

    private void checkCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void setName(String name) {
        checkName(name);
        this.name = name;
    }

    public void setPrice(double price) {
        checkPrice(price);
        this.price = price;
        this.total = this.price * this.count;
    }

    public void setCount(int count) {
        checkCount(count);
        this.count = count;
        this.total = this.price * this.count;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }


    public int getCount() {
        return this.count;
    }


    public double getTotal() {
        return this.total;
    }

    // ------------------------------------------------JSON-------------------------------------------------------------

    public JSONObject toJsonObject(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", this.name);
        jsonObject.put("price", this.price);
        jsonObject.put("count", this.count);
        jsonObject.put("total", this.total);
        return jsonObject;
    }

    public static Product fromJsonObject(JSONObject jsonObject){
        return new Product(
                jsonObject.getString("name"),
                jsonObject.getDouble("price"),
                jsonObject.getInt("count")
        );
    }

    // -----------------------------------------required IO methods-----------------------------------------------------

    /** Writes a Product object as a JSON to a Writer
     *  Writer won't be closed afterwards
     */
    public static void writeProduct(Product product, Writer writer) {
        product.toJsonObject().write(writer, 4, 0);
    }

    /** Reads all from the Reader object to a Product (expecting JSON architecture)
     *  Reader will be closed afterwards
     */
    public static Product readProduct(Reader reader) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)){
            String collect = bufferedReader.lines().collect(Collectors.joining());
            return fromJsonObject(new JSONObject(collect));
        }
    }

    // --------------------------------------------Object methods-------------------------------------------------------

    @Override
    public String toString() {
        return this.toJsonObject().toString(4);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Product))
            return false;

        Product product = (Product) obj;
        return this.name.equals(product.name) &&
                (this.price == product.price) &&
                (this.count == product.count) &&
                (this.total == product.total);
    }

}
