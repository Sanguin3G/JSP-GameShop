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

/**
 * Handles account management functionality
 *
 * @author huanv
 */
@WebServlet(name = "AccountServlet", urlPatterns = {"/account"})
public class AccountServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AccountServlet.class.getName());
    private static final String ACCOUNT_JSP = "account.jsp";
    private static final String HOME_SERVLET = "home";
    private static final String SESSION_ACCOUNT_KEY = "account";
    private static final int ADMIN_LEVEL = 1;

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the account management page
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
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT_KEY);
        
        if (account == null) {
            response.sendRedirect("login");
            return;
        }
        
        try (DAO dao = new DAO()) {
            List<Account> accounts = account.getAdminLevel() == ADMIN_LEVEL
                    ? dao.getAllAccounts() : java.util.Collections.singletonList(account);
            
            // Set CSS file for the page
            request.setAttribute("accounts", accounts);
            
            // Get page parameters for pagination
            int page = 1;
            int accountsPerPage = 5;
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
            
            int totalAccounts = accounts.size();
            int totalPages = (totalAccounts + accountsPerPage - 1) / accountsPerPage;
            
            page = HomeServlet.safePage(pageParam, totalPages);
            
            int start = (page - 1) * accountsPerPage;
            int end = Math.min(page * accountsPerPage, totalAccounts);
            
            List<Account> paginatedAccounts = dao.getAllAccountByPage(accounts, start, end);
            
            request.setAttribute("accounts", paginatedAccounts);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
            
            // Forward to account management page
            request.getRequestDispatcher(ACCOUNT_JSP).forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving account information", e);
            response.sendRedirect(HOME_SERVLET);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Currently not implemented - redirects to GET
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to GET method
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Account Management Servlet for GameStore";
    }
}
