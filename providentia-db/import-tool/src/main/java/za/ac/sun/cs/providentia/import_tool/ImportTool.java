package za.ac.sun.cs.providentia.import_tool;

import ch.qos.logback.classic.Logger;
import me.tongfei.progressbar.ProgressBar;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.import_tool.janus.JanusTransactionManager;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;
import za.ac.sun.cs.providentia.import_tool.util.ImportConfig;

import java.io.File;
import java.util.LinkedList;

public class ImportTool {

    public enum YELP {BUSINESS, USER, REVIEW}

    private final ImportConfig config;

    private final Logger LOG = (Logger) LoggerFactory.getLogger(ImportTool.class);

    public ImportTool() {
        // Pass properties into configuration object
        config = ImportConfig.getInstance();
        // Import Yelp data
        importYelpData();
        // Close connection to JanusGraph server
        LOG.info("Closing connections.");
        closeConnections();
        System.exit(0);
    }

    /**
     * Closes the database connections if they are currently open.
     */
    private void closeConnections() {
        config.janusGraphConfig.close();
    }

    /**
     * Attempts to import_tool Yelp data into database.
     */
    private void importYelpData() {
        ImportConfig.DataConfig dataConfig = config.dataConfig;
        if (dataConfig.importJanusGraph) {
            LOG.info("Importing Yelp data into JanusGraph.");
            // Import business data: Vertex mode adds all business, city, attributes (with edge), categories, and state
            // vertices. Edge mode adds all edges IN_CITY, IN_STATE, IN_CATEGORY
            importJanusData(YELP.BUSINESS, JanusTransactionManager.INSERT_MODE.VERTEX);
            importJanusData(YELP.BUSINESS, JanusTransactionManager.INSERT_MODE.EDGE);
            // Import user data: Vertex mode creates all users, edge mode adds FRIENDS edge
            importJanusData(YELP.USER, JanusTransactionManager.INSERT_MODE.VERTEX);
            importJanusData(YELP.USER, JanusTransactionManager.INSERT_MODE.EDGE);
            // Import review data: Vertex mode adds review vertices and edge mode adds edges between users who wrote the
            // review and businesses the review reviews.
            importJanusData(YELP.REVIEW, JanusTransactionManager.INSERT_MODE.EDGE);
        }
        if (dataConfig.importPostgres) {
            LOG.info("Importing Yelp data into PostgreSQL.");
        }
        if (dataConfig.importCassandra) {
            LOG.info("Importing Yelp data into Cassandra.");
        }
    }

    /**
     * Imports the selected Yelp data based on the given data type.
     *
     * @param dataType   the Yelp data type indicating which file to import_tool.
     */
    private void importJanusData(YELP dataType, JanusTransactionManager.INSERT_MODE insertMode) {
        ImportConfig.JanusGraphConfig janusGraphConfig = config.janusGraphConfig;
        File f = null;
        switch (dataType) {
            case BUSINESS:
                f = new File(config.dataConfig.businessDir);
                break;
            case USER:
                f = new File(config.dataConfig.userDir);
                break;
            case REVIEW:
                f = new File(config.dataConfig.reviewDir);
                break;
        }
        if (f.exists()) {
            FileReaderWrapper reader = new FileReaderWrapper(f);
            reader.open();
            boolean completed = false;
            long totalTransactions = Math.round(FileReaderWrapper.countLines(f) * janusGraphConfig.percentageData);

            try (final ProgressBar pb = new ProgressBar(JanusTransactionManager.getDataDescriptorShort(dataType, insertMode), totalTransactions)) {
                // Read objects and insert them into graph
                String line = reader.readLine();
                long linesRead = 0L;
                int sectorCount = 1;
                do {
                    // Create a batch of records
                    LinkedList<String> records = new LinkedList<>();
                    while (line != null && records.size() < janusGraphConfig.queueSize && linesRead < pb.getMax()) {
                        records.add(line);
                        line = reader.readLine();
                        linesRead++;
                    }
                    // Process and insert batch into graph
                    janusGraphConfig.tm.insertRecords(records, dataType, pb, insertMode);

                    // Waiting for transaction count to catch up to lines read - prevent memory overflow and save
                    // space for cache
                    if (linesRead > sectorCount * pb.getMax() * janusGraphConfig.sectorSize) {
                        blockReads(linesRead, pb);
                        sectorCount++;
                    }
                } while (linesRead < pb.getMax());
                LOG.info("All records from " + dataType + " successfully read. Waiting for threads to process "
                        + JanusTransactionManager.getDataDescriptorLong(dataType, insertMode) + ".");

                if (pb.getMax() == pb.getCurrent()) {
                    completed = true;
                }
            } finally {
                if (!reader.isClosed())
                    reader.close();
            }

            if (completed) {
                LOG.info(JanusTransactionManager.getDataDescriptorLong(dataType, insertMode) + " data imported successfully!");
            } else {
                LOG.error(JanusTransactionManager.getDataDescriptorLong(dataType, insertMode) + "  data could not be imported.");
            }
        } else {
            LOG.error("Could not import " + dataType.toString() + " data as JSON file was not found at '"
                    + f.getAbsolutePath() + "'!");
        }
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
