/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.DAO;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.Item;
import model.Product;

/**
 * Handles shopping cart operations including adding items to cart
 *
 * @author huanv
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CartServlet.class.getName());
    private static final String SESSION_CART_KEY = "cart";
    private static final String SESSION_SIZE_KEY = "size";
    private static final String CART_JSP = "cart.jsp";

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the cart page
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
        
        // Forward to cart page
        request.getRequestDispatcher(CART_JSP).forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Adds items to the cart
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
        
        HttpSession session = request.getSession();
        Cart cart = getCartFromSession(session);
        
        String quantityRaw = request.getParameter("num");
        String productId = request.getParameter("pid");
        
        // Validate parameters
        if (quantityRaw == null || productId == null || quantityRaw.trim().isEmpty() || productId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Invalid parameters for adding to cart: quantity={0}, productId={1}", 
                    new Object[]{quantityRaw, productId});
            request.setAttribute("errorMessage", "Invalid product information");
            request.getRequestDispatcher(CART_JSP).forward(request, response);
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityRaw);
            
            // Validate quantity
            if (quantity <= 0) {
                LOGGER.log(Level.WARNING, "Invalid quantity for cart: {0}", quantity);
                request.setAttribute("errorMessage", "Quantity must be greater than zero");
                request.getRequestDispatcher(CART_JSP).forward(request, response);
                return;
            }
            
            // Get product information
            try (DAO dao = new DAO()) {
                Product product = dao.getProductByProductID(productId);
                
                if (product == null) {
                    LOGGER.log(Level.WARNING, "Product not found with ID: {0}", productId);
                    request.setAttribute("errorMessage", "Product not found");
                    request.getRequestDispatcher(CART_JSP).forward(request, response);
                    return;
                }
                
                // Add item to cart
                double price = product.getPrice();
                Item item = new Item(product, quantity, price);
                cart.addItem(item);
                
                LOGGER.log(Level.INFO, "Added product {0} to cart, quantity: {1}", 
                        new Object[]{productId, quantity});
                
                // Update session
                updateCartSession(session, cart);
                
                // Forward to cart page
                request.getRequestDispatcher(CART_JSP).forward(request, response);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in cart", e);
            request.setAttribute("errorMessage", "Invalid quantity format");
            request.getRequestDispatcher(CART_JSP).forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing cart request", e);
            request.setAttribute("errorMessage", "An error occurred while processing your request");
            request.getRequestDispatcher(CART_JSP).forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Cart Servlet for GameStore";
    }
    
    /**
     * Helper method to get cart from session or create new one if not exists
     * 
     * @param session The HTTP session
     * @return The cart object
     */
    private Cart getCartFromSession(HttpSession session) {
        Object cartObj = session.getAttribute(SESSION_CART_KEY);
        Cart cart = (cartObj != null) ? (Cart) cartObj : new Cart();
        return cart;
    }
    
    /**
     * Helper method to update cart in session
     * 
     * @param session The HTTP session
     * @param cart The cart to store in session
     */
    private void updateCartSession(HttpSession session, Cart cart) {
        List<Item> items = cart.getItems();
        session.setAttribute(SESSION_CART_KEY, cart);
        session.setAttribute(SESSION_SIZE_KEY, items.size());
    }
}
