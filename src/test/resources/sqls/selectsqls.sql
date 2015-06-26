SELECT table_name FROM information_schema.tables WHERE table_schema = 'wifdemo';
SELECT column_name FROM information_schema.columns WHERE table_name ='uaz_demonstration';
SELECT column_name FROM information_schema.columns WHERE table_name ='uaz_herveybay';
SELECT column_name FROM information_schema.columns WHERE table_name ='wif_03a0cc924954a0dde9db5b873d51f7bd';
SELECT "score_industrial" FROM wifdemo.wif_03a0cc924954a0dde9db5b873d51f7bd order by "score_industrial" desc;

UPDATE wifmodel.factortype SET "value"='3.0' WHERE id=267;
UPDATE wifdemo.uaz_demonstration SET "ALU_1"=0.0;
UPDATE wifdemo.uaz_demonstration SET "ALU_2"=0.0;
UPDATE wifdemo.uaz_demonstration SET "ALU_3"=0.0;
UPDATE wifdemo.uaz_demonstration SET "SCORE_1"=0.0;
--WA
ALTER TABLE wifdemo.wif_d80ef1c0e0841a1850ea154819ac53f2 ADD COLUMN "score_conservation" double precision;
ALTER TABLE wifdemo.wif_d80ef1c0e0841a1850ea154819ac53f2 ADD COLUMN "score_commercial" double precision;
ALTER TABLE wifdemo.wif_d80ef1c0e0841a1850ea154819ac53f2 ADD COLUMN "score_residential" double precision;
ALTER TABLE wifdemo.wif_d80ef1c0e0841a1850ea154819ac53f2 ADD COLUMN "score_industrial" double precision;

ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "" double precision;

  
--to support values not funding union file andthe new way to name score for columns in demonstration as well
ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "TOTPOP_CY" double precision;
  ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "POLITICAL" character varying;
  ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "CLIP_ACRES" double precision;
  ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "BG_ACRES" double precision;
  UPDATE wifdemo.uaz_demonstration SET "CLIP_ACRES"=1.0;
  UPDATE wifdemo.uaz_demonstration SET "BG_ACRES"=1.0;

-- alis
    ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "BLK_GRP" double precision;
      UPDATE wifdemo.uaz_demonstration SET "BLK_GRP"=1.0;
--ali
      
  -- --------------------------------- Should run after importing UAZ
ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "score_mixed_use" double precision;
ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "score_office" double precision;
ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "score_residential" double precision;
ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "score_retail" double precision;
ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "score_conservation" double precision;
ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "score_industrial" double precision;
ALTER TABLE wifdemo.uaz_demonstration ADD COLUMN "UAZ_AREA" double precision;
-- -------------
UPDATE wifdemo.uaz_demonstration SET "SCORE_1"="score_residential";
UPDATE wifdemo.uaz_demonstration SET "SCORE_4"="score_office";
UPDATE wifdemo.uaz_demonstration SET "SCORE_2"="score_mixed_use";
UPDATE wifdemo.uaz_demonstration SET "SCORE_3"="score_retail";
UPDATE wifdemo.uaz_demonstration SET "SCORE_5"="score_industrial";
UPDATE wifdemo.uaz_demonstration SET "SCORE_6"="score_conservation";

ALTER TABLE wifdemo.nw_demo ADD COLUMN "score_industry" double precision;
ALTER TABLE wifdemo.nw_demo ADD COLUMN "score_cons" double precision;
ALTER TABLE wifdemo.nw_demo ADD COLUMN "score_res" double precision;

UPDATE wifdemo.uaz_demonstration SET "UAZ_AREA"=(ST_Area(the_geom));
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration ;
SELECT * FROM wifdemo.uaz_hervey_bay order by ogc_fid asc;
SELECT * FROM wifdemo.uaz_demonstration order by gid asc;
SELECT gid,"FACTOR_1" FROM wifdemo.uaz_demonstration order by gid asc;

SELECT COUNT (*) FROM wifdemo.uaz_demonstration;
SELECT COUNT (*) FROM wifdemo.demonstration_union;
SELECT COUNT (*) FROM wifdemo.uaz_demonstration WHERE "SCORE_5">0;

SELECT ST_Area(the_geom) As Area FROM wifdemo.demonstration_union;

