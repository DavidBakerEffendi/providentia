package za.ac.sun.cs.providentia.import_tool;

import ch.qos.logback.classic.Logger;
import me.tongfei.progressbar.ProgressBar;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.SimResponse;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.import_tool.config.ImportConfig;
import za.ac.sun.cs.providentia.import_tool.transaction.TransactionManager;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;

import java.io.File;
import java.util.LinkedList;

import static za.ac.sun.cs.providentia.import_tool.transaction.JanusTransactionManager.EDGE_MODE;
import static za.ac.sun.cs.providentia.import_tool.transaction.JanusTransactionManager.VERTEX_MODE;
import static za.ac.sun.cs.providentia.import_tool.transaction.PostgresTransactionManager.FRIENDS_MODE;
import static za.ac.sun.cs.providentia.import_tool.transaction.PostgresTransactionManager.USERS_MODE;

public class ImportTool {

    private final ImportConfig config;

    /**
     * Attempts to import Yelp data into database.
     */
    private void importYelpData() {
        ImportConfig.DataConfig dataConfig = config.dataConfig;
        if (dataConfig.importJanusGraph) {
            // JanusGraph import speed is enhanced if vertices are inserted before edges
            LOG.info("Importing Yelp data into JanusGraph.");
            // Adds all business, city, categories, and states
            insertDataClass(Business.class, DATABASE.JANUS_GRAPH, VERTEX_MODE);
            // Add all users
            insertDataClass(User.class, DATABASE.JANUS_GRAPH, VERTEX_MODE);
            // Add all edges IN_CITY, IN_STATE, IN_CATEGORY
            insertDataClass(Business.class, DATABASE.JANUS_GRAPH, EDGE_MODE);
            // Add all FRIENDS edge
            insertDataClass(User.class, DATABASE.JANUS_GRAPH, EDGE_MODE);
            // Adds REVIEWS edges between users who wrote the review and the businesses reviewed
            insertDataClass(Review.class, DATABASE.JANUS_GRAPH, EDGE_MODE);
        }
        if (dataConfig.importPostgres) {
            LOG.info("Importing Yelp data into PostgreSQL.");
            // Adds all business, city, categories, and states
            insertDataClass(Business.class, DATABASE.POSTGRESQL);
            // Adds all users. The varargs is my cheap way to determine adding friends or users.
            insertDataClass(User.class, DATABASE.POSTGRESQL, USERS_MODE);
            // Adds all user's friends
            insertDataClass(User.class, DATABASE.POSTGRESQL, FRIENDS_MODE);
            // Adds all reviews
            insertDataClass(Review.class, DATABASE.POSTGRESQL);
        }
    }

    /**
     * Attempts to import simulation data into database.
     */
    private void importSimData() {
        ImportConfig.DataConfig dataConfig = config.dataConfig;
        if (dataConfig.importJanusGraph) {
            // JanusGraph import speed is enhanced if vertices are inserted before edges
            LOG.info("Importing pre-hospital optimization simulation data into JanusGraph.");
            // Adds all simulation response vertices to JanusGraph
            insertDataClass(SimResponse.class, DATABASE.JANUS_GRAPH, VERTEX_MODE);
            // Adds all simulation response edges to JanusGraph
            insertDataClass(SimResponse.class, DATABASE.JANUS_GRAPH, EDGE_MODE);
        }
        if (dataConfig.importPostgres) {
            LOG.info("Importing pre-hospital optimization simulation data into PostgreSQL.");
            // Adds all simulation response data to PostgreSQL
            insertDataClass(SimResponse.class, DATABASE.POSTGRESQL);
        }
    }

    private final Logger LOG = (Logger) LoggerFactory.getLogger(ImportTool.class);

    public ImportTool() {
        // Pass properties into configuration object
        config = ImportConfig.getInstance();
        // Import Yelp data
        if (config.dataConfig.importYelp)
            importYelpData();
        if (config.dataConfig.importSim)
            importSimData();
        // Close connection to JanusGraph server
        LOG.info("Closing connections.");
        config.closeAll();
        System.exit(0);
    }

    private void insertDataClass(Class<?> classType, DATABASE db, boolean... optional) {
        File f = getDataFile(classType);
        TransactionManager tm;
        ImportConfig.DataConfig dataConfig = this.config.dataConfig;

        switch (db) {
            case JANUS_GRAPH:
                tm = this.config.janusGraphConfig.tm;
                break;
            case POSTGRESQL:
                tm = this.config.postgresConfig.tm;
                break;
            default:
                LOG.error("No valid database selected! Dataclass '" + classType + "' will not be inserted.");
                return;
        }

        if (tm == null) {
            LOG.error("Transaction manager for '" + db + "' is not initialized! Dataclass '" + classType + "' will not be inserted.");
            return;
        }

        if (f.exists()) {
            FileReaderWrapper reader = new FileReaderWrapper(f);
            reader.open();
            boolean completed = false;
            long totalTransactions = FileReaderWrapper.countLines(f);

            try (final ProgressBar pb = new ProgressBar(tm.getDataDescriptorShort(classType, optional), totalTransactions)) {
                String line = reader.readLine();
                int linesRead = 0;
                int sectorCount = 1;

                do {
                    // Create a batch of records
                    LinkedList<String> records = new LinkedList<>();
                    while (line != null && records.size() < dataConfig.queueSize && linesRead < pb.getMax()) {
                        records.add(line);
                        line = reader.readLine();
                        linesRead++;
                        pb.step();
                    }

                    // Insert records into DB
                    tm.createTransaction(records, classType, optional);

                    // Waiting for transaction count to catch up to lines read - prevent memory overflow and save
                    // space for cache
                    if (linesRead > sectorCount * pb.getMax() * dataConfig.sectorSize) {
                        blockReads(linesRead, pb);
                        sectorCount++;
                    }
                } while (linesRead < pb.getMax());
                LOG.info("All records from " + classType + " successfully read. Waiting to process "
                        + tm.getDataDescriptorLong(classType, optional) + ".");

                if (pb.getMax() == pb.getCurrent()) {
                    completed = true;
                }
            } finally {
                if (!reader.isClosed())
                    reader.close();
            }

            if (completed) {
                LOG.info(tm.getDataDescriptorLong(classType) + " data imported successfully!");
            } else {
                LOG.error(tm.getDataDescriptorLong(classType) + "  data could not be imported.");
            }
        } else {
            LOG.error("Could not import " + classType.toString() + " data as JSON file was not found at '"
                    + f.getAbsolutePath() + "'!");
        }
    }

    private enum DATABASE {JANUS_GRAPH, POSTGRESQL}

    private File getDataFile(Class<?> classType) {
        File f = null;
        if (classType == Business.class) {
            f = new File(config.dataConfig.businessDir);
        } else if (classType == User.class) {
            f = new File(config.dataConfig.userDir);
        } else if (classType == Review.class) {
            f = new File(config.dataConfig.reviewDir);
        } else if (classType == SimResponse.class) {
            f = new File(config.dataConfig.simDir);
        }
        return f;
    }

    /**
     * Blocks the reader from reading more lines until all current transactions are processed.
     *
     * @param linesRead number of lines read.
     * @param pb        the progress bar.
     */
    private void blockReads(long linesRead, ProgressBar pb) {
        long current;
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            current = pb.getCurrent();
        } while (current < linesRead);
    }

    public static void main(String[] args) {
        new ImportTool();
    }

}
