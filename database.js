//Import the mysql module
const mysql = require('mysql');

//Create a connection object with the user details
const connectionPool = mysql.createPool({
    connectionLimit: 1,
    host: "localhost",
    user: "root",
    password: "",
    database: "price_comparison_db",
    debug: false
});


/** Returns a promise to get the total number of products */
module.exports.getProductCount = async ()=> {
    // SQL query to get number of all products
    const sql = "SELECT COUNT(*) FROM product_info";

    // Run query
    let result = await runQuery(sql);

    // Extract the data we need from the result
    return result[0]["COUNT(*)"];
}
//Gets all phones
exports.getAllPhones = (response) => {
    //SQL query to select all phones
    let sql = "SELECT * FROM product_info";

    //Execute query 
    connectionPool.query(sql, (err, result) => {
        if (err){//Checking for errors
            let errMsg = "{Error: " + err + "}";
            console.error(errMsg);
            response.status(400).json(errMsg);
        }
        else{//Result returned using JSON format 
            response.send(JSON.stringify(result))
        }
    });
};

/** Searching for phone model based on specific search term */
module.exports.getSearch = async (model, numItems, offset) => {
    // SQL query to show all the product info details where a user input matches a result of a model in the database
    let sql = "SELECT product_info.id, product_info.model, product_info.description, product_info.img_url FROM product_info WHERE product_info.model LIKE '%" + model + "%' ";

    // Check if numItems or offset has been provided, if it has then run query to provide limit and offset
    if(numItems !== undefined && offset !== undefined ){
        sql = sql + "ORDER BY product_info.id LIMIT " + numItems + " OFFSET " + offset;
    }

    //Run query
    return runQuery(sql);
}
// Search for a specific phone model and provide the comparison details similar to this model
module.exports.getComparisonSearch = async (model, numItems, offset) => {
    /* SQL query to select data from different tables in order to provide comparison details. 
        If user selects specific model to compare, then display the comparison details of similar items. */
    const sql = "SELECT product_info.id, product_info.model,phone_instance.memory, phone_instance.colour, phone.prices, phone.website_url, phone.website_name, phone.website_img FROM" + 
    "((phone INNER JOIN phone_instance ON phone.phone_instance_id=phone_instance.id)" +
    "INNER JOIN product_info ON phone_instance.product_info_id=product_info.id) WHERE product_info.model LIKE '%" + model + "%'";

    // Check if numItems or offset has been provided, if it has then run query to provide limit and offset
    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY product_info.id LIMIT " + numItems + " OFFSET " + offset;
    }

    // Run query
    return runQuery(sql);
}

/** Returns all the products and provides option to give number of items and offset to enable pagination */
module.exports.getProducts = (numItems, offset) => {
    // SQL query to select all items from product_info table
    let sql = "SELECT * FROM product_info ";

    // Check if numItems or offset has been provided, if it has then run query to provide limit and offset
    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY product_info.id LIMIT " + numItems + " OFFSET " + offset;
    }

    // Run query
    return runQuery(sql);
}

/** Returns all the comparison details and provides option to give number of items and offset to enable pagination */
module.exports.getComparisons = (numItems, offset) => {
    /* SQL query to select data from different tables in order to provide comparison details. */
    let sql = "SELECT product_info.id, product_info.model,phone_instance.memory, phone_instance.colour, phone.prices, phone.website_url "+
    "FROM ((phone INNER JOIN phone_instance ON phone.phone_instance_id=phone_instance.id) "+
    "INNER JOIN product_info ON phone_instance.product_info_id=product_info.id)";

    // Check if numItems or offset has been provided, if it has then run query to provide limit and offset
    if(numItems !== undefined && offset !== undefined) {
        sql = sql + "ORDER BY phone.id LIMIT " + numItems + " OFFSET " + offset; 
    }
    // Run query
    return runQuery(sql)
}
/** Get comparison details on a single product based on product ID */
module.exports.getComparison = (prodId) => {
    /* SQL query to select data from different tables in order to provide comparison details. 
        Selects specific product based on ID*/
    const sql = "SELECT product_info.id, product_info.model,phone_instance.memory, phone_instance.colour, phone.prices, phone.website_url FROM" + 
    "((phone INNER JOIN phone_instance ON phone.phone_instance_id=phone_instance.id)" +
    "INNER JOIN product_info ON phone_instance.product_info_id=product_info.id) WHERE product_info.id=" + prodId;

    // Run SQL query
    return runQuery(sql);
}
/** Get product details on a single product based on product ID */
module.exports.getProduct = (prodId) => {
    const sql = "SELECT * FROM product_info WHERE id=" + prodId;
    return runQuery(sql);
}
/** Runs a query */
async function runQuery(sql){
    // promise containing query
    let queryPromise = new Promise( (resolve, reject)=> {
        //Run the query
        connectionPool.query(sql, function (err, result) {
            //Check for errors
            if (err) {
                // ifthere are errors, then promise is rejected
                reject(err);
            }

            //Resolve promise with data from database.
            resolve(result);
        });
    });

    //Return promise
    return queryPromise;
}



