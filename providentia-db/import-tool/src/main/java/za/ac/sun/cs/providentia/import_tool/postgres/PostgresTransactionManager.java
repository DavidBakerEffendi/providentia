package za.ac.sun.cs.providentia.import_tool.postgres;

import za.ac.sun.cs.providentia.import_tool.ImportTool;

import java.sql.Connection;

public class PostgresTransactionManager {

    private final Connection db;

    /**
     * Creates an instance of {@link PostgresTransactionManager}.
     */
    public PostgresTransactionManager(Connection db) {
        this.db = db;
    }

    public static String getDataDescriptorShort(ImportTool.YELP dataType) {
        switch (dataType) {
            case BUSINESS:
                return "BUS";
            case USER:
                return "USR";
            case REVIEW:
                return "REV";
        }
        return "UNKNWN";
    }

    public static String getDataDescriptorLong(ImportTool.YELP dataType) {
        switch (dataType) {
            case BUSINESS:
                return "business, attribute, category, city, and state tables";
            case USER:
                return "user and friend tables";
            case REVIEW:
                return "review table";
        }
        return "unknown";
    }
}
