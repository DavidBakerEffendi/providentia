SELECT  avg(RES.time_to_ambulance_starts) 
            as avg_ttas,
        avg(TNS.travel_time_hospital)
	        as avg_tth
FROM priority PRI
JOIN response RES ON RES.id = PRI.response_id
JOIN transfer TNS ON RES.id = TNS.response_id
WHERE PRI.id = 1
	AND ST_DWithin(
	    RES.destination,
	    ST_MakePoint(19.11, 63.67)::geography,
	    500);