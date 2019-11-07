package za.ac.sun.cs.providentia.import_tool.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ImportConfig {

    private static ImportConfig config;

    public final DataConfig dataConfig;
    public final JanusGraphConfig janusGraphConfig;
    public final PostgresConfig postgresConfig;
    public final CassandraConfig cassandraConfig;

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ImportConfig.class);

    private ImportConfig() {
        DataConfig tempDataConfig = null;
        JanusGraphConfig tempJanusGraphConfig = null;
        PostgresConfig tempPostgresConfig = null;
        CassandraConfig tempCassandraConfig = null;
        try {
            // Read properties
            tempDataConfig = new DataConfig();
            if (tempDataConfig.importJanusGraph)
                tempJanusGraphConfig = new JanusGraphConfig();
            if (tempDataConfig.importPostgres)
                tempPostgresConfig = new PostgresConfig();
            if (tempDataConfig.importCassandra)
                tempCassandraConfig = new CassandraConfig();
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

    public static final class DataConfig {

        public final String businessDir;
        public final String userDir;
        public final String reviewDir;

        public final boolean importJanusGraph;
        public final boolean importPostgres;
        public final boolean importCassandra;
        public final double sectorSize;
        public final int queueSize;
        private final String PROPERTIES = "data.properties";

        DataConfig() throws IOException {
            // Get property files
            ClassLoader loader = this.getClass().getClassLoader();
            InputStream propertyStream = loader.getResourceAsStream(PROPERTIES);

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

            double tempSectorSize = 0.3;
            int tempQueueSize = 100;

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

            this.sectorSize = tempSectorSize;
            this.queueSize = tempQueueSize;

            this.businessDir = tempBusinessDir;
            this.userDir = tempUserDir;
            this.reviewDir = tempReviewDir;

            this.importJanusGraph = tempImportJanusGraph;
            this.importPostgres = tempImportPostgres;
            this.importCassandra = tempImportCassandra;
        }

    }

}
