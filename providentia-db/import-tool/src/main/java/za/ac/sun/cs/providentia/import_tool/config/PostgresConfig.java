package za.ac.sun.cs.providentia.import_tool.config;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.import_tool.transaction.PostgresTransactionManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConfig implements DBConfig {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(PostgresConfig.class);
    public final PostgresTransactionManager tm;
    private final String PROPERTIES = "postgres.properties";
    private final Properties props;
    private final Connection db;
    private final String hostname;
    private final String port;
    private final String database;

    PostgresConfig() throws IOException {
        LOG.info("Loading Postgres config.");
        // Get property files
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream propertyStream = loader.getResourceAsStream(PROPERTIES);
        if (propertyStream == null) {
            LOG.error("Properties file not found!");
            throw new IOException("Properties file not found!");
        }
        props = new Properties();
        props.load(propertyStream);

        String tempHostName = "localhost";
        String tempPort = "5432";
        String tempDatabase = "yelp";

        try {
            tempHostName = props.getProperty("hostname");
        } catch (Exception ignored) {
        }
        try {
            tempPort = props.getProperty("port");
        } catch (Exception ignored) {
        }
        try {
            tempDatabase = props.getProperty("database");
        } catch (Exception ignored) {
        }

        this.hostname = tempHostName;
        this.port = tempPort;
        this.database = tempDatabase;

        LOG.info("Connecting to PostgreSQL server.");
        db = connect();
        this.tm = new PostgresTransactionManager(db);
    }

    /**
     * Connects to Postgres over sockets using postgres.properties.
     */
    public Connection connect() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            Path temp = Files.createTempFile("resource-", ".ext");
            InputStream isProperties = classLoader.getResourceAsStream(PROPERTIES);
            if (isProperties == null) {
                throw new IOException("Could not find " + PROPERTIES + "!");
            } else {
                Files.copy(isProperties, temp, StandardCopyOption.REPLACE_EXISTING);
                String url = "jdbc:postgresql://" + hostname + ":" + port + "/" + database;
                return DriverManager.getConnection(url, props);
            }
        } catch (IOException e) {
            LOG.error("IOException while creating temporary copy of " + PROPERTIES + " to give to PostgreSQL" +
                    "keyspace.", e);
            System.exit(1);
        } catch (SQLException e) {
            LOG.error("SQLException while connecting to " + hostname + ":" + port + "/" + database, e);
            System.exit(1);
        }
        return null;
    }

    public void close() {
        try {
            if (db != null)
                db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
