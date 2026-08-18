/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.DAO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;

/**
 * Handles user registration functionality
 *
 * @author huanv
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    private static final String REGISTER_JSP = "register.jsp";
    private static final String LOGIN_PAGE = "login";
    private static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the registration form.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(REGISTER_JSP).forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Processes user registration form submission.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        
        String username = request.getParameter("user");
        String password = request.getParameter("pass");
        String confirmPassword = request.getParameter("repass");
        
        // Validate input
        if (username == null || password == null || confirmPassword == null || 
            username.trim().isEmpty() || password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required!");
            request.getRequestDispatcher(REGISTER_JSP).forward(request, response);
            return;
        }
        
        // Validate username format (alphanumeric, 4-20 chars)
        if (!username.matches("^[a-zA-Z0-9]{4,20}$")) {
            request.setAttribute("error", "Username must be 4-20 alphanumeric characters!");
            request.getRequestDispatcher(REGISTER_JSP).forward(request, response);
            return;
        }
        
        // Validate password strength
        if (password.length() < MIN_PASSWORD_LENGTH) {
            request.setAttribute("error", "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long!");
            request.getRequestDispatcher(REGISTER_JSP).forward(request, response);
            return;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match!");
            request.setAttribute("username", username); // Keep username in form
            request.getRequestDispatcher(REGISTER_JSP).forward(request, response);
            return;
        }
        
        try (DAO dao = new DAO()) {
            // Check if username already exists
            Account existingAccount = dao.checkUserExist(username);
            
            if (existingAccount != null) {
                LOGGER.log(Level.INFO, "Registration attempt with existing username: {0}", username);
                request.setAttribute("error", "Username already exists!");
                request.getRequestDispatcher(REGISTER_JSP).forward(request, response);
                return;
            }
            
            // Create new account
            dao.signup(username, password);
            LOGGER.log(Level.INFO, "New user registered: {0}", username);
            
            // Redirect to login page with a one-time success message
            request.getSession().setAttribute("registrationSuccess", "Account created successfully! Please log in.");
            response.sendRedirect(LOGIN_PAGE);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during user registration", e);
            request.setAttribute("error", "An error occurred during registration. Please try again.");
            request.getRequestDispatcher(REGISTER_JSP).forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Registration Servlet for GameStore";
    }
}
