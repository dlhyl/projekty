const assert = require("assert");
const http = require("http");
var sessionCookie = null;

const request = (url, options = {}, post_data = null) => {
  if (sessionCookie != null) {
    if ("headers" in options) options.headers.Cookie = sessionCookie;
    else options["headers"] = { Cookie: sessionCookie };
  }
  return new Promise(function (res, rej) {
    const req = http
      .request(url, options, (resp) => {
        let data = "";
        resp.on("data", (chunk) => {
          data += chunk;
        });
        resp.on("end", () => {
          if ("set-cookie" in resp.headers) sessionCookie = resp.headers["set-cookie"][0].split(";")[0];
          res({ header: resp.headers, status: resp.statusCode, data: JSON.parse(data) });
        });
      })
      .on("error", (err) => {
        rej(err);
      });

    if ("method" in options && options["method"] == "POST" && post_data != null) {
      req.write(post_data);
    }
    req.end();
  });
};

describe("GET Products", function () {
  const products_url = "http://localhost:8081/products";
  it("Status code is 200.", function (done) {
    request(products_url)
      .then((res) => {
        assert(res.status === 200);
        done();
      })
      .catch(done);
  });

  it("Content type is JSON.", function (done) {
    request(products_url)
      .then((res) => {
        assert(res.header["content-type"].indexOf("application/json") >= 0);
        done();
      })
      .catch(done);
  });

  it("Contains array of 3 products.", function (done) {
    request(products_url)
      .then((res) => {
        assert(res.data.length === 3);
        res.data.forEach((item) => {
          assert("id" in item && "nazov" in item && "obrazok" in item && "cena" in item);
        });
        done();
      })
      .catch(done);
  });

  describe("Add first product to cart. (POST /buy)", function () {
    var response_add, errors_add;
    const add_url = "http://localhost:8081/buy";
    const add_data = JSON.stringify({ product: 1, qty: 1 });
    const add_opts = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Content-Length": add_data.length,
      },
    };

    before("Make POST request.", function (done) {
      request(add_url, add_opts, add_data)
        .then((res) => {
          response_add = res;
          done();
        })
        .catch((err) => {
          errors_add = err;
          done();
        });
    });

    it("Status code is 200.", function (done) {
      assert(response_add.status === 200);
      done();
    });

    it("Product was added to cart.", function (done) {
      assert(response_add.data.data.products.length > 0);
      assert(response_add.data.data.products.filter((item) => item.id == 1).length === 1);
      done();
    });

    it("Cart total has changed.", function (done) {
      assert(response_add.data.data.total > 0);
      done();
    });
  });

  describe("Add second product to cart. (POST /buy)", function () {
    var response_add, errors_add;
    const add_url = "http://localhost:8081/buy";
    const add_data = JSON.stringify({ product: 2, qty: 1 });
    const add_opts = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Content-Length": add_data.length,
      },
    };

    before("Make POST request.", function (done) {
      request(add_url, add_opts, add_data)
        .then((res) => {
          response_add = res;
          done();
        })
        .catch((err) => {
          errors_add = err;
          done();
        });
    });

    it("Status code is 200.", function (done) {
      assert(response_add.status === 200);
      done();
    });

    it("Product was added to cart.", function (done) {
      assert(response_add.data.data.products.length > 0);
      assert(response_add.data.data.products.filter((item) => item.id == 2).length === 1);
      done();
    });

    it("Cart total has changed.", function (done) {
      assert(response_add.data.data.total > 0);
      done();
    });
  });

  describe("Cart (GET /cart)", function () {
    const cart_url = "http://localhost:8081/cart";
    it("Both products are in the cart.", function (done) {
      request(cart_url, { header: { Cookie: sessionCookie } })
        .then((res) => {
          assert(res.status === 200);
          assert(res.data.products.length == 2);
          assert(res.data.products.filter((item) => item.id == 1).length === 1);
          assert(res.data.products.filter((item) => item.id == 2).length === 1);
          done();
        })
        .catch(done);
    });
  });

  describe("Change quantity of product (POST /cart/change)", function () {
    var response, errors;
    const change_url = "http://localhost:8081/cart/change";
    const change_data = JSON.stringify({ product: 1, qty: 5 });
    const change_opts = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Content-Length": change_data.length,
      },
    };

    before("Make POST request.", function (done) {
      request(change_url, change_opts, change_data)
        .then((res) => {
          response = res;
          done();
        })
        .catch((err) => {
          errors = err;
          done();
        });
    });

    it("Status code is 200.", function (done) {
      assert(response.status === 200);
      done();
    });

    it("Quantity of product #1 has changed to 5.", function (done) {
      assert(response.data.data.products.filter((item) => item.id == 1)[0].qty === 5);
      done();
    });
  });

  describe("Make order (POST /order)", function () {
    var response, errors;
    const url = "http://localhost:8081/order";
    const data = JSON.stringify({
      email: "jozko.bugi@gmail.com" + (Math.random() + 1).toString(36).substring(7),
      meno: "Jozef Bugal" + (Math.random() + 1).toString(36).substring(7),
      ulica: "Racianska",
      cislo: "39",
      mesto: "BA",
      psc: "84104",
    });
    const opts = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Content-Length": data.length,
      },
    };

    before("Make POST request.", function (done) {
      request(url, opts, data)
        .then((res) => {
          response = res;
          done();
        })
        .catch((err) => {
          errors = err;
          done();
        });
    });

    it("Status code is 200.", function (done) {
      assert(response.status === 200);
      done();
    });

    it("Received order id.", function (done) {
      assert(response.data.orderid);
      done();
    });

    describe("Order confirmation. (GET /order/:id)", function () {
      var response_order, errors_order;

      before("Make GET request.", function (done) {
        request("http://localhost:8081/order/" + response.data.orderid)
          .then((res) => {
            response_order = res;
            done();
          })
          .catch((err) => {
            errors_order = err;
            done();
          });
      });

      it("Status code is 200.", function (done) {
        assert(response_order.status === 200);
        done();
      });

      it("Order status is 'created'", function (done) {
        assert(response_order.data.data.info.stav === "created");
        done();
      });

      it("Ad has link, image and click counter.", function (done) {
        assert(response_order.data.data.ad.link);
        assert(response_order.data.data.ad.image);
        assert(Number.isInteger(response_order.data.data.ad.pocet_klikov));
        done();
      });

      it("Clicked ad & counter increased by 1.", function (done) {
        request("http://localhost:8081/ad/click")
          .then((res) => {
            assert(res.data.pocet_klikov === response_order.data.data.ad.pocet_klikov + 1);
            done();
          })
          .catch(done);
      });

      describe("Admin page (GET /orders)", function () {
        var response_admin, errors_admin;

        before("Make GET request.", function (done) {
          request("http://localhost:8081/orders")
            .then((res) => {
              response_admin = res;
              done();
            })
            .catch((err) => {
              errors_admin = err;
              done();
            });
        });

        it("Status code is 200.", function (done) {
          assert(response_admin.status === 200);
          done();
        });

        it("Order list is not empty.", function (done) {
          assert(Object.keys(response_admin.data.orders).length > 0);
          done();
        });

        it("Order list contains our created order.", function (done) {
          assert(response_admin.data.orders[response.data.orderid]);
          done();
        });
      });
    });
  });
});
