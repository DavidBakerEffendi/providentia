package za.ac.sun.cs.providentia.import_tool.config;

import ch.qos.logback.classic.Logger;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.import_tool.transaction.JanusTransactionManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public final class JanusGraphConfig implements DBConfig {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(JanusGraphConfig.class);
    public final JanusTransactionManager tm;
    private final String PROPERTIES = "janus-graph.properties";
    private final JanusGraph db;
    private final boolean loadSchema;

    JanusGraphConfig() throws IOException {
        LOG.info("Loading JanusGraph config.");
        // Get property files
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream propertyStream = loader.getResourceAsStream(PROPERTIES);
        if (propertyStream == null) {
            LOG.error("Properties file not found!");
            throw new IOException("Properties file not found!");
        }
        Properties props = new Properties();
        props.load(propertyStream);

        boolean tempLoadSchema = false;
        try {
            tempLoadSchema = Boolean.parseBoolean(props.getProperty("import.load-schema"));
        } catch (Exception ignored) {
        }
        this.loadSchema = tempLoadSchema;

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
    public JanusGraph connect() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            Path temp = Files.createTempFile("resource-", ".ext");
            InputStream isProperties = classLoader.getResourceAsStream(PROPERTIES);
            if (isProperties == null) {
                throw new IOException("Could not find " + PROPERTIES + "!");
            } else {
                Files.copy(isProperties, temp, StandardCopyOption.REPLACE_EXISTING);
                return JanusGraphFactory.open(temp.toFile().getAbsolutePath());
            }
        } catch (IOException e) {
            LOG.error("IOException while creating temporary copy of " + PROPERTIES + " to give to JanusGraph" +
                    "keyspace.", e);
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
