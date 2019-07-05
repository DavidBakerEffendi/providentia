package za.ac.sun.cs.providentia.import_tool.transaction;

import ch.qos.logback.classic.Logger;
import jline.internal.Log;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;

import java.sql.*;
import java.util.LinkedList;
import java.util.UUID;

public class PostgresTransactionManager {

    private final Connection conn;
    private Statement stmt;

    private final Logger LOG = (Logger) LoggerFactory.getLogger(PostgresTransactionManager.class);

    /**
     * Creates an instance of {@link PostgresTransactionManager}.
     */
    public PostgresTransactionManager(Connection db) {
        this.conn = db;
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            LOG.error("Failed to set autocommit to false for PostgreSQL connection.", e);
        }
    }

    public void createTransaction(LinkedList<String> records, Class<?> selectedClass, boolean... optional) {
        try {
            stmt = conn.createStatement();
            for (String row : records) {
                Object obj = FileReaderWrapper.processJSON(row, selectedClass);
                if (obj == null) continue;
                if (selectedClass == Business.class) {
                    insertBusiness((Business) obj);
                } else if (selectedClass == User.class) {
                    if (optional[0])
                        insertUser((User) obj);
                    else
                        insertUserFriends((User) obj);
                } else if (selectedClass == Review.class) {
                    insertReview((Review) obj);
                }
            }
            conn.commit();
        } catch (SQLException se) {
            //Handle errors for JDBC
            LOG.error("Error processing PostgreSQL transaction.", se);
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Inserts the city into the database. If the city already exists, return the id.
     *
     * @param cityName  the name of the state to insert.
     * @param stateName the name of the state which this city falls under.
     */
    private void insertCity(String cityName, String stateName) {
        try {
            String sql = "SELECT id FROM city WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, cityName);
            ResultSet rs = p.executeQuery();
            if (rs.first()) {
                return;
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + cityName + " from table 'city'.", e);
        }

        try {
            String sql = "INSERT INTO city (id, state) VALUES (?, ?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, cityName);
            p.setObject(2, stateName);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating '" + cityName + "' failed, no rows affected.");
            }

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating '" + cityName + "' failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOG.error("Error inserting " + cityName + " into table 'city'.", e);
        }
    }

    /**
     * Inserts the link between a business and category into the database.
     *
     * @param businessName the name of the business to link.
     * @param categoryName the name of the category which this business falls under.
     */
    private void insertBusinessByCategory(String businessName, String categoryName) {
        try {
            String sql = "SELECT business_id FROM bus_by_cat WHERE business_id = ? AND category = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, businessName);
            p.setObject(2, categoryName);
            ResultSet rs = p.executeQuery();
            if (rs.first()) {
                return;
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + businessName + " from table 'bus_by_cat'.", e);
        }

        try {
            String sql = "INSERT INTO bus_by_cat (business_id, category) VALUES (?, ?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, businessName);
            p.setObject(2, categoryName);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating a '" + businessName + "' link failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOG.error("Error inserting a " + businessName + " link into table 'bus_by_cat'.", e);
        }
    }

    /**
     * Inserts all the data linked to this business into the database.
     *
     * @param b the business POJO containing the data to insert.
     */
    private void insertBusiness(Business b) {
        insertCity(b.getCity(), b.getState());

        try {
            String sql = "SELECT id FROM business WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, b.getBusinessId());
            ResultSet rs = p.executeQuery();
            if (!rs.first()) {
                sql = "INSERT INTO business " +
                        "(id, name, address, city, postal_code, location, stars, review_count, is_open) " +
                        "VALUES (?, ?, ?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326), ?, ?, ?)";
                p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                p.setString(1, b.getBusinessId());
                p.setString(2, b.getName());
                p.setString(3, b.getAddress());
                p.setObject(4, b.getCity());
                p.setString(5, b.getPostalCode());
                p.setDouble(6, b.getLatitude());
                p.setDouble(7, b.getLongitude());
                p.setDouble(8, b.getStars());
                p.setInt(9, b.getReviewCount());
                p.setBoolean(10, b.isOpen());

                if (p.executeUpdate() == 0) {
                    throw new SQLException("Creating '" + b.getName() + "' failed, no rows affected.");
                }
            }
        } catch (SQLException e) {
            LOG.error("Error creating '" + b.getName() + "' for table 'business'.", e);
            Log.error(e.toString());
            System.exit(1);
        }

        for (String categoryName : b.getCategories()) {
            insertBusinessByCategory(b.getBusinessId(), categoryName);
        }
    }

    /**
     * Inserts the link between a user and his friend.
     *
     * @param user   the user to link.
     * @param friend the user's friend to link.
     */
    private void insertFriend(String user, String friend) {
        // Does friend exist?
        try {
            String sql = "SELECT id FROM users WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setObject(1, friend);
            ResultSet rs = p.executeQuery();
            if (!rs.first()) {
                return;
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + user + " from table 'friends'.", e);
        }
        // Does link exist?
        try {
            String sql = "SELECT user_id FROM friends WHERE user_id = ? AND friend_id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, user);
            p.setObject(2, friend);
            ResultSet rs = p.executeQuery();
            if (rs.first()) {
                return;
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + user + " from table 'friends'.", e);
        }

        try {
            String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, user);
            p.setObject(2, friend);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating a '" + user + "' link failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOG.error("Error inserting a " + user + " link into table 'friends'.", e);
        }
    }

    /**
     * Inserts all the user's friends.
     *
     * @param u the user to insert for.
     */
    private void insertUserFriends(User u) {
        for (String friendId : u.getFriends()) {
            insertFriend(u.getUserId(), friendId);
        }
    }

    /**
     * Inserts all the data linked to this user into the database.
     *
     * @param u the user POJO containing the data to insert.
     */
    private void insertUser(User u) {
        try {
            String sql = "SELECT id FROM users WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, u.getUserId());
            ResultSet rs = p.executeQuery();
            if (!rs.first()) {
                sql = "INSERT INTO users (" +
                        "id, name, review_count, yelping_since, useful, funny, cool, fans, average_stars) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                p.setString(1, u.getUserId());
                p.setString(2, u.getName());
                p.setInt(3, u.getReviewCount());
                p.setTimestamp(4, new Timestamp(u.getYelpingSince().toEpochMilli()));
                p.setInt(5, u.getUseful());
                p.setInt(6, u.getFunny());
                p.setInt(7, u.getCool());
                p.setInt(8, u.getFans());
                p.setDouble(9, u.getAverageStars());

                if (p.executeUpdate() == 0) {
                    throw new SQLException("Creating '" + u.getName() + "' failed, no rows affected.");
                }

                try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new SQLException("Creating '" + u.getName() + "' failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.error("Error creating '" + u.getName() + "' for table 'users'.", e);
            Log.error(e.toString());
            System.exit(1);
        }
    }

    /**
     * Inserts all the data linked to this review into the database.
     *
     * @param r the review POJO containing the data to insert.
     */
    private void insertReview(Review r) {
        // Does user exist?
        try {
            String sql = "SELECT id FROM users WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setObject(1, r.getUserId());
            ResultSet rs = p.executeQuery();
            if (!rs.first()) {
                return;
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + r.getUserId() + " from table 'friends'.", e);
        }
        // Does business exist?
        try {
            String sql = "SELECT id FROM business WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setObject(1, r.getBusinessId());
            ResultSet rs = p.executeQuery();
            if (!rs.first()) {
                return;
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + r.getBusinessId() + " from table 'business'.", e);
        }

        try {
            String sql = "SELECT id FROM review WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, r.getReviewId());
            ResultSet rs = p.executeQuery();
            if (!rs.first()) {
                sql = "INSERT INTO review (" +
                        "id, user_id, business_id, stars, useful, funny, cool, text, date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                p.setString(1, r.getReviewId());
                p.setString(2, r.getUserId());
                p.setString(3, r.getBusinessId());
                p.setDouble(4, r.getStars());
                p.setInt(5, r.getUseful());
                p.setInt(6, r.getFunny());
                p.setInt(7, r.getCool());
                p.setString(8, r.getText());
                p.setTimestamp(9, new Timestamp(r.getDate().toEpochMilli()));

                if (p.executeUpdate() == 0) {
                    throw new SQLException("Creating '" + r.getReviewId() + "' failed, no rows affected.");
                }
            }
        } catch (SQLException e) {
            LOG.error("Error creating '" + r.getReviewId() + "' for table 'review'.", e);
        }
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
            return "business, category, city and tables";
        else if (classType == User.class)
            return "user and friend tables";
        else if (classType == Review.class)
            return "review table";
        else
            return "unknown";
    }

}
