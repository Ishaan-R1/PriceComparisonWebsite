//Database code to test
let db = require("../database");

//Server code to test
let server = require("../server");

//Set up Chai library
let chai = require("chai");
let should = chai.should();
let assert = chai.assert;
let expect = chai.expect;

//Set up Chai to test web service
let chaiHttp = require("chai-http");
chai.use(chaiHttp);

//Import the mysql module and create connection pool
const mysql = require("mysql");
const connectionPool = mysql.createPool({
  connectionLimit: 1,
  host: "localhost",
  user: "root",
  password: "",
  database: "price_comparison_db",
  debug: false,
});

//Create all tests for web service only
describe("Web Service Tests", () => {
  //Test GET /products
  describe("/GET products", () => {
    it("should GET all the products", (done) => {
      chai
        .request(server)
        .get("/products")
        .end((err, response) => {
          //Check the status code
          response.should.have.status(200);

          //JSON converted to JavaScript object
          let responseObject = JSON.parse(response.text);

          //Check object has appropriate properties
          if (responseObject.length > 1) {
            responseObject[0].should.have.property("id");
            responseObject[0].should.have.property("model");
            responseObject[0].should.have.property("description");
            responseObject[0].should.have.property("img_url");
            responseObject[0].should.have.property("manufacturer");
          }

          //End test
          done();
        });
    });
  });

  //Test GET /comparison
  describe("/GET comparison", () => {
    it("should GET all the comparison details", (done) => {
      chai
        .request(server)
        .get("/comparison/1")
        .end((err, response) => {
          //Check the status code
          response.should.have.status(200);

          //JSON converted to JavaScript object
          let responseObject = JSON.parse(response.text);

          //Check object has appropriate properties
          if (responseObject.length > 1) {
            responseObject[0].should.have.property("id");
            responseObject[0].should.have.property("model");
            responseObject[0].should.have.property("device_memory");
            responseObject[0].should.have.property("price");
            responseObject[0].should.have.property("website_url");
          }

          //End test
          done();
        });
    });
  });
  //Test GET /comparison/*
  describe("/GET comparison", () => {
    it("should GET the comparison of a specific ID", (done) => {
      chai
        .request(server)
        .get("/comparison")
        .end((err, response) => {
          //Check the status code
          response.should.have.status(200);

          //JSON converted to JavaScript object
          let responseObject = JSON.parse(response.text);

          //Check object has appropriate properties
          if (responseObject.length > 1) {
            responseObject[0].should.have.property("id");
            responseObject[0].should.have.property("model");
            responseObject[0].should.have.property("device_memory");
            responseObject[0].should.have.property("price");
            responseObject[0].should.have.property("website_url");
          }

          //End test
          done();
        });
    });
  });
  //Test GET /search/*
  describe("/GET search?model=", () => {
    it('should GET all the search results with the term "flip"', (done) => {
      chai
        .request(server)
        .get("/search?model=flip")
        .end((err, response) => {
          //Check the status code
          response.should.have.status(200);

          //JSON converted to JavaScript object
          let responseObject = JSON.parse(response.text);

          //Check object has appropriate properties
          if (responseObject.length > 1) {
            responseObject[0].should.have.property("id");
            responseObject[0].should.have.property("model");
            responseObject[0].should.have.property("description");
            responseObject[0].should.have.property("img_url");
          }

          //End test
          done();
        });
    });
  });
});
//Create all tests for database only
describe("Database Tests", () => {
  //Mocha test for getAllProducts method in database.
  describe("#getProducts", () => {
    it("should return all of the phones in the database", (done) => {
      //Create Mock response object to test
      let response = {};

      response.status = (errorCode) => {
        return {
          json: (errorMessage) => {
            console.log(
              "Error code: " + errorCode + "; Error message: " + errorMessage
            );
            assert.fail(
              "Error code: " + errorCode + "; Error message: " + errorMessage
            );
            done();
          },
        };
      };

      // send result to mock object
      response.send = (result) => {
        // result converted to JavaScript object
        let responseObject = JSON.parse(result);

        //Check object has appropriate properties
        if (responseObject.length > 1) {
          responseObject[0].should.have.property("id");
          responseObject[0].should.have.property("model");
          responseObject[0].should.have.property("description");
          responseObject[0].should.have.property("img_url");
          responseObject[0].should.have.property("manufacturer");
        }

        //End test
        done();
      };

      //Call function to test
      db.getAllPhones(response);
    });
  });
});
