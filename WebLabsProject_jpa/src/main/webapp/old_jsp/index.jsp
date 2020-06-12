<%@ page import="database.DatabaseManager" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="entities.Customer" %>
<%@ page import="jspservlets.Constants" %>
<%
    final String loginInputName = "logininput";
    final String passwordInputName = "passwordinput";
    final String REQUIRED_FIELD_MSG = "Required field";
    final String USER_NOT_FOUND_MSG = "User not found";
    final String DB_ERROR_MSG = "Database error";
    final String FORM_INPUT_CLASS_VALUE = "form-input";
    final String FORM_INPUT_INVALID_CLASS_VALUE = "form-input-invalid";
    String loginPrompt = "Login";
    String passwordPrompt = "Password";
    String loginClassValue = FORM_INPUT_CLASS_VALUE;
    String passwordClassValue = FORM_INPUT_CLASS_VALUE;

    if (request.getParameter(loginInputName) != null) {
        String login = request.getParameter(loginInputName);
        String password = request.getParameter(passwordInputName);
        if ("".equals(login) || "".equals(password)) {
            // some field is empty
            if ("".equals(login)) {
                loginPrompt = REQUIRED_FIELD_MSG;
                loginClassValue = FORM_INPUT_INVALID_CLASS_VALUE;
            }
            if ("".equals(password)) {
                passwordPrompt = REQUIRED_FIELD_MSG;
                passwordClassValue = FORM_INPUT_INVALID_CLASS_VALUE;
            }
        } else {

            try (DatabaseManager databaseManager = new DatabaseManager()) {
                if (databaseManager.customerExistsByLoginPassword(login, password)) {
                    // user exists
                    Customer customer = new Customer(null, login, password);

                    session.setAttribute(Constants.CUSTOMER_ATTR_NAME, customer);

                    request.getRequestDispatcher("/result.html").forward(request, response);

                } else {
                    // user not found
                    loginPrompt = USER_NOT_FOUND_MSG;
                    passwordPrompt = USER_NOT_FOUND_MSG;
                    loginClassValue = FORM_INPUT_INVALID_CLASS_VALUE;
                    passwordClassValue = FORM_INPUT_INVALID_CLASS_VALUE;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                loginPrompt = DB_ERROR_MSG;
                passwordPrompt = DB_ERROR_MSG;
                loginClassValue = FORM_INPUT_INVALID_CLASS_VALUE;
                passwordClassValue = FORM_INPUT_INVALID_CLASS_VALUE;
            }

        }
    }
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,minimum-scale=1,maximum-scale=10">
    <title>Web Labs Project | Login</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="content-wrapper">
    <header class="header">
        <p class="header-text">Web Labs Project</p>
    </header>

    <div class="container clearfix">
        <main class="content">
            <!-- for-example -->
            <p class="message">Please log in</p>

            <form action="index.jsp"
                  method="post"
                  class="form">
                <fieldset>

                    <!-- Form Name -->
                    <legend class="form-text" align="center">Authorization</legend>

                    <!-- Text input-->
                    <div class="form-group">
                        <label class="form-text">Login</label>
                        <div>
                            <input
                                name="<%=loginInputName%>"
                                type="text"
                                placeholder="<%=loginPrompt%>"
                                class="<%=loginClassValue%>">
                        </div>
                    </div>

                    <!-- Password input-->
                    <div class="form-group">
                        <label class="form-text">Password</label>
                        <div>
                            <input
                                name="<%=passwordInputName%>"
                                type="password"
                                placeholder="<%=passwordPrompt%>"
                                class="<%=passwordClassValue%>">
                        </div>
                    </div>

                    <!-- Button -->
                    <div class="form-group" align="center">
                        <button class="form-button">Log in</button>
                    </div>

                </fieldset>
            </form>

        </main>
    </div>
</div>
</body>
</html>