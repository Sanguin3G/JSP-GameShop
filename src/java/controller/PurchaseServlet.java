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
import jakarta.servlet.http.HttpSession;
import model.Account;
import model.Cart;

/**
 * Handles purchase/checkout functionality
 *
 * @author huanv
 */
@WebServlet(name = "PurchaseServlet", urlPatterns = {"/purchase"})
public class PurchaseServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(PurchaseServlet.class.getName());
    private static final String HOME_SERVLET = "home";
    private static final String LOGIN_SERVLET = "login";
    private static final String CART_JSP = "cart.jsp";
    private static final String SESSION_ACCOUNT_KEY = "account";
    private static final String SESSION_CART_KEY = "cart";

    /**
     * Handles the HTTP <code>GET</code> method.
     * Processes purchase request
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        
        HttpSession session = request.getSession();
        
        // Check if user is logged in
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT_KEY);
        if (account == null) {
            LOGGER.log(Level.INFO, "Unauthenticated user attempted to make a purchase");
            request.setAttribute("errorMessage", "Please log in to complete your purchase");
            response.sendRedirect(LOGIN_SERVLET);
            return;
        }
        
        // Check if cart exists and is not empty
        Cart cart = (Cart) session.getAttribute(SESSION_CART_KEY);
        if (cart == null || cart.getItems().isEmpty()) {
            LOGGER.log(Level.INFO, "Purchase attempted with empty cart");
            request.setAttribute("errorMessage", "Your cart is empty");
            request.getRequestDispatcher(CART_JSP).forward(request, response);
            return;
        }
        
        try (DAO dao = new DAO()) {
            // Process the order
            dao.addOrder(account, cart);
            LOGGER.log(Level.INFO, "Purchase completed successfully for user ID: {0}, Total: {1}", 
                    new Object[]{account.getId(), cart.getTotalMoney()});
            
            // Clear the cart after successful purchase
            session.removeAttribute(SESSION_CART_KEY);
            session.setAttribute("size", 0);
            
            // Set success message
            session.setAttribute("successMessage", "Order placed. Your games are now officially someone else’s problem.");
            
            // Redirect to home page
            response.sendRedirect(HOME_SERVLET);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing purchase", e);
            request.setAttribute("errorMessage", "An error occurred while processing your purchase. Please try again.");
            request.getRequestDispatcher(CART_JSP).forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Redirects to GET method
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Purchase Servlet for GameStore";
    }
}