SELECT gid, "ELU", "ALU_1" FROM wifdemo.uaz_demonstration WHERE "ELU" = '1.0' AND "ALU_1" = '0.0';
SELECT SUM (DISTINCT ("UAZ_AREA")) FROM wifdemo.uaz_demonstration WHERE "ELU" = '1.0' AND "ALU_1" = '0.0';
SELECT SUM (DISTINCT ("UAZ_AREA")) FROM wifdemo.uaz_demonstration WHERE "SCORE_1"  BETWEEN 6001 AND 9000.0;
 SELECT SUM(DISTINCT ("UAZ_AREA")) FROM wifdemo.uaz_demonstration WHERE "SCORE_6"=-100.0;
 SELECT SUM(DISTINCT ("UAZ_AREA")) FROM wifdemo.uaz_demonstration WHERE "FACTOR_1"=-98.0;
 
 SELECT "UAZ_AREA", "ELU" FROM wifdemo.uaz_demonstration WHERE "ALU_1"='1.0';
 
SELECT gid, "UAZ_AREA","SCORE_1" FROM wifdemo.uaz_demonstration order by "SCORE_1" desc;
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='1.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='2.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='3.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='4.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='11.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='12.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='13.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='21.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='41.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='51.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='81.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='82.0';
SELECT SUM("UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='83.0';
SELECT SUM(DISTINCT "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "SCORE_6"'81.0';
SELECT SUM( "UAZ_AREA") FROM wifdemo.uaz_demonstration WHERE "ELU"='12.0';
SELECT * FROM wifdemo.uaz_demonstration limit 10;

SELECT SUM(DISTINCT "UAZ_POP") FROM wifdemo.uaz_demonstration;
SELECT SUM(DISTINCT ("TOTPOP_CY"*("CLIP_ACRES"/ "BG_ACRES"))) FROM wifdemo.demonstration_union;
SELECT SUM(DISTINCT ("TOTHH_CY"*("CLIP_ACRES"/ "BG_ACRES"))) FROM wifdemo.demonstration_union;
SELECT SUM(DISTINCT ("TOTHU_CY"*("CLIP_ACRES"/ "BG_ACRES"))) FROM wifdemo.demonstration_union;
SELECT SUM(DISTINCT ("VACANT_CY"*("CLIP_ACRES"/ "BG_ACRES"))) FROM wifdemo.demonstration_union;

SELECT SUM(DISTINCT ("TOTHU_CY"*("CLIP_ACRES"/ "BG_ACRES"))) 
FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Low Density Res.';

SELECT SUM (DISTINCT ("TOTHU_CY")) FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Low Density Res.';
SELECT SUM ("TOTHU_CY") FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Low Density Res.';
SELECT "TOTHU_CY" FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Low Density Res.';

SELECT gid, "UAZ_AREA", "ELU" FROM wifdemo.uaz_demonstration WHERE "ELU"='1.0' order by "UAZ_AREA";
SELECT "UAZ_AREA", "ELU" FROM wifdemo.uaz_demonstration WHERE "ELU"='1.0' AND "UAZ_AREA" BETWEEN 34222 AND 34224;

SELECT SUM (DISTINCT "AREA") FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Low Density Res.';
SELECT "AREA","LAND_USE" FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Low Density Res.';
SELECT SUM("AREA") FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Low Density Res.';

SELECT SUM (DISTINCT ("TOTHU_CY")) FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Mixed Use';
SELECT SUM (DISTINCT ("TOTHU_CY")) FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Med. Density Res';
SELECT SUM(DISTINCT ("TOTHU_CY"*("CLIP_ACRES"/ "BG_ACRES"))) FROM wifdemo.demonstration_union WHERE "LAND_USE" = 'Med. Density Res';

SELECT SUM(DISTINCT ("TOTPOP_CY"*("CLIP_ACRES"/ "BG_ACRES"))) FROM wifdemo."Demonstration_union";
SELECT SUM(DISTINCT ("TOTHU_CY"*("CLIP_ACRES"/ "BG_ACRES"))) FROM wifdemo."Demonstration_union";
SELECT SUM(DISTINCT ("VACANT_CY"*("CLIP_ACRES"/ "BG_ACRES"))) FROM wifdemo."Demonstration_union";

SELECT DISTINCT "ELU" FROM wifdemo.uaz_demonstration;
SELECT DISTINCT "FACTOR_1" FROM wifdemo.uaz_demonstration;
SELECT DISTINCT "FACTOR_6" FROM wifdemo.uaz_demonstration;
