SELECT count(*) as responses_by_prio
FROM resource RSC
JOIN response RES ON RSC.response_id = RES.id
JOIN on_scene SCN ON SCN.response_id = RES.id
JOIN priority PRI ON PRI.response_id = RES.id
WHERE RSC.id = 2
    AND ST_DWithin(
	    RES.destination,
	    ST_MakePoint(19.11, 63.67)::geography,
	    500)
GROUP BY PRI.id;