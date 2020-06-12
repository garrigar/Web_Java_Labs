package entities;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /** order id */
    @XmlElement(name = "orderId")
    private int id;

    /** products in an order */
    @XmlElementWrapper(name = "productList")
    @XmlElement(name = "product")
    private List<entities.Product> products;

    // ----------------------------------------------- OLD VERSION -----------------------------------------------------
    /** first free id, used to generate unique ids */
    /*
    private static int freeId = 0;

    public Order(){
        this.id = freeId;
        freeId++;
        this.products = new ArrayList<>();
    }
    */
    // --------------------------------------------- END OLD VERSION ---------------------------------------------------

    // ----------------------------------------------- NEW VERSION -----------------------------------------------------
    public Order() {
        this.id = -1; // means virgin ID, it will be changed, when this object will be added to database
        this.products = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }
    // --------------------------------------------- END NEW VERSION ---------------------------------------------------

    public int getId(){
        return this.id;
    }

    public int getProductsCount(){
        return this.products.size();
    }

    public Product getProduct(int index){
        return this.products.get(index);
    }

    public void addProduct(Product product){
        this.products.add(product);
    }

    public void addProduct(int index, Product product) {
        this.products.add(index, product);
    }

    public Product deleteProduct(int index) {
        return this.products.remove(index);
    }

    /**
     * Sorts products list by Product::getTotal
     * */
    public void sortProducts(){
        this.products.sort(Comparator.comparingDouble(Product::getTotal));
    }


    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    // -----------------------------------------------------JSON--------------------------------------------------------

    public JSONObject toJsonObject(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);

        JSONArray jsonArray = new JSONArray();
        for (Product product : this.products) {
            jsonArray.put(product.toJsonObject());
        }
        jsonObject.put("products", jsonArray);

        return jsonObject;
    }

    public static Order fromJsonObject(JSONObject jsonObject){
        Order order = new Order();
        order.id = jsonObject.getInt("id");

        for (Object o : jsonObject.getJSONArray("products")) {
            order.addProduct(Product.fromJsonObject((JSONObject) o));
        }

        return order;
    }

    // ---------------------------------------------required IO methods-------------------------------------------------

    /** Writes an Order object as a JSON to a Writer
     *  Writer won't be closed afterwards
     */
    public void writeProducts(Writer writer) {
        this.toJsonObject().write(writer, 4, 0);
    }

    /** Reads all from the Reader object to an Order (expecting JSON architecture)
     *  Reader will be closed afterwards
     */
    public static Order readProducts(Reader reader) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)){
            String collect = bufferedReader.lines().collect(Collectors.joining());
            return fromJsonObject(new JSONObject(collect));
        }
    }

    // ------------------------------------------------Object methods---------------------------------------------------

    @Override
    public String toString() {
        return this.toJsonObject().toString(4);
    }
}
