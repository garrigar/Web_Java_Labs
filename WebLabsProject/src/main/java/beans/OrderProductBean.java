package beans;

import entities.Product;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.IOException;
import java.sql.SQLException;

@ManagedBean
@SessionScoped
public class OrderProductBean {

    private static final String ACTION_SUCCESS = "success";
    private static final String ACTION_SQL_ERROR = "fatal_error";

    @EJB
    private final OrderProductEJB orderProductEJB = new OrderProductEJB();

    private int orderId;

    /** product name */
    private String name;
    /** product price for single */
    private String price;
    /** product count */
    private String count;


    public OrderProductBean() {
        reset();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void insertProduct(int orderId) {
        this.orderId = orderId;
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("insert.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void productNameValidator(FacesContext facesContext, UIComponent uiComponent, Object o) {
        String toValidate;
        try {
            toValidate = o.toString();
            if (toValidate.equals("")) {
                FacesMessage facesMessage = new FacesMessage("Название должно быть непустым");
                throw new ValidatorException(facesMessage);
            }
            if (orderProductEJB.checkProductExistsInOrderByName(orderId, toValidate)) {
                FacesMessage facesMessage = new FacesMessage("Такой товар уже существует в этом заказе");
                throw new ValidatorException(facesMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FacesMessage facesMessage = new FacesMessage("Ошибка БД");
            throw new ValidatorException(facesMessage);
        }
    }

    public void priceValidator(FacesContext facesContext, UIComponent uiComponent, Object o) {
        double toValidate;
        try {
            toValidate = Double.parseDouble(o.toString());
            if (toValidate < 0) {
                FacesMessage facesMessage = new FacesMessage("Цена должна быть неотрицательной");
                throw new ValidatorException(facesMessage);
            }
        } catch (NumberFormatException e) {
            FacesMessage facesMessage = new FacesMessage("Цена введена некорректно");
            throw new ValidatorException(facesMessage);
        }
    }

    public void countValidator(FacesContext facesContext, UIComponent uiComponent, Object o) {
        int toValidate;
        try {
            toValidate = Integer.parseInt(o.toString());
            if (toValidate < 0) {
                FacesMessage facesMessage = new FacesMessage("Количество должно быть неотрицательным");
                throw new ValidatorException(facesMessage);
            }
        } catch (NumberFormatException e) {
            FacesMessage facesMessage = new FacesMessage("Количество должно быть целым числом");
            throw new ValidatorException(facesMessage);
        }
    }

    public String addProduct(CustomerBean bean) {
        Product product = new Product(name, Double.valueOf(price), Integer.valueOf(count));
        try {
            orderProductEJB.addProduct(product, orderId);
            bean.updateCustomer();
            reset();
            return ACTION_SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
            reset();
            return ACTION_SQL_ERROR;
        }
    }

    private void reset() {
        orderId = -1;
        name = "";
        price = "";
        count = "";
    }

}
