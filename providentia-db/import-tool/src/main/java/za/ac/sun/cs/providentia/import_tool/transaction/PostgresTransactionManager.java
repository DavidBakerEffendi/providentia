package za.ac.sun.cs.providentia.import_tool.transaction;

import ch.qos.logback.classic.Logger;
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
     * Inserts the state into the database. If the state already exists, return the id.
     *
     * @param stateName the name of the state to insert.
     * @return the UUID associated with that state.
     */
    private UUID insertState(String stateName) {
        try {
            String sql = "SELECT id FROM state WHERE name = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, stateName);
            ResultSet rs = p.executeQuery();
            if (rs.first()) {
                return UUID.fromString(rs.getString(1));
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + stateName + " from table 'state'.", e);
        }

        try {
            String sql = "INSERT INTO state (name) VALUES (?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, stateName);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating '" + stateName + "' failed, no rows affected.");
            }

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return UUID.fromString(generatedKeys.getString(1));
                } else {
                    throw new SQLException("Creating '" + stateName + "' failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOG.error("Error inserting " + stateName + " into table 'state'.", e);
        }

        return null;
    }

    /**
     * Inserts the city into the database. If the city already exists, return the id.
     *
     * @param cityName the name of the state to insert.
     * @param stateId  the UUID of the state which this city falls under.
     * @return the UUID associated with that city.
     */
    private UUID insertCity(String cityName, UUID stateId) {
        try {
            String sql = "SELECT id FROM city WHERE name = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, cityName);
            ResultSet rs = p.executeQuery();
            if (rs.first()) {
                return UUID.fromString(rs.getString(1));
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + cityName + " from table 'city'.", e);
        }

        try {
            String sql = "INSERT INTO city (name, state_id) VALUES (?, ?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, cityName);
            p.setObject(2, stateId);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating '" + cityName + "' failed, no rows affected.");
            }

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return UUID.fromString(generatedKeys.getString(1));
                } else {
                    throw new SQLException("Creating '" + cityName + "' failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOG.error("Error inserting " + cityName + " into table 'city'.", e);
        }

        return null;
    }

    /**
     * Inserts the category into the database. If the state already exists, return the id.
     *
     * @param categoryName the name of the category to insert.
     * @return the UUID associated with that category.
     */
    private UUID insertCategory(String categoryName) {
        try {
            String sql = "SELECT id FROM category WHERE name = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, categoryName);
            ResultSet rs = p.executeQuery();
            if (rs.first()) {
                return UUID.fromString(rs.getString(1));
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + categoryName + " from table 'category'.", e);
        }

        try {
            String sql = "INSERT INTO category (name) VALUES (?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, categoryName);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating '" + categoryName + "' failed, no rows affected.");
            }

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return UUID.fromString(generatedKeys.getString(1));
                } else {
                    throw new SQLException("Creating '" + categoryName + "' failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOG.error("Error inserting " + categoryName + " into table 'category'.", e);
        }

        return null;
    }

    /**
     * Inserts the link between a business and category into the database.
     *
     * @param businessName the name of the business to link.
     * @param categoryId   the UUID of the category which this business falls under.
     */
    private void insertBus2Cat(String businessName, UUID categoryId) {
        try {
            String sql = "SELECT business_id FROM bus_cat WHERE business_id = ? AND category_id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, businessName);
            p.setObject(2, categoryId);
            ResultSet rs = p.executeQuery();
            if (rs.first()) {
                return;
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + businessName + " from table 'bus_cat'.", e);
        }

        try {
            String sql = "INSERT INTO bus_cat (business_id, category_id) VALUES (?, ?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, businessName);
            p.setObject(2, categoryId);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating a '" + businessName + "' link failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOG.error("Error inserting a " + businessName + " link into table 'bus_cat'.", e);
        }
    }

    /**
     * Inserts all the data linked to this business into the database.
     *
     * @param b the business POJO containing the data to insert.
     */
    private void insertBusiness(Business b) {
        UUID stateId = insertState(b.getState());
        UUID cityId = insertCity(b.getCity(), stateId);

        try {
            String sql = "SELECT id FROM business WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, b.getBusinessId());
            ResultSet rs = p.executeQuery();
            if (!rs.first()) {
                sql = "INSERT INTO business (" +
                        "id, name, address, city_id, state_id, postal_code, latitude, longitude, stars, review_count, " +
                        "is_open) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                p.setString(1, b.getBusinessId());
                p.setString(2, b.getName());
                p.setString(3, b.getAddress());
                p.setObject(4, cityId);
                p.setObject(5, stateId);
                p.setString(6, b.getPostalCode());
                p.setDouble(7, b.getLatitude());
                p.setDouble(8, b.getLongitude());
                p.setDouble(9, b.getStars());
                p.setInt(10, b.getReviewCount());
                p.setBoolean(11, b.isOpen());

                if (p.executeUpdate() == 0) {
                    throw new SQLException("Creating '" + b.getName() + "' failed, no rows affected.");
                }
            }
        } catch (SQLException e) {
            LOG.error("Error creating '" + b.getName() + "' for table 'business'.", e);
        }

        for (String categoryName : b.getCategories()) {
            insertBus2Cat(b.getBusinessId(), insertCategory(categoryName));
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
                p.setObject(4, u.getYelpingSince());
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
                p.setObject(9, r.getDate());

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
            return "business, attribute, category, city, and state tables";
        else if (classType == User.class)
            return "user and friend tables";
        else if (classType == Review.class)
            return "review table";
        else
            return "unknown";
    }

}
