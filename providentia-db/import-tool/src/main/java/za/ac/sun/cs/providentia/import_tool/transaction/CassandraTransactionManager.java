package za.ac.sun.cs.providentia.import_tool.transaction;

import ch.qos.logback.classic.Logger;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;

import java.io.IOException;
import java.util.LinkedList;

public class CassandraTransactionManager {

    private final Session session;
    private final RestClient client;

    private final Logger LOG = (Logger) LoggerFactory.getLogger(CassandraTransactionManager.class);

    /**
     * Creates an instance of {@link CassandraTransactionManager}.
     */
    public CassandraTransactionManager(RestClient client) {
        this.session = null;
        this.client = client;
    }

    /**
     * @deprecated Now using ElasticSearch instead of vanilla Cassandra.
     * Creates an instance of {@link CassandraTransactionManager}.
     */
    @Deprecated
    public CassandraTransactionManager(Session session) {
        this.session = session;
        this.client = null;
    }

    /**
     * Creates an ElasticSearch transaction.
     *
     * @param records       records to insert.
     * @param selectedClass the selected class of records to insert.
     */
    public void createESTransaction(LinkedList<String> records, Class<?> selectedClass) {
        try {
            for (String row : records) {
                Object obj = FileReaderWrapper.processJSON(row, selectedClass);
                if (obj == null) continue;
                if (selectedClass == Business.class) {
                    insertBusinessViaES((Business) obj);
                } else if (selectedClass == User.class) {
                    insertUserViaES((User) obj);
                } else if (selectedClass == Review.class) {
                    insertReviewViaES((Review) obj);
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
    private void insertBusinessViaES(Business obj) {
        Request request = new Request(
                "POST",
                "/business-locations/business");
        request.setJsonEntity(obj.toEsString());
        try {
            // Optimistically post data to business index
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
     * Inserts the given User record into ElasticSearch via the API.
     *
     * @param obj User POJO.
     */
    private void insertUserViaES(User obj) {
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
    private void insertReviewViaES(Review obj) {
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

    @Deprecated
    public void createTransaction(LinkedList<String> records, Class<?> selectedClass) {
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
     * @param b the business POJO containing the data to insert.
     * @deprecated Now using ElasticSearch instead of vanilla Cassandra
     * Inserts all the data linked to this business into the database.
     */
    @Deprecated
    private void insertBusiness(Business b) {
        StringBuilder cql = new StringBuilder();
        cql.append("SELECT COUNT(*) FROM yelp.business WHERE ");
        cql.append("business_id = $$").append(b.getBusinessId()).append("$$ LIMIT 1");
        ResultSet rs = session.execute(cql.toString());
        if (rs.one() == null) {
            return;
        }
        cql = new StringBuilder();
        cql.append("INSERT INTO yelp.business ");
        cql.append("(business_id, name, address, categories, city, state, postal_code, location, stars, review_count, is_open) ");
        cql.append("VALUES (");
        cql.append("$$").append(b.getBusinessId()).append("$$");
        cql.append(", $$").append(b.getName()).append("$$");
        cql.append(", $$").append(b.getAddress()).append("$$");
        cql.append(", ").append(toCassandraSet(b.getCategories()));
        cql.append(", $$").append(b.getCity()).append("$$");
        cql.append(", $$").append(b.getState()).append("$$");
        cql.append(", $$").append(b.getPostalCode()).append("$$");
        cql.append(", ").append("{ \"lat\": ").append(b.getLatitude()).append(",  \"lon\": ").append(b.getLongitude()).append("}");
        cql.append(", ").append(b.getStars());
        cql.append(", ").append(b.getReviewCount());
        cql.append(", ").append(b.isOpen());
        cql.append(")");

        session.execute(cql.toString());
    }

    /**
     * @param u the user POJO containing the data to insert.
     * @deprecated Now using ElasticSearch instead of vanilla Cassandra
     * Inserts all the data linked to this user into the database.
     */
    @Deprecated
    private void insertUser(User u) {
        StringBuilder cql = new StringBuilder();
        cql.append("SELECT COUNT(*) FROM yelp.users WHERE ");
        cql.append("user_id = $$").append(u.getUserId()).append("$$ LIMIT 1");
        ResultSet rs = session.execute(cql.toString());
        if (rs.one() == null) {
            return;
        }
        cql = new StringBuilder();
        cql.append("INSERT INTO yelp.users ");
        cql.append("(user_id, name, review_count, yelping_since, useful, funny, cool, fans, average_stars, friends) ");
        cql.append("VALUES (");
        cql.append("$$").append(u.getUserId()).append("$$");
        cql.append(", $$").append(u.getName()).append("$$");
        cql.append(", ").append(u.getReviewCount());
        cql.append(", $$").append(u.getYelpingSince().toString()).append("$$");
        cql.append(", ").append(u.getUseful());
        cql.append(", ").append(u.getFunny());
        cql.append(", ").append(u.getCool());
        cql.append(", ").append(u.getFans());
        cql.append(", ").append(u.getAverageStars());
        cql.append(", ").append(toCassandraSet(u.getFriends()));
        cql.append(")");

        session.execute(cql.toString());
    }

    /**
     * @param a the array to convert.
     * @return the String representation of the array.
     * @deprecated Now using ElasticSearch instead of vanilla Cassandra
     * Converts an array to Cassandra set notation.
     */
    @Deprecated
    private String toCassandraSet(String[] a) {
        if (a.length == 0) return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append("{$$").append(a[0]).append("$$ ");
        for (int i = 1; i < a.length; i++)
            sb.append(", $$").append(a[i]).append("$$");
        sb.append("}");

        return sb.toString();
    }

    /**
     * @param r the review POJO containing the data to insert.
     * @deprecated Now using ElasticSearch instead of vanilla Cassandra
     * Inserts all the data linked to this review into the database.
     */
    @Deprecated
    private void insertReview(Review r) {
        StringBuilder cql = new StringBuilder();
        cql.append("SELECT COUNT(*) FROM yelp.review WHERE ");
        cql.append("review_id = $$").append(r.getReviewId()).append("$$ LIMIT 1");
        ResultSet rs = session.execute(cql.toString());
        if (rs.one() == null) {
            return;
        }
        cql = new StringBuilder();
        cql.append("INSERT INTO yelp.review ");
        cql.append("(review_id, user_id, business_id, stars, useful, funny, cool, text, date) ");
        cql.append("VALUES (");
        cql.append("$$").append(r.getReviewId()).append("$$");
        cql.append(", $$").append(r.getUserId()).append("$$");
        cql.append(", $$").append(r.getBusinessId()).append("$$");
        cql.append(", ").append(r.getStars());
        cql.append(", ").append(r.getUseful());
        cql.append(", ").append(r.getFunny());
        cql.append(", ").append(r.getCool());
        // Dollar signs mess with the query - since NLTK will ignore them anyway we can disregard them
        cql.append(", $$").append(r.getText().replaceAll("\\$", "_")).append("$$");
        cql.append(", $$").append(r.getDate().toString()).append("$$");
        cql.append(")");

        session.execute(cql.toString());
    }

    public static String getDataDescriptorShort(Class<?> classType) {
        if (classType == Business.class)
            return "BUS";
        else if (classType == User.class)
            return "USR";
        else if (classType == Review.class)
            return "REV";
        else
            return "UNKNWN";
    }

    public static String getDataDescriptorLong(Class<?> classType) {
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
