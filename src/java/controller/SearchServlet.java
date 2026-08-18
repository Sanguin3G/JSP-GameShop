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
 * Handles product search functionality
 *
 * @author huanv
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SearchServlet.class.getName());
    private static final String HOME_JSP = "home.jsp";
    private static final String HOME_SERVLET = "home";
    private static final int DEFAULT_PRODUCTS_PER_PAGE = 8;

    /**
     * Handles the HTTP <code>GET</code> method.
     * Processes product search request
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
        
        String searchTerm = request.getParameter("search");
        if (searchTerm == null) {
            // Keep old bookmarks/forms working while the navigation uses `search`.
            searchTerm = request.getParameter("keyword");
        }
        
        // Validate search term
        if (searchTerm == null) {
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        // Trim and sanitize search term
        searchTerm = searchTerm.trim();
        if (searchTerm.isEmpty()) {
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        try (DAO dao = new DAO()) {
            // Get search results
            List<Product> searchResults = dao.getProductBySearchName(searchTerm);
            
            // Get all categories for navigation
            List<Category> categories = dao.getAllCategory();
            
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
            
            int totalProducts = searchResults.size();
            int totalPages = (totalProducts + productsPerPage - 1) / productsPerPage;
            
            page = HomeServlet.safePage(pageParam, totalPages);
            
            int start = (page - 1) * productsPerPage;
            int end = Math.min(page * productsPerPage, totalProducts);
            
            List<Product> paginatedResults = dao.getAllProductByPage(searchResults, start, end);
            
            // Set attributes for the view
            request.setAttribute("products", paginatedResults);
            request.setAttribute("categories", categories);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalResults", totalProducts);
            
            // Log search query
            LOGGER.log(Level.INFO, "Search query: \"{0}\", found {1} results", 
                    new Object[]{searchTerm, totalProducts});
            
            // Forward to home page with search results
            request.getRequestDispatcher(HOME_JSP).forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing search query: " + searchTerm, e);
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
        return "Product Search Servlet for GameStore";
    }
}
