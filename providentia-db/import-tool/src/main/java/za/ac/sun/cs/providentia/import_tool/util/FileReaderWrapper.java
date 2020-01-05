package za.ac.sun.cs.providentia.import_tool.util;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.SimResponse;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.domain.deserializers.BusinessDeserializer;
import za.ac.sun.cs.providentia.domain.deserializers.PHOSimDeserializer;
import za.ac.sun.cs.providentia.domain.deserializers.ReviewDeserializer;
import za.ac.sun.cs.providentia.domain.deserializers.UserDeserializer;

import java.io.*;

public class FileReaderWrapper {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(FileReaderWrapper.class);

    private final File f;
    private FileReader fr;
    private BufferedReader br;
    private boolean closed;
    private static final ObjectMapper objectMapper;
    private static CSVParser csvParser;

    static {
        // Deserializer
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module
                .addDeserializer(Business.class, new BusinessDeserializer())
                .addDeserializer(User.class, new UserDeserializer())
                .addDeserializer(Review.class, new ReviewDeserializer());
        objectMapper.registerModule(module);
        csvParser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(true).build();
    }

    public FileReaderWrapper(File f) {
        this.f = f;
        this.closed = true;
    }

    /**
     * Reads a line from the currently opened file expecting data for the given type.
     *
     * @return True if read successfully, false if otherwise.
     */
    public String readLine() {
        String line;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return line;
    }

    /**
     * Counts the number of lines in the given file.
     *
     * @param f the file to check.
     * @return the number of lines in the file.
     */
    public static int countLines(File f) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(f));
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i = 0; i < 1024; ) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }
            return count == 0 ? 1 : count;
        } catch (IOException e) {
            return -1;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Deserializes a given line interpreted as being in JSON format to the given class.
     *
     * @param line     the JSON string.
     * @param selectedClass the class to deserialize to.
     */
    public static Object processJSON(String line, Class<?> selectedClass) {
        try {
            if (selectedClass == Business.class) {
                return objectMapper.readValue(line, Business.class);
            } else if (selectedClass == User.class) {
                return objectMapper.readValue(line, User.class);
            } else if (selectedClass == Review.class) {
                return objectMapper.readValue(line, Review.class);
            }
        } catch (IOException e) {
            LOG.error("Error processing JSON.", e);
        }
        return null;
    }

    public static Object processCSV(String line, Class<?> selectedClass) {
        try {
            String[] record = csvParser.parseLine(line);
            if (selectedClass == SimResponse.class) {
                return PHOSimDeserializer.deserialize(record);
            }
        } catch (Exception e) {
            LOG.error("Error processing CSV.", e);
        }
        return null;
    }

    /**
     * @return True if file reader is closed, false if otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Attempts to open the file into a {@link FileReader} and {@link BufferedReader} respectively.
     *
     * @return True if opened successfully, false if otherwise.
     */
    public boolean open() {
        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            closed = false;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Attempts to close the {@link FileReader} and {@link BufferedReader} respectively.
     *
     * @return True if closed successfully, false if otherwise.
     */
    public boolean close() {
        try {
            br.close();
            fr.close();
            closed = true;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
