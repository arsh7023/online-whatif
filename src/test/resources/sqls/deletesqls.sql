VACUUM ANALYZE wifdemo.uaz_demonstration;
--deletefrom information
DELETE FROM wifmodel.factortyperating;
DELETE FROM wifmodel.factorimportance;
DELETE FROM wifmodel.suitabilityrules_allocationlu;
DELETE FROM wifmodel.suitabilityrules;
DELETE FROM wifmodel.factortype;
DELETE FROM wifmodel.factor;
DELETE FROM wifmodel.allocationlu;
DELETE FROM wifmodel.suitabilitylu;
DELETE FROM wifmodel.suitabilityscenario;
DELETE FROM wifmodel.wifproject;
DELETE FROM wifmodel.arearequirement;
DELETE FROM wifmodel.wifproject WHERE id=81;

DROP SCHEMA wifmodel CASCADE;
DROP TABLE wifdemo.wif_* CASCADE;
DROP TABLE wifdemo.uaz_demonstration CASCADE;
CREATE SCHEMA wifmodel AUTHORIZATION whatif;
  
DELETE FROM  wifmodel.factorrating  WHERE id>105;
DELETE FROM  wifmodel.allocationlu  WHERE id=126;

DROP VIEW wifdemo.uaz_demonstration_score_1;


DELETE FROM wifmodel.;

-- TO DROP MULTIPLE TABLES, I HAVE TO UPDATE TO USE WILDCARDS wif_*
CREATE OR REPLACE FUNCTION footgun(IN _schema TEXT, IN _parttionbase TEXT) 
RETURNS void 
LANGUAGE plpgsql
AS
$$
DECLARE
    row     record;
BEGIN
    FOR row IN 
        SELECT
            table_schema,
            table_name
        FROM
            information_schema.tables
        WHERE
            table_type = 'BASE TABLE'
        AND
            table_schema = _schema
        AND
            table_name ILIKE (_parttionbase || '%')
    LOOP
        EXECUTE 'DROP TABLE ' || quote_ident(row.table_schema) || '.' || quote_ident(row.table_name);
        RAISE INFO 'Dropped table: %', quote_ident(row.table_schema) || '.' || quote_ident(row.table_name);
    END LOOP;
END;
$$;

SELECT footgun('wifdemo', 'wif_');