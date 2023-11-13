package eu.ishaan.webscraping;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Scrapes phone data from John Lewis
 */
public class JohnLewisScraper extends WebScrape {
    WebScrapeDao dao;
    // Specifies the interval between HTTP requests to the server in seconds.
    private int crawlDelay = 1;
    private int thread;
    /**
     * Initialise DAO to save John Lewis phone data to database
     */
    public JohnLewisScraper() {
        dao = new WebScrapeDao();
        dao.init();

        this.thread = thread;
    }
    // Allows application to be shut down cleanly
    volatile private boolean runThread = false;
    /** Starts thread which starts the John Lewis Scraper */
    @Override
    public void run(){
        runThread = true;
        
        //While loop will keep running until runThread is set to false;
        while(runThread){
            System.out.println("Scraper 1 is scraping data from thread number: 1.");
                try {
                    // Scrape the phones from john lewis, then stop the thread
                    scrapePhones();
                    runThread = false;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            // Sleep for the crawl delay
            try{
                sleep(1000 * crawlDelay);// Set crawl delay by 1000
            }
            catch(InterruptedException ex){
                System.err.println(ex.getMessage());
            }
        }                   
    }

    /** Scrapes samsung phone data from the John Lewis website */
    void scrapePhones() throws Exception {
        try {
            // Get the data
            Document doc = Jsoup.connect(
                            "https://www.johnlewis.com/browse/electricals/mobile-phones-accessories/view-all-mobile-phones/samsung/_/N-a8vZ1z13z13")
                    .get();

            // Variable containing CSS selector for all product cards
            Elements prods = doc.select("article.product-card_c-product-card__UAdsG");

            // Looping through all products in each product card
            for (int i = 0; i < prods.size(); ++i) {
                // Get the product description, price. brand, image and colour
                Elements title = prods.get(i).select("span.title_title__desc__ZCdyp");
                Elements price = prods.get(i).select("span.price_price__now__3B4yM");
                Elements brand = prods.get(i).select("span.title_title__brand__UX8j9");
                Elements img_url = prods.get(i).select("src.image_image__jhaxk");
                Elements images = prods.get(i).select("img");
                Elements colour = prods.get(i).select("a[href][title]");

                // Split description string into different bits
                String[] arrOfStr = title.text().split(",");
                // Output the data of all the phone attributes
                System.out.println("--------------------------------------------------------");
                System.out.println("MODEL: " + arrOfStr[0]);
                System.out.println("--------------------------------------------------------");
                System.out.println("DESCRIPTION: " + title.text());
                System.out.println("PRICE: " + price.text());
                System.out.println("MEMORY: " + arrOfStr[arrOfStr.length - 1]);
                System.out.println("MANUFACTURER: " + brand.text());
                System.out.println("IMAGE: " + images.attr("src"));
                System.out.println("COLOUR: " + colour.attr("title"));
                System.out.println();

                // Saving the data to the Database
                ProductInfo productInfo = new ProductInfo();
                productInfo.setName(arrOfStr[0]);
                productInfo.setManufacturer(brand.text());
                productInfo.setProduct_description(title.text());
                productInfo.setProduct_img_url(images.attr("src"));

                PhoneInstance instance = new PhoneInstance();
                instance.setMemory(arrOfStr[arrOfStr.length - 1]);
                instance.setColour(colour.attr("title"));
                instance.setProductInfo(productInfo);

                Phone phone = new Phone();
                phone.setPhoneInstance(instance);
                phone.setWebsite_url("https://www.johnlewis.com/browse/electricals/mobile-phones-accessories/view-all-mobile-phones/samsung/_/N-a8vZ1z13z13");
                phone.setPrice(price.text());
                phone.setWebsite_name("John Lewis");
                phone.setWebsite_img("Images/johnlewis.png");

                try {
                    // Save all data to the database which is all linked to the phone table
                    dao.saveToDb(phone);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //method to terminate thread.
    public void stopThread(){
        runThread = false;
    }

}
