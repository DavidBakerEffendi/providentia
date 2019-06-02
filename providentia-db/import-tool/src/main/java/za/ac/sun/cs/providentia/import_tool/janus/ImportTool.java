package za.ac.sun.cs.providentia.import_tool.janus;

import ch.qos.logback.classic.Logger;
import me.tongfei.progressbar.ProgressBar;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.import_tool.janus.util.FileReaderWrapper;
import za.ac.sun.cs.providentia.import_tool.janus.util.ImportConfig;
import za.ac.sun.cs.providentia.import_tool.janus.util.TransactionManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

import static za.ac.sun.cs.providentia.import_tool.janus.util.TransactionManager.INSERT_MODE;
import static za.ac.sun.cs.providentia.import_tool.janus.util.TransactionManager.YELP;
import static za.ac.sun.cs.providentia.import_tool.janus.util.TransactionManager.YELP.*;

public class ImportTool {

    private final String PROPERTIES_FILE = "import-tool.properties";

    private final ImportConfig config;
    private final TransactionManager tm;

    private final Logger LOG = (Logger) LoggerFactory.getLogger(ImportTool.class);

    public ImportTool() {
        // Get property file
        InputStream propertiesStream = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        try {
            // Read properties
            ImportConfig.setProperties(propertiesStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Pass properties into configuration object
        config = ImportConfig.getInstance();
        tm = TransactionManager.getInstance();
        // Connect to JanusGraph server
        LOG.info("Connecting to JanusGraph server.");
        JanusGraph janusGraph = connectJanus();
        // Import Yelp data
        LOG.info("Importing Yelp data.");
        importYelpData(janusGraph);
        // Close connection to JanusGraph server
        LOG.info("Closing connection to JanusGraph server.");
        closeJanus(janusGraph);
        System.exit(0);
    }

    /**
     * Note that if the graph doesn't already exist, Janus' behavior is to
     * create an empty graph.  Since this demo is assuming there is some
     * existing graph to connect to, it makes no effort to define schema
     * or any other such thing.
     */
    private JanusGraph connectJanus() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            Path temp = Files.createTempFile("resource-", ".ext");
            InputStream isProperties = classLoader.getResourceAsStream(PROPERTIES_FILE);
            if (isProperties == null) {
                throw new IOException("Could not find " + PROPERTIES_FILE + "!");
            } else {
                Files.copy(isProperties, temp, StandardCopyOption.REPLACE_EXISTING);
                JanusGraph janusGraph = JanusGraphFactory.open(temp.toFile().getAbsolutePath());
                // If user has set drop and load schema property, then drop and recreate the graph
                if (config.loadSchema()) {
                    // Load Yelp schema
                    tm.loadSchema(janusGraph);
                }
                return janusGraph;
            }
        } catch (IOException e) {
            LOG.error("IOException while creating temporary copy of " + PROPERTIES_FILE + " to give to JanusGraph" +
                    "database.");
            System.exit(1);
        }
        return null;
    }

