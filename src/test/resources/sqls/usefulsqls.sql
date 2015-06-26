VACUUM ANALYZE wifdemo.uaz_demonstration;

ALTER TABLE wifdemo.uaz RENAME TO uaz_demonstration;

ALTER TABLE wifdemo.uaz_original OWNER TO whatif;

ALTER TABLE wifmodel.wifproject ADD unifiedareazone character varying(255);

ALTER TABLE wifdemo.old RENAME TO uaz_demonstration;
CREATE TABLE wifdemo.uazPlans AS  TABLE wifdemo.uaz_demonstration;
CREATE TABLE wifdemo.uazIControls AS  TABLE wifdemo.uaz_demonstration;

CREATE INDEX areaidx ON wifdemo.uaz_demonstration ("UAZ_AREA" desc);

CREATE INDEX gr1idx ON wifdemo.uaz_demonstration ("GROWTH_1" asc);
CREATE INDEX gr2idx ON wifdemo.uaz_demonstration ("GROWTH_2" asc);
CREATE INDEX score1idx ON wifdemo.uaz_demonstration ("SCORE_1" desc);
CREATE INDEX score2idx ON wifdemo.uaz_demonstration ("SCORE_2" desc);
CREATE INDEX score5idx ON wifdemo.uaz_demonstration ("SCORE_5" desc);

CREATE OR REPLACE VIEW wifdemo.uaz_demonstration_score_1 AS
 SELECT gid,the_geom,"ELU","SCORE_1","SCORE_2","SCORE_3","SCORE_4","SCORE_5","SCORE_6",
 "UAZ_AREA","ALU_1","ALU_2","ALU_3" FROM wifdemo.uaz_demonstration order by "SCORE_1" desc,"UAZ_AREA" desc;

CREATE OR REPLACE RULE update_view1 AS ON UPDATE TO wifdemo.uaz_demonstration_score_1 DO INSTEAD 
UPDATE  wifdemo.uaz_demonstration SET "ALU_1" =  new."ALU_1" 
        WHERE wifdemo.uaz_demonstration.gid =  new.gid;
UPDATE  wifmodel.wifproject p SET existingluattributename = 'ELU' 
        WHERE id =  700;
UPDATE  wifmodel.wifproject p SET ready = true WHERE id =  961;
UPDATE  wifmodel.wifproject p SET ready = true WHERE id =  961;

UPDATE  wifmodel.demographictrend p SET "label" = 'Past Trend' 
        WHERE id =  105;

DROP VIEW wifdemo.uaz_demonstration_score_1;

GRANT ALL ON SCHEMA wifmodel TO marcos;
GRANT ALL ON TABLE wifdemo.uaz_demonstration TO whatif;


INSERT INTO wifmodel.factorrating
(id,"value",score,suitabilityfactor_fk)
VALUES(10200,'1',80,10100);


INSERT into spatial_ref_sys (srid, auth_name, auth_srid, proj4text, srtext) values 
( 9102723, 'esri', 102723, 
'+proj=lcc +lat_1=38.73333333333333 +lat_2=40.03333333333333 +lat_0=38 +lon_0=-82.5 +x_0=600000.0000000001 +y_0=0 +ellps=GRS80 +datum=NAD83 +to_meter=0.3048006096012192 +no_defs ', 'PROJCS["NAD_1983_StatePlane_Ohio_South_FIPS_3402_Feet",GEOGCS["GCS_North_American_1983",DATUM["North_American_Datum_1983",SPHEROID["GRS_1980",6378137,298.257222101]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]],PROJECTION["Lambert_Conformal_Conic_2SP"],PARAMETER["False_Easting",1968500],PARAMETER["False_Northing",0],PARAMETER["Central_Meridian",-82.5],PARAMETER["Standard_Parallel_1",38.73333333333333],PARAMETER["Standard_Parallel_2",40.03333333333333],PARAMETER["Latitude_Of_Origin",38],UNIT["Foot_US",0.30480060960121924],AUTHORITY["EPSG","102723"]]');

SELECT * FROM spatial_ref_sys WHERE srid= 9102723;
SELECT ST_SRID(the_geom),GeometryType(the_geom),ST_Dimension(the_geom)  FROM wifdemo.uaz_demonstration LIMIT 1;
UPDATE wifdemo.uaz_demonstration SET the_geom  = ST_SetSRID(the_geom, 9102723);
SELECT AddGeometryColumn ('wifdemo','uaz_demonstration','the_geom',9102723,'POLYGON',2);
SELECT * FROM geometry_columns WHERE f_table_name='uaz_demonstration'; 
SELECT * FROM geometry_columns WHERE f_table_schema ='wifdemo'; 
SELECT Populate_Geometry_Columns();
INSERT INTO geometry_columns 
(f_table_catalog,f_table_schema,f_table_name,f_geometry_column,coord_dimension,srid,type)
VALUES('','wifdemo','uaz_demonstration','the_geom',2,9102723, 'POLYGON');
INSERT INTO geometry_columns 
(f_table_catalog,f_table_schema,f_table_name,f_geometry_column,coord_dimension,srid,type)
VALUES('','wifdemo','uazPlans','the_geom',2,9102723, 'POLYGON');
INSERT INTO geometry_columns 
(f_table_catalog,f_table_schema,f_table_name,f_geometry_column,coord_dimension,srid,type)
VALUES('','wifdemo','uazIControls','the_geom',2,9102723, 'POLYGON');


ALTER TABLE spatial_ref_sys  ADD CONSTRAINT spatial_ref_sys_srid_check CHECK (srid > 0 AND srid <= 9102724);

CREATE  SCHEMA wifmodel;
CREATE TABLE kirsty.movements (
  name character varying(255),
  place character varying(255),
  slng character varying(255),
  slat character varying(255),
  sdate character varying(255)
  );
-- Database: "marcos-geo2"

-- DROP DATABASE "marcos-geo2";

CREATE DATABASE "marcos-geo2"
  WITH OWNER = marcos;

ALTER TABLE kirsty.movements ADD COLUMN id SERIAL PRIMARY KEY;

ALTER TABLE kirsty.movements RENAME COLUMN slng TO lat;
ALTER TABLE kirsty.movements RENAME COLUMN slat TO lng;

SELECT AddGeometryColumn ('kirsty','movements','the_geom',4326,'POINT',2);
UPDATE kirsty.movements SET the_geom = ST_GeomFromText ('POINT('|| lng || ' ' || lat || ')', 4326);
SELECT * FROM kirsty.movements;-- WHERE f_table_name='uaz_demonstration'; 
SELECT place, lng, lat, ST_AsEWKT(the_geom) FROM kirsty.movements;-- WHERE f_table_name='uaz_demonstration'; 
SELECT * FROM geometry_columns WHERE f_table_name='movements'; 
