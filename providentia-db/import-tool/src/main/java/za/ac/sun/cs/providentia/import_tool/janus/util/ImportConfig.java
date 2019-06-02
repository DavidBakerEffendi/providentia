package za.ac.sun.cs.providentia.import_tool.janus.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ImportConfig {

    private static Properties props = null;
    private static ImportConfig config;
    private double sectorSize;
    private int queueSize;
    private boolean loadSchema;
    private double percentageData;

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ImportConfig.class);

    private ImportConfig() {
        // Set logging level
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (props.getProperty("import.log.root") != null) {
            root.setLevel(Level.valueOf(props.getProperty("import.log.root")));
        } else {
            root.setLevel(DEFAULT_LOG_LEVEL);
        }

        // Size of queue of operations per transaction before committing
        try {
            this.sectorSize = Double.parseDouble(props.getProperty("import.sector-size"));
        } catch (Exception e) {
            this.sectorSize = DEFAULT_SECTOR_SIZE;
        }

        // Size of queue of operations per transaction before committing
        try {
            queueSize = Integer.parseInt(props.getProperty("import.queue-size"));
        } catch (Exception e) {
            queueSize = DEFAULT_QUEUE_LENGTH;
        }

        try {
            loadSchema = Boolean.parseBoolean(props.getProperty("import.load-schema"));
        } catch (Exception e) {
            loadSchema = DEFAULT_LOAD_SCHEMA;
        }

        try {
            percentageData = Double.parseDouble(props.getProperty("import.data.percentage"));
            if (percentageData > 1.0) throw new Exception("Percentage data setting invalid.");
        } catch (Exception e) {
            percentageData = DEFAULT_DATA_PERCENTAGE;
        }
    }

    /**
     * Set the properties file from which to retrieve properties from.
     *
     * @param propsFile the input stream of the properties file to read from.
     */
    public static void setProperties(InputStream propsFile) throws IOException {
        if (propsFile == null) {
            LOG.error("Properties file not found!");
            throw new IOException("Properties file not found!");
        }

        props = new Properties();
        props.load(propsFile);
    }

    /**
     * @return the singleton {@link ImportConfig} or null if properties aren't set.
     */
    public static ImportConfig getInstance() {
        if (props == null) {
            LOG.error("No properties set for " + ImportConfig.class + "!");
            System.exit(1);
        }
        if (config == null) {
            config = new ImportConfig();
        }
        return config;
    }

    public double getSectorSize() {
        if (config == null) return DEFAULT_SECTOR_SIZE;
        return this.sectorSize;
    }

    public long getQueueSize() {
        if (config == null) return DEFAULT_QUEUE_LENGTH;
        return this.queueSize;
    }

    public boolean loadSchema() {
        if (config == null) return DEFAULT_LOAD_SCHEMA;
        return this.loadSchema;
    }

    public double getPercentageData() {
        if (config == null) return DEFAULT_DATA_PERCENTAGE;
        return this.percentageData;
    }

    public File getYelpFile(TransactionManager.YELP fileType) {
        String dir = null;
        // Yelp Properties
        switch (fileType) {
            case BUSINESS:
                dir = props.getProperty("yelp.business");
                break;
            case USER:
                dir = props.getProperty("yelp.user");
                break;
            case REVIEW:
                dir = props.getProperty("yelp.review");
                break;
        }
        return new File(dir);
    }

    private static final int DEFAULT_QUEUE_LENGTH = 100;
    private static final double DEFAULT_SECTOR_SIZE = 0.3;
    private static final Level DEFAULT_LOG_LEVEL = Level.INFO;
    private static final boolean DEFAULT_LOAD_SCHEMA = false;
    private static final double DEFAULT_DATA_PERCENTAGE = 1.0;

}
