g.V().hasLabel("Resource")
    .has("resource_id", "2")
        .in("RESPONSE_RESOURCE")
    .hasLabel("Response")
    .has("destination",
        geoWithin(
            Geoshape.circle(63.67, 19.11, 0.5))
        ).as("RES")
    .out("RESPONSE_SCENE").as("SCN")
		  .select("RES")
    .groupCount()
        .by(out("RESPONSE_PRIORITY")
            .values("priority_id"))