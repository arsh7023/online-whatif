VACUUM ANALYZE wifdemo.uaz_demonstration;

ALTER TABLE wifdemo."Demonstration_union"
    RENAME TO demonstration_union;

    INSERT into spatial_ref_sys (srid, auth_name, auth_srid, proj4text, srtext) values ( 94283, 'epsg', 4283, '+proj=longlat +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +no_defs ', 'GEOGCS["GDA94",DATUM["Geocentric_Datum_of_Australia_1994",SPHEROID["GRS 1980",6378137,298.257222101,AUTHORITY["EPSG","7019"]],TOWGS84[0,0,0,0,0,0,0],AUTHORITY["EPSG","6283"]],PRIMEM["Greenwich",0,AUTHORITY["EPSG","8901"]],UNIT["degree",0.01745329251994328,AUTHORITY["EPSG","9122"]],AUTHORITY["EPSG","4283"]]');
 --demonstration union   esri
INSERT into spatial_ref_sys (srid, auth_name, auth_srid, proj4text, srtext) values 
( 9102723, 'esri', 102723, 
'+proj=lcc +lat_1=38.73333333333333 +lat_2=40.03333333333333 +lat_0=38 +lon_0=-82.5 +x_0=600000.0000000001 +y_0=0 +ellps=GRS80 +datum=NAD83 +to_meter=0.3048006096012192 +no_defs ', 'PROJCS["NAD_1983_StatePlane_Ohio_South_FIPS_3402_Feet",GEOGCS["GCS_North_American_1983",DATUM["North_American_Datum_1983",SPHEROID["GRS_1980",6378137,298.257222101]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]],PROJECTION["Lambert_Conformal_Conic_2SP"],PARAMETER["False_Easting",1968500],PARAMETER["False_Northing",0],PARAMETER["Central_Meridian",-82.5],PARAMETER["Standard_Parallel_1",38.73333333333333],PARAMETER["Standard_Parallel_2",40.03333333333333],PARAMETER["Latitude_Of_Origin",38],UNIT["Foot_US",0.30480060960121924],AUTHORITY["EPSG","102723"]]');
--hey be pay
INSERT into spatial_ref_sys (srid, auth_name, auth_srid, proj4text, srtext) values 
( 928356, 'epsg', 28356, '+proj=utm +zone=56 +south +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs ', 'PROJCS["GDA94 / MGA zone 56",GEOGCS["GDA94",DATUM["Geocentric_Datum_of_Australia_1994",SPHEROID["GRS 1980",6378137,298.257222101,AUTHORITY["EPSG","7019"]],TOWGS84[0,0,0,0,0,0,0],AUTHORITY["EPSG","6283"]],PRIMEM["Greenwich",0,AUTHORITY["EPSG","8901"]],UNIT["degree",0.01745329251994328,AUTHORITY["EPSG","9122"]],AUTHORITY["EPSG","4283"]],UNIT["metre",1,AUTHORITY["EPSG","9001"]],PROJECTION["Transverse_Mercator"],PARAMETER["latitude_of_origin",0],PARAMETER["central_meridian",153],PARAMETER["scale_factor",0.9996],PARAMETER["false_easting",500000],PARAMETER["false_northing",10000000],AUTHORITY["EPSG","28356"],AXIS["Easting",EAST],AXIS["Northing",NORTH]]');
--demonstration standard
INSERT into spatial_ref_sys (srid, auth_name, auth_srid, proj4text, srtext) values 
( 93735, 'epsg', 3735, '+proj=lcc +lat_1=40.03333333333333 +lat_2=38.73333333333333 +lat_0=38 +lon_0=-82.5 +x_0=600000 +y_0=0 +ellps=GRS80 +datum=NAD83 +to_meter=0.3048006096012192 +no_defs ', 'PROJCS["NAD83 / Ohio South (ftUS)",GEOGCS["NAD83",DATUM["North_American_Datum_1983",SPHEROID["GRS 1980",6378137,298.257222101,AUTHORITY["EPSG","7019"]],AUTHORITY["EPSG","6269"]],PRIMEM["Greenwich",0,AUTHORITY["EPSG","8901"]],UNIT["degree",0.01745329251994328,AUTHORITY["EPSG","9122"]],AUTHORITY["EPSG","4269"]],UNIT["US survey foot",0.3048006096012192,AUTHORITY["EPSG","9003"]],PROJECTION["Lambert_Conformal_Conic_2SP"],PARAMETER["standard_parallel_1",40.03333333333333],PARAMETER["standard_parallel_2",38.73333333333333],PARAMETER["latitude_of_origin",38],PARAMETER["central_meridian",-82.5],PARAMETER["false_easting",1968500],PARAMETER["false_northing",0],AUTHORITY["EPSG","3735"],AXIS["X",EAST],AXIS["Y",NORTH]]');
--North West Perth
INSERT into spatial_ref_sys (srid, auth_name, auth_srid, proj4text, srtext) values 
( 928350, 'epsg', 28350, '+proj=utm +zone=50 +south +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs ', 'PROJCS["GDA94 / MGA zone 50",GEOGCS["GDA94",DATUM["Geocentric_Datum_of_Australia_1994",SPHEROID["GRS 1980",6378137,298.257222101,AUTHORITY["EPSG","7019"]],TOWGS84[0,0,0,0,0,0,0],AUTHORITY["EPSG","6283"]],PRIMEM["Greenwich",0,AUTHORITY["EPSG","8901"]],UNIT["degree",0.01745329251994328,AUTHORITY["EPSG","9122"]],AUTHORITY["EPSG","4283"]],UNIT["metre",1,AUTHORITY["EPSG","9001"]],PROJECTION["Transverse_Mercator"],PARAMETER["latitude_of_origin",0],PARAMETER["central_meridian",117],PARAMETER["scale_factor",0.9996],PARAMETER["false_easting",500000],PARAMETER["false_northing",10000000],AUTHORITY["EPSG","28350"],AXIS["Easting",EAST],AXIS["Northing",NORTH]]');

SELECT * FROM spatial_ref_sys WHERE srid= 9102723;
SELECT ST_SRID(the_geom),GeometryType(the_geom),ST_Dimension(the_geom)  FROM wifdemo.uaz_demonstration LIMIT 1;
UPDATE wifdemo.uaz_demonstration SET the_geom  = ST_SetSRID(the_geom, 9102723);
SELECT AddGeometryColumn ('wifdemo','uaz_demonstration','the_geom',9102723,'POLYGON',2);
SELECT * FROM geometry_columns WHERE f_table_name='uaz_demonstration'; 
SELECT * FROM geometry_columns WHERE f_table_schema ='wifdemo'; 
SELECT Populate_Geometry_Columns();
INSERT INTO geometry_columns 
(f_table_catalog,f_table_schema,f_table_name,f_geometry_column,coord_dimension,srid,type)
VALUES('','wifdemo','uaz_demonstration','the_geom',2,93735, 'POLYGON');

ALTER TABLE spatial_ref_sys  ADD CONSTRAINT spatial_ref_sys_srid_check CHECK (srid > 0 AND srid <= 9102724);

CREATE  SCHEMA wifmodel;

-- DROP DATABASE "marcos-geo2";

CREATE DATABASE "marcos-geo2"
  WITH OWNER = marcos;
