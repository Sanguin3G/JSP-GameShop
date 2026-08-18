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
import model.Category;
import model.Product;

/**
 * Handles product filtering by category
 *
 * @author huanv
 */
@WebServlet(name = "CategoryServlet", urlPatterns = {"/category"})
public class CategoryServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CategoryServlet.class.getName());
    private static final String HOME_JSP = "home.jsp";
    private static final String HOME_SERVLET = "home";
    private static final int DEFAULT_PRODUCTS_PER_PAGE = 8;

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays products filtered by category
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
        
        String categoryId = request.getParameter("cid");
        
        // Validate category ID
        if (categoryId == null || categoryId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Category ID not provided");
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        try (DAO dao = new DAO()) {
            // Get all categories for navigation
            List<Category> categories = dao.getAllCategory();
            
            // Get products in the selected category
            List<Product> products = dao.getAllProductByCategoryID(categoryId);
            
            if (products.isEmpty()) {
                LOGGER.log(Level.INFO, "No products found in category: {0}", categoryId);
            }
            
            // Handle pagination
            int page = 1;
            int productsPerPage = DEFAULT_PRODUCTS_PER_PAGE;
            String pageParam = request.getParameter("page");
            
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) {
                        page = 1;
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Invalid page number: {0}", pageParam);
                }
            }
            
            int totalProducts = products.size();
            int totalPages = (totalProducts + productsPerPage - 1) / productsPerPage;
            
            page = HomeServlet.safePage(pageParam, totalPages);
            
            int start = (page - 1) * productsPerPage;
            int end = Math.min(page * productsPerPage, totalProducts);
            
            List<Product> paginatedProducts = dao.getAllProductByPage(products, start, end);
            
            // Find the current category for display
            Category currentCategory = null;
            for (Category c : categories) {
                if (String.valueOf(c.getId()).equals(categoryId)) {
                    currentCategory = c;
                    break;
                }
            }
            
            // Set attributes for the view
            request.setAttribute("categories", categories);
            request.setAttribute("products", paginatedProducts);
            request.setAttribute("currentCategory", currentCategory);
            request.setAttribute("tag", Integer.parseInt(categoryId)); // For highlighting active category
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
            
            // Forward to home page with filtered products
            request.getRequestDispatcher(HOME_JSP).forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving products by category", e);
            response.sendRedirect(HOME_SERVLET);
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
        return "Category Filter Servlet for GameStore";
    }
}
