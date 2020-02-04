SELECT count(*)
FROM transfer TNS
JOIN response RES ON TNS.response_id = RES.id
WHERE RES.time_to_ambulance_starts
	+ RES.on_scene_duration
	+ TNS.travel_time_hospital > 15 * 60;