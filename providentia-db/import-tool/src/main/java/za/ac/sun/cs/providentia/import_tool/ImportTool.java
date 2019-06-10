package za.ac.sun.cs.providentia.import_tool;

import ch.qos.logback.classic.Logger;
import me.tongfei.progressbar.ProgressBar;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.import_tool.transaction.JanusTransactionManager;
import za.ac.sun.cs.providentia.import_tool.transaction.PostgresTransactionManager;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;
import za.ac.sun.cs.providentia.import_tool.util.ImportConfig;

import java.io.File;
import java.util.LinkedList;

public class ImportTool {

    private final ImportConfig config;

    private final Logger LOG = (Logger) LoggerFactory.getLogger(ImportTool.class);

    public ImportTool() {
        // Pass properties into configuration object
        config = ImportConfig.getInstance();
        // Import Yelp data
        importYelpData();
        // Close connection to JanusGraph server
        LOG.info("Closing connections.");
        config.closeAll();
        System.exit(0);
    }

    /**
     * Attempts to import_tool Yelp data into database.
     */
    private void importYelpData() {
        ImportConfig.DataConfig dataConfig = config.dataConfig;
        if (dataConfig.importJanusGraph) {
            LOG.info("Importing Yelp data into JanusGraph.");
            // Adds all business, city, categories, and states
            importJanusData(Business.class, JanusTransactionManager.INSERT_MODE.VERTEX);
            // Add all edges IN_CITY, IN_STATE, IN_CATEGORY
            importJanusData(Business.class, JanusTransactionManager.INSERT_MODE.EDGE);
            // Add all users
            importJanusData(User.class, JanusTransactionManager.INSERT_MODE.VERTEX);
            // Add all FRIENDS edge
            importJanusData(User.class, JanusTransactionManager.INSERT_MODE.EDGE);
            // Adds REVIEWS edges between users who wrote the review and the businesses reviewed
            importJanusData(Review.class, JanusTransactionManager.INSERT_MODE.EDGE);
        }
        if (dataConfig.importPostgres) {
            LOG.info("Importing Yelp data into PostgreSQL.");
            // Adds all business, city, categories, and states
            importPostgresData(Business.class);
            // Adds all users. The varargs is my cheap way to determine adding friends or users.
            importPostgresData(User.class, true);
            // Adds all user's friends
            importPostgresData(User.class, false);
            // Adds all reviews
            importPostgresData(Review.class);
        }
        if (dataConfig.importCassandra) {
            LOG.info("Importing Yelp data into Cassandra.");
            // Adds all business, city, categories, and states
            importCassandraData(Business.class);
            // Adds all users. The varargs is my cheap way to determine adding friends or users.
            importCassandraData(User.class);
            // Adds all reviews
            importCassandraData(Review.class);
        }
    }

    private void importCassandraData(Class<?> classType) {
        ImportConfig.CassandraConfig cassandraConfig = config.cassandraConfig;
        File f = getYelpFile(classType);

        if (f.exists()) {
            FileReaderWrapper reader = new FileReaderWrapper(f);
            reader.open();
            boolean completed = false;
            long totalTransactions = Math.round(FileReaderWrapper.countLines(f) * cassandraConfig.percentageData);

            try (final ProgressBar pb = new ProgressBar(PostgresTransactionManager.getDataDescriptorShort(classType), totalTransactions)) {
                String line = reader.readLine();
                int linesRead = 0;
                int sectorCount = 1;

                do {
                    // Create a batch of records
                    LinkedList<String> records = new LinkedList<>();
                    while (line != null && records.size() < cassandraConfig.queueSize && linesRead < pb.getMax()) {
                        records.add(line);
                        line = reader.readLine();
                        linesRead++;
                        pb.step();
                    }

                    // Insert records into DB
                    cassandraConfig.tm.createTransaction(records, classType);

                    // Waiting for transaction count to catch up to lines read - prevent memory overflow and save
                    // space for cache
                    if (linesRead > sectorCount * pb.getMax() * cassandraConfig.sectorSize) {
                        blockReads(linesRead, pb);
                        sectorCount++;
                    }
                } while (linesRead < pb.getMax());
                LOG.info("All records from " + classType + " successfully read. Waiting to process "
                        + PostgresTransactionManager.getDataDescriptorLong(classType) + ".");

                if (pb.getMax() == pb.getCurrent()) {
                    completed = true;
                }
            } finally {
                if (!reader.isClosed())
                    reader.close();
            }

            if (completed) {
                LOG.info(PostgresTransactionManager.getDataDescriptorLong(classType) + " data imported successfully!");
            } else {
                LOG.error(PostgresTransactionManager.getDataDescriptorLong(classType) + "  data could not be imported.");
            }
        } else {
            LOG.error("Could not import " + classType.toString() + " data as JSON file was not found at '"
                    + f.getAbsolutePath() + "'!");
        }
    }

    private void importPostgresData(Class<?> classType, boolean... optional) {
        ImportConfig.PostgresConfig postgresConfig = config.postgresConfig;
        File f = getYelpFile(classType);

        if (f.exists()) {
            FileReaderWrapper reader = new FileReaderWrapper(f);
            reader.open();
            boolean completed = false;
            long totalTransactions = Math.round(FileReaderWrapper.countLines(f) * postgresConfig.percentageData);

            try (final ProgressBar pb = new ProgressBar(PostgresTransactionManager.getDataDescriptorShort(classType), totalTransactions)) {
                String line = reader.readLine();
                int linesRead = 0;
                int sectorCount = 1;

                do {
                    // Create a batch of records
                    LinkedList<String> records = new LinkedList<>();
                    while (line != null && records.size() < postgresConfig.queueSize && linesRead < pb.getMax()) {
                        records.add(line);
                        line = reader.readLine();
                        linesRead++;
                        pb.step();
                    }

                    // Insert records into DB
                    postgresConfig.tm.createTransaction(records, classType, optional);

                    // Waiting for transaction count to catch up to lines read - prevent memory overflow and save
                    // space for cache
                    if (linesRead > sectorCount * pb.getMax() * postgresConfig.sectorSize) {
                        blockReads(linesRead, pb);
                        sectorCount++;
                    }
                } while (linesRead < pb.getMax());
                LOG.info("All records from " + classType + " successfully read. Waiting to process "
                        + PostgresTransactionManager.getDataDescriptorLong(classType) + ".");

                if (pb.getMax() == pb.getCurrent()) {
                    completed = true;
                }
            } finally {
                if (!reader.isClosed())
                    reader.close();
            }

            if (completed) {
                LOG.info(PostgresTransactionManager.getDataDescriptorLong(classType) + " data imported successfully!");
            } else {
                LOG.error(PostgresTransactionManager.getDataDescriptorLong(classType) + "  data could not be imported.");
            }
        } else {
            LOG.error("Could not import " + classType.toString() + " data as JSON file was not found at '"
                    + f.getAbsolutePath() + "'!");
        }
    }

    /**
     * Imports the selected Yelp data based on the given data type.
     *
     * @param classType the Yelp data type indicating which file to import_tool.
     */
    private void importJanusData(Class<?> classType, JanusTransactionManager.INSERT_MODE insertMode) {
        ImportConfig.JanusGraphConfig janusGraphConfig = config.janusGraphConfig;
        File f = getYelpFile(classType);

        if (f.exists()) {
            FileReaderWrapper reader = new FileReaderWrapper(f);
            reader.open();
            boolean completed = false;
            long totalTransactions = Math.round(FileReaderWrapper.countLines(f) * janusGraphConfig.percentageData);

            try (final ProgressBar pb = new ProgressBar(JanusTransactionManager.getDataDescriptorShort(classType, insertMode), totalTransactions)) {
                // Read objects and insert them into graph
                String line = reader.readLine();
                int linesRead = 0;
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
                    janusGraphConfig.tm.insertRecords(records, classType, pb, insertMode);

                    // Waiting for transaction count to catch up to lines read - prevent memory overflow and save
                    // space for cache
                    if (linesRead > sectorCount * pb.getMax() * janusGraphConfig.sectorSize) {
                        blockReads(linesRead, pb);
                        sectorCount++;
                    }
                } while (linesRead < pb.getMax());
                LOG.info("All records from " + classType + " successfully read. Waiting for threads to process "
                        + JanusTransactionManager.getDataDescriptorLong(classType, insertMode) + ".");

                if (pb.getMax() == pb.getCurrent()) {
                    completed = true;
                }
            } finally {
                if (!reader.isClosed())
                    reader.close();
            }

            if (completed) {
                LOG.info(JanusTransactionManager.getDataDescriptorLong(classType, insertMode) + " data imported successfully!");
            } else {
                LOG.error(JanusTransactionManager.getDataDescriptorLong(classType, insertMode) + "  data could not be imported.");
            }
        } else {
            LOG.error("Could not import " + classType.toString() + " data as JSON file was not found at '"
                    + f.getAbsolutePath() + "'!");
        }
    }

    private File getYelpFile(Class<?> classType) {
        File f = null;
        if (classType == Business.class) {
            f = new File(config.dataConfig.businessDir);
        } else if (classType == User.class) {
            f = new File(config.dataConfig.userDir);
        } else if (classType == Review.class) {
            f = new File(config.dataConfig.reviewDir);
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
