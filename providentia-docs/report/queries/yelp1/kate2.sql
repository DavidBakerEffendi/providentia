SELECT review.stars, review.text, review.business_id
FROM review
JOIN business
    ON review.business_id = business.id
    AND review.user_id = "..."
    AND ST_DWithin(location,
       ST_MakePoint(-80.79, 35.15)::geography,
       5000)
    AND review.stars > 3
ORDER BY review.date DESC
LIMIT 10