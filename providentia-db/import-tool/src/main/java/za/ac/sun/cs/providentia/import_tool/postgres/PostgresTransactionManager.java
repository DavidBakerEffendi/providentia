package za.ac.sun.cs.providentia.import_tool.postgres;

import java.sql.Connection;

public class PostgresTransactionManager {

    private final Connection postgres;

    /**
     * Creates an instance of {@link PostgresTransactionManager}.
     */
    public PostgresTransactionManager(Connection postgres) {
        this.postgres = postgres;
    }

}
