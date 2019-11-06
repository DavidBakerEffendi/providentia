// NOTE: This can be loaded automatically by setting 'import.load-schema' to true in 'janus-graph.properties'

import org.apache.tinkerpop.gremlin.structure.Direction
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.Cardinality
import org.janusgraph.core.EdgeLabel
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.VertexLabel
import org.janusgraph.core.attribute.Geoshape
import org.janusgraph.core.schema.ConsistencyModifier
import org.janusgraph.core.schema.JanusGraphIndex
import org.janusgraph.core.schema.JanusGraphManagement

JanusGraphManagement mgmt = janusGraph.openManagement()

// === Create labels ===

// Create vertex labels
final VertexLabel businessLabel = mgmt.makeVertexLabel("Business").make()
final VertexLabel categoryLabel = mgmt.makeVertexLabel("Category").make()
final VertexLabel cityLabel = mgmt.makeVertexLabel("City").make()
final VertexLabel stateLabel = mgmt.makeVertexLabel("State").make()
final VertexLabel userLabel = mgmt.makeVertexLabel("User").make()
// Create edge labels
final EdgeLabel reviewLabel = mgmt.makeEdgeLabel("REVIEWS").make()
mgmt.makeEdgeLabel("IN_CATEGORY").make()
mgmt.makeEdgeLabel("IN_CITY").make()
mgmt.makeEdgeLabel("IN_STATE").make()
mgmt.makeEdgeLabel("FRIENDS").make()

// === Create properties ===

// Business properties
mgmt.makePropertyKey("business_id").dataType(String.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("address").dataType(String.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("postal_code").dataType(String.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("is_open").dataType(Boolean.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("review_count").dataType(Integer.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("name").dataType(String.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("location").dataType(Geoshape.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("stars").dataType(Double.class).cardinality(Cardinality.SINGLE).make()

// User properties
mgmt.makePropertyKey("user_id").dataType(String.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("average_stars").dataType(Double.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("yelping_since").dataType(Long.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("cool").dataType(Integer.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("funny").dataType(Integer.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("useful").dataType(Integer.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("fans").dataType(Integer.class).cardinality(Cardinality.SINGLE).make()

// Review properties
mgmt.makePropertyKey("review_id").dataType(String.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("date").dataType(Long.class).cardinality(Cardinality.SET).make()
mgmt.makePropertyKey("text").dataType(String.class).cardinality(Cardinality.SINGLE).make()

// === Create indexes ===

// --- ID based indexes ---
// Create index for business ids
PropertyKey busPK = mgmt.getPropertyKey("business_id")
JanusGraphIndex byBusinessId = mgmt.buildIndex("byBusinessId", Vertex.class)
        .addKey(busPK)
        .indexOnly(businessLabel)
        .buildCompositeIndex()
mgmt.setConsistency(byBusinessId, ConsistencyModifier.LOCK)
// Create index for category ids
PropertyKey catPK = mgmt.getPropertyKey("name")
JanusGraphIndex byCatId = mgmt.buildIndex("byCatName", Vertex.class)
        .addKey(catPK)
        .indexOnly(categoryLabel)
        .buildCompositeIndex()
mgmt.setConsistency(byCatId, ConsistencyModifier.LOCK)
// Create index for city ids
PropertyKey cityPK = mgmt.getPropertyKey("name")
JanusGraphIndex byCityId = mgmt.buildIndex("byCityName", Vertex.class)
        .addKey(cityPK)
        .indexOnly(cityLabel)
        .buildCompositeIndex()
mgmt.setConsistency(byCityId, ConsistencyModifier.LOCK)
// Create index for state ids
PropertyKey statePK = mgmt.getPropertyKey("name")
JanusGraphIndex byStateId = mgmt.buildIndex("byStateName", Vertex.class)
        .addKey(statePK)
        .indexOnly(stateLabel)
        .buildCompositeIndex()
mgmt.setConsistency(byStateId, ConsistencyModifier.LOCK)
// Create index for user ids
PropertyKey userPK = mgmt.getPropertyKey("user_id")
JanusGraphIndex byUserId = mgmt.buildIndex("byUserId", Vertex.class)
        .addKey(userPK)
        .indexOnly(userLabel)
        .buildCompositeIndex()
mgmt.setConsistency(byUserId, ConsistencyModifier.LOCK)
// Create index for review ids
PropertyKey reviewPK = mgmt.getPropertyKey("review_id")
mgmt.buildEdgeIndex(
        reviewLabel,
        "byReviewId",
        Direction.BOTH,
        reviewPK)

// --- Spatio-temporal indexes ---
PropertyKey datePK = mgmt.getPropertyKey("date")
PropertyKey locPK = mgmt.getPropertyKey("location")
// Location indexes
mgmt.buildIndex("byLocation", Vertex.class)
        .addKey(locPK)
        .indexOnly(businessLabel)
        .buildMixedIndex("search")
// Date index for reviews
mgmt.buildEdgeIndex(
        reviewLabel,
        "byReviewDate",
        Direction.BOTH,
        datePK)

// === Commit transaction ===
mgmt.commit()
