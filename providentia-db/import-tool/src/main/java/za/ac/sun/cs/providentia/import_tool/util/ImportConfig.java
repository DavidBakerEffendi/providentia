package za.ac.sun.cs.providentia.import_tool.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.import_tool.transaction.CassandraTransactionManager;
import za.ac.sun.cs.providentia.import_tool.transaction.JanusTransactionManager;
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

public class ImportConfig {

    private static ImportConfig config;

    public final DataConfig dataConfig;
    public final JanusGraphConfig janusGraphConfig;
    public final PostgresConfig postgresConfig;
    public final CassandraConfig cassandraConfig;

    private final String DATA_PROPERTIES = "data.properties";
    private final String JANUS_GRAPH_PROPERTIES = "janus-graph.properties";
    private final String POSTGRES_PROPERTIES = "postgres.properties";
    private final String CASSANDRA_PROPERTIES = "cassandra.properties";

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ImportConfig.class);

    private ImportConfig() {
        DataConfig tempDataConfig = null;
        JanusGraphConfig tempJanusGraphConfig = null;
        PostgresConfig tempPostgresConfig = null;
        CassandraConfig tempCassandraConfig = null;
        // Get property files
        ClassLoader loader = this.getClass().getClassLoader();
        try {
            // Read properties
            tempDataConfig = new DataConfig(loader.getResourceAsStream(DATA_PROPERTIES));
            if (tempDataConfig.importJanusGraph)
                tempJanusGraphConfig = new JanusGraphConfig(loader.getResourceAsStream(JANUS_GRAPH_PROPERTIES));
            if (tempDataConfig.importPostgres)
                tempPostgresConfig = new PostgresConfig(loader.getResourceAsStream(POSTGRES_PROPERTIES));
            if (tempDataConfig.importCassandra)
                tempCassandraConfig = new CassandraConfig(loader.getResourceAsStream(CASSANDRA_PROPERTIES));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Set final variables
        this.dataConfig = tempDataConfig;
        this.janusGraphConfig = tempJanusGraphConfig;
        this.postgresConfig = tempPostgresConfig;
        this.cassandraConfig = tempCassandraConfig;
    }

    public void closeAll() {
        if (janusGraphConfig != null) janusGraphConfig.close();
        if (postgresConfig != null) postgresConfig.close();
        if (cassandraConfig != null) cassandraConfig.close();
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

            // Set logging level
            Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.INFO);

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
        final boolean loadSchema;
        public final double percentageData;

        JanusGraphConfig(InputStream propertyStream) throws IOException {
            LOG.info("Loading JanusGraph config.");

            if (propertyStream == null) {
                LOG.error("Properties file not found!");
                throw new IOException("Properties file not found!");
            }
            Properties props = new Properties();
            props.load(propertyStream);

            double tempSectorSize = 0.3;
            int tempQueueSize = 100;
            boolean tempLoadSchema = false;
            double tempPercentageData = 1.0;

            // Size of queue of operations per transaction before committing
            try {
                tempSectorSize = Double.parseDouble(props.getProperty("import.sector-size"));
            } catch (Exception ignored) {
            }
            // Size of queue of operations per transaction before committing
            try {
                tempQueueSize = Integer.parseInt(props.getProperty("import.queue-size"));
            } catch (Exception ignored) {
            }
            try {
                tempLoadSchema = Boolean.parseBoolean(props.getProperty("import.load-schema"));
            } catch (Exception ignored) {
            }
            try {
                tempPercentageData = Double.parseDouble(props.getProperty("import.data.percentage"));
                if (tempPercentageData > 1.0 || tempPercentageData < 0.0)
                    tempPercentageData = 1.0;
                throw new Exception("Percentage data setting invalid. Using 1.0 default.");
            } catch (Exception ignored) {
            }

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
                        "keyspace.", e);
                System.exit(1);
            }
            return null;
        }

        void close() {
            try {
                if (db != null)
                    db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class PostgresConfig {

        private final Properties props;
        private final Connection db;
        public final PostgresTransactionManager tm;
        private final String hostname;
        private final String port;
        private final String database;
        public final double sectorSize;
        public final int queueSize;
        public final double percentageData;

        PostgresConfig(InputStream propertyStream) throws IOException {
            LOG.info("Loading Postgres config.");

            if (propertyStream == null) {
                LOG.error("Properties file not found!");
                throw new IOException("Properties file not found!");
            }
            props = new Properties();
            props.load(propertyStream);

            String tempHostName = "localhost";
            String tempPort = "5432";
            String tempDatabase = "yelp";
            double tempSectorSize = 0.3;
            int tempQueueSize = 100;
            double tempPercentageData = 1.0;

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
            // Size of queue of operations per transaction before committing
            try {
                tempSectorSize = Double.parseDouble(props.getProperty("import.sector-size"));
            } catch (Exception ignored) {
            }
            // Size of queue of operations per transaction before committing
            try {
                tempQueueSize = Integer.parseInt(props.getProperty("import.queue-size"));
            } catch (Exception ignored) {
            }
            try {
                tempPercentageData = Double.parseDouble(props.getProperty("import.data.percentage"));
                if (tempPercentageData > 1.0 || tempPercentageData < 0.0)
                    tempPercentageData = 1.0;
                throw new Exception("Percentage data setting invalid. Using 1.0 default.");
            } catch (Exception ignored) {
            }

            this.sectorSize = tempSectorSize;
            this.queueSize = tempQueueSize;
            this.percentageData = tempPercentageData;

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
                        "keyspace.", e);
                System.exit(1);
            } catch (SQLException e) {
                LOG.error("SQLException while connecting to " + hostname + ":" + port + "/" + database, e);
                System.exit(1);
            }
            return null;
        }

        void close() {
            try {
                if (db != null)
                    db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class CassandraConfig {

        private final Properties props;
        private final Session db;
        private final RestClient es;
        public final CassandraTransactionManager tm;
        private final String storageAddress;
        private final int storagePort;
        private final String storageKeyspace;
        private final String indexAddress;
        private final int indexPort;
        private final boolean useES;
        public final double sectorSize;
        public final int queueSize;
        public final double percentageData;

        CassandraConfig(InputStream propertyStream) throws IOException {
            LOG.info("Loading Cassandra config.");

            if (propertyStream == null) {
                LOG.error("Properties file not found!");
                throw new IOException("Properties file not found!");
            }
            props = new Properties();
            props.load(propertyStream);

            boolean tempUsES = false;

            String tempStorageAddress = "localhost";
            int tempStoragePort = 9042;
            String tempStorageKeyspace = "yelp";

            String tempIndexAddress = "localhost";
            int tempIndexPort = 9200;

            double tempSectorSize = 0.3;
            int tempQueueSize = 100;
            double tempPercentageData = 1.0;

            // Check setting
            try {
                tempUsES = Boolean.parseBoolean(props.getProperty("import.elassandra"));
            } catch (Exception ignored) {
            }
            // Plain Cassandra connection details
            try {
                tempStorageAddress = props.getProperty("storage.address");
            } catch (Exception ignored) {
            }
            try {
                tempStoragePort = Integer.parseInt(props.getProperty("storage.port"));
            } catch (Exception ignored) {
            }
            try {
                tempStorageKeyspace = props.getProperty("storage.keyspace");
            } catch (Exception ignored) {
            }
            // ElasticSearch connection details
            try {
                tempIndexAddress = props.getProperty("index.address");
            } catch (Exception ignored) {
            }
            try {
                tempIndexPort = Integer.parseInt(props.getProperty("index.port"));
            } catch (Exception ignored) {
            }
            // Size of queue of operations per transaction before committing
            try {
                tempSectorSize = Double.parseDouble(props.getProperty("import.sector-size"));
            } catch (Exception ignored) {
            }
            // Size of queue of operations per transaction before committing
            try {
                tempQueueSize = Integer.parseInt(props.getProperty("import.queue-size"));
            } catch (Exception ignored) {
            }
            try {
                tempPercentageData = Double.parseDouble(props.getProperty("import.data.percentage"));
                if (tempPercentageData > 1.0 || tempPercentageData < 0.0)
                    tempPercentageData = 1.0;
                throw new Exception("Percentage data setting invalid. Using 1.0 default.");
            } catch (Exception ignored) {
            }

            this.useES = tempUsES;

            this.sectorSize = tempSectorSize;
            this.queueSize = tempQueueSize;
            this.percentageData = tempPercentageData;

            this.storageAddress = tempStorageAddress;
            this.storagePort = tempStoragePort;
            this.storageKeyspace = tempStorageKeyspace;

            this.indexAddress = tempIndexAddress;
            this.indexPort = tempIndexPort;

            if (!this.useES) {
                LOG.info("Connecting to Cassandra server.");
                this.db = connect();
                this.es = null;
                this.tm = new CassandraTransactionManager(db);
            } else {
                LOG.info("Connecting to Elassandra server.");
                this.db = null;
                this.es = RestClient.builder(
                        new HttpHost(indexAddress, indexPort, "http")).build();
                this.tm = new CassandraTransactionManager(es);
            }
        }

        /**
         * Connects to Cassandra over sockets using cassandra.properties.
         */
        private Session connect() {
            ClassLoader classLoader = this.getClass().getClassLoader();
            try {
                Path temp = Files.createTempFile("resource-", ".ext");
                InputStream isProperties = classLoader.getResourceAsStream(CASSANDRA_PROPERTIES);
                if (isProperties == null) {
                    throw new IOException("Could not find " + POSTGRES_PROPERTIES + "!");
                } else {
                    Files.copy(isProperties, temp, StandardCopyOption.REPLACE_EXISTING);
                    Cluster.Builder b = Cluster.builder().addContactPoint(storageAddress).withPort(storagePort);
                    return b.build().connect(storageKeyspace);
                }
            } catch (IOException e) {
                LOG.error("IOException while connecting to Cassandra cluster '" + storageAddress + ":" + storagePort +
                        "' using properties file '" + CASSANDRA_PROPERTIES + "'.", e);
                System.exit(1);
            }
            return null;
        }

        void close() {
            try {
//                if (db != null)
//                    db.close();
                if (es != null) {
                    es.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
