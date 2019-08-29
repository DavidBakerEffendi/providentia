package za.ac.sun.cs.providentia.import_tool.config;

import ch.qos.logback.classic.Logger;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.import_tool.transaction.CassandraTransactionManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class CassandraConfig implements DBConfig {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(CassandraConfig.class);
    public final CassandraTransactionManager tm;
    private final String PROPERTIES = "cassandra.properties";
    private final RestClient es;
    private final String indexAddress;
    private final int indexPort;
    private final double sectorSize;
    private final int queueSize;
    private final double percentageData;

    CassandraConfig() throws IOException {
        LOG.info("Loading Cassandra config.");
        // Get property files
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream propertyStream = loader.getResourceAsStream(PROPERTIES);
        if (propertyStream == null) {
            LOG.error("Properties file not found!");
            throw new IOException("Properties file not found!");
        }
        Properties props = new Properties();
        props.load(propertyStream);

        String tempIndexAddress = "localhost";
        int tempIndexPort = 9200;

        double tempSectorSize = 0.3;
        int tempQueueSize = 100;
        double tempPercentageData = 1.0;

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

        this.sectorSize = tempSectorSize;
        this.queueSize = tempQueueSize;
        this.percentageData = tempPercentageData;

        this.indexAddress = tempIndexAddress;
        this.indexPort = tempIndexPort;

        LOG.info("Connecting to Elassandra server.");
        this.es = connect();
        this.tm = new CassandraTransactionManager(es);
    }

    /**
     * Connects to Cassandra over sockets using cassandra.properties.
     */
    public RestClient connect() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            Path temp = Files.createTempFile("resource-", ".ext");
            InputStream isProperties = classLoader.getResourceAsStream(PROPERTIES);
            if (isProperties == null) {
                throw new IOException("Could not find " + PROPERTIES + "!");
            } else {
                Files.copy(isProperties, temp, StandardCopyOption.REPLACE_EXISTING);
                return RestClient.builder(new HttpHost(indexAddress, indexPort, "http")).build();
            }
        } catch (IOException e) {
            LOG.error("IOException while creating ES REST client '" + indexAddress + ":" + indexPort +
                    "' using properties file '" + PROPERTIES + "'.", e);
            System.exit(1);
        }
        return null;
    }

    public void close() {
        try {
            if (es != null) {
                es.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
