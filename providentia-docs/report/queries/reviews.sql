SELECT text, review.stars, cool, funny, useful
FROM business
JOIN review ON business.id = review.business_id
    AND ST_DWithin(
        location,
        ST_MakePoint(-112.56, 33.45)::geography,
        50000)
    AND date_part("year", date) = 2018)