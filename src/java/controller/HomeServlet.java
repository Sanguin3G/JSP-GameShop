package controller;

import dal.DAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Product;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    private static final int DEFAULT_PRODUCTS_PER_PAGE = 8;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try (DAO dao = new DAO()) {
            List<Product> allProducts = dao.getAllProduct();
            int pageSize = getProductsPerPage();
            int totalPages = pageCount(allProducts.size(), pageSize);
            int page = safePage(request.getParameter("page"), totalPages);
            int start = (page - 1) * pageSize;

            request.setAttribute("categories", dao.getAllCategory());
            request.setAttribute("products", dao.getAllProductByPage(allProducts, start, start + pageSize));
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("successMessage") != null) {
                request.setAttribute("successMessage", session.getAttribute("successMessage"));
                session.removeAttribute("successMessage");
            }
            request.getRequestDispatcher("home.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private int getProductsPerPage() {
        try {
            return Math.max(1, Integer.parseInt(getServletContext().getInitParameter("productsPerPage")));
        } catch (Exception ex) {
            return DEFAULT_PRODUCTS_PER_PAGE;
        }
    }

    static int pageCount(int itemCount, int pageSize) {
        return itemCount == 0 ? 0 : (itemCount + pageSize - 1) / pageSize;
    }

    static int safePage(String value, int totalPages) {
        int requested = 1;
        try {
            requested = Integer.parseInt(value);
        } catch (Exception ignored) {
            // The first page is the useful fallback for malformed URLs.
        }
        if (requested < 1) {
            return 1;
        }
        return totalPages == 0 ? 1 : Math.min(requested, totalPages);
    }
}
