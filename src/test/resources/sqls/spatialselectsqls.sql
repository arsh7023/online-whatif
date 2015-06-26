SELECT ST_Area(the_geom), "ELU"
FROM wifdemo.uaz_demonstration 
order by gid;

--Northwest perth
SELECT * 
	FROM wifdemo.wif_d80ef1c0e0841a1850ea15481963b1b7
	WHERE 
	ST_Intersects (the_geom, ST_GeomFromText(
	'POLYGON ((386415.0218000002 6489447.6531, 386415.0218000002 6475535.1362, 405695.32870000036 6475535.1362, 405695.32870000036 6489447.6531, 386415.0218000002 6489447.6531))'
,ST_SRID(the_geom)));

-- Hervey Bay
SELECT gid, ST_AsText (the_geom), "ELU","SCORE_1", "SCORE_2", "SCORE_3", "SCORE_4", "SCORE_5", "SCORE_6"
	FROM wifdemo.uaz_hervey_bay 
	WHERE 
	ST_Intersects (the_geom, ST_GeomFromText(
	'POLYGON((471871.491889042 7182882.39986455,471838.573671077 7182782.2718174,471787.598363 7182810.81417976,471792.003467625 7182820.83414179,471827.698050419 7182902.02593934,471871.491889042 7182882.39986455))',ST_SRID(the_geom))) 
	;
SELECT gid, "FACTOR_3", "ELU","SCORE_1"
	FROM wifdemo.uaz_herveybay 
	WHERE 
	ST_Intersects (the_geom, ST_GeomFromText(
	'POLYGON((471871.491889042 7182882.39986455,471838.573671077 7182782.2718174,471787.598363 7182810.81417976,471792.003467625 7182820.83414179,471827.698050419 7182902.02593934,471871.491889042 7182882.39986455))',ST_SRID(the_geom))) 
	AND gid = 35296 order by gid;

SELECT gid,"ELU","UAZ_AREA","SCORE_1","ALU_1" FROM wifdemo.uaz_demonstration order by "SCORE_1" desc;
SELECT gid,"ELU","SCORE_1","UAZ_AREA","ALU_1" FROM wifdemo.uaz_demonstration order by "SCORE_1" desc,"UAZ_AREA" desc;

-- Total query
SELECT gid,the_geom,"ELU","GROWTH_1","SCORE_6",
 "UAZ_AREA","ALU_1"
	FROM wifdemo.uaz_demonstration 
	 WHERE 
	 ST_Intersects(the_geom, ST_GeomFromText(
	 'POLYGON((1780378.5 750276.9,1780378.5 750276.95,1780378.6 750276.95,1780378.5 750276.95,1780378.5 750276.9))',ST_SRID(the_geom))) 
	order by "SCORE_6" desc,"GROWTH_1" asc, "UAZ_AREA" desc;
	
-- CONSERVATION NO controls
SELECT gid,the_geom,"ELU","SCORE_6",
 "UAZ_AREA","ALU_1"
	FROM wifdemo.uaz_demonstration 
	WHERE 
	"ELU" IN (81, 82)
	AND "SCORE_6">0
--	gid=14367 --be one to the lower left,88.plus 
	--ST_Intersects(the_geom, ST_GeomFromText(
	--'POLYGON((1799535 788274,1799535 787172,1794852 787172,1794852 788274,1799535 788274))',ST_SRID(the_geom))) 
	order by 
	 "SCORE_6" desc,
	 "UAZ_AREA" desc;

--81 rows
SELECT gid,the_geom,"ELU","SCORE_1","SCORE_2","SCORE_3","SCORE_4","SCORE_5","SCORE_6",
 "UAZ_AREA","ALU_1","ALU_2","ALU_3"
	FROM wifdemo.uaz_demonstration 
	WHERE ST_Intersects(the_geom, ST_GeomFromText(
	'POLYGON((1799535 788274,1799535 787172,1794852 787172,1794852 788274,1799535 788274))',ST_SRID(the_geom))) 
	order by "ALU_1" desc, "SCORE_1" desc,"UAZ_AREA" desc;
--political units, necessary for local land-use allocation
SELECT gid,the_geom,"ELU","SCORE_1", "BOUNDARY1",  
 "UAZ_AREA","ALU_1","ALU_2","ALU_3"
	FROM wifdemo.uaz_demonstration 
	WHERE 
	ST_Intersects(the_geom, ST_GeomFromText(
	'POLYGON((1777432 753041, 1777432 758092, 1784110 758092, 1784110 753041, 1777432 753041))',ST_SRID(the_geom))) 
	and "BOUNDARY1"=2
	order by "ALU_1" desc, "SCORE_1" desc,"UAZ_AREA" desc;

--PLANNED CONSERVATION:order by land use plan, growth pattern concentric, scenario is "Medium Growth 2030 Plan",no infrastructure control
SELECT gid,the_geom,"ELU","PLU_1","GROWTH_1","SCORE_6",
 "UAZ_AREA","ALU_1"
	FROM wifdemo.uaz_demonstration 
	WHERE 
	"PLU_1" = 381
	AND
	"ELU" IN (81, 82)
	AND "SCORE_6">0
