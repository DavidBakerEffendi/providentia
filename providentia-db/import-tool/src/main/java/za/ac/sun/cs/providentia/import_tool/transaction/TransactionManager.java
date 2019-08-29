package za.ac.sun.cs.providentia.import_tool.transaction;

import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.User;

import java.util.LinkedList;

public interface TransactionManager {

    String PROPERTIES = "db.properties";

    void createTransaction(LinkedList<String> records, Class<?> selectedClass, boolean... optional);

    void insertBusiness(Business obj);

    void insertUser(User obj);

    void insertReview(Review obj);

    String getDataDescriptorShort(Class<?> classType);

    String getDataDescriptorLong(Class<?> classType);

}
