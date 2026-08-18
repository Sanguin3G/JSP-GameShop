/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.DAO;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Handles chart display functionality
 *
 * @author huanv
 */
@WebServlet(name = "ChartServlet", urlPatterns = {"/chart"})
public class ChartServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ChartServlet.class.getName());
    private static final String CHART_JSP = "chart.jsp";
    private static final String HOME_SERVLET = "home";
    private static final String SESSION_ACCOUNT_KEY = "account";
    private static final int ADMIN_LEVEL = 1;

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the chart page with product data
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
            LOGGER.log(Level.WARNING, "Unauthorized access attempt to chart page");
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        try (DAO dao = new DAO()) {
            // Get top products by price for chart display
            List<Product> topProducts = dao.getAllProductByTop10();
            
            // Get top products by rating for chart display
            List<Product> topRatedProducts = dao.getAllProductByRating();
            
            // Get all categories for navigation
            List<Category> categories = dao.getAllCategory();
            
            // Set CSS file for the page
            request.setAttribute("topProducts", topProducts);
            request.setAttribute("topRatedProducts", topRatedProducts);
            request.setAttribute("topProductsJson", toChartRows(topProducts, true));
            request.setAttribute("topRatedProductsJson", toChartRows(topRatedProducts, false));
            request.setAttribute("categories", categories);
            
            // Forward to chart page
            request.getRequestDispatcher(CHART_JSP).forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving data for chart display", e);
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
        return "Chart Display Servlet for GameStore";
    }

    private String toChartRows(List<Product> products, boolean usePrice) {
        List<List<Object>> rows = new ArrayList<>();
        for (Product product : products) {
            rows.add(Arrays.asList(product.getName(), usePrice ? product.getPrice() : product.getRating()));
        }
        return new Gson().toJson(rows);
    }
}
