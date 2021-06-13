-- ## Mapovanie stlpcov ##
-- cin -> cin
-- name -> corporate_body_name
-- br_section -> br_section
-- last_update -> updated_at
-- address_line -> street + ', ' + postal_code + ' ' + city

-- ## PORADIE TABULIEK ##
-- or_podanie_issues
-- likvidator_issues
-- konkurz_vyrovnanie_issues
-- znizenie_imania_issues
-- konkurz_restrukturalizacia_actors


CREATE TABLE IF NOT EXISTS ov.companies (
	cin bigint PRIMARY KEY,
	name varchar,
	br_section varchar,
	address_line varchar,
	last_update timestamp without time zone,
	created_at timestamp without time zone,
	updated_at timestamp without time zone
);

-- ov.or_podanie_issues
INSERT INTO ov.companies(cin, name, br_section, address_line, last_update, created_at, updated_at) 
SELECT cin, corporate_body_name, br_section, CONCAT(street, ', ',postal_code,' ',city), updated_at, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM(
    SELECT *,
        rank() OVER (PARTITION BY cin ORDER BY updated_at DESC) 
    FROM ov.or_podanie_issues
	WHERE cin is not NULL
)s
WHERE rank = 1
ON CONFLICT (cin) DO NOTHING;


-- likvidator_issues
INSERT INTO ov.companies(cin, name, br_section, address_line, last_update, created_at, updated_at) 
SELECT cin, corporate_body_name, br_section, CONCAT(street, ', ',postal_code,' ',city), updated_at, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM(
    SELECT *,
        rank() OVER (PARTITION BY cin ORDER BY updated_at DESC) 
    FROM ov.likvidator_issues
	WHERE cin is not NULL
)s
WHERE rank = 1
ON CONFLICT (cin) DO NOTHING;


-- konkurz_vyrovnanie_issues
INSERT INTO ov.companies(cin, name, br_section, address_line, last_update, created_at, updated_at) 
SELECT cin, corporate_body_name, '', CONCAT(street, ', ',postal_code,' ',city), updated_at, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM(
    SELECT *,
        rank() OVER (PARTITION BY cin ORDER BY updated_at DESC) 
    FROM ov.konkurz_vyrovnanie_issues
	WHERE cin is not NULL
)s
WHERE rank = 1
ON CONFLICT (cin) DO NOTHING;


-- znizenie_imania_issues
INSERT INTO ov.companies(cin, name, br_section, address_line, last_update, created_at, updated_at) 
SELECT cin, corporate_body_name, br_section, CONCAT(street, ', ',postal_code,' ',city), updated_at, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM(
    SELECT *,
        rank() OVER (PARTITION BY cin ORDER BY updated_at DESC) 
    FROM ov.znizenie_imania_issues
	WHERE cin is not NULL
)s
WHERE rank = 1
ON CONFLICT (cin) DO NOTHING;


-- konkurz_restrukturalizacia_actors
INSERT INTO ov.companies(cin, name, br_section, address_line, last_update, created_at, updated_at) 
SELECT cin, corporate_body_name, '', CONCAT(street, ', ',postal_code,' ',city), updated_at, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM(
    SELECT *,
        rank() OVER (PARTITION BY cin ORDER BY updated_at DESC) 
    FROM ov.konkurz_restrukturalizacia_actors
	WHERE cin is not NULL
)s
WHERE rank = 1
ON CONFLICT (cin) DO NOTHING;


-- FOREIGN KEYS
-- or_podanie_issues
ALTER TABLE ov.or_podanie_issues
DROP CONSTRAINT IF EXISTS fk_or_podanie_issues_companies,
ADD COLUMN IF NOT EXISTS company_id bigint,
ADD CONSTRAINT fk_or_podanie_issues_companies 
FOREIGN KEY (company_id) 
REFERENCES ov.companies(cin);

-- likvidator_issues
ALTER TABLE ov.likvidator_issues
DROP CONSTRAINT IF EXISTS fk_likvidator_issues_companies,
ADD COLUMN IF NOT EXISTS company_id bigint,
ADD CONSTRAINT fk_likvidator_issues_companies 
FOREIGN KEY (company_id) 
REFERENCES ov.companies(cin);

-- konkurz_vyrovnanie_issues
ALTER TABLE ov.konkurz_vyrovnanie_issues
DROP CONSTRAINT IF EXISTS fk_konkurz_vyrovnanie_issues_companies,
ADD COLUMN IF NOT EXISTS company_id bigint,
ADD CONSTRAINT fk_konkurz_vyrovnanie_issues_companies 
FOREIGN KEY (company_id) 
REFERENCES ov.companies(cin);

-- znizenie_imania_issues
ALTER TABLE ov.znizenie_imania_issues
DROP CONSTRAINT IF EXISTS fk_znizenie_imania_issues_companies,
ADD COLUMN IF NOT EXISTS company_id bigint,
ADD CONSTRAINT fk_znizenie_imania_issues_companies 
FOREIGN KEY (company_id) 
REFERENCES ov.companies(cin);

-- konkurz_restrukturalizacia_actors
ALTER TABLE ov.konkurz_restrukturalizacia_actors
DROP CONSTRAINT IF EXISTS fk_konkurz_restrukturalizacia_actors_companies,
ADD COLUMN IF NOT EXISTS company_id bigint,
ADD CONSTRAINT fk_konkurz_restrukturalizacia_actors_companies 
FOREIGN KEY (company_id) 
REFERENCES ov.companies(cin);

-- FILL COLUMN company_id
UPDATE ov.or_podanie_issues
SET company_id = cin;

UPDATE ov.likvidator_issues
SET company_id = cin;

UPDATE ov.konkurz_vyrovnanie_issues
SET company_id = cin;

UPDATE ov.znizenie_imania_issues
SET company_id = cin;

UPDATE ov.konkurz_restrukturalizacia_actors
SET company_id = cin;

-- INDEXY na vyhladavanie company_id
CREATE INDEX IF NOT EXISTS or_podanie_issues_company_id ON ov.or_podanie_issues(company_id);
CREATE INDEX IF NOT EXISTS likvidator_issues_company_id ON ov.likvidator_issues(company_id);
CREATE INDEX IF NOT EXISTS konkurz_vyrovnanie_issues_company_id ON ov.konkurz_vyrovnanie_issues(company_id);
CREATE INDEX IF NOT EXISTS znizenie_imania_issues_company_id ON ov.znizenie_imania_issues(company_id);
CREATE INDEX IF NOT EXISTS konkurz_restrukturalizacia_actors_company_id ON ov.konkurz_restrukturalizacia_actors(company_id);