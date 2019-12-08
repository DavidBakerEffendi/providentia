package za.ac.sun.cs.providentia.import_tool.transaction;

import ch.qos.logback.classic.Logger;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.SimResponse;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;

import java.io.IOException;
import java.util.LinkedList;

public class CassandraTransactionManager implements TransactionManager {

    private final RestClient client;

    private final Logger LOG = (Logger) LoggerFactory.getLogger(CassandraTransactionManager.class);

    /**
     * Creates an instance of {@link CassandraTransactionManager}.
     */
    public CassandraTransactionManager(RestClient client) {
        this.client = client;
    }

    /**
     * Creates an ElasticSearch transaction.
     *
     * @param records       records to insert.
     * @param selectedClass the selected class of records to insert.
     */
    @Override
    public void createTransaction(LinkedList<String> records, Class<?> selectedClass, boolean... optional) {
        try {
            for (String row : records) {
                Object obj = FileReaderWrapper.processJSON(row, selectedClass);
                if (obj == null) continue;
                if (selectedClass == Business.class) {
                    insertBusiness((Business) obj);
                } else if (selectedClass == User.class) {
                    insertUser((User) obj);
                } else if (selectedClass == Review.class) {
                    insertReview((Review) obj);
                }
            }
        } catch (Exception e) {
            //Handle errors for Cassandra
            LOG.error("Error processing Cassandra transaction.", e);
        }
    }

    /**
     * Inserts the given Business record into ElasticSearch via the API.
     *
     * @param obj Business POJO.
     */
    @Override
    public void insertBusiness(Business obj) {
        Request request = new Request(
                "POST",
                "/business-locations/business");
        request.setJsonEntity(obj.toEsString());
        try {
            // Optimistically post data to business index
            if (client == null) throw new NullPointerException();
            client.performRequest(request);
        } catch (IOException e) {
            LOG.error(obj.toEsString());
            if (e instanceof ResponseException) {
                // If status code not 2xx or 404
                e.printStackTrace();
            } else {
                // Other issue
                e.printStackTrace();
            }
            System.exit(1);
        } catch (NullPointerException e) {
            LOG.error(this.getClass() + " needs to be constructed using the RestClient constructor!");
            System.exit(1);
        }
    }

    /**
     * Inserts the given User record into ElasticSearch via the API.
     *
     * @param obj User POJO.
     */
    @Override
    public void insertUser(User obj) {
        Request request = new Request(
                "POST",
                "/user-reviews/users");
        request.setJsonEntity(obj.toEsString());
        try {
            // Optimistically post data to user index
            client.performRequest(request);
        } catch (IOException e) {
            LOG.error(obj.toEsString());
            if (e instanceof ResponseException) {
                // If status code not 2xx or 404
                e.printStackTrace();
            } else {
                // Other issue
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    /**
     * Inserts the given Review record into ElasticSearch via the API.
     *
     * @param obj Review POJO.
     */
    @Override
    public void insertReview(Review obj) {
        Request request = new Request(
                "POST",
                "/review-times/review");
        request.setJsonEntity(obj.toEsString());
        try {
            // Optimistically post data to review index
            client.performRequest(request);
        } catch (IOException e) {
            LOG.error(obj.toEsString());
            if (e instanceof ResponseException) {
                // If status code not 2xx or 404
                e.printStackTrace();
            } else {
                // Other issue
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    @Override
    public void insertSimResponse(SimResponse obj) {
        throw new UnsupportedOperationException();
    }

    public String getDataDescriptorShort(Class<?> classType, boolean... optional) {
        if (classType == Business.class)
            return "BUS";
        else if (classType == User.class)
            return "USR";
        else if (classType == Review.class)
            return "REV";
        else
            return "UNKNWN";
    }

    public String getDataDescriptorLong(Class<?> classType, boolean... optional) {
        if (classType == Business.class)
            return "business and category tables";
        else if (classType == User.class)
            return "user table";
        else if (classType == Review.class)
            return "review table";
        else
            return "unknown";
    }

}
