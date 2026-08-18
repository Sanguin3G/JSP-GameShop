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
 * Handles product listing functionality for admin management
 *
 * @author huanv
 */
@WebServlet(name = "ListServlet", urlPatterns = {"/list"})
public class ListServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ListServlet.class.getName());
    private static final String LIST_JSP = "list.jsp";
    private static final String HOME_SERVLET = "home";
    private static final String SESSION_ACCOUNT_KEY = "account";
    private static final int ADMIN_LEVEL = 1;
    private static final int DEFAULT_PRODUCTS_PER_PAGE = 8;

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the product list for admin management
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
            LOGGER.log(Level.WARNING, "Unauthorized access attempt to product list management");
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        try (DAO dao = new DAO()) {
            // Get all products
            List<Product> allProducts = dao.getAllProduct();
            
            // Get all categories for filtering
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
            
            int totalProducts = allProducts.size();
            int totalPages = (totalProducts + productsPerPage - 1) / productsPerPage;
            
            page = HomeServlet.safePage(pageParam, totalPages);
            
            int start = (page - 1) * productsPerPage;
            int end = Math.min(page * productsPerPage, totalProducts);
            
            List<Product> paginatedProducts = dao.getAllProductByPage(allProducts, start, end);
            
            // Set attributes for the view
            request.setAttribute("products", paginatedProducts);
            request.setAttribute("categories", categories);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
            
            // Forward to list page
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving product list", e);
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
        return "Product List Management Servlet for GameStore";
    }
}
