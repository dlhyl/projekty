
## Základné informácie

**Programovací jazyk:** JavaScript (Node.js)

## Inštalácia a používanie

**Knižnice (dependencies):**
- express (API server)
- body-parser (Parsovanie parametrov z requestov)
- joi (Validácia údajov pomocou custom schémy)
- pg (postgreSQL)
- node-postgres-named (Podpora pomenovaných parametrov v sql stringu)
- sequelize (ORM framework)
- dotenv (Environment variables)
- postgrator (Migrácia databazy)

**Inštalácia**

Po stiahnutí súborov potrebujete nainštalovať potrebné knižnice.
```
npm install
```

Následne je potrebné vytvoriť súbor ```.env``` a vložiť doň údaje na pripojenie k databáze v tvare:
```
DB_HOST=host
DB_NAME=database_name
DB_USER=username
DB_PASS=password
```

Potom už bude možné spustiť aplikáciu pomocou
```
npm start
```
Po spustení príkazu ```npm start``` sa začne vykonávať pre-script s migráciou
```
D:\dbs\xdlhyl> npm start

> fiit-dbs-xdlhyl@1.0.0 prestart D:\dbs\xdlhyl
> node migrate.js migrate

Migration started for migration companies-migrate
 {
  version: 1,
  action: 'do',
  filename: '001.do.companies-migrate.sql',
  name: 'companies-migrate',
  md5: '7d5563276b2a27093e6135c7a6a59eb3',
  getSql: [Function: getSql]
}
```
Po dokončení migrácie sa automaticky spustí aplikačný server
```
> fiit-dbs-xdlhyl@1.0.0 start D:\dbs\xdlhyl
> node index.js

App running on port 3066.
```
Pre zvrátenie migrácie treba použiť príkaz
```
node migrate rollback
```

## Endpointy

- **[GET]** /v1/health/
- **[GET]** /v1/ov/submissions/
- **[POST]** /v1/ov/submissions/
- **[DELETE]** /v1/ov/submissions/:id
- **[GET]** /v1/companies/

[Controller submissions endpointov](controllers/ovsubmissions.js)
[Controller companies endpointu](controllers/ovcompanies.js)

## ORM Endpointy
- **[GET]** /v2/ov/submissions/
- **[GET]** /v2/companies/]
- **[GET]** /v2/ov/submissions/:id
- **[POST]** /v2/ov/submissions/
- **[PUT]** /v2/ov/submissions/:id
- **[DELETE]** /v2/ov/submissions/:id

Modely tabuliek sú v [db/models/](db/models/)

Inicializácia modelov je v [db/models/init-models.js](db/models/init-models.js)

Pripojenie na databázu a konfigurácia ORM je v [db/orm.js](db/orm.js)

Controller všetkých ORM endpointov je [controllers/ORMcontroller.js](controllers/ORMcontroller.js)
