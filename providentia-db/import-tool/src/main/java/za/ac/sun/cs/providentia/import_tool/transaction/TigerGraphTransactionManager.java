package za.ac.sun.cs.providentia.import_tool.transaction;

import ch.qos.logback.classic.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

public class TigerGraphTransactionManager implements TransactionManager {

    public static final boolean USERS_MODE = true;
    public static final boolean FRIENDS_MODE = false;
    private final URI uri;
    private final Logger LOG = (Logger) LoggerFactory.getLogger(TigerGraphTransactionManager.class);
    private final CloseableHttpClient httpClient;

    public TigerGraphTransactionManager(URI uri) {
        this.uri = uri;
        this.httpClient = HttpClients.createDefault();
    }

    @Override
    public void createTransaction(LinkedList<String> records, Class<?> selectedClass, boolean... optional) {
        boolean mode = false;
        if (optional.length > 0)
            mode = optional[0];
        try {
            for (String row : records) {
                Object obj = FileReaderWrapper.processJSON(row, selectedClass);
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
                }
            }
        } catch (Exception se) {
            //Handle errors for JDBC
            LOG.error("Error processing TigerGraph transaction.", se);
        }
    }

    /**
     * Inserts a business, city, state, and category with respective edges into the graph.
     *
     * @param obj business object.
     */
    @Override
    public void insertBusiness(Business obj) {
        URI insertUri = null;
        try {
            insertUri = new URIBuilder()
                    .setScheme("http")
                    .setPath(this.uri.toString() + "/query/MyGraph/insertBusiness")
                    .setParameter("businessId", obj.getBusinessId())
                    .setParameter("name", obj.getName())
                    .setParameter("address", obj.getAddress())
                    .setParameter("postalCode", obj.getPostalCode())
                    .setParameter("stars", String.valueOf(obj.getStars()))
                    .setParameter("reviewCount", String.valueOf(obj.getReviewCount()))
                    .setParameter("isOpen", String.valueOf(obj.isOpen()))
                    .setParameter("city", obj.getCity())
                    .setParameter("state", obj.getState())
                    .setParameter("lat", String.valueOf(obj.getLatitude()))
                    .setParameter("lon", String.valueOf(obj.getLongitude()))
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
        HttpGet httpGet = new HttpGet(insertUri);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    String json = IOUtils.toString(entity.getContent());
                    JSONObject payload = new JSONObject(json);
                    if (payload.getBoolean("error")) {
                        throw new IOException(payload.getString("message"));
                    }
                } catch (Exception e) {
                    LOG.warn("Error parsing JSON payload for the following request: " + httpGet.toString(), e);
                }
            }
        } catch (IOException e) {
            LOG.error("Error while sending the following request: " + httpGet.toString(), e);
            System.exit(1);
        } finally {
            if (response != null)
                try {
                    response.close();
                } catch (Exception ignored) {
                }
        }
        // Add all the categories
        for (String category : obj.getCategories()) insertCategory(obj.getBusinessId(), category);
    }

    /**
     * Inserts an edge between a business and category and creates the category if it does not yet exist.
     *
     * @param businessId business to connect.
     * @param category   category to connect.
     */
    public void insertCategory(String businessId, String category) {
        URI insertUri = null;
        try {
            insertUri = new URIBuilder()
                    .setScheme("http")
                    .setPath(this.uri.toString() + "/query/MyGraph/insertCategory")
                    .setParameter("businessId", businessId)
                    .setParameter("category", category)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
        HttpGet httpGet = new HttpGet(insertUri);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    String json = IOUtils.toString(entity.getContent());
                    JSONObject payload = new JSONObject(json);
                    if (payload.getBoolean("error")) {
                        throw new IOException(payload.getString("message"));
                    }
                } catch (Exception e) {
                    LOG.warn("Error parsing JSON payload for the following request: " + httpGet.toString(), e);
                }
            }
        } catch (IOException e) {
            LOG.error("Error while sending the following request: " + httpGet.toString(), e);
            System.exit(1);
        } finally {
            if (response != null)
                try {
                    response.close();
                } catch (Exception ignored) {
                }
        }
    }

    /**
     * Adds a friends edge between two user vertices.
     *
     * @param hostUser   the user with friends to add.
     * @param friendUser the friend to add.
     */
    private void insertFriend(String hostUser, String friendUser) {
        URI insertUri = null;
        try {
            insertUri = new URIBuilder()
                    .setScheme("http")
                    .setPath(this.uri.toString() + "/query/MyGraph/insertFriendship")
                    .setParameter("user1", hostUser)
                    .setParameter("user2", friendUser)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
        HttpGet httpGet = new HttpGet(insertUri);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    String json = IOUtils.toString(entity.getContent());
                    JSONObject payload = new JSONObject(json);
                    if (payload.getBoolean("error")) {
                        throw new IOException(payload.getString("message"));
                    }
                } catch (Exception e) {
                    LOG.warn("Error parsing JSON payload for the following request: " + httpGet.toString(), e);
                }
            }
        } catch (IOException e) {
            LOG.error("Error while sending the following request: " + httpGet.toString(), e);
            System.exit(1);
        } finally {
            if (response != null)
                try {
                    response.close();
                } catch (Exception ignored) {
                }
        }
    }

    /**
     * Inserts all the friend edges of the given user to the graph granted the friends exist.
     *
     * @param obj user object.
     */
    private void insertUserFriends(User obj) {
        for (String friendId : obj.getFriends()) {
            insertFriend(obj.getUserId(), friendId);
        }
    }

    /**
     * Inserts a user into the graph.
     *
     * @param obj user object.
     */
    @Override
    public void insertUser(User obj) {
        URI insertUri = null;
        try {
            insertUri = new URIBuilder()
                    .setScheme("http")
                    .setPath(this.uri.toString() + "/query/MyGraph/insertUser")
                    .setParameter("userId", obj.getUserId())
                    .setParameter("name", obj.getName())
                    .setParameter("reviewCount", String.valueOf(obj.getReviewCount()))
                    .setParameter("yelpingSince", obj.getYelpingSince().toString())
                    .setParameter("cool", String.valueOf(obj.getCool()))
                    .setParameter("funny", String.valueOf(obj.getFunny()))
                    .setParameter("useful", String.valueOf(obj.getUseful()))
                    .setParameter("fans", String.valueOf(obj.getFans()))
                    .setParameter("averageStars", String.valueOf(obj.getAverageStars()))
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
        HttpGet httpGet = new HttpGet(insertUri);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    String json = IOUtils.toString(entity.getContent());
                    JSONObject payload = new JSONObject(json);
                    if (payload.getBoolean("error")) {
                        throw new IOException(payload.getString("message"));
                    }
                } catch (Exception e) {
                    LOG.warn("Error parsing JSON payload for the following request: " + httpGet.toString(), e);
                }
            }
        } catch (IOException e) {
            LOG.error("Error while sending the following request: " + httpGet.toString(), e);
            System.exit(1);
        } finally {
            if (response != null)
                try {
                    response.close();
                } catch (Exception ignored) {
                }
        }
    }


    /**
     * Inserts a review edge between business and user vertices.
     *
     * @param obj review object.
     */
    @Override
    public void insertReview(Review obj) {
        URI insertUri = null;
        try {
            insertUri = new URIBuilder()
                    .setScheme("http")
                    .setPath(this.uri.toString() + "/query/MyGraph/insertReview")
                    .setParameter("businessId", obj.getBusinessId())
                    .setParameter("userId", obj.getUserId())
                    .setParameter("stars", String.valueOf(obj.getStars()))
                    .setParameter("useful", String.valueOf(obj.getUseful()))
                    .setParameter("funny", String.valueOf(obj.getFunny()))
                    .setParameter("cool", String.valueOf(obj.getCool()))
                    .setParameter("text", obj.getText())
                    .setParameter("reviewDate", obj.getDate().toString())
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
        HttpGet httpGet = new HttpGet(insertUri);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    String json = IOUtils.toString(entity.getContent());
                    JSONObject payload = new JSONObject(json);
                    if (payload.getBoolean("error")) {
                        throw new IOException(payload.getString("message"));
                    }
                } catch (Exception e) {
                    LOG.warn("Error parsing JSON payload for the following request: " + httpGet.toString(), e);
                }
            }
        } catch (IOException e) {
            LOG.error("Error while sending the following request: " + httpGet.toString(), e);
            System.exit(1);
        } finally {
            if (response != null)
                try {
                    response.close();
                } catch (Exception ignored) {
                }
        }
    }

    @Override
    public String getDataDescriptorShort(Class<?> classType) {
        if (classType == Business.class)
            return "BUS";
        else if (classType == User.class)
            return "USR";
        else if (classType == Review.class)
            return "REV";
        else
            return "UNKNWN";
    }

    @Override
    public String getDataDescriptorLong(Class<?> classType) {
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
