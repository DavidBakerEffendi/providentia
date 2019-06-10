package za.ac.sun.cs.providentia.import_tool.transaction;

import ch.qos.logback.classic.Logger;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;

import java.util.LinkedList;

public class CassandraTransactionManager {

    private final Session session;
    private final Gson gson = new GsonBuilder().create();

    private final Logger LOG = (Logger) LoggerFactory.getLogger(CassandraTransactionManager.class);

    /**
     * Creates an instance of {@link CassandraTransactionManager}.
     */
    public CassandraTransactionManager(Session db) {
        this.session = db;
    }

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
     * Inserts the link between a business and category into the database.
     *
     * @param businessId   the id of the business to link.
     * @param categoryName the name of the category which this business falls under.
     */
    private void insertBusByCat(String businessId, String categoryName) {
        StringBuilder cql = new StringBuilder();
        cql.append("SELECT COUNT(*) FROM yelp.busByCat WHERE ");
        cql.append("category = $$").append(categoryName).append("$$ AND ");
        cql.append("business_id = $$").append(businessId).append("$$ LIMIT 1");
        ResultSet rs = session.execute(cql.toString());
        if (rs.one() == null) {
            return;
        }
        cql = new StringBuilder();
        cql.append("INSERT INTO yelp.busByCat (category, business_id) ");
        cql.append("VALUES (");
        cql.append("$$").append(categoryName).append("$$");
        cql.append(", $$").append(businessId).append("$$");
        cql.append(")");

        session.execute(cql.toString());
    }

    /**
     * Inserts all the data linked to this business into the database.
     *
     * @param b the business POJO containing the data to insert.
     */
    private void insertBusiness(Business b) {
        StringBuilder cql = new StringBuilder();
        cql.append("SELECT COUNT(*) FROM yelp.business WHERE ");
        cql.append("id = $$").append(b.getBusinessId()).append("$$ LIMIT 1");
        ResultSet rs = session.execute(cql.toString());
        if (rs.one() == null) {
            return;
        }
        cql = new StringBuilder();
        cql.append("INSERT INTO yelp.business ");
        cql.append("(id, name, address, city, state, postal_code, latitude, longitude, stars, review_count, is_open) ");
        cql.append("VALUES (");
        cql.append("$$").append(b.getBusinessId()).append("$$");
        cql.append(", $$").append(b.getName()).append("$$");
        cql.append(", $$").append(b.getAddress()).append("$$");
        cql.append(", $$").append(b.getCity()).append("$$");
        cql.append(", $$").append(b.getState()).append("$$");
        cql.append(", $$").append(b.getPostalCode()).append("$$");
        cql.append(", ").append(b.getLatitude());
        cql.append(", ").append(b.getLongitude());
        cql.append(", ").append(b.getStars());
        cql.append(", ").append(b.getReviewCount());
        cql.append(", ").append(b.isOpen());
        cql.append(")");

        session.execute(cql.toString());

        for (String category : b.getCategories())
            insertBusByCat(b.getBusinessId(), category);
    }

    /**
     * Inserts all the data linked to this user into the database.
     *
     * @param u the user POJO containing the data to insert.
     */
    private void insertUser(User u) {
        StringBuilder cql = new StringBuilder();
        cql.append("SELECT COUNT(*) FROM yelp.users WHERE ");
        cql.append("id = $$").append(u.getUserId()).append("$$ LIMIT 1");
        ResultSet rs = session.execute(cql.toString());
        if (rs.one() == null) {
            return;
        }
        cql = new StringBuilder();
        cql.append("INSERT INTO yelp.users ");
        cql.append("(id, name, review_count, yelping_since, useful, funny, cool, fans, average_stars, friends) ");
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
        cql.append(", ").append(gson.toJson(u.getFriends()).replaceAll("\"", "'"));
        cql.append(")");

        session.execute(cql.toString());
    }

    /**
     * Inserts all the data linked to this review into the database.
     *
     * @param r the review POJO containing the data to insert.
     */
    private void insertReview(Review r) {
        StringBuilder cql = new StringBuilder();
        cql.append("SELECT COUNT(*) FROM yelp.review WHERE ");
        cql.append("id = $$").append(r.getReviewId()).append("$$ AND ");
        cql.append("user_id = $$").append(r.getUserId()).append("$$ AND ");
        cql.append("business_id = $$").append(r.getBusinessId()).append("$$ LIMIT 1");
        ResultSet rs = session.execute(cql.toString());
        if (rs.one() == null) {
            return;
        }
        cql = new StringBuilder();
        cql.append("INSERT INTO yelp.review ");
        cql.append("(id, user_id, business_id, stars, useful, funny, cool, text, date) ");
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

}
