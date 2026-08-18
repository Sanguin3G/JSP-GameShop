package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Account;
import model.Cart;
import model.Category;
import model.Item;
import model.Product;

/** Data access used by the legacy JSP/Servlet pages. */
public class DAO extends DBContext {
    private static final Logger LOGGER = Logger.getLogger(DAO.class.getName());
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final String PRODUCT_SELECT =
            "SELECT p.id, p.name, p.image_url, p.price, p.description, "
            + "p.release_date, p.rating, c.id AS category_id, c.name AS category_name "
            + "FROM product p INNER JOIN category c ON c.id = p.category_id ";

    public List<Category> getAllCategory() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name, description, display_order FROM category ORDER BY display_order, name";
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Category category = new Category(result.getInt("id"), result.getString("name"));
                category.setDescription(result.getString("description"));
                category.setDisplayOrder(result.getInt("display_order"));
                categories.add(category);
            }
        } catch (SQLException ex) {
            logDatabaseError("retrieving categories", ex);
        }
        return categories;
    }

    public List<Product> getAllProduct() {
        return queryProducts(PRODUCT_SELECT + "ORDER BY p.id");
    }

    public List<Product> getAllProductByTop10() {
        return queryProducts(PRODUCT_SELECT + "ORDER BY p.price DESC, p.name LIMIT 10");
    }

    public List<Product> getAllProductByRating() {
        return queryProducts(PRODUCT_SELECT + "ORDER BY p.rating DESC, p.name LIMIT 10");
    }

    public List<Product> getAllProductByCategoryID(int categoryId) {
        return queryProducts(PRODUCT_SELECT + "WHERE c.id = ? ORDER BY p.name", categoryId);
    }

    public List<Product> getAllProductByCategoryID(String categoryId) {
        try {
            return getAllProductByCategoryID(Integer.parseInt(categoryId));
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Invalid category ID: {0}", categoryId);
            return new ArrayList<>();
        }
    }

    public Product getProductByProductID(int productId) {
        List<Product> products = queryProducts(PRODUCT_SELECT + "WHERE p.id = ?", productId);
        return products.isEmpty() ? null : products.get(0);
    }

    public Product getProductByProductID(String productId) {
        try {
            return getProductByProductID(Integer.parseInt(productId));
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Invalid product ID: {0}", productId);
            return null;
        }
    }

    public List<Product> getProductBySearchName(String search) {
        String term = search == null ? "" : search.trim();
        // LOWER + LIKE gives case-insensitive search on SQLite.
        return queryProducts(PRODUCT_SELECT
                + "WHERE LOWER(p.name) LIKE LOWER(?) ORDER BY p.name", "%" + term + "%");
    }

    public Account login(String username, String password) {
        String sql = "SELECT id, username, pass, role_id FROM account WHERE username = ? COLLATE NOCASE AND pass = ?";
        return findAccount(sql, username, password);
    }

    public Account checkUserExist(String username) {
        String sql = "SELECT id, username, pass, role_id FROM account WHERE username = ? COLLATE NOCASE";
        return findAccount(sql, username);
    }

    public void signup(String username, String password) {
        String sql = "INSERT INTO account (username, pass, role_id) VALUES (?, ?, 2)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            statement.setString(2, password);
            statement.executeUpdate();
        } catch (SQLException ex) {
            logDatabaseError("creating account", ex);
            throw new IllegalStateException("Could not create the account", ex);
        }
    }

    public void delete(int productId) {
        executeUpdate("DELETE FROM product WHERE id = ?", productId);
    }

    public void delete(String productId) {
        try {
            delete(Integer.parseInt(productId));
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Invalid product ID for deletion: {0}", productId);
        }
    }

    public void insert(String name, String imageUrl, double price, String description,
            int categoryId, String releaseDate, double rating) {
        String sql = "INSERT INTO product (name, image_url, price, description, category_id, release_date, rating) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.trim());
            statement.setString(2, imageUrl.trim());
            statement.setDouble(3, price);
            statement.setString(4, description.trim());
            statement.setInt(5, categoryId);
            statement.setString(6, parseDate(releaseDate).format(INPUT_DATE));
            statement.setDouble(7, rating);
            statement.executeUpdate();
        } catch (SQLException | RuntimeException ex) {
            logDatabaseError("inserting product", ex);
            throw new IllegalStateException("Could not add the product", ex);
        }
    }

    public void update(String name, String imageUrl, double price, String description,
            int categoryId, String releaseDate, double rating, int productId) {
        String sql = "UPDATE product SET name = ?, image_url = ?, price = ?, description = ?, "
                + "category_id = ?, release_date = ?, rating = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.trim());
            statement.setString(2, imageUrl.trim());
            statement.setDouble(3, price);
            statement.setString(4, description.trim());
            statement.setInt(5, categoryId);
            statement.setString(6, parseDate(releaseDate).format(INPUT_DATE));
            statement.setDouble(7, rating);
            statement.setInt(8, productId);
            statement.executeUpdate();
        } catch (SQLException | RuntimeException ex) {
            logDatabaseError("updating product", ex);
            throw new IllegalStateException("Could not update the product", ex);
        }
    }

    public void update(String name, String imageUrl, float price, String description,
            int categoryId, String releaseDate, float rating, String productId) {
        try {
            update(name, imageUrl, price, description, categoryId, releaseDate, rating,
                    Integer.parseInt(productId));
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Invalid product ID for update: {0}", productId);
        }
    }

    public void updateAccount(String username, String password, int roleId, int accountId) {
        String sql = "UPDATE account SET username = ?, pass = ?, role_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            statement.setString(2, password);
            statement.setInt(3, roleId == 1 ? 1 : 2);
            statement.setInt(4, accountId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            logDatabaseError("updating account", ex);
            throw new IllegalStateException("Could not update the account", ex);
        }
    }

    public void updateAcc(String username, String password, int adminLevel, int accountId) {
        updateAccount(username, password, adminLevel, accountId);
    }

    public void addOrder(Account account, Cart cart) {
        if (account == null || cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("An order needs an account and at least one item");
        }

        boolean autoCommit = true;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            int orderId;
            String orderSql = "INSERT INTO orders (account_id, total_amount) VALUES (?, ?)";
            try (PreparedStatement orderStatement = connection.prepareStatement(
                    orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStatement.setInt(1, account.getId());
                orderStatement.setDouble(2, cart.getTotalMoney());
                orderStatement.executeUpdate();
                try (ResultSet keys = orderStatement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("SQLite did not return the new order ID");
                    }
                    orderId = keys.getInt(1);
                }
            }

            String itemSql = "INSERT INTO order_item (order_id, product_id, quantity, price, subtotal) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement itemStatement = connection.prepareStatement(itemSql)) {
                for (Item item : cart.getItems()) {
                    itemStatement.setInt(1, orderId);
                    itemStatement.setInt(2, item.getProduct().getId());
                    itemStatement.setInt(3, item.getQuantity());
                    itemStatement.setDouble(4, item.getPrice());
                    itemStatement.setDouble(5, item.getSubtotal());
                    itemStatement.addBatch();
                }
                itemStatement.executeBatch();
            }
            connection.commit();
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                ex.addSuppressed(rollbackException);
            }
            logDatabaseError("creating order", ex);
            throw new IllegalStateException("Could not complete the order", ex);
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not restore database transaction mode", ex);
            }
        }
    }

    public List<Account> getAllAccounts() {
        return queryAccounts("SELECT id, username, pass, role_id FROM account ORDER BY id");
    }

    public List<Account> getAllAccount() {
        return getAllAccounts();
    }

    public Account getAccountById(int accountId) {
        List<Account> accounts = queryAccounts("SELECT id, username, pass, role_id FROM account WHERE id = ?", accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public Account getAllAccountById(String accountId) {
        try {
            return getAccountById(Integer.parseInt(accountId));
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Invalid account ID: {0}", accountId);
            return null;
        }
    }

    public List<Account> getNonAdminAccounts() {
        return queryAccounts("SELECT id, username, pass, role_id FROM account WHERE role_id != 1 ORDER BY id");
    }

    public List<Account> getAllAccountNotAdmin() {
        return getNonAdminAccounts();
    }

    public void removeAccount(int accountId) {
        executeUpdate("DELETE FROM account WHERE id = ?", accountId);
    }

    public void remove(String accountId) {
        try {
            removeAccount(Integer.parseInt(accountId));
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Invalid account ID for removal: {0}", accountId);
        }
    }

    public List<Product> getAllProductByPage(List<Product> products, int start, int end) {
        if (products == null || products.isEmpty() || start >= products.size() || end <= start) {
            return new ArrayList<>();
        }
        int safeStart = Math.max(0, start);
        int safeEnd = Math.min(products.size(), end);
        return products.subList(safeStart, safeEnd);
    }

    public List<Account> getAllAccountByPage(List<Account> accounts, int start, int end) {
        if (accounts == null || accounts.isEmpty() || start >= accounts.size() || end <= start) {
            return new ArrayList<>();
        }
        int safeStart = Math.max(0, start);
        int safeEnd = Math.min(accounts.size(), end);
        return accounts.subList(safeStart, safeEnd);
    }

    private List<Product> queryProducts(String sql, Object... parameters) {
        List<Product> products = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    products.add(mapProduct(result));
                }
            }
        } catch (SQLException ex) {
            logDatabaseError("retrieving products", ex);
        }
        return products;
    }

    private Product mapProduct(ResultSet result) throws SQLException {
        Category category = new Category(result.getInt("category_id"), result.getString("category_name"));
        String releaseDate = result.getString("release_date");
        return new Product(result.getInt("id"), result.getString("name"),
                result.getString("image_url"), result.getDouble("price"),
                result.getString("description"), formatDate(releaseDate),
                result.getDouble("rating"), category);
    }

    private Account findAccount(String sql, Object... parameters) {
        List<Account> accounts = queryAccounts(sql, parameters);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    private List<Account> queryAccounts(String sql, Object... parameters) {
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    accounts.add(new Account(result.getInt("id"), result.getString("username"),
                            result.getString("pass"), result.getInt("role_id")));
                }
            }
        } catch (SQLException ex) {
            logDatabaseError("retrieving accounts", ex);
        }
        return accounts;
    }

    private void executeUpdate(String sql, int id) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            logDatabaseError("updating database", ex);
            throw new IllegalStateException("Could not update the database", ex);
        }
    }

    private void bind(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Release date is required");
        }
        String date = value.trim();
        try {
            return LocalDate.parse(date, INPUT_DATE);
        } catch (Exception ignored) {
            return LocalDate.parse(date, DISPLAY_DATE);
        }
    }

    private String formatDate(String value) {
        try {
            return LocalDate.parse(value, INPUT_DATE).format(DISPLAY_DATE);
        } catch (Exception ex) {
            return value == null ? "" : value;
        }
    }

    private void logDatabaseError(String action, Exception ex) {
        LOGGER.log(Level.SEVERE, "Error " + action, ex);
    }
}
