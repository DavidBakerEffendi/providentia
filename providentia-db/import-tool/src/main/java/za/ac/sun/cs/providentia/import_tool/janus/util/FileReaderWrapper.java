package za.ac.sun.cs.providentia.import_tool.janus.util;

import java.io.*;

public class FileReaderWrapper {

    private File f;
    private FileReader fr;
    private BufferedReader br;
    private boolean closed;

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
     * @return True if file readers closed, false if otherwise.
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
