package eu.ishaan.webscraping;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Scrapes phone data from Freemans
 */
public class FreemansScraper extends WebScrape  {
    WebScrapeDao dao;
    //Specifies the interval between HTTP requests to the server in seconds.
    private int crawlDelay = 1;
    private int thread;

    /**
     * Initialise DAO to save Freemans phone data to database
     */
    FreemansScraper() {
        dao = new WebScrapeDao();
        dao.init();

        this.thread = thread;
    }
    volatile private boolean runThread = false;
/** Starts thread which starts the Freemans Scraper */
@Override
public void run(){
    runThread = true;

    //While loop will run until runThread is set to false, after data has been scraped
    while(runThread){
        System.out.println("Scraper 5 is scraping data from thread number: 5.");
            try {
                // Scrape the phones from freemans, then stop the thread
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

/** Scrapes samsung phone data from the Freemans websit */
void scrapePhones() throws Exception {
    try {
        // fetch the document over HTTP
        Document doc = Jsoup.connect(
                "https://www.freemans.com/search/_/N-1c?searchType=FullText&Nty=1&Ntt=samsung+phone&typedText=sam").userAgent(
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
            .maxBodySize(0).get();
        // Variable containing CSS selector for all product cards
        Elements prods = doc.select("div.pDetailsContainer");
        // Variable containing CSS selector for all images
        Elements img = doc.select("div.pImageContainer");

        // Looping through all products in each product card
        for (int i = 0; i < prods.size(); ++i) {
            // Retrieving all the data from Freemans
            Elements title = prods.get(i).select("div.pDescriptionContainer");
            Elements price = prods.get(i).select("div.pPriceContainer");
            Elements images = img.get(i).select("img");

            // Output the downloaded data
            String[] arrOfStr = title.text().split("[-]");
            List<String> list  = new ArrayList<>(Arrays.asList(arrOfStr));

            // Calling function to know which memory the phone has
            memory(title, price, images, "64GB", i);
            memory(title, price, images, "128GB", i);
            memory(title, price, images, "256GB", i);
            memory(title, price, images, "512GB", i);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

}
// method to terminate the thread.
public void stopThread(){
    runThread = false;
}

    /**
     * Outputs all phone information based on amount of memory phone uses
     * @param description the description of the phone
     * @param price the prices of the phone
     * @param images the image links of the phone image
     * @param memory the memory of the phone
     * @param i the index
     */
void memory(Elements description, Elements price, Elements images, String memory, int i) {
    try {
        // Output the data that we have downloaded
        String[] arrOfStr = description.text().split("-");
        List<String> list  = new ArrayList<>(Arrays.asList(arrOfStr));

        int ind = description.text().indexOf("Sim Free");
        String model = "";
        int startIndex = description.text().indexOf("Samsung");
        int endIndex = description.text().indexOf("Galaxy");
        // model to retrieve bit of string after Samsung Galaxy
        model = description.text().substring(startIndex, endIndex);

        if (list.get(0).contains(memory)) {
            // Output the data based on it's memory
            System.out.println("--------------------------------------------------------");
            System.out.println(i + "     MODEL: " + arrOfStr[0]);
            System.out.println("--------------------------------------------------------");
            System.out.println("DESCRIPTION: " + description.text());
            System.out.println("PRICE: " + price.text());
            System.out.println("MEMORY: " + memory);
            System.out.println("MANUFACTURER: " + model);
            System.out.println("IMAGE: " + images.attr("src"));
            System.out.println("COLOUR: " + arrOfStr[arrOfStr.length - 1]);
            System.out.println();

            // Saving the data to the Database
            ProductInfo productInfo = new ProductInfo();
            productInfo.setName(arrOfStr[0]);
            productInfo.setManufacturer(model);
            productInfo.setProduct_description(description.text());
            productInfo.setProduct_img_url(images.attr("src"));

            PhoneInstance instance = new PhoneInstance();
            instance.setMemory(memory);
            instance.setColour(arrOfStr[arrOfStr.length - 1]);
            instance.setProductInfo(productInfo);

            Phone phone = new Phone();
            phone.setPhoneInstance(instance);
            phone.setWebsite_url("https://www.freemans.com/search/_/N-1c?searchType=FullText&Nty=1&Ntt=samsung+phone&typedText=sam");
            phone.setPrice(price.text());
            phone.setWebsite_name("Freemans");
            phone.setWebsite_img("Images/freemans.png");

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