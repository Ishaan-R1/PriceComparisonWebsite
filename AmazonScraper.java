package eu.ishaan.webscraping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Scrapes phone data from Amazon
 */
public class AmazonScraper extends WebScrape {
    WebScrapeDao dao;
    private int crawlDelay = 2;
    private int thread;

    /**
     * Initialise DAO to save Amazon phone data to database
     */
    public AmazonScraper() {
        dao = new WebScrapeDao();
        dao.init();

        this.thread = thread;
    }
    // Allows us to shut down our application cleanly
    volatile private boolean runThread = false;

    /** Starts thread which starts the Amazon Scraper */
    @Override
    public void run() {
        runThread = true;

        //While loop will run until runThread is set to false, after data has been scraped
        while (runThread) {
            System.out.println("Scraper 2 is scraping data from thread number: 2.");
            try {
                // Scrape the phones from amazon, then stop the thread
                scrapeAmazon();
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

    /** Scrapes samsung phone data from the Amazon website */
    void scrapeAmazon() throws Exception {
        // Name of item that will be scraped
        String itemName1 = "samsung";
        String itemName2 = "phones";

        // For loop to loop through a certain amount of pages on Amazon, in this case there are 20 pages
        for (int j = 2; j <= 40; j++) {
            System.out.println("******************************************** " + j
                    + " ******************************************************");

            // Using JSoup to connect to the specific web page from Amazon
            Document doc = Jsoup
                    // Amazon scraper sometimes runs with or without userAgent
                    .connect("https://www.amazon.co.uk/s?k=samsung+phones&i=electronics&rh=n%3A560798%2Cp_89%3ASamsung&dc&page="+j+"&crid=59QN2F2Z1K6E&qid=1671057160&rnid=1632651031&sprefix=samsung+phones%2Caps%2C73&ref=sr_pg_"+j)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                    .maxBodySize(0)
                    .get();

            // Variable containing CSS selector for all product cards
            Elements prods = doc.select("div.s-card-container");

            // Looping through all products in each product card
            for (int i = 0; i < prods.size(); ++i) {

                // Retrieving all the data from Amazon
                Elements description = prods.get(i).select("span.a-text-normal");
                Elements price = prods.get(i).select("span.a-price-whole");
                Elements priceDecimal = prods.get(i).select("span.a-price-fraction");
                Elements images = prods.get(i).select("img");
                Elements colour = prods.get(i).select("a.a-link-normal");

                // Calling function to know which memory the phone has
                memory(description, price, priceDecimal, images, "64GB");
                memory(description, price, priceDecimal, images, "128GB");
                memory(description, price, priceDecimal, images, "256GB");
                memory(description, price, priceDecimal, images, "512GB");
                System.out.println();
            }
        }
    }

    // Other classes can use this method to terminate the thread.
    public void stopThread() {
        runThread = false;
    }

    /**
     * Outputs all phone information based on amount of memory phone uses
     * @param description the description of the phone
     * @param price the prices of the phone
     * @param priceDecimal the decimal prices of the phone
     * @param images the image links of the phone image
     * @param memory the memory of the phone
     */
    void memory(Elements description, Elements price, Elements priceDecimal, Elements images, String memory) {
        try {
            // array to split each data into separate bits from the description to find data for other attributes
            String[] splitDesc = description.text().split("\\s+");
            String[] all = description.text().split("[(),\\[]");

            List<String> ColourList = Arrays.asList(all);

            ArrayList<String> descriptionList = new ArrayList<String>();
            descriptionList.add(description.text());

            // Empty string to insert text based on start and end index
            String model = "";
            // create start index and end index to find the model of the phone which is between the term 'Samsung' and the phones memory
            int startIndex = description.text().indexOf("Samsung");
            int endIndex = description.text().indexOf(memory);
            model = description.text().substring(startIndex, endIndex);

            // Check if certain memory is contained in the description, otherwise don't include this phone from the website
            if (descriptionList.get(0).contains(memory)) {
                // Output the data that has been downloaded
                System.out.println("DESCRIPTION: " + description.text());
                System.out.println("MODEL: "+ model);
                System.out.println("PRICE: £" + price.text() + priceDecimal.text());
                System.out.println("IMAGE: " + images.attr("src"));
                System.out.println("MEMORY: " + memory);
                System.out.println("COLOUR: " + ColourList.get(ColourList.size() - 1));
                System.out.println("BRAND: " + splitDesc[0]);
                System.out.println();

                // Saving the data to the Database
                ProductInfo productInfo = new ProductInfo();
                productInfo.setName(model);
                productInfo.setManufacturer(splitDesc[0]);
                productInfo.setProduct_description(description.text());
                productInfo.setProduct_img_url(images.attr("src"));

                PhoneInstance instance = new PhoneInstance();
                instance.setMemory(memory);
                instance.setColour(ColourList.get(ColourList.size() - 1));
                instance.setProductInfo(productInfo);

                Phone phone = new Phone();
                phone.setPhoneInstance(instance);
                phone.setWebsite_url("https://www.amazon.co.uk/s?k=samsung+phones&page=1&crid=17Q3ETJ9IOE9O&qid=1669926781&sprefix=samsung+phones%2Caps%2C70&ref=sr_pg_1");
                phone.setPrice(price.text() + priceDecimal.text());
                phone.setWebsite_name("Amazon");
                phone.setWebsite_img("Images/amazon.png");

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