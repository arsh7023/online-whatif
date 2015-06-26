SELECT column_name FROM information_schema.columns WHERE table_name ='Demonstration_union';
SELECT COUNT (DISTINCT "AREA") FROM wifdemo."Demonstration_union";
SELECT DISTINCT "LAND_USE" FROM wifdemo."Demonstration_union";
SELECT DISTINCT "CONCENTRIC" FROM wifdemo."Demonstration_union";
SELECT DISTINCT "RADIAL" FROM wifdemo."Demonstration_union";
SELECT DISTINCT "POLITICAL" FROM wifdemo."Demonstration_union";

-- And the same for the other fields!
