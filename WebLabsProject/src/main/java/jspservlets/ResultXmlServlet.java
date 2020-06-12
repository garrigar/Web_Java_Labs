package jspservlets;

import entities.Customer;
import entities.Order;
import entities.Product;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.PrintWriter;

//@WebServlet("/result.xml")
public class ResultXmlServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        Customer customer = (Customer) session.getAttribute(Constants.CUSTOMER_ATTR_NAME);

        response.setContentType("application/xml");

        try (PrintWriter writer = response.getWriter()){
            JAXBContext context = JAXBContext.newInstance(Customer.class, Order.class, Product.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(customer, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
