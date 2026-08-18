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
 * Handles product detail display functionality
 *
 * @author huanv
 */
@WebServlet(name = "DetailServlet", urlPatterns = {"/detail"})
public class DetailServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DetailServlet.class.getName());
    private static final String DETAIL_JSP = "detail.jsp";
    private static final String HOME_SERVLET = "home";

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays detailed information about a product
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
        
        String productId = request.getParameter("pid");
        
        // Validate product ID
        if (productId == null || productId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Product ID not provided for detail view");
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        try (DAO dao = new DAO()) {
            // Get product details
            Product product = dao.getProductByProductID(productId);
            
            if (product == null) {
                LOGGER.log(Level.WARNING, "Product not found with ID: {0}", productId);
                response.sendRedirect(HOME_SERVLET);
                return;
            }
            
            // Get all categories for navigation
            List<Category> categories = dao.getAllCategory();
            
            // Get related products in the same category
            List<Product> relatedProducts = dao.getAllProductByCategoryID(String.valueOf(product.getCategory().getId()));
            
            // Remove current product from related products
            relatedProducts.removeIf(p -> p.getId() == product.getId());
            
            // Limit related products to 4
            if (relatedProducts.size() > 4) {
                relatedProducts = relatedProducts.subList(0, 4);
            }
            
            // Set attributes for the view
            request.setAttribute("detail", product);
            request.setAttribute("categories", categories);
            request.setAttribute("relatedProducts", relatedProducts);
            
            // Forward to detail page
            request.getRequestDispatcher(DETAIL_JSP).forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving product details", e);
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
        return "Product Detail Servlet for GameStore";
    }
}
