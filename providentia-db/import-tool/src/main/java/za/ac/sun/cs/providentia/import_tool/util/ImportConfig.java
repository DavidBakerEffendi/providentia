package za.ac.sun.cs.providentia.import_tool.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.import_tool.janus.JanusTransactionManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ImportConfig {

    private static ImportConfig config;

    public final DataConfig dataConfig;
    public final JanusGraphConfig janusGraphConfig;
    public final PostgresConfig postgresConfig;

    final String DATA_PROPERTIES = "data.properties";
    final String JANUS_GRAPH_PROPERTIES = "janus-graph.properties";
    final String POSTGRES_PROPERTIES = "postgres.properties";

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ImportConfig.class);

    private ImportConfig() {
        DataConfig tempDataConfig = null;
        JanusGraphConfig tempJanusGraphConfig = null;
        PostgresConfig tempPostgresConfig = null;
        // Get property files
        ClassLoader loader = this.getClass().getClassLoader();
        try {
            // Read properties
            tempDataConfig = new DataConfig(loader.getResourceAsStream(DATA_PROPERTIES));
            tempJanusGraphConfig = new JanusGraphConfig(loader.getResourceAsStream(JANUS_GRAPH_PROPERTIES));
            tempPostgresConfig = new PostgresConfig(loader.getResourceAsStream(POSTGRES_PROPERTIES));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Set final variables
        this.dataConfig = tempDataConfig;
        this.janusGraphConfig = tempJanusGraphConfig;
        this.postgresConfig = tempPostgresConfig;
    }

    /**
     * @return the singleton {@link ImportConfig} or null if properties aren't set.
     */
    public static ImportConfig getInstance() {
        if (config == null) {
            config = new ImportConfig();
        }
        return config;
    }

    public final class DataConfig {

        public final String businessDir;
        public final String userDir;
        public final String reviewDir;

        public final boolean importJanusGraph;
        public final boolean importPostgres;
        public final boolean importCassandra;

        DataConfig(InputStream propertyStream) throws IOException {
            if (propertyStream == null) {
                LOG.error("Properties file not found!");
                throw new IOException("Properties file not found!");
            }
            Properties props = new Properties();
            props.load(propertyStream);

            String tempBusinessDir;
            String tempUserDir;
            String tempReviewDir;

            boolean tempImportJanusGraph;
            boolean tempImportPostgres;
            boolean tempImportCassandra;

            try {
                tempBusinessDir = props.getProperty("yelp.business");
            } catch (Exception e) {
                tempBusinessDir = null;
            }
            try {
                tempUserDir = props.getProperty("yelp.user");
            } catch (Exception e) {
                tempUserDir = null;
            }
            try {
                tempReviewDir = props.getProperty("yelp.review");
            } catch (Exception e) {
                tempReviewDir = null;
            }

            try {
                tempImportJanusGraph = Boolean.parseBoolean(props.getProperty("database.janus-graph"));
            } catch (Exception e) {
                tempImportJanusGraph = false;
            }
            try {
                tempImportPostgres = Boolean.parseBoolean(props.getProperty("database.postgres"));
            } catch (Exception e) {
                tempImportPostgres = false;
            }
            try {
                tempImportCassandra = Boolean.parseBoolean(props.getProperty("database.cassandra"));
            } catch (Exception e) {
                tempImportCassandra = false;
            }

            this.businessDir = tempBusinessDir;
            this.userDir = tempUserDir;
            this.reviewDir = tempReviewDir;

            this.importJanusGraph = tempImportJanusGraph;
            this.importPostgres = tempImportPostgres;
            this.importCassandra = tempImportCassandra;
        }

    }

    public final class JanusGraphConfig {

        private final JanusGraph db;
        public final JanusTransactionManager tm;
        public final double sectorSize;
        public final int queueSize;
        public final boolean loadSchema;
        public final double percentageData;

        JanusGraphConfig(InputStream propertyStream) throws IOException {
            LOG.info("Loading JanusGraph config.");

            if (propertyStream == null) {
                LOG.error("Properties file not found!");
                throw new IOException("Properties file not found!");
            }
            Properties props = new Properties();
            props.load(propertyStream);

            // Set logging level
            Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.INFO);

            double tempSectorSize = 0.3;
            int tempQueueSize = 100;
            boolean tempLoadSchema = false;
            double tempPercentageData = 1.0;

            // Size of queue of operations per transaction before committing
            try {
                tempSectorSize = Double.parseDouble(props.getProperty("import.sector-size"));
            } catch (Exception ignored) {}
            // Size of queue of operations per transaction before committing
            try {
                tempQueueSize = Integer.parseInt(props.getProperty("import.queue-size"));
            } catch (Exception ignored) {}
            try {
                tempLoadSchema = Boolean.parseBoolean(props.getProperty("import.load-schema"));
            } catch (Exception ignored) {}
            try {
                tempPercentageData = Double.parseDouble(props.getProperty("import.data.percentage"));
                if (tempPercentageData > 1.0 || tempPercentageData < 0.0)
                    tempPercentageData = 1.0;
                    throw new Exception("Percentage data setting invalid. Using 1.0 default.");
            } catch (Exception ignored) {}

            this.sectorSize = tempSectorSize;
            this.queueSize = tempQueueSize;
            this.loadSchema = tempLoadSchema;
            this.percentageData = tempPercentageData;

            LOG.info("Connecting to JanusGraph server.");
            this.db = connect();
            this.tm = new JanusTransactionManager(this.db);
            // If user has set drop and load schema property, then drop and recreate the graph
            if (this.loadSchema) {
                // Load Yelp schema
                tm.loadSchema();
            }
        }

        /**
         * Note that if the graph doesn't already exist, Janus' behavior is to
         * create an empty graph.  Since this demo is assuming there is some
         * existing graph to connect to, it makes no effort to define schema
         * or any other such thing.
         */
        private JanusGraph connect() {
            ClassLoader classLoader = this.getClass().getClassLoader();
            try {
                Path temp = Files.createTempFile("resource-", ".ext");
                InputStream isProperties = classLoader.getResourceAsStream(JANUS_GRAPH_PROPERTIES);
                if (isProperties == null) {
                    throw new IOException("Could not find " + JANUS_GRAPH_PROPERTIES + "!");
                } else {
                    Files.copy(isProperties, temp, StandardCopyOption.REPLACE_EXISTING);
                    return JanusGraphFactory.open(temp.toFile().getAbsolutePath());
                }
            } catch (IOException e) {
                LOG.error("IOException while creating temporary copy of " + JANUS_GRAPH_PROPERTIES + " to give to JanusGraph" +
                        "database.", e);
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

    public class PostgresConfig {

        final Properties props;
        final Connection db;
        final String hostname;
        final String port;
        final String database;

        PostgresConfig(InputStream propertyStream) throws IOException {
            LOG.info("Loading JanusGraph config.");

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
            } catch (Exception ignored) { }
            try {
                tempPort = props.getProperty("port");
            } catch (Exception ignored) { }
            try {
                tempDatabase = props.getProperty("database");
            } catch (Exception ignored) { }

            this.hostname = tempHostName;
            this.port = tempPort;
            this.database = tempDatabase;

            db = connect();
        }

        /**
         * Note that if the graph doesn't already exist, Janus' behavior is to
         * create an empty graph.  Since this demo is assuming there is some
         * existing graph to connect to, it makes no effort to define schema
         * or any other such thing.
         */
        private Connection connect() {
            ClassLoader classLoader = this.getClass().getClassLoader();
            try {
                Path temp = Files.createTempFile("resource-", ".ext");
                InputStream isProperties = classLoader.getResourceAsStream(POSTGRES_PROPERTIES);
                if (isProperties == null) {
                    throw new IOException("Could not find " + POSTGRES_PROPERTIES + "!");
                } else {
                    Files.copy(isProperties, temp, StandardCopyOption.REPLACE_EXISTING);

                    String url = "jdbc:postgresql://" + hostname + ":" + port + "/" + database;

                    return DriverManager.getConnection(url, props);
                }
            } catch (IOException e) {
                LOG.error("IOException while creating temporary copy of " + POSTGRES_PROPERTIES + " to give to PostgreSQL" +
                        "database.", e);
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

}
