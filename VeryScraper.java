package eu.ishaan.webscraping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Scrapes phone data from Very
 */
public class VeryScraper extends WebScrape {
    WebScrapeDao dao;
    // Specifies the interval between HTTP requests to the server in seconds.
    private int crawlDelay = 1;
    private int thread;

    /**
     * Initialise DAO to save Very phone data to database
     */
    public VeryScraper() {
        dao = new WebScrapeDao();
        dao.init();


        this.thread = thread;
    }
    // Allows us to shut down our application cleanly
    volatile private boolean runThread = false;

    /** Starts thread which starts the Very Scraper */
    @Override
    public void run() {
        runThread = true;

        //While loop will run until runThread is set to false, after data has been scraped
        while (runThread) {
            System.out.println("Scraper 4 is scraping data from thread number: 4.");
            try {
                // Scrape the phones from very, then stop the thread
                scrapePhones();
                runThread = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Sleep for the crawl delay, which is in seconds
            try {
                sleep(1000 * crawlDelay);// Sleep is in milliseconds, so we need to multiply the crawl delay by 1000
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    /** Scrapes samsung phone data from the Very website */
    void scrapePhones() throws Exception {
        // Name of item that will be scraped
        String itemName1 = "samsung";
        String itemName2 = "phone";

        // Array List to store prices
        ArrayList<String> prices = new ArrayList<String>();

        // For loop to loop through 3 pages on very
        for (int pageNumber = 1; pageNumber <= 3; pageNumber++) {
            System.out.println("******************** " + pageNumber + " ***********************");
            Document doc = Jsoup.connect(
                    "https://www.very.co.uk/e/q/" + itemName1 + "-" + itemName2 + ".end?pageNumber=" + pageNumber)
                    .get();
            // Variable containing CSS selector for all product cards
            Elements prods = doc.select("li.product");

            // Looping through all products in each product card
            for (int i = 0; i < prods.size(); ++i) {
                // Get the product description, price, brand and images
                Elements title = prods.get(i).select("span.productBrandDesc");
                Elements price = prods.get(i).select("dd.productPrice");
                Elements brand = prods.get(i).select("em.productBrand");
                Elements images = prods.get(i).select("img");

                String[] arrOfStr = title.text().split("[-,]");

                List checkMem = Arrays.asList(arrOfStr);

                // Add all prices to array
                prices.add(price.text());

                // Check if prices is empty, if so then load phones with current prices
                if (prices.get(i).isEmpty()) {
                    price = prods.get(i).select("dd.productNowPrice");

                    // Output the data that we have downloaded
                    price(title, price, images, brand, "64GB", " 64GB", " 64GB ", "  64GB", i);
                    price(title, price, images, brand, "128GB", " 128GB", " 128GB ", "  128GB", i);
                    price(title, price, images, brand, "256GB", " 256GB", " 256GB ", "  256GB", i);

                    System.out.println();
                } else {
                    // Load phones with updated prices
                    price(title, price, images, brand, "64GB", " 64GB", " 64GB ", "  64GB", i);
                    price(title, price, images, brand, "128GB", " 128GB", " 128GB ", "  128GB", i);
                    price(title, price, images, brand, "256GB", " 256GB", " 256GB ", "  256GB", i);

                    System.out.println();
                }
            }
        }
    }

    /**
     * Outputs all phone information based on phone prices
     * @param description the description of the phone
     * @param price the price of the phone
     * @param images the image link of the phone image
     * @param brand the brand of the phone
     * @param memory the 1st memory option of the phone
     * @param memory2  the 2nd memory option of the phone
     * @param memory3 the 3rd memory option of the phone
     * @param memory4 the 4th memory option of the phone
     * @param i the index of the phone
     * */
    void price(Elements description, Elements price, Elements images, Elements brand, String memory, String memory2,
            String memory3, String memory4, int i) {
        try {
            // Delimited string array to find all colours in description
            String[] colour = description.text().split("[-,\\s+]");
            List checkMem = Arrays.asList(colour);
            String model = "";
            int startIndex = description.text().indexOf("Galaxy");
            int endIndex = description.text().indexOf("5G");
            // model to retrieve bit of string after Galaxy and 5G
            model = description.text().substring(startIndex, endIndex);
            // Check which memory storage should be assigned to a particular phone
            if (checkMem.contains(memory) || checkMem.contains(memory2) || checkMem.contains(memory3)
                    || checkMem.contains(memory4)) {
                // Outputting all the data
                System.out.println("--------------------------------------------------------");
                System.out.println("MODEL: " + model);
                System.out.println("--------------------------------------------------------");
                System.out.println("DESCRIPTION: " + description.text());
                System.out.println("PRICE: " + price.text());
                System.out.println("MANUFACTURER: " + brand.text());
                System.out.println("IMAGE_URL: " + images.attr("src"));
                System.out.println("COLOUR: " + colour[colour.length - 1]);
                System.out.println("MEMORY: " + memory);

                // Saving the data to the Database
                ProductInfo productInfo = new ProductInfo();
                productInfo.setName(model);
                productInfo.setManufacturer(brand.text());
                productInfo.setProduct_description(description.text());
                productInfo.setProduct_img_url(images.attr("src"));

                PhoneInstance instance = new PhoneInstance();
                instance.setMemory(memory);
                instance.setColour(colour[colour.length - 1]);
                instance.setProductInfo(productInfo);

                Phone phone = new Phone();
                phone.setPhoneInstance(instance);
                phone.setWebsite_url("https://www.very.co.uk/e/q/samsung-phone.end?pageNumber=1");
                phone.setPrice(price.text());
                phone.setWebsite_name("Very");
                phone.setWebsite_img("Images/very.png");

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
