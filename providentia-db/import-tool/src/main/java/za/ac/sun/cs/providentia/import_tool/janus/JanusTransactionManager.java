package za.ac.sun.cs.providentia.import_tool.janus;

import ch.qos.logback.classic.Logger;
import me.tongfei.progressbar.ProgressBar;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.*;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.ConsistencyModifier;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.LoggerFactory;
import za.ac.sun.cs.providentia.domain.Business;
import za.ac.sun.cs.providentia.domain.Review;
import za.ac.sun.cs.providentia.domain.User;
import za.ac.sun.cs.providentia.import_tool.ImportTool.YELP;
import za.ac.sun.cs.providentia.import_tool.util.FileReaderWrapper;

import java.util.List;

/**
 * Manages transactions to the {@link JanusGraph}.
 */
public class JanusTransactionManager {

    public enum INSERT_MODE {
        VERTEX, EDGE
    }

    private final JanusGraph janusGraph;

    private final Logger LOG = (Logger) LoggerFactory.getLogger(JanusTransactionManager.class);

    /**
     * Creates an instance of {@link JanusTransactionManager}.
     */
    public JanusTransactionManager(JanusGraph janusGraph) {
        this.janusGraph = janusGraph;
    }

    /**
     * Loads the necessary vertex and edge labels representing the different vertex and edge types as well as sets indexes.
     */
    public void loadSchema() {
        JanusGraphManagement mgmt = janusGraph.openManagement();

        // === Create labels ===

        // Create vertex labels
        final VertexLabel businessLabel = mgmt.makeVertexLabel("Business").make();
        final VertexLabel categoryLabel = mgmt.makeVertexLabel("Category").make();
        final VertexLabel cityLabel = mgmt.makeVertexLabel("City").make();
        final VertexLabel stateLabel = mgmt.makeVertexLabel("State").make();
        final VertexLabel userLabel = mgmt.makeVertexLabel("User").make();
        // Create edge labels
        final EdgeLabel reviewLabel = mgmt.makeEdgeLabel("REVIEWS").make();
        mgmt.makeEdgeLabel("IN_CATEGORY").make();
        mgmt.makeEdgeLabel("IN_CITY").make();
        mgmt.makeEdgeLabel("IN_STATE").make();
        mgmt.makeEdgeLabel("FRIENDS").make();


        // === Create properties ===

        // Business properties
        mgmt.makePropertyKey("business_id").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("address").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("postal_code").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("is_open").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("open").dataType(Boolean.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("review_count").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("name").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("location").dataType(Geoshape.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("stars").dataType(Double.class).cardinality(Cardinality.SINGLE).make();

        // User properties
        mgmt.makePropertyKey("user_id").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("average_stars").dataType(Double.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("yelping_since").dataType(Long.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("cool").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("funny").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("useful").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("fans").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentHot").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentMore").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentProfile").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentCute").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentList").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentNote").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentPlain").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentCool").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentFunny").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentWriter").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("complimentPhotos").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();

        // Review properties
        mgmt.makePropertyKey("review_id").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("date").dataType(Long.class).cardinality(Cardinality.SET).make();
        mgmt.makePropertyKey("text").dataType(String.class).cardinality(Cardinality.SINGLE).make();

        // === Create indexes ===

