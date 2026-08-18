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

/**
 * Handles product addition functionality
 *
 * @author huanv
 */
@WebServlet(name = "AddServlet", urlPatterns = {"/add"})
public class AddServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AddServlet.class.getName());
    private static final String ADD_JSP = "add.jsp";
    private static final String LIST_SERVLET = "list";
    private static final String HOME_SERVLET = "home";
    private static final String SESSION_ACCOUNT_KEY = "account";
    private static final int ADMIN_LEVEL = 1;

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the product addition form
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
            LOGGER.log(Level.WARNING, "Unauthorized access attempt to add product");
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        try (DAO dao = new DAO()) {
            // Get categories for dropdown
            List<Category> categories = dao.getAllCategory();
            
            // Set CSS file for the page
            request.setAttribute("categories", categories);
            
            // Forward to add product page
            request.getRequestDispatcher(ADD_JSP).forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving categories for product addition", e);
            response.sendRedirect(LIST_SERVLET);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Processes product addition form submission
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
            LOGGER.log(Level.WARNING, "Unauthorized attempt to add product");
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        String name = request.getParameter("name");
        String image = request.getParameter("image");
        String priceRaw = request.getParameter("price");
        String description = request.getParameter("description");
        String categoryIdRaw = request.getParameter("cid");
        String releaseDate = request.getParameter("releasedate");
        String ratingRaw = request.getParameter("rating");
        
        // Validate required fields
        if (name == null || image == null || priceRaw == null || 
            description == null || categoryIdRaw == null || releaseDate == null || ratingRaw == null ||
            name.trim().isEmpty() || image.trim().isEmpty() || description.trim().isEmpty()
                || priceRaw.trim().isEmpty() || categoryIdRaw.trim().isEmpty()
                || releaseDate.trim().isEmpty() || ratingRaw.trim().isEmpty()) {
            
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
                dao.insert(name, image, price, description, categoryId, releaseDate, rating);
                LOGGER.log(Level.INFO, "Product added successfully: {0}", name);
                response.sendRedirect(LIST_SERVLET);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in product addition", e);
            request.setAttribute("errorMessage", "Invalid number format");
            doGet(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding product", e);
            request.setAttribute("errorMessage", "An error occurred while adding the product");
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
        return "Product Addition Servlet for GameStore";
    }
}
