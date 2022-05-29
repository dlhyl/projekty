CREATE TABLE produkty(
    id SERIAL PRIMARY KEY,
    nazov varchar not null,
    obrazok varchar not null,
    cena numeric not null
);

CREATE TABLE zakaznici(
    id SERIAL PRIMARY KEY,
    email varchar UNIQUE,
    meno varchar not null,
    ulica varchar not null,
    cislo varchar not null,
    mesto varchar not null,
    psc varchar not null
);

CREATE TABLE objednavky(
    id SERIAL PRIMARY KEY,
    id_zakaznik integer REFERENCES zakaznici(id),
    stav varchar not null
);

CREATE TABLE objednavka_produkt(
    id SERIAL PRIMARY KEY,
    id_objednavky integer REFERENCES objednavky(id),
    id_produktu integer REFERENCES produkty(id),
    pocet integer not null
);

CREATE TABLE reklama(
    link varchar not null,
    image varchar not null,
    pocet_klikov int not null
);

INSERT INTO produkty(nazov, obrazok, cena)
VALUES ('Hard disk 1TB', 'https://images.pexels.com/photos/117729/pexels-photo-117729.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260', '55.50'),
('Klavesnica RGB', 'https://images.pexels.com/photos/4792716/pexels-photo-4792716.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260', '22.22'),
('Vykonny pocitac', 'https://images.pexels.com/photos/5985264/pexels-photo-5985264.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260', '299.90');

INSERT INTO reklama(link, image, pocet_klikov) VALUES('https://www.alza.sk','https://images.pexels.com/photos/705164/computer-laptop-work-place-camera-705164.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260', 0);

-- Produkty: id, nazov, obrazok, cena
-- Zakaznici: id, e-mail (unikatny), meno, ulica, cislo, mesto , psc
-- Objednavky: zakaznik+produkty + stav objednavky
-- Reklama: link,
-- Seed iba na produkty, 3ks - musia byt v JS "CREATE TABLE produkty", INSERT 3x produkty