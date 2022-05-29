const e = require("express");
const express = require("express");
const session = require("express-session");
const appPort = 8081;
const { pool } = require("./db");
const app = express();
const sessionParser = session({
  secret: "vavjs-zadanie-3",
  saveUninitialized: true,
  resave: true,
});
const sessions = new Map();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(function (req, res, next) {
  res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
  res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  res.setHeader("Access-Control-Allow-Credentials", true);
  if (req.method === "OPTIONS") {
    res.header("Access-Control-Allow-Methods", "PUT, POST, PATCH, DELETE, GET");
    return res.status(200).json({});
  }
  next();
});
app.use(sessionParser);

app.get("/orders", async (req, res) => {
  const query = `
    SELECT objednavky.*, objednavka_produkt.*, produkty.*,zakaznici.*
    FROM objednavky
    JOIN objednavka_produkt ON objednavky.id = objednavka_produkt.id_objednavky
    JOIN produkty ON produkty.id = objednavka_produkt.id_produktu
    JOIN zakaznici ON zakaznici.id = objednavky.id_zakaznik;
  `;
  const data = await pool.query(query);
  const result = {};
  const key = "id_objednavky";
  data.rows.forEach((obj) => {
    result[obj[key]] = obj[key] in result ? result[obj[key]].concat([obj]) : [obj];
  });

  const queryReklama = `
    SELECT *
    FROM reklama;
  `;
  const dataReklama = await pool.query(queryReklama);

  res.json({ orders: result, reklama: dataReklama.rows[0] });
});

app.get("/products", async (req, res) => {
  const query = `
    SELECT *
    FROM produkty;
  `;
  const data = await pool.query(query);
  res.json(data.rows);
});

app.get("/pay/:id", async (req, res) => {
  const query = `
    UPDATE objednavky
    SET stav = 'paid'
    WHERE id = $1;
  `;
  const data = await pool.query(query, [req.params.id]);
  res.json(data.rows);
});

app.get("/cart", (req, res) => {
  var cart = sessions.get(req.sessionID);
  if (typeof cart === "undefined" || cart === null) {
    cart = { products: [], total: 0 };
    sessions.set(req.sessionID, cart);
  }
  return res.json(cart);
});

app.post("/cart/change", async (req, res) => {
  var cart = sessions.get(req.sessionID);
  if (typeof cart === "undefined" || cart === null) {
    return res.json({ status: "error" });
  }

  const product = Number(req.body.product);
  const qty = Number(req.body.qty);

  const cart_product = cart.products.filter((item) => item.id === product);
  if (cart_product.length > 0) {
    cart.total += cart_product[0].cena * (qty - cart_product[0].qty);
    cart_product[0].qty = qty;
    if (cart_product[0].qty === 0) {
      cart.products.splice(cart.products.indexOf(cart_product[0]), 1);
    }
    return res.json({ status: "ok", data: cart });
  }
  return res.json({ status: "error" });
});

app.get("/ad/click", async (req, res) => {
  const query = `
    UPDATE reklama
    SET pocet_klikov = pocet_klikov+1
    RETURNING *;
  `;
  const data = await pool.query(query);
  res.json(data.rows[0]);
});

app.post("/ad", async (req, res) => {
  const query = `
    UPDATE reklama
    SET link = $1, image = $2;
  `;
  await pool.query(query, [req.body.link, req.body.image]);
  res.json({ link: req.body.link, image: req.body.image });
});

app.get("/order/:id", async (req, res) => {
  const orderid = req.params.id;
  const query = `
    SELECT p.*, op.pocet
    FROM objednavka_produkt op 
    JOIN produkty p
    ON op.id_produktu = p.id
    WHERE op.id_objednavky = $1;
  `;
  const data = await pool.query(query, [orderid]);
  const queryI = `
    SELECT z.*,o.stav, o.id as orderID, 
      (SELECT SUM(p.cena * op.pocet)
      FROM objednavka_produkt op 
      JOIN produkty p
      ON op.id_produktu = p.id
      WHERE op.id_objednavky = $1) as total
    FROM objednavky o
    JOIN zakaznici z
    ON z.id = o.id_zakaznik
    WHERE o.id = $1;
  `;
  const dataI = await pool.query(queryI, [orderid]);

  const queryAd = `SELECT * FROM reklama;`;
  const dataAd = await pool.query(queryAd);

  res.json({ status: "ok", data: { products: data.rows, info: dataI.rows[0], ad: dataAd.rows[0] } });
});

app.post("/order", async (req, res) => {
  const cart = sessions.get(req.sessionID);
  const email = req.body.email;
  const name = req.body.meno;
  const street = req.body.ulica;
  const streetNo = req.body.cislo;
  const city = req.body.mesto;
  const zipcode = req.body.psc;

  if (typeof cart === "undefined" || cart === null) {
    return res.json({ status: "error", message: "session error" });
  }
  if (cart.products.length === 0) {
    return res.json({ status: "error", message: "cart empty" });
  }

  const queryZakaznik = `
  INSERT INTO zakaznici(email, meno, ulica, cislo, mesto, psc) VALUES ($1,$2,$3,$4,$5,$6) ON CONFLICT ON CONSTRAINT zakaznici_email_key DO NOTHING RETURNING *;
`;
  const dataZakaznik = await pool.query(queryZakaznik, [email, name, street, streetNo, city, zipcode]);

  if (dataZakaznik.rows.length === 0) {
    return res.json({ status: "error", message: "email not unique!" });
  }

  const customer = dataZakaznik.rows[0];
  const queryObjednavka = `INSERT INTO objednavky(id_zakaznik, stav) VALUES ($1,'created') RETURNING *;`;
  const dataObjednavka = await pool.query(queryObjednavka, [customer.id]);
  const objednavkaID = dataObjednavka.rows[0].id;
  const queryObjednavkaProdukty = `INSERT INTO objednavka_produkt(id_objednavky,id_produktu,pocet) VALUES ${cart.products
    .map((product) => {
      return "(" + objednavkaID + "," + product.id + "," + product.qty + ")";
    })
    .join(",")} RETURNING *;`;
  const dataObjadnavkaProdukty = await pool.query(queryObjednavkaProdukty);
  sessions.delete(req.sessionID);
  return res.json({ status: "ok", orderid: objednavkaID });
});

app.post("/buy", async (req, res) => {
  var cart = sessions.get(req.sessionID);
  if (typeof cart === "undefined" || cart === null) {
    cart = { products: [], total: 0 };
    sessions.set(req.sessionID, cart);
  }
  const product = Number(req.body.product);
  const qty = Number(req.body.qty);
  const query = `
    SELECT *
    FROM produkty
    WHERE id = $1;
  `;
  const data = await pool.query(query, [product]);
  if (data.rows.length === 1 && qty) {
    const cart_product = cart.products.filter((item) => item.id === product);
    if (cart_product.length > 0) {
      cart.total += parseFloat(cart_product[0].cena) * (qty - cart_product[0].qty);
      cart_product[0].qty = qty;
    } else {
      cart.products.push({ ...data.rows[0], ...{ qty } });
      cart.total += qty * parseFloat(data.rows[0].cena);
    }
    return res.json({ status: "ok", data: cart });
  }
  return res.json({ status: "error" });
});

app.listen(appPort, () => {
  console.log(`App running on port ${appPort}.`);
});
