<%@ page import="entities.Customer" %>
<%@ page import="entities.Order" %>
<%@ page import="entities.Product" %>
<%@ page import="jspservlets.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    final int TIMEOUT_SECONDS = 5;
    Customer customer = (Customer) session.getAttribute(Constants.CUSTOMER_ATTR_NAME);
    if (customer == null) {
        response.setHeader("Refresh", TIMEOUT_SECONDS + ";url=index.jsp");
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,minimum-scale=1,maximum-scale=10">
    <title>Web Labs Project | <%=(customer != null) ? "Orders of " + customer.getName() : "Error"%>
    </title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="content-wrapper">
    <header class="header">
        <p class="header-text">Web Labs Project</p>
    </header>

    <div class="container clearfix">
        <main class="content">
            <% if (customer == null) { %>
                <p class="error-msg">Error: customer does not exist in the session</p>
                <p class="message">You will be redirected to login page in <%=TIMEOUT_SECONDS%> seconds</p>
                <a href="index.jsp">Back to login page</a>;
            <% } else { %>
                <p class="message">Hello, <%=customer.getName()%>!</p>
                <a href="index.jsp">Back to login page</a>
                <a href="result.xml">Download xml file</a>
                <table>
                    <caption>ORDERS DESCRIPTION</caption>
                    <tr>
                        <th rowspan="2" class="first">Order (id)</th>
                        <th colspan="4" class="first">Items</th>
                    </tr>
                    <tr>
                        <td class="first">Name</td>
                        <td class="first">Price</td>
                        <td class="first">Count</td>
                        <td class="first">Total</td>
                    </tr>

                    <%
                        for (int i = 0; i < customer.getOrdersCount(); i++) {
                            Order order = customer.getOrder(i);
                            for (int j = 0; j < order.getProductsCount(); j++) {
                    %>
                                <tr>
                                    <% if (j == 0) { %>
                                        <td rowspan="<%=order.getProductsCount()%>" class="first"><%=order.getId()%></td>
                                    <%
                                        }
                                        Product product = order.getProduct(j);
                                    %>
                                    <td><%=product.getName()%></td>
                                    <td><%=product.getPrice()%></td>
                                    <td><%=product.getCount()%></td>
                                    <td><%=product.getTotal()%></td>
                                </tr>
                    <%
                            }
                        }
                    %>
                </table>
            <% } %>
        </main>
    </div>
</div>
</body>
</html>
