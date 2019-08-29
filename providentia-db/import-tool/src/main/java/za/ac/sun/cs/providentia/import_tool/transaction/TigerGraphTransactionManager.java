package za.ac.sun.cs.providentia.import_tool.transaction;

import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.User;

import java.util.LinkedList;

public class TigerGraphTransactionManager implements TransactionManager {

    @Override
    public void createTransaction(LinkedList<String> records, Class<?> selectedClass, boolean... optional) {

    }

    @Override
    public void insertBusiness(Business obj) {

    }

    @Override
    public void insertUser(User obj) {

    }

    @Override
    public void insertReview(Review obj) {

    }

    @Override
    public String getDataDescriptorShort(Class<?> classType) {
        return null;
    }

    @Override
    public String getDataDescriptorLong(Class<?> classType) {
        return null;
    }
}