--	gid=14367 --be one to the lower left,88.plus 
	--ST_Intersects(the_geom, ST_GeomFromText(
	--'POLYGON((1799535 788274,1799535 787172,1794852 787172,1794852 788274,1799535 788274))',ST_SRID(the_geom))) 
	order by 
	 "SCORE_6" desc,
	 "GROWTH_1" asc,
	 "UAZ_AREA" desc;
	
--CONSERVATION:order by control services, no growth pattern, scenario is "Extended services",excludingwater and sewer for conservation
SELECT gid,the_geom,"CONTROL_1","CONTROL_2","CONTROL_3","GROWTH_1","SCORE_6",
 "UAZ_AREA","ALU_1"
	FROM wifdemo.uaz_demonstration 
	WHERE 
	"CONTROL_2" NOT IN (0)
	AND
	"CONTROL_1" NOT IN (0) 
	AND
	"ELU" IN (81, 82)
	AND "SCORE_6">0
--	gid=14367 --be one to the lower left,88.plus 
	--ST_Intersects(the_geom, ST_GeomFromText(
	--'POLYGON((1799535 788274,1799535 787172,1794852 787172,1794852 788274,1799535 788274))',ST_SRID(the_geom))) 
	order by 
	 "SCORE_6" desc,
	 -- "GROWTH_1" asc,
	 "UAZ_AREA" desc;

-- TESTING FOR CONTROLS
SELECT gid,the_geom,"CONTROL_1","CONTROL_2","CONTROL_3","GROWTH_1","ELU","SCORE_6",
 "UAZ_AREA","ALU_1","ALU_2"
	FROM wifdemo.uaz_demonstration 
	WHERE 
	"CONTROL_1" NOT IN (0)
--	gid=14367
	--ST_Intersects(the_geom, ST_GeomFromText(
	--'POLYGON((1799535 788274,1799535 787172,1794852 787172,1794852 788274,1799535 788274))',ST_SRID(the_geom))) 
	order by 
	 "SCORE_6" desc,
	 "GROWTH_1" asc,
	 "UAZ_AREA" desc;
	 
	 --industrial  , concentric growth
SELECT gid,"ELU","SCORE_5","GROWTH_1",
 "UAZ_AREA","ALU_1"--,"ALU_2","ALU_3","RND_NUM"
	FROM wifdemo.uaz_demonstration 
	WHERE "ELU" IN (82,81,2,1) 
	AND "SCORE_5">0
	AND "GROWTH_1" = 1
	AND gid=17768
		order by
		"ELU" desc, 
	 "SCORE_5" desc,
	 "GROWTH_1" asc,
	 "UAZ_AREA" desc;
	 
	 	 	 --Conservation, concentric growth
SELECT gid,"ELU","SCORE_6","GROWTH_1",
 "UAZ_AREA","ALU_1","ALU_2" --"ALU_3","RND_NUM"
	FROM wifdemo.uaz_demonstration 
	WHERE "ELU" IN (82,81) 
--	AND "ALU_1" IN (4)
	AND "SCORE_6">0
--		AND gid=17768 --this one is the first one of the in inner concentric rings  that is very  big, 40.056 in ALU_1
		AND gid=18037 --this one is a big one touching the blue of the in inner concentric rings 24.273 in ALU_2
		order by
	"SCORE_6" desc,
	 "GROWTH_1" asc,
	 "UAZ_AREA" desc;
	
SELECT gid,the_geom,"CONTROL_3","ELU","SCORE_6",
 "UAZ_AREA","ALU_1","ALU_2","ALU_3"
	FROM wifdemo.uaz_demonstration 
	WHERE 
	gid=17572
	--ST_Intersects(the_geom, ST_GeomFromText(
	--'POLYGON((1799535 788274,1799535 787172,1794852 787172,1794852 788274,1799535 788274))',ST_SRID(the_geom))) 
		order by 
	 "SCORE_6" desc,
	 "UAZ_AREA" desc;
	 
-- Suitability Residential	 
SELECT gid,the_geom,"ELU","SCORE_1" 
FROM wifdemo.uaz_demonstration 
WHERE 
	ST_Intersects(the_geom, ST_GeomFromText(
	'POLYGON((1799535 788274,1799535 787172,1794852 787172,1794852 788274,1799535 788274))',ST_SRID(the_geom)))
	;

-- AND 
--gid=14367 --this one is the biggest in the lower left corner, that is medium high lighter green
order by "SCORE_1" desc;

-- Suitability Mixed Use	 
SELECT gid,the_geom,"ELU","SCORE_2" 
FROM wifdemo.uaz_demonstration 
WHERE 
-- AND 
gid=14367 --this one is the biggest in the lower left corner, that is medium high lighter green
order by "SCORE_2" desc;
