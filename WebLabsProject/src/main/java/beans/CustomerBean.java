package beans;

import entities.Customer;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

@ManagedBean
@SessionScoped
public class CustomerBean {

    private static final String LBL_LOGIN = "Вход";
    private static final String LBL_NOT_FOUND = "Пользователь не найден";
    private static final String LBL_SQL_ERROR = "Ошибка базы данных";

    private static final String ACTION_USER_FOUND = "found";
    // private static final String ACTION_USER_NOT_FOUND = "notfound";
    // private static final String ACTION_SQL_ERROR = "sqlerror";
    private static final String ACTION_LOGOUT = "logout";

    @EJB
    private final CustomerEJB customerEJB = new CustomerEJB();
    private Customer customer;

    private String topMessage = LBL_LOGIN;

    private boolean error = false;

    private String login;
    private String password;


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getTopMessage() {
        return topMessage;
    }

    public boolean isError() {
        return error;
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

    public String validateUserLogin() {
        try {
            customer = customerEJB.validateUserLogin(login, password);

            if (customer == null) {
                topMessage = LBL_NOT_FOUND;
                error = true;
                return null;
            }
            else {
                topMessage = LBL_LOGIN;
                error = false;
                return ACTION_USER_FOUND;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            topMessage = LBL_SQL_ERROR;
            error = true;
            return null;
        }
    }

    public String logOut() {
        customer = null;
        return ACTION_LOGOUT;
    }


    public void downloadXML() {
        JAXBContext context;
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletResponse resp = (HttpServletResponse) ctx.getExternalContext().getResponse();
        try {
            context = JAXBContext.newInstance(Customer.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            m.marshal(customer, System.out);
            resp.setHeader("Content-disposition","attachment; filename=result.xml");
            resp.setContentType("application/xml");

            StringWriter writer = new StringWriter();
            m.marshal(customer, writer);
            PrintWriter respWriter = resp.getWriter();
            respWriter.println(writer.toString());
            writer.close();
            ctx.responseComplete();
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(String productName) {
        try {
            customerEJB.deleteProduct(productName);
            updateCustomer();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer() throws SQLException {
        customer = customerEJB.validateUserLogin(login, password);
    }

}
