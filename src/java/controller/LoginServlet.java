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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;

/**
 * Handles user authentication and login functionality
 * 
 * @author huanv
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private static final int COOKIE_MAX_AGE = 86400 * 30; // 30 days
    private static final String SESSION_ACCOUNT_KEY = "account";

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the login form or redirects if already logged in.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SESSION_ACCOUNT_KEY) != null) {
            // User is already logged in, redirect to home
            response.sendRedirect("home");
            return;
        }
        
        // Forward to login page
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Processes login form submission.
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
        String remember = request.getParameter("remember");
        
        // Validate input
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Username and password are required!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        try (DAO dao = new DAO()) {
            Account account = dao.login(username, password);
            
            if (account == null) {
                request.setAttribute("error", "Invalid username or password!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }
            
            // Login successful
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_ACCOUNT_KEY, account);
            
            // Handle "Remember me" functionality
            if (remember != null) {
                // Store only the username in a cookie, never the password
                Cookie usernameCookie = new Cookie("username", username);
                Cookie rememberCookie = new Cookie("remember", "1");
                
                usernameCookie.setMaxAge(COOKIE_MAX_AGE);
                rememberCookie.setMaxAge(COOKIE_MAX_AGE);
                
                // Set cookie path to root
                usernameCookie.setPath("/");
                rememberCookie.setPath("/");
                
                // Set secure and httpOnly flags for better security
                usernameCookie.setHttpOnly(true);
                rememberCookie.setHttpOnly(true);
                
                response.addCookie(usernameCookie);
                response.addCookie(rememberCookie);
            } else {
                // Remove any existing cookies
                Cookie usernameCookie = new Cookie("username", "");
                Cookie rememberCookie = new Cookie("remember", "");
                
                usernameCookie.setMaxAge(0);
                rememberCookie.setMaxAge(0);
                
                usernameCookie.setPath("/");
                rememberCookie.setPath("/");
                
                response.addCookie(usernameCookie);
                response.addCookie(rememberCookie);
            }
            
            // Log successful login
            LOGGER.log(Level.INFO, "User {0} logged in successfully", username);
            
            // Redirect to home page
            response.sendRedirect("home");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login process", e);
            request.setAttribute("error", "An error occurred during login. Please try again.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Login Servlet for GameStore";
    }
}
