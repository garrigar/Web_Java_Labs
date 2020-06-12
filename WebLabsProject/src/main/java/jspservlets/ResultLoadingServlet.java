package jspservlets;

import database.DatabaseManager;
import entities.Customer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

//@WebServlet("/result.html")
public class ResultLoadingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        Customer customer = (Customer) session.getAttribute(Constants.CUSTOMER_ATTR_NAME);
        session.removeAttribute(Constants.CUSTOMER_ATTR_NAME);

        try (DatabaseManager databaseManager = new DatabaseManager()){
            session.setAttribute(Constants.CUSTOMER_ATTR_NAME, databaseManager.downloadCustomer(customer));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getServletContext().getRequestDispatcher("/result.jsp").forward(request, response);

    }
}
