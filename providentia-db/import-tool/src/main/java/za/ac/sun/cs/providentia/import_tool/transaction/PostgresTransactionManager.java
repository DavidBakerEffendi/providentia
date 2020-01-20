package za.ac.sun.cs.providentia.import_tool.transaction;

import ch.qos.logback.classic.Logger;
import jline.internal.Log;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.SimResponse;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;

import java.sql.*;
import java.util.LinkedList;

public class PostgresTransactionManager implements TransactionManager {

    private final Connection conn;
    private Statement stmt;
    public static final boolean USERS_MODE = true;
    public static final boolean FRIENDS_MODE = false;

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
        boolean mode = false;
        if (optional.length > 0)
            mode = optional[0];
        try {
            stmt = conn.createStatement();
            for (String row : records) {
                Object obj;
                if (selectedClass != SimResponse.class) {
                    obj = FileReaderWrapper.processJSON(row, selectedClass);
                } else {
                    obj = FileReaderWrapper.processCSV(row, selectedClass);
                }
                if (obj == null) continue;
                if (selectedClass == Business.class) {
                    insertBusiness((Business) obj);
                } else if (selectedClass == User.class) {
                    if (mode == USERS_MODE)
                        insertUser((User) obj);
                    else if (mode == FRIENDS_MODE)
                        insertUserFriends((User) obj);
                } else if (selectedClass == Review.class) {
                    insertReview((Review) obj);
                } else if (selectedClass == SimResponse.class) {
                    insertSimResponse((SimResponse) obj);
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
     * @param businessId   the ID of the business to link.
     * @param categoryName the name of the category which this business falls under.
     */
    private void insertBusinessByCategory(String businessId, String categoryName) {
        // Check if category exists
        long categoryId = 0;
        try {
            String sql = "SELECT id FROM category WHERE category.name = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, categoryName);
            ResultSet rs = p.executeQuery();
            // If category exists, obtain ID, else create one and obtain ID.
            if (rs.first()) {
                categoryId = rs.getInt("id");
            } else {
                String insertQuery = "INSERT INTO category (name) VALUES (?)";
                try (
                        PreparedStatement statement = conn.prepareStatement(insertQuery,
                                Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, categoryName);
                    int affectedRows = statement.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException("Category creation failed, no rows affected.");
                    }
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            categoryId = generatedKeys.getLong(1);
                        } else {
                            throw new SQLException("Creating category failed, no ID obtained.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while querying category table for category '" + categoryName + "'", e);
        }
        try {
            String sql = "SELECT business_id FROM bus_2_cat WHERE business_id = ? AND category_id = ?";
            PreparedStatement p = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            p.setString(1, businessId);
            p.setObject(2, categoryId);
            ResultSet rs = p.executeQuery();
            // If the relationship exists, then return
            if (rs.first()) {
                return;
            }
        } catch (SQLException e) {
            LOG.error("Error selecting " + businessId + " from table 'bus_2_cat'.", e);
        }

        try {
            String sql = "INSERT INTO bus_2_cat (business_id, category_id) VALUES (?, ?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, businessId);
            p.setObject(2, categoryId);

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating a '" + businessId + "' link failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOG.error("Error inserting a " + businessId + " to " + categoryId + " link into table 'bus_2_cat'.", e);
        }
    }

    /**
     * Inserts all the data linked to this business into the database.
     *
     * @param b the business POJO containing the data to insert.
     */
    public void insertBusiness(Business b) {
        insertCity(b.getCity(), b.getState());

        try {
            String sql = "INSERT INTO business " +
                    "(id, name, address, city, postal_code, location, stars, is_open) " +
                    "VALUES (?, ?, ?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326), ?, ?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, b.getBusinessId());
            p.setString(2, b.getName());
            p.setString(3, b.getAddress());
            p.setObject(4, b.getCity());
            p.setString(5, b.getPostalCode());
            p.setDouble(6, b.getLongitude());
            p.setDouble(7, b.getLatitude());
            p.setDouble(8, b.getStars());
            p.setBoolean(9, b.isOpen());

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating '" + b.getName() + "' failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOG.error("Error creating '" + b.getName() + "' for table 'business'.", e);
            Log.error(e.toString());
            System.exit(1);
        }

        if (b.getCategories() != null) {
            for (String categoryName : b.getCategories()) {
                insertBusinessByCategory(b.getBusinessId(), categoryName);
            }
        }
    }

    /**
     * Inserts the link between a user and his friend.
     *
     * @param user   the user to link.
     * @param friend the user's friend to link.
     */
    private void insertFriend(String user, String friend) {
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
    public void insertUser(User u) {
        try {
            String sql = "INSERT INTO users (" +
                    "id, name, yelping_since, useful, funny, cool, fans) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, u.getUserId());
            p.setString(2, u.getName());
            p.setTimestamp(3, new Timestamp(u.getYelpingSince().toEpochMilli()));
            p.setInt(4, u.getUseful());
            p.setInt(5, u.getFunny());
            p.setInt(6, u.getCool());
            p.setInt(7, u.getFans());

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating '" + u.getName() + "' failed, no rows affected.");
            }

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating '" + u.getName() + "' failed, no ID obtained.");
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
    public void insertReview(Review r) {
        try {
            String sql = "INSERT INTO review (id, user_id, business_id, stars, useful, funny, cool, text, date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
        } catch (SQLException e) {
            LOG.error("Error creating '" + r.getReviewId() + "' for table 'review'.", e);
        }
    }

    @Override
    public void insertSimResponse(SimResponse obj) {
        try {
            String sql = "INSERT INTO response (" +
                    "id, origin, destination, t, time_to_ambulance_starts, on_scene_duration, time_at_hospital, " +
                    "travel_time_patient, resource_ready_time" +
                    ") VALUES (?,  ST_SetSRID(ST_MakePoint(?, ?), 4326),  ST_SetSRID(ST_MakePoint(?, ?), 4326), " +
                    "?, ?, ?, ?, ?, ?)" +
                    "ON CONFLICT (id) DO NOTHING";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, obj.getId());
            p.setDouble(2, obj.getLon());
            p.setDouble(3, obj.getLat());
            p.setDouble(4, obj.getLonDest());
            p.setDouble(5, obj.getLatDest());
            p.setInt(6, obj.getT());
            p.setFloat(7, obj.getTimeToAmbulanceStarts());
            p.setFloat(8, obj.getOnSceneDuration());
            p.setFloat(9, obj.getTimeAtHospital());
            p.setFloat(10, obj.getTravelTimePatient());
            p.setDouble(11, obj.getResourceReadyTime());

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating '" + obj.getId() + "' failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOG.error("Error creating '" + obj.getId() + "' for table 'response'.", e);
        }

        if (obj.isTransfer()) {
            try {
                String sql = "INSERT INTO transfer (" +
                        "response_id, travel_time_hospital" +
                        ") VALUES (?, ?)" +
                        "ON CONFLICT (response_id) DO NOTHING";
                PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                p.setInt(1, obj.getId());
                p.setDouble(2, obj.getTravelTimeHospital());

                if (p.executeUpdate() == 0) {
                    throw new SQLException("Creating '" + obj.getId() + "' failed, no rows affected.");
                }
            } catch (SQLException e) {
                LOG.error("Error creating '" + obj.getId() + "' for table 'transfer'.", e);
            }
        } else {
            try {
                String sql = "INSERT INTO on_scene (" +
                        "response_id, travel_time_station" +
                        ") VALUES (?, ?)" +
                        "ON CONFLICT (response_id) DO NOTHING";
                PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                p.setInt(1, obj.getId());
                p.setDouble(2, obj.getTravelTimeStation());

                if (p.executeUpdate() == 0) {
                    throw new SQLException("Creating '" + obj.getId() + "' failed, no rows affected.");
                }
            } catch (SQLException e) {
                LOG.error("Error creating '" + obj.getId() + "' for table 'transfer'.", e);
            }
        }

        try {
            String sql = "INSERT INTO resource (" +
                    "id, response_id" +
                    ") VALUES (?, ?)" +
                    "ON CONFLICT (response_id) DO NOTHING";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, obj.getResource());
            p.setDouble(2, obj.getId());

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating '" + obj.getId() + "' failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOG.error("Error creating '" + obj.getId() + "' for table 'resource'.", e);
        }

        try {
            String sql = "INSERT INTO priority (" +
                    "id, response_id, description" +
                    ") VALUES (?, ?, ?)" +
                    "ON CONFLICT (response_id) DO NOTHING";
            PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            p.setDouble(2, obj.getId());
            switch (obj.getPrio()) {
                case 1:
                    p.setInt(1, 1);
                    p.setString(3, "HIGH");
                    break;
                case 2:
                    p.setInt(1, 2);
                    p.setString(3, "MODERATE");
                    break;
                case 3:
                    p.setInt(1, 3);
                    p.setString(3, "LOW");
                    break;
            }

            if (p.executeUpdate() == 0) {
                throw new SQLException("Creating '" + obj.getId() + "' failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOG.error("Error creating '" + obj.getId() + "' for table 'resource'.", e);
        }
    }

    @Override
    public String getDataDescriptorShort(Class<?> classType, boolean... optional) {
        if (classType == Business.class)
            return "BUS";
        else if (classType == User.class)
            return "USR";
        else if (classType == Review.class)
            return "REV";
        else if (classType == SimResponse.class)
            return "SIM";
        else
            return "UNKNWN";
    }

    @Override
    public String getDataDescriptorLong(Class<?> classType, boolean... optional) {
        if (classType == Business.class)
            return "business, category, city and tables";
        else if (classType == User.class)
            return "user and friend tables";
        else if (classType == Review.class)
            return "review table";
        else if (classType == SimResponse.class)
            return "simulation tables";
        else
            return "unknown";
    }

}
