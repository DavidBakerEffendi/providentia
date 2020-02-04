g.V().has("Business",
    "location", geoWithin(
        Geoshape.circle(33.45,-112.56, 50)))
    .inE("REVIEWS")
    .has("date", between(
      Instant.parse("2018-01-01T00:00:00.00Z"),
      Instant.parse("2018-12-31T23:59:59.99Z")
    )).valueMap()