package eu.ishaan.webscraping;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;

/**
 * Scrapes phone data from Argos
 */
public class ArgosScraper extends WebScrape {
    WebScrapeDao dao;
    // Specifies the interval between HTTP requests to the server in seconds.
    private int crawlDelay = 1;
    private int thread;

    /**
     * Initialise DAO to save Argos phone data to database
     */
    public ArgosScraper() {
        dao = new WebScrapeDao();
        dao.init();

        this.thread = thread;
    }

    // Allows application to be shut down cleanly
    volatile private boolean runThread = false;

    /** Starts thread which starts the Argos Scraper */
    @Override
    public void run() {
        runThread = true;

        // While loop will run until runThread is set to false, after data has been scraped
        while (runThread) {
            System.out.println("Scraper 3 is scraping data from thread number: 3.");
            try {
                // Scrape the phones from argos, then stop the thread
                scrapePhones();
                runThread = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Sleep for the crawl delay
            try {
                Thread.sleep(1000 * crawlDelay);// Set crawl delay by 1000
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    /** Scrapes samsung phone data from the Argos website */
    void scrapePhones() throws Exception {
        try {
            // fetch the document over HTTP
            Document doc = Jsoup.connect(
                            "https://www.argos.co.uk/browse/technology/mobile-phones-and-accessories/sim-free-phones/c:30147/brands:samsung?tag=ar:brands:samsung-mobiles:header:shop-all")
                    .get();
            // Variable containing CSS selector for all product cards
            Elements prods = doc.select("div.styles__LazyHydrateCard-sc-1rzb1sn-0");

            String memory = "";
            // Looping through all products in each product card
            for (int i = 0; i < prods.size(); ++i) {
                // Get the product description, price, brand and images
                Elements description = prods.get(i).select("a.ProductCardstyles__Title-h52kot-12");
                Elements price = prods.get(i).select("div.ProductCardstyles__PriceText-h52kot-15");
                Elements img_url = prods.get(i).select("div.styles__LazyHydrateCard-sc-1rzb1sn-0 img");
                Elements images = prods.get(i).select("img");

                String prodTitle = description.text();

                // Output the data based on the memory each phone holds
                memory(description, price, images, "64GB", i);
                memory(description, price, images, "128GB", i);
                memory(description, price, images, "256GB", i);
            }

            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param description the description of the phone
     * @param price the price of the phone
     * @param images the image link of the phone image
     * @param memory the memory of the phone
     * @param i the index
     */
    void memory(Elements description, Elements price, Elements images, String memory, int i) {
        try {
            // Array of colours by using delimiters to find colour in description
            String[] arrOfColours = description.text().split("[-\\s+]");
            // Array of manufacturer by using delimiters to find phones manufacturer in description
            String[] arrOfTitle = description.text().split("[\\s+]");
            // Create array of all phone models
            List<String> list = Arrays.asList(arrOfTitle);
            String model = "";
            // Create model of all text between the start of the name and the phone memory
            int startIndex = description.text().indexOf("Samsung");
            int endIndex = description.text().indexOf(memory);
            model = description.text().substring(startIndex, endIndex);

            // Check which memory storage option list of phones contains
            if (list.contains(memory)) {
                System.out.println("--------------------------------------------------------");
                System.out.println(i + "    MODEL: " + model);
                System.out.println("--------------------------------------------------------");
                System.out.println("DESCRIPTION: " + description.text());
                System.out.println("PRICE: " + price.text());
                System.out.println("IMAGE: " + images.attr("src"));
                System.out.println("MANUFACTURER: " + arrOfTitle[2]);
                System.out.println("MEMORY: " + memory);
                System.out.println("COLOUR " + arrOfColours[arrOfColours.length - 1]);
                System.out.println();

                // Saving the data to the Database
                ProductInfo productInfo = new ProductInfo();
                productInfo.setName(model);
                productInfo.setManufacturer(arrOfTitle[2]);
                productInfo.setProduct_description(description.text());
                productInfo.setProduct_img_url(images.attr("src"));

                PhoneInstance instance = new PhoneInstance();
                instance.setMemory(memory);
                instance.setColour(arrOfColours[arrOfColours.length - 1]);
                instance.setProductInfo(productInfo);

                Phone phone = new Phone();
                phone.setPhoneInstance(instance);
                phone.setWebsite_url("https://www.argos.co.uk/browse/technology/mobile-phones-and-accessories/sim-free-phones/c:30147/brands:samsung?tag=ar:brands:samsung-mobiles:header:shop-all");
                phone.setPrice(price.text());
                phone.setWebsite_name("Argos");
                phone.setWebsite_img("Images/argos.png");

                try {
                    // Save all data to the database which is all linked to the phone table
                    dao.saveToDb(phone);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {

        }
    }
}