    /**
     * Closes the Janus graph if is it currently open.
     *
     * @param janusGraph The Janus graph to close.
     */
    private void closeJanus(JanusGraph janusGraph) {
        try {
            if (janusGraph != null)
                janusGraph.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to import_tool Yelp data into database.
     *
     * @param janusGraph the JanusGraph to import_tool the Yelp dataset into.
     */
    private void importYelpData(JanusGraph janusGraph) {
        Runtime.getRuntime().addShutdownHook(new Thread(TransactionManager::shutDownTransactionManager));
        // Import business data: Vertex mode adds all business, city, attributes (with edge), categories, and state
        // vertices. Edge mode adds all edges IN_CITY, IN_STATE, IN_CATEGORY
        importData(janusGraph, BUSINESS, TransactionManager.INSERT_MODE.VERTEX);
        importData(janusGraph, BUSINESS, TransactionManager.INSERT_MODE.EDGE1);
        importData(janusGraph, BUSINESS, TransactionManager.INSERT_MODE.EDGE2);
        importData(janusGraph, BUSINESS, TransactionManager.INSERT_MODE.EDGE3);
        // Import user data: Vertex mode creates all users, edge mode adds FRIENDS edge
        importData(janusGraph, USER, TransactionManager.INSERT_MODE.VERTEX);
        importData(janusGraph, USER, TransactionManager.INSERT_MODE.EDGE1);
        // Import review data: Vertex mode adds review vertices and edge mode adds edges between users who wrote the
        // review and businesses the review reviews.
        importData(janusGraph, REVIEW, TransactionManager.INSERT_MODE.EDGE1);
    }

    /**
     * Imports the selected Yelp data based on the given data type.
     *
     * @param janusGraph the Janus graph to import_tool data to.
     * @param dataType   the Yelp data type indicating which file to import_tool.
     */
    private void importData(JanusGraph janusGraph, YELP dataType, TransactionManager.INSERT_MODE insertMode) {
        // Initialize transaction manager. Business data is susceptible to ghost vertices and edges so we run it
        // serially for safety.
        if (dataType == BUSINESS) TransactionManager.reinitializeTransactionManager(1);
        else TransactionManager.reinitializeTransactionManager();

        File f = config.getYelpFile(dataType);
        if (f.exists()) {
            FileReaderWrapper reader = new FileReaderWrapper(f);
            reader.open();
            boolean completed = false;
            long totalTransactions = Math.round(FileReaderWrapper.countLines(f) * ImportConfig.getInstance().getPercentageData());

            try (final ProgressBar pb = new ProgressBar(getDataDescriptorShort(dataType, insertMode), totalTransactions)) {
                // Read objects and insert them into graph
                CountDownLatch cdl = new CountDownLatch((int) pb.getMax());
                String line = reader.readLine();
                long linesRead = 0L;
                int sectorCount = 1;
                do {
                    // Create a batch of records
                    LinkedList<String> records = new LinkedList<>();
                    while (line != null && records.size() < ImportConfig.getInstance().getQueueSize() && linesRead < pb.getMax()) {
                        records.add(line);
                        line = reader.readLine();
                        linesRead++;
                    }
                    // Process and insert batch into graph
                    tm.newTransaction(janusGraph, records, dataType, pb, cdl, insertMode);

                    // Waiting for transaction count to catch up to lines read - prevent memory overflow and save
                    // space for cache
                    if (linesRead > sectorCount * pb.getMax() * config.getSectorSize()) {
                        blockReads(linesRead, pb);
                        sectorCount++;
                    }
                } while (linesRead < pb.getMax());
                LOG.info("All records from " + dataType + " successfully read. Waiting for threads to process " + getDataDescriptorLong(dataType, insertMode) + ".");
                cdl.await();

                if (pb.getMax() == pb.getCurrent()) {
                    completed = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (!reader.isClosed())
                    reader.close();
            }

            if (completed) {
                LOG.info(getDataDescriptorLong(dataType, insertMode) + " data imported successfully!");
            } else {
                LOG.error(getDataDescriptorLong(dataType, insertMode) + "  data could not be imported.");
            }
        } else {
            LOG.error("Could not import_tool " + dataType.toString() + " data as JSON file was not found at '" + f.getAbsolutePath() + "'!");
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

    /**
     * Depending on data type and insert mode selected, a code identifying the operation is returned.
     *
     * @param dataType   the Yelp data being imported.
     * @param insertMode the insert mode.
     * @return a short code identifying data being imported.
     */
    private String getDataDescriptorShort(YELP dataType, INSERT_MODE insertMode) {
        switch (dataType) {
            case BUSINESS:
                switch (insertMode) {
                    case VERTEX:
                        return "BUS_VERT";
                    case EDGE1:
                        return "BUS2CAT";
                    case EDGE2:
                        return "BUS2CITY";
                    case EDGE3:
                        return "CITY2STATE";
                }
                break;
            case USER:
                switch (insertMode) {
                    case VERTEX:
                        return "USR_VERT";
                    case EDGE1:
                        return "USR2USR";
                }
                break;
            case REVIEW:
                return "USRRVW2BUS";
        }
        return "UNKNWN";
    }

    /**
     * Depending on data type and insert mode selected, a phrase identifying the operation is returned.
     *
     * @param dataType   the Yelp data being imported.
     * @param insertMode the insert mode.
     * @return a short code identifying data being imported.
     */
    private String getDataDescriptorLong(YELP dataType, INSERT_MODE insertMode) {
        switch (dataType) {
            case BUSINESS:
                switch (insertMode) {
                    case VERTEX:
                        return "business, attribute, category, city, and state vertices";
                    case EDGE1:
                        return "business to category edges";
                    case EDGE2:
                        return "business to city edges";
                    case EDGE3:
                        return "city to state edges";
                }
                break;
            case USER:
                switch (insertMode) {
                    case VERTEX:
                        return "user vertices";
                    case EDGE1:
                        return "user to user friend edges";
                }
                break;
            case REVIEW:
                return "user to business review edge";
        }
        return "unknown";
    }

    public static void main(String[] args) {
        new ImportTool();
    }

}
