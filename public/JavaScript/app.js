let app = new Vue({
  el: "#app",
  data: {
    phones: [],
    // Create search bar object to store the users inputs as they type
    searchBar: {
      searchInput: "",
    },
    currentPageNumber: 0,
    numOfItemsPerPage: 6,
    numResults: 0,
    numOfItemsArr: [],
    offsetArr: [],
    productDetails: [],
    compare: [],
    compare2: [],
    shopName: "",
    images: {
      shopImage: "",
    },
  },
  methods: {
    /* Function to compare products based on specific product provided by ID number */
    comparePhones: async function (id) {
      var localApp = this;
      console.log("Comparison of ID: " + id);
      // Make call to recieve all products with a specific ID and store in array
      this.productDetails = (await axios.get("/products/" + id)).data;
      // Make call to recieve all comparison details with a specific ID and store in array
      this.compare = (await axios.get("/comparison/" + id)).data;
      let model = this.compare[0].model;
      let searchUrl = "/comparisonSearch?model=" + model;
      let searchResult = await axios.get(searchUrl);
      localApp.compare2 = searchResult.data.data;

      console.log(this.compare[0].model);
      console.log(JSON.stringify(this.compare));

      console.log(JSON.stringify(searchResult.data.data));
      console.log(localApp.compare2[0].website_url);

      // Store all names of websites in an array
      const websites = ["argos", "freemans", "amazon", "ebay", "very", "john"];
      // Store all images of corresponding websites in array
      const websiteImages = [
        "argos.png",
        "freemans.png",
        "amazon.png",
        "ebay.png",
        "very.png",
        "johnlewis.png",
      ];
      for (let i = 0; i < websites.length; i++) {
        // Check if the website URL of the product the user has selected includes one of the terms in the website array
        if (this.compare[0].website_url.includes(websites[i])) {
          // If so then make first letter of a specific element in the array upper case
          console.log(
            websites[i].charAt(0).toUpperCase() + websites[i].slice(1)
          );
          // Store the specific image that corresponds to the selected website
          console.log(websiteImages[websites.indexOf(websites[i])]);
          if (websites[i] === websites[5]) {
            console.log("John Lewis");
          } else {
            console.log(
              websites[i].charAt(0).toUpperCase() + websites[i].slice(1)
            );
          }
        }
      }
    },
    /* Search for products by retrieving search term by user and using search to check 
            for specific model matching this term */
    search: async function () {
      var localApp = this;
      // Create offset with the current page number multiplied by how many phones are displayed on the page
      let offset = this.currentPageNumber * this.numOfItemsPerPage;
      /* Create URL to use search to search for specific model, search input being the user inputted term. 
                Also with the number of items and the limit calaulated above to provide pagination */
      let searchUrl = `/search?model=${this.searchBar.searchInput}&num_items=${this.numOfItemsPerPage}&offset=${offset}`;

      console.log(searchUrl);

      try {
        // Get results from the search URL provided
        let searchResult = await axios.get(searchUrl);

        // Store all data in phones array
        localApp.phones = searchResult.data.data;
        console.log(JSON.stringify(searchResult.data.data));
        localApp.numResults = searchResult.data.count;
      } catch (ex) {
        console.error(ex);
      }
    },
    /* Function to increase page number and change content */
    nextPage: function () {
      this.currentPageNumber++;

      // Call function to display all products based on user input
      this.search();

      // Disable button if there is no products left in phone array
      if (this.phones.length === 0) {
        document.getElementById("nxtPg").disabled = true;
      }
    },
    /* Function to decrease page number and change content */
    prevPage: function () {
      this.currentPageNumber--;

      // Call function to display all products based on user input
      this.search();

      // Disable button if user is on page 1
      if (this.currentPageNumber < 1) {
        document.getElementById("prevPg").disabled = true;
      } else {
        document.getElementById("prevPg").disabled = false;
      }
    },
  },
  computed: {
    canViewProduct() {
      return true;
    },
  },
}).$mount("#app");
