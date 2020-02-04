SELECT DISTINCT R.text, R.stars FROM review R
JOIN business B ON R.business_Id = B.id 
INNER JOIN friends F2 ON R.user_id = F2.friend_id
INNER JOIN friends F1 ON F2.user_id = F1.friend_id
WHERE F1.user_id = "7weuSPSSqYLUFga6IYP4pg"
    AND F2.user_id <> "7weuSPSSqYLUFga6IYP4pg"
    AND (R.user_id = F1.user_id 
    OR   R.user_id = F1.friend_id)
    AND ST_DWithin(
        B.location,
        ST_MakePoint(-115.14, 36.16)::geography,
        30000)
    AND (date_part("month", R.date) >= 11
    AND  date_part("month", R.date) <= 12)