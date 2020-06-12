<%@ page import="entities.Customer" %>
<%@ page import="entities.Order" %>
<%@ page import="entities.Product" %>
<%@ page import="jspservlets.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%!
    private static final int TIMEOUT_SECONDS = 5;

    private void appendOrder(StringBuffer sb, Order order) {
        for (int i = 0; i < order.getProductsCount(); i++) {
            sb.append("<tr>\n");
            if (i == 0) {
                sb.append("<td rowspan=\"").append(order.getProductsCount()).append("\" class=\"first\">")
                        .append(order.getId())
                        .append("</td>\n");
            }
            Product product = order.getProduct(i);
            sb.append("<td>").append(product.getName()).append("</td>\n");
            sb.append("<td>").append(product.getPrice()).append("</td>\n");
            sb.append("<td>").append(product.getCount()).append("</td>\n");
            sb.append("<td>").append(product.getTotal()).append("</td>\n");
            sb.append("</tr>\n");
        }
    }

    private String markupCustomerContent(Customer customer) {
        StringBuffer sb = new StringBuffer();

        sb.append("<p class=\"message\">Hello, ").append(customer.getName()).append("!</p>\n");
        sb.append("<a href=\"index.jsp\">Back to login page</a>\n" +
                "<a href=\"result.xml\">Download xml file</a>\n");
        sb.append("<table>\n" +
                "    <caption>ORDERS DESCRIPTION</caption>\n" +
                "    <tr>\n" +
                "        <th rowspan=\"2\" class=\"first\">Order (id)</th>\n" +
                "        <th colspan=\"4\" class=\"first\">Items</th>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td class=\"first\">Name</td>\n" +
                "        <td class=\"first\">Price</td>\n" +
                "        <td class=\"first\">Count</td>\n" +
                "        <td class=\"first\">Total</td>\n" +
                "    </tr>\n");
        for (int i = 0; i < customer.getOrdersCount(); i++){
            appendOrder(sb, customer.getOrder(i));
        }
        sb.append("</table>\n");

        return sb.toString();
    }

    private String markupError(){
        return "<p class=\"error-msg\">Error: customer does not exist in the session</p>\n" +
                "<p class=\"message\">You will be redirected to login page in " + TIMEOUT_SECONDS + " seconds</p>\n" +
                "<a href=\"index.jsp\">Back to login page</a>";
    }
%>
<%
    Customer customer = (Customer) session.getAttribute(Constants.CUSTOMER_ATTR_NAME);
    if (customer == null){
        response.setHeader("Refresh", TIMEOUT_SECONDS + ";url=index.jsp");
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,minimum-scale=1,maximum-scale=10">
    <title>Web Labs Project | <%=(customer != null) ? "Orders of " + customer.getName() : "Error"%></title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="content-wrapper">
    <header class="header">
        <p class="header-text">Web Labs Project</p>
    </header>

    <div class="container clearfix">
        <main class="content">
            <%=(customer != null) ? markupCustomerContent(customer) : markupError()%>
        </main>
    </div>
</div>
</body>
</html>
