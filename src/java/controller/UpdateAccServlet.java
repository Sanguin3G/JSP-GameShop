/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.DAO;
import java.io.IOException;
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
 * Handles account update functionality
 *
 * @author huanv
 */
@WebServlet(name = "UpdateAccServlet", urlPatterns = {"/updateacc"})
public class UpdateAccServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateAccServlet.class.getName());
    private static final String UPDATE_ACC_JSP = "updateAcc.jsp";
    private static final String ACCOUNT_SERVLET = "account";
    private static final String HOME_SERVLET = "home";
    private static final String SESSION_ACCOUNT_KEY = "account";
    private static final int ADMIN_LEVEL = 1;

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the account update form
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
            LOGGER.log(Level.WARNING, "Unauthorized access attempt to update account");
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        String accountId = request.getParameter("aid");
        if (accountId == null || accountId.trim().isEmpty()) {
            accountId = String.valueOf(account.getId());
        }
        
        // Validate account ID
        if (accountId == null || accountId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Account ID not provided for update");
            response.sendRedirect(ACCOUNT_SERVLET);
            return;
        }
        
        try (DAO dao = new DAO()) {
            // Get account details
            Account targetAccount = dao.getAllAccountById(accountId);
            
            if (targetAccount == null) {
                LOGGER.log(Level.WARNING, "Account not found with ID: {0}", accountId);
                response.sendRedirect(ACCOUNT_SERVLET);
                return;
            }
            
            request.setAttribute("account", targetAccount);
            
            // Forward to account update page
            request.getRequestDispatcher(UPDATE_ACC_JSP).forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving account information for update", e);
            response.sendRedirect(ACCOUNT_SERVLET);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Processes account update form submission
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
            LOGGER.log(Level.WARNING, "Unauthorized attempt to update account");
            response.sendRedirect(HOME_SERVLET);
            return;
        }
        
        String accountIdRaw = request.getParameter("aid");
        String username = request.getParameter("username");
        if (username == null) {
            username = request.getParameter("user");
        }
        String password = request.getParameter("pass");
        String adminLevelRaw = request.getParameter("admin");
        
        // Validate required fields
        if (accountIdRaw == null || username == null || password == null || adminLevelRaw == null ||
            accountIdRaw.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty() || adminLevelRaw.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "All fields are required");
            doGet(request, response);
            return;
        }
        
        try {
            int accountId = Integer.parseInt(accountIdRaw);
            int adminLevel = Integer.parseInt(adminLevelRaw);
            
            // Validate admin level
            if (adminLevel < 0 || adminLevel > 1) {
                request.setAttribute("errorMessage", "Admin level must be 0 or 1");
                doGet(request, response);
                return;
            }
            
            // Prevent changing admin status of own account
            if (account.getId() == accountId && account.getAdminLevel() != adminLevel) {
                LOGGER.log(Level.WARNING, "Admin attempted to change their own admin status");
                request.setAttribute("errorMessage", "You cannot change your own admin status");
                doGet(request, response);
                return;
            }
            
            try (DAO dao = new DAO()) {
                // Update the account
                dao.updateAcc(username, password, adminLevel, accountId);
                if (account.getId() == accountId) {
                    account.setUsername(username);
                    account.setPass(password);
                    account.setAdminLevel(adminLevel);
                    session.setAttribute(SESSION_ACCOUNT_KEY, account);
                }
                LOGGER.log(Level.INFO, "Account updated successfully: {0}", accountId);
                
                // Redirect back to account list
                response.sendRedirect(ACCOUNT_SERVLET);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in account update", e);
            request.setAttribute("errorMessage", "Invalid number format");
            doGet(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating account", e);
            request.setAttribute("errorMessage", "An error occurred while updating the account");
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
        return "Account Update Servlet for GameStore";
    }
}