        // --- ID based indexes ---
        // Create index for business ids
        PropertyKey busPK = mgmt.getPropertyKey("business_id");
        JanusGraphIndex byBusinessId = mgmt.buildIndex("byBusinessId", Vertex.class)
                .addKey(busPK)
                .indexOnly(businessLabel)
                .buildCompositeIndex();
        mgmt.setConsistency(byBusinessId, ConsistencyModifier.LOCK);
        // Create index for category ids
        PropertyKey catPK = mgmt.getPropertyKey("name");
        JanusGraphIndex byCatId = mgmt.buildIndex("byCatName", Vertex.class)
                .addKey(catPK)
                .indexOnly(categoryLabel)
                .buildCompositeIndex();
        mgmt.setConsistency(byCatId, ConsistencyModifier.LOCK);
        // Create index for city ids
        PropertyKey cityPK = mgmt.getPropertyKey("name");
        JanusGraphIndex byCityId = mgmt.buildIndex("byCityName", Vertex.class)
                .addKey(cityPK)
                .indexOnly(cityLabel)
                .buildCompositeIndex();
        mgmt.setConsistency(byCityId, ConsistencyModifier.LOCK);
        // Create index for state ids
        PropertyKey statePK = mgmt.getPropertyKey("name");
        JanusGraphIndex byStateId = mgmt.buildIndex("byStateName", Vertex.class)
                .addKey(statePK)
                .indexOnly(stateLabel)
                .buildCompositeIndex();
        mgmt.setConsistency(byStateId, ConsistencyModifier.LOCK);
        // Create index for user ids
        PropertyKey userPK = mgmt.getPropertyKey("user_id");
        JanusGraphIndex byUserId = mgmt.buildIndex("byUserId", Vertex.class)
                .addKey(userPK)
                .indexOnly(userLabel)
                .buildCompositeIndex();
        mgmt.setConsistency(byUserId, ConsistencyModifier.LOCK);
        // Create index for review ids
        PropertyKey reviewPK = mgmt.getPropertyKey("review_id");
        mgmt.buildEdgeIndex(
                reviewLabel,
                "byReviewId",
                Direction.BOTH,
                reviewPK);

        // --- Spatio-temporal indexes ---
        PropertyKey datePK = mgmt.getPropertyKey("date");
        PropertyKey locPK = mgmt.getPropertyKey("location");
        // Location indexes
        mgmt.buildIndex("byLocation", Vertex.class)
                .addKey(locPK)
                .indexOnly(businessLabel)
                .buildMixedIndex("search");
        // Date index for reviews
        mgmt.buildEdgeIndex(
                reviewLabel,
                "byReviewDate",
                Direction.BOTH,
                datePK);

