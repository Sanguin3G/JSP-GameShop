package dal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Opens the local SQLite database and creates the small catalog on first use.
 * Keeping the setup here lets the old JSP application run without a separate
 * database server or a container-specific connection pool.
 */
public class DBContext implements AutoCloseable {
    protected Connection connection;

    private static final Logger LOGGER = Logger.getLogger(DBContext.class.getName());
    private static final String DEFAULT_URL = "jdbc:sqlite:GameStore.db";
    private static final String SCHEMA_RESOURCE = "/GameStore_sqlite.sql";

    public DBContext() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(resolveDatabaseUrl());
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            LOGGER.log(Level.SEVERE, "Unable to open the SQLite database", ex);
            throw new IllegalStateException("GameShop could not open its SQLite database", ex);
        }
    }

    private String resolveDatabaseUrl() {
        String configuredUrl = System.getProperty("gamestore.db.url");
        return configuredUrl == null || configuredUrl.trim().isEmpty()
                ? DEFAULT_URL
                : configuredUrl.trim();
    }

    private void initializeDatabase() throws SQLException, IOException {
        if (!isInitialized()) {
            runSchemaScript();
            LOGGER.log(Level.INFO, "Initialized SQLite database with GameShop seed data");
        }
    }

    private boolean isInitialized() throws SQLException {
        String sql = "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name IN ('account', 'category', 'product', 'orders', 'order_item')";
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            return result.next() && result.getInt(1) == 5;
        }
    }

    private void runSchemaScript() throws IOException, SQLException {
        InputStream input = DBContext.class.getResourceAsStream(SCHEMA_RESOURCE);
        if (input == null) {
            throw new IOException("Missing database resource " + SCHEMA_RESOURCE);
        }

        StringBuilder script = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.startsWith("--")) {
                    script.append(line).append('\n');
                }
            }
        }

        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try (Statement statement = connection.createStatement()) {
            for (String sql : script.toString().split(";")) {
                if (!sql.trim().isEmpty()) {
                    statement.execute(sql);
                }
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Error closing SQLite connection", ex);
            }
        }
    }
}
