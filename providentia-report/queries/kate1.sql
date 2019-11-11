SELECT DISTINCT OtherReviews.user_id
FROM users 
JOIN review KateReviews 
    ON users.id = KateReviews.user_id 
    AND users.id = "qUL3CdRRF1vedNvaq06rIA"
    AND KateReviews.stars > 3 
JOIN business KateBus
    ON KateReviews.business_id = KateBus.id
JOIN review OtherReviews
    ON OtherReviews.user_id != KateReviews.user_id 
    AND OtherReviews.business_id = KateReviews.business_id
JOIN bus_2_cat Bus2Cat
    ON OtherReviews.business_id = Bus2Cat.business_id
JOIN category Categories
    ON Bus2Cat.category_id = Categories.id 
        AND Categories.name = "Restaurants"