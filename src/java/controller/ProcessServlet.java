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
 * Handles cart processing operations including updating quantities and removing items
 *
 * @author huanv
 */
@WebServlet(name = "ProcessServlet", urlPatterns = {"/process"})
public class ProcessServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProcessServlet.class.getName());
    private static final String SESSION_CART_KEY = "cart";
    private static final String SESSION_SIZE_KEY = "size";
    private static final String CART_JSP = "cart.jsp";

    /**
     * Handles the HTTP <code>GET</code> method.
     * Updates item quantity in the cart
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
        Cart cart = getCartFromSession(session);
        
        String numRaw = request.getParameter("num");
        String idRaw = request.getParameter("id");
        
        // Validate parameters
        if (numRaw == null || idRaw == null || numRaw.trim().isEmpty() || idRaw.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Invalid parameters for cart update: num={0}, id={1}", new Object[]{numRaw, idRaw});
            response.sendRedirect(CART_JSP);
            return;
        }
        
        try {
            int id = Integer.parseInt(idRaw);
            int num = Integer.parseInt(numRaw.trim());

            if (num != -1 && num != 1) {
                LOGGER.log(Level.WARNING, "Unsupported cart adjustment: {0}", num);
                response.sendRedirect(CART_JSP);
                return;
            }
            
            // Handle item removal if quantity becomes zero or negative
            if ((num == -1) && (cart.getQuantityByID(id) <= 1)) {
                cart.removeItem(id);
                LOGGER.log(Level.INFO, "Removed item {0} from cart", id);
            } else {
                // Update item quantity
                try (DAO dao = new DAO()) {
                    Product product = dao.getProductByProductID(id);
                    if (product != null) {
                        double price = product.getPrice();
                        Item item = new Item(product, num, price);
                        cart.addItem(item);
                        LOGGER.log(Level.INFO, "Updated item {0} quantity to {1}", new Object[]{id, cart.getQuantityByID(id)});
                    } else {
                        LOGGER.log(Level.WARNING, "Product not found with ID: {0}", id);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error retrieving product information", e);
                }
            }
            
            // Update session attributes
            updateCartSession(session, cart);
            
            // Forward to cart page
            request.getRequestDispatcher(CART_JSP).forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in cart processing", e);
            response.sendRedirect(CART_JSP);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Removes an item from the cart
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
        
        String idRaw = request.getParameter("id");
        
        // Validate parameter
        if (idRaw == null || idRaw.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Invalid ID parameter for item removal");
            response.sendRedirect(CART_JSP);
            return;
        }
        
        try {
            int id = Integer.parseInt(idRaw);
            cart.removeItem(id);
            LOGGER.log(Level.INFO, "Removed item {0} from cart", id);
            
            // Update session attributes
            updateCartSession(session, cart);
            
            // Forward to cart page
            request.getRequestDispatcher(CART_JSP).forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid ID format for item removal", e);
            response.sendRedirect(CART_JSP);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Process Servlet for cart operations";
    }
    
    /**
     * Helper method to get cart from session or create new one if not exists
     * 
     * @param session The HTTP session
     * @return The cart object
     */
    private Cart getCartFromSession(HttpSession session) {
        Object cartObj = session.getAttribute(SESSION_CART_KEY);
        return (cartObj != null) ? (Cart) cartObj : new Cart();
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
