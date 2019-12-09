package schema.sim
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
final VertexLabel transferLabel = mgmt.makeVertexLabel("Transfer").make()
final VertexLabel resourceLabel = mgmt.makeVertexLabel("Resource").make()
final VertexLabel priorityLabel = mgmt.makeVertexLabel("Priority").make()
final VertexLabel responseLabel = mgmt.makeVertexLabel("Response").make()
// Create edge labels
mgmt.makeEdgeLabel("RESPONSE_PRIORITY").make()
mgmt.makeEdgeLabel("RESPONSE_TRANSFER").make()
mgmt.makeEdgeLabel("RESPONSE_RESOURCE").make()

// === Create properties ===

mgmt.makePropertyKey("response_id").dataType(Integer.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("origin").dataType(Geoshape.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("destination").dataType(Geoshape.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("t").dataType(Integer.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("time_to_ambulance_starts").dataType(Float.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("on_scene_duration").dataType(Float.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("time_at_hospital").dataType(Float.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("travel_time_patient").dataType(Float.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("travel_time_hospital").dataType(Float.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("travel_time_station").dataType(Float.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("resource_ready_time").dataType(Double.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("priority_id").dataType(Integer.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("resource_id").dataType(Integer.class).cardinality(Cardinality.SINGLE).make()
mgmt.makePropertyKey("description").dataType(String.class).cardinality(Cardinality.SINGLE).make()

// === Create indexes ===

// --- ID based indexes ---
// Create index for response ids
PropertyKey respPK = mgmt.getPropertyKey("response_id")
JanusGraphIndex ByResponseId = mgmt.buildIndex("ByResponseId", Vertex.class)
        .addKey(respPK)
        .indexOnly(responseLabel)
        .buildCompositeIndex()
mgmt.setConsistency(ByResponseId, ConsistencyModifier.LOCK)
// Create index for priority ids
PropertyKey prioPK = mgmt.getPropertyKey("priority_id")
JanusGraphIndex byPriorityId = mgmt.buildIndex("byPriorityId", Vertex.class)
        .addKey(prioPK)
        .indexOnly(priorityLabel)
        .buildCompositeIndex()
mgmt.setConsistency(byPriorityId, ConsistencyModifier.LOCK)
// Create index for resource ids
PropertyKey resourcePK = mgmt.getPropertyKey("resource_id")
JanusGraphIndex byResourceId = mgmt.buildIndex("byResourceId", Vertex.class)
        .addKey(resourcePK)
        .indexOnly(resourceLabel)
        .buildCompositeIndex()
mgmt.setConsistency(byResourceId, ConsistencyModifier.LOCK)

// --- Spatio-temporal indexes ---
PropertyKey originPK = mgmt.getPropertyKey("origin")
PropertyKey destinationPK = mgmt.getPropertyKey("destination")
PropertyKey ttasPK = mgmt.getPropertyKey("time_to_ambulance_starts")
PropertyKey ttpPK = mgmt.getPropertyKey("travel_time_patient")
PropertyKey tthPK = mgmt.getPropertyKey("travel_time_hospital")
PropertyKey ttsPK = mgmt.getPropertyKey("travel_time_station")

// Location indexes
mgmt.buildIndex("byOrigin", Vertex.class)
        .addKey(originPK)
        .indexOnly(responseLabel)
        .buildMixedIndex("search")
mgmt.buildIndex("byDestination", Vertex.class)
        .addKey(destinationPK)
        .indexOnly(responseLabel)
        .buildMixedIndex("search")
// Date index for reviews
mgmt.buildIndex("byTTAS", Vertex.class)
        .addKey(ttasPK)
        .indexOnly(responseLabel)
        .buildMixedIndex("search")
mgmt.buildIndex("byTTP", Vertex.class)
        .addKey(ttpPK)
        .indexOnly(responseLabel)
        .buildMixedIndex("search")
mgmt.buildIndex("byTTH", Vertex.class)
        .addKey(tthPK)
        .indexOnly(responseLabel)
        .buildMixedIndex("search")
mgmt.buildIndex("byTTS", Vertex.class)
        .addKey(ttsPK)
        .indexOnly(transferLabel)
        .buildMixedIndex("search")

// === Commit transaction ===
mgmt.commit()
