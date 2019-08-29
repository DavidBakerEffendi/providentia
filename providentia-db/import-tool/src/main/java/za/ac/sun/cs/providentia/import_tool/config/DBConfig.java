package za.ac.sun.cs.providentia.import_tool.config;

import za.ac.sun.cs.providentia.import_tool.transaction.TransactionManager;

public interface DBConfig {

    TransactionManager tm = null;

    Object connect();

    void close();
}
