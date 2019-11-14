g.V().has("User", "user_id", "...")
    .outE("REVIEWS").has("stars", gt(3))
        .order().by("date", desc)
        .as("stars", "text")
    .inV().has("location",
        geoWithin(
            Geoshape.circle(35.15,-80.79, 5)
        )).as("business_id")
    .select("stars").limit(10)
    .select("stars", "text", "business_id")
    .by("stars").by("text").by("business_id")