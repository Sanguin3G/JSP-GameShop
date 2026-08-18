package dal;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Releases JDBC drivers loaded by this legacy web application during shutdown.
 * Tomcat otherwise reports the SQLite driver as a webapp classloader leak.
 */
public class DatabaseCleanupListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(DatabaseCleanupListener.class.getName());

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        ClassLoader applicationLoader = DatabaseCleanupListener.class.getClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == applicationLoader) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "Could not deregister JDBC driver " + driver, ex);
                }
            }
        }
    }
}
