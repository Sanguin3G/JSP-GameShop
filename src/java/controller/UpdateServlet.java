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
import model.Account;
import model.Category;
import model.Product;

/**
 * Handles product update functionality
 *
 * @author huanv
 */
@WebServlet(name = "UpdateServlet", urlPatterns = {"/update"})
public class UpdateServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateServlet.class.getName());
    private static final String UPDATE_JSP = "update.jsp";
    private static final String LIST_SERVLET = "list";
    private static final String SESSION_ACCOUNT_KEY = "account";
    private static final int ADMIN_LEVEL = 1;

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the product update form
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
        
        // Check if user is admin
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT_KEY);
        
        if (account == null || account.getAdminLevel() != ADMIN_LEVEL) {
            LOGGER.log(Level.WARNING, "Unauthorized access attempt to update product");
            response.sendRedirect("home");
            return;
        }
        
        String productId = request.getParameter("pid");
        
        if (productId == null || productId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Product ID not provided for update");
            response.sendRedirect(LIST_SERVLET);
            return;
        }
        
        try (DAO dao = new DAO()) {
            List<Category> categories = dao.getAllCategory();
            Product product = dao.getProductByProductID(productId);
            
            if (product == null) {
                LOGGER.log(Level.WARNING, "Product not found with ID: {0}", productId);
                response.sendRedirect(LIST_SERVLET);
                return;
            }
            
            request.setAttribute("product", product);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher(UPDATE_JSP).forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving product information for update", e);
            response.sendRedirect(LIST_SERVLET);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Processes product update form submission
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
        
        // Check if user is admin
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT_KEY);
        
        if (account == null || account.getAdminLevel() != ADMIN_LEVEL) {
            LOGGER.log(Level.WARNING, "Unauthorized attempt to update product");
            response.sendRedirect("home");
            return;
        }
        
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String image = request.getParameter("image");
        String priceRaw = request.getParameter("price");
        String description = request.getParameter("description");
        String categoryIdRaw = request.getParameter("cid");
        String releaseDate = request.getParameter("releasedate");
        String ratingRaw = request.getParameter("rating");
        
        // Validate required fields
        if (id == null || name == null || image == null || priceRaw == null || 
            description == null || categoryIdRaw == null || releaseDate == null || ratingRaw == null ||
            id.trim().isEmpty() || name.trim().isEmpty() || image.trim().isEmpty()
            || priceRaw.trim().isEmpty() || description.trim().isEmpty()
            || categoryIdRaw.trim().isEmpty() || releaseDate.trim().isEmpty()
            || ratingRaw.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "All fields are required");
            doGet(request, response);
            return;
        }
        
        try {
            int categoryId = Integer.parseInt(categoryIdRaw);
            double price = Double.parseDouble(priceRaw);
            double rating = Double.parseDouble(ratingRaw);
            
            // Validate price and rating
            if (price < 0) {
                request.setAttribute("errorMessage", "Price cannot be negative");
                doGet(request, response);
                return;
            }
            
            if (rating < 0 || rating > 5) {
                request.setAttribute("errorMessage", "Rating must be between 0 and 5");
                doGet(request, response);
                return;
            }
            
            try (DAO dao = new DAO()) {
                int productId = Integer.parseInt(id);
                dao.update(name, image, price, description, categoryId, releaseDate, rating, productId);
                LOGGER.log(Level.INFO, "Product updated successfully: {0}", id);
                response.sendRedirect(LIST_SERVLET);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in product update", e);
            request.setAttribute("errorMessage", "Invalid number format");
            doGet(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product", e);
            request.setAttribute("errorMessage", "An error occurred while updating the product");
            doGet(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Product Update Servlet for GameStore";
    }
}
