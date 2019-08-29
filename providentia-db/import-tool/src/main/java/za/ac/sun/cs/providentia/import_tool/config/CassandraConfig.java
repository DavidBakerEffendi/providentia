package za.ac.sun.cs.providentia.import_tool.config;

import ch.qos.logback.classic.Logger;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.import_tool.transaction.CassandraTransactionManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CassandraConfig implements DBConfig {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(CassandraConfig.class);
    public final CassandraTransactionManager tm;
    private final String PROPERTIES = "cassandra.properties";
    private final RestClient es;
    private final String indexAddress;
    private final int indexPort;

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

        // ElasticSearch connection details
        try {
            tempIndexAddress = props.getProperty("index.address");
        } catch (Exception ignored) {
        }
        try {
            tempIndexPort = Integer.parseInt(props.getProperty("index.port"));
        } catch (Exception ignored) {
        }

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
        try {
            return RestClient.builder(new HttpHost(indexAddress, indexPort, "http")).build();
        } catch (Exception e) {
            LOG.error("IOException while creating ES REST client '" + indexAddress + ":" + indexPort, e);
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
