CALL apoc.periodic.iterate(
    "MATCH (p1:User) RETURN p1",
    "MATCH (p1)-[:REVIEWS]->(r1)-->()<--(r2)<-[:REVIEWS]-(p2)
        WHERE id(p1) < id(p2)",
    {
        batchSize:100,
        parallel:true,
        iterateList:true
    }
);