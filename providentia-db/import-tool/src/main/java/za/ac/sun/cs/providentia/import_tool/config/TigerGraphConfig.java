package za.ac.sun.cs.providentia.import_tool.config;

import ch.qos.logback.classic.Logger;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.import_tool.transaction.TigerGraphTransactionManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

public class TigerGraphConfig implements DBConfig {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(TigerGraphConfig.class);
    public final TigerGraphTransactionManager tm;
    public final String hostAddress;
    public final int hostPort;
    private final String PROPERTIES = "tiger-graph.properties";

    TigerGraphConfig() throws IOException {
        LOG.info("Loading TigerGraph config.");
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
        int tempIndexPort = 9000;

        // TigerGraph connection details
        try {
            tempIndexAddress = props.getProperty("host.address");
        } catch (Exception ignored) {
        }
        try {
            tempIndexPort = Integer.parseInt(props.getProperty("host.port"));
        } catch (Exception ignored) {
        }

        this.hostAddress = tempIndexAddress;
        this.hostPort = tempIndexPort;

        LOG.info("Creating HTTP Rest Client for TigerGraph endpoints.");
        URI uri = connect();
        this.tm = new TigerGraphTransactionManager(uri);
    }

    @Override
    public URI connect() {
        try {
            return new URIBuilder()
                    .setHost(hostAddress)
                    .setPort(hostPort)
                    .build();
        } catch (Exception e) {
            LOG.error("IOException while creating Apache HTTP REST client '" + hostAddress + ":" + hostPort, e);
            System.exit(1);
        }
        return null;
    }

    @Override
    public void close() {
        // This class does not establish a connection so no close is needed here.
    }
}