        // === Commit transaction ===
        try {
            mgmt.commit();
            LOG.info("Schema created successfully!");
        } catch (Exception e) {
            LOG.error("Error committing schema.", e);
        }
    }

    /**
     * Creates and adds Business, Category, Attributes (with edge), City, and State vertices to the Janus graph.
     *
     * @param tx the Janus graph transaction.
     * @param b  the {@link Business} object holding the data to insert.
     */
    private void addBusinessVertex(JanusGraphTransaction tx, Business b) throws JanusGraphException, IllegalStateException {
        GraphTraversalSource g = tx.traversal();

        // If the business exists, then this record has been processed
        if (g.V().hasLabel("Business").has("business_id", b.getBusinessId()).hasNext()) {
            return;
        }

        JanusGraphVertex bVert = tx.addVertex("Business");
        bVert.property("business_id", b.getBusinessId());
        if (b.getAddress() != null) bVert.property("address", b.getAddress());
        if (b.getPostalCode() != null) bVert.property("postal_code", b.getPostalCode());
        bVert.property("is_open", b.isOpen());
        bVert.property("review_count", b.getReviewCount());
        bVert.property("name", b.getName());
        bVert.property("location", Geoshape.point(b.getLatitude(), b.getLongitude()));
        bVert.property("stars", b.getStars());

        // Create new categories if they do not yet exist
        if (b.getCategories() != null) {
            for (String category : b.getCategories()) {
                final GraphTraversal<Vertex, Vertex> categoryTraversal = g.V()
                        .hasLabel("Category").has("name", category);
                if (!categoryTraversal.hasNext()) {
                    tx.addVertex("Category").property("name", category);
                }
            }
        }
        // Create new city if it does not yet exist
        final GraphTraversal<Vertex, Vertex> cityTraversal = g.V().hasLabel("City").has("name", b.getCity());
        if (!cityTraversal.hasNext()) {
            tx.addVertex("City").property("name", b.getCity());
        }
        // Create new state if it does not yet exist
        final GraphTraversal<Vertex, Vertex> stateTraversal = g.V().hasLabel("State").has("name", b.getState());
        if (!stateTraversal.hasNext()) {
            tx.addVertex("State").property("name", b.getState());
        }
    }

    private void addBusinessEdge(JanusGraphTransaction tx, Business b) throws JanusGraphException, IllegalStateException {
        addBusinessToCategoryEdge(tx, b);
        addBusinessToCityEdge(tx, b);
        addCityToStateEdge(tx, b);
    }

    private void addBusinessToCategoryEdge(JanusGraphTransaction tx, Business b) throws JanusGraphException, IllegalStateException {
        GraphTraversalSource g = tx.traversal();

        // Check that all three vertices to connect exist
        final GraphTraversal<Vertex, Vertex> businessTraversal = g.V().hasLabel("Business").has("business_id", b.getBusinessId());

        final Vertex busVert;

        if (businessTraversal.hasNext()) {
            busVert = businessTraversal.next();
        } else {
            return;
        }

        // Link business to a category
        if (b.getCategories() != null) {
            for (String category : b.getCategories()) {
                // Check that category exists
                final GraphTraversal<Vertex, Vertex> categoryTraversal = g.V().hasLabel("Category").has("name", category);
                final Vertex catVert;
                if (!categoryTraversal.hasNext()) continue;

                catVert = categoryTraversal.next();
                // Check if edge exists, if not, add it, else do nothing
                if (!g.V(catVert).in("IN_CATEGORY").hasLabel("Business").has("business_id", b.getBusinessId()).hasNext()) {
                    busVert.addEdge("IN_CATEGORY", catVert);
                }
            }
        }
    }

    private void addBusinessToCityEdge(JanusGraphTransaction tx, Business b) throws JanusGraphException, IllegalStateException {
        GraphTraversalSource g = tx.traversal();

        // Check that all three vertices to connect exist
        final GraphTraversal<Vertex, Vertex> businessTraversal = g.V().hasLabel("Business").has("business_id", b.getBusinessId());
        final GraphTraversal<Vertex, Vertex> cityTraversal = g.V().hasLabel("City").has("name", b.getCity());

        final Vertex busVert;
        final Vertex cityVertex;

        if (businessTraversal.hasNext() && cityTraversal.hasNext()) {
            busVert = businessTraversal.next();
            cityVertex = cityTraversal.next();
        } else {
            return;
        }

        // Link business to a city
        // Check if edge exists, if not, add it, else do nothing
        if (!g.V(cityVertex).in("IN_CITY").hasLabel("Business").has("business_id", b.getBusinessId()).hasNext()) {
            busVert.addEdge("IN_CITY", cityVertex);
        }
    }

    /**
     * Creates and adds IN_CATEGORY, IN_CITY, and IN_STATE edges to the Janus graph.
     *
     * @param tx the Janus graph transaction.
     * @param b  the {@link Business} object holding the data to insert.
     */
    private void addCityToStateEdge(JanusGraphTransaction tx, Business b) throws JanusGraphException, IllegalStateException {
        GraphTraversalSource g = tx.traversal();

        // Check that all three vertices to connect exist
        final GraphTraversal<Vertex, Vertex> cityTraversal = g.V().hasLabel("City").has("name", b.getCity());
        final GraphTraversal<Vertex, Vertex> stateTraversal = g.V().hasLabel("State").has("name", b.getState());

        final Vertex cityVertex;
        final Vertex stateVertex;

        if (cityTraversal.hasNext() && stateTraversal.hasNext()) {
            cityVertex = cityTraversal.next();
            stateVertex = stateTraversal.next();
        } else {
            return;
        }

        // Link city to an existing state or create new state if it does not yet exist
        // Check if edge exists, if not, add it, else do nothing
        if (!g.V(stateVertex).in("IN_STATE").hasLabel("City").has("name", b.getCity()).hasNext()) {
            cityVertex.addEdge("IN_STATE", stateVertex);
        }
    }

    /**
     * Creates and adds a User vertex to the Janus graph.
     *
     * @param tx the Janus graph transaction.
     * @param u  the {@link User} object holding the data to insert.
     */
    private void addUserVertex(JanusGraphTransaction tx, User u) throws JanusGraphException, IllegalStateException {
        GraphTraversalSource g = tx.traversal();

        // If user exists, return
        if (g.V().hasLabel("User").has("user_id", u.getUserId()).hasNext()) {
            return;
        }

        JanusGraphVertex uVert = tx.addVertex("User");
        uVert.property("user_id", u.getUserId());
        uVert.property("name", u.getName());
        uVert.property("review_count", u.getReviewCount());
        uVert.property("average_stars", u.getAverageStars());
        uVert.property("yelping_since", u.getYelpingSince());
        uVert.property("cool", u.getCool());
        uVert.property("funny", u.getFunny());
        uVert.property("useful", u.getUseful());
        uVert.property("fans", u.getFans());
        uVert.property("complimentHot", u.getComplimentHot());
        uVert.property("complimentMore", u.getComplimentMore());
        uVert.property("complimentProfile", u.getComplimentProfile());
        uVert.property("complimentCute", u.getComplimentCute());
        uVert.property("complimentList", u.getComplimentList());
        uVert.property("complimentNote", u.getComplimentNote());
        uVert.property("complimentPlain", u.getComplimentPlain());
        uVert.property("complimentCool", u.getComplimentCool());
        uVert.property("complimentFunny", u.getComplimentFunny());
        uVert.property("complimentWriter", u.getComplimentWriter());
        uVert.property("complimentPhotos", u.getComplimentPhotos());
    }

    /**
     * Adds the friend edges between users.
     *
     * @param tx the Janus graph transaction.
     * @param u  the {@link User} object holding the data to insert.
     */
    private void addUserFriends(JanusGraphTransaction tx, User u) throws JanusGraphException, IllegalStateException {
        GraphTraversalSource g = tx.traversal();

        Vertex uVert = g.V().hasLabel("User").has("user_id", u.getUserId()).next();
        // Add friend relations
        for (String friendId : u.getFriends()) {
            // Turns out some users have friends that aren't in the database
            final GraphTraversal<Vertex, Vertex> friendTraversal = g.V().hasLabel("User").has("user_id", friendId);
            if (!friendTraversal.hasNext()) continue;
            Vertex friendVert = friendTraversal.next();
            // Check if edge exists, if not, add it, else do nothing
            if (!g.V(friendVert).both("FRIENDS").hasLabel("User").has("user_id", u.getUserId()).hasNext()) {
                // Edge is bidirectional
                uVert.addEdge("FRIENDS", friendVert);
            }
        }
    }

    /**
     * Creates and adds a Review edges to the Janus graph.
     *
     * @param tx the Janus graph transaction.
     * @param r  the {@link Review} object holding the data to insert.
     */
    private void addReviewEdge(JanusGraphTransaction tx, Review r) throws JanusGraphException, IllegalStateException {
        GraphTraversalSource g = tx.traversal();

        // If review exists, return
        if (g.V().hasLabel("User").has("user_id", r.getUserId())
                .outE().hasLabel("REVIEWS").has("review_id", r.getReviewId()).hasNext()) return;

        Vertex busVert;
        Vertex userVert;
        final GraphTraversal<Vertex, Vertex> businessTraversal = g.V().hasLabel("Business").has("business_id", r.getBusinessId());
        final GraphTraversal<Vertex, Vertex> userTraversal = g.V().hasLabel("User").has("user_id", r.getUserId());
        // If business and user vertex don't exist, then return
        if (businessTraversal.hasNext() && userTraversal.hasNext()) {
            busVert = businessTraversal.next();
            userVert = userTraversal.next();
        } else {
            return;
        }

        final Edge review = userVert.addEdge("REVIEWS", busVert);
        review.property("review_id", r.getReviewId());
        review.property("cool", r.getCool());
        review.property("funny", r.getFunny());
        review.property("useful", r.getUseful());
        review.property("date", r.getDate());
        review.property("stars", r.getStars());
        review.property("text", r.getText());
    }

    /**
     * Depending on data type and insert mode selected, a code identifying the operation is returned.
     *
     * @param dataType   the Yelp data being imported.
     * @param insertMode the insert mode.
     * @return a short code identifying data being imported.
     */
    public static String getDataDescriptorShort(YELP dataType, INSERT_MODE insertMode) {
        switch (dataType) {
            case BUSINESS:
                switch (insertMode) {
                    case VERTEX:
                        return "BUS_VERT";
                    case EDGE:
                        return "BUS_EDGE";
                }
                break;
            case USER:
                switch (insertMode) {
                    case VERTEX:
                        return "USR_VERT";
                    case EDGE:
                        return "USR_EDGE";
                }
                break;
            case REVIEW:
                return "REV_EDGE";
        }
        return "UNKNWN";
    }

    /**
     * Depending on data type and insert mode selected, a phrase identifying the operation is returned.
     *
     * @param dataType   the Yelp data being imported.
     * @param insertMode the insert mode.
     * @return a short code identifying data being imported.
     */
    public static String getDataDescriptorLong(YELP dataType, INSERT_MODE insertMode) {
        switch (dataType) {
            case BUSINESS:
                switch (insertMode) {
                    case VERTEX:
                        return "business, attribute, category, city, and state vertices";
                    case EDGE:
                        return "business to category, city edges and city to state edges";
                }
                break;
            case USER:
                switch (insertMode) {
                    case VERTEX:
                        return "user vertices";
                    case EDGE:
                        return "user to user friend edges";
                }
                break;
            case REVIEW:
                return "user to business review edge";
        }
        return "unknown";
    }

    public void insertRecords(List<String> records, YELP
            type, ProgressBar pb, INSERT_MODE insertMode) {
        Transaction tw = new Transaction(records, type, pb, insertMode);
        tw.commit();
    }

    /**
     * A worker thread designed to process a record from a Yelp file, serialize the values, add the values to the graph
     * and add them to a {@link JanusGraphTransaction}.
     */
    class Transaction {

        private final List<String> records;
        private final YELP type;
        private final ProgressBar pb;
        private final INSERT_MODE insertMode;

        Transaction(
                List<String> records,
                YELP type,
                ProgressBar pb,
                INSERT_MODE insertMode) {
            this.records = records;
            this.type = type;
            this.pb = pb;
            this.insertMode = insertMode;
        }

        void commit() {
            boolean success = false;
            int failures = 0;
            int waitTime = 50;
            TransactionBuilder builder = janusGraph.buildTransaction();
            do {
                JanusGraphTransaction tx = null;
                try {
                    tx = builder.enableBatchLoading().consistencyChecks(false).start();
                    createAndCommitTx(tx);
                    success = true;
                } catch (JanusGraphException | IllegalStateException e) {
                    failures++;
                    try {
                        Thread.sleep(failures * waitTime);
                        if (tx != null)
                            tx.rollback();
                    } catch (Exception ignored) {
                    }
                    if (waitTime * 2 > 0) {
                        waitTime *= 2;
                    }
                    if (failures > 1000) {
                        LOG.warn("Over 1000 failures on transaction for thread " + Thread.currentThread().getName());
                    }
                }
            } while (!success);
            for (int i = 0; i < records.size(); i++) {
                // Update counters
                pb.step();
            }
        }

        private void createAndCommitTx(JanusGraphTransaction tx) throws JanusGraphException {
            // Process and add batch to a single transaction for bulk-loading
            for (String record : records) {
                Object obj = FileReaderWrapper.processJSON(record, type);
                // Send object to correct transaction
                if (obj instanceof Business) {
                    if (insertMode == INSERT_MODE.VERTEX)
                        addBusinessVertex(tx, (Business) obj);
                    else if (insertMode == INSERT_MODE.EDGE)
                        addBusinessEdge(tx, (Business) obj);
                } else if (obj instanceof User) {
                    if (insertMode == INSERT_MODE.VERTEX)
                        addUserVertex(tx, (User) obj);
                    else if (insertMode == INSERT_MODE.EDGE)
                        addUserFriends(tx, (User) obj);
                } else if (obj instanceof Review) {
                    addReviewEdge(tx, (Review) obj);
                }
            }
            // Commit all changes
            tx.commit();
        }

    }

}
