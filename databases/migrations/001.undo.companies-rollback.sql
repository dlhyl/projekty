ALTER TABLE ov.or_podanie_issues DROP IF EXISTS company_id;
ALTER TABLE ov.likvidator_issues DROP IF EXISTS company_id;
ALTER TABLE ov.konkurz_vyrovnanie_issues DROP IF EXISTS company_id;
ALTER TABLE ov.znizenie_imania_issues DROP IF EXISTS company_id;
ALTER TABLE ov.konkurz_restrukturalizacia_actors DROP IF EXISTS company_id;

DROP TABLE IF EXISTS ov.companies;
