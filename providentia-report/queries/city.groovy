g.V().has("User",
    "user_id", "7weuSPSSqYLUFga6IYP4pg")
    .as("julie")
    .out("FRIENDS").as("f1")
    .out("FRIENDS").as("f2")
    .union(select("f1"), select("f2"))
        .dedup().where(neq("julie"))
    .outE("REVIEWS").filter{
        it.get().value("date")
            .atZone(ZoneId.of("-07:00"))
            .toLocalDate().getMonthValue() >= 11
        &&
        it.get().value("date")
            .atZone(ZoneId.of("-07:00"))
            .toLocalDate().getMonthValue() <= 12
    }.as("text").as("stars")
    .inV().has("location", geoWithin(
        Geoshape.circle(36.16, -115.14, 30)))
    .select("text", "stars")
    .by("text", "stars")