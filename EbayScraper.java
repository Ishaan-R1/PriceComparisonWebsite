package eu.ishaan.webscraping;

import java.io.IOException;
import java.util.ArrayList;

import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Scrapes phone data from Ebay
 */
public class EbayScraper extends WebScrape {
    WebScrapeDao dao;

    private int crawlDelay = 1;
    private int thread;

    /**
     * Initialise DAO to save Ebay phone data to database
     */
    EbayScraper() {
        dao = new WebScrapeDao();
        dao.init();

        this.thread = thread;
    }
    volatile private boolean runThread = false;
    /** Starts thread which starts the Ebay Scraper */
    @Override
    public void run(){
        runThread = true;

        //While loop will run until runThread is set to false, after data has been scraped
        while(runThread){
            System.out.println("Scraper 6 is scraping data from thread number: 6.");
            try {
                // Scrape the phones from ebay, then stop the thread
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


/** Scrapes samsung phone data from the Ebay website */
void scrapePhones() throws Exception{
    try {
        // For loop to loop through 10 pages on ebay
        for (int pageNumber = 1; pageNumber <= 10; pageNumber++) {
            // fetch the document over HTTP
            Document doc = Jsoup.connect("https://www.ebay.co.uk/sch/i.html?_from=R40&_nkw=samsung+phones&_sacat=0&Brand=Samsung&_dcat=9355&LH_ItemCondition=1000&rt=nc&LH_BIN=1&_ipg=60&_pgn="+ pageNumber).get();

            // Variable containing CSS selector for all product cards
            Elements prods = doc.select("li.s-item");

            // Looping through all products in each product card
            for (int i = 0; i < prods.size(); ++i) {

                // Retrieving all the data from Ebay
                Elements title = prods.get(i).select("div.s-item__title");
                Elements price = prods.get(i).select("span.s-item__price");
                Elements images = prods.get(i).select("img");


                //Output the downloaded data
                ArrayList<String> phones = new ArrayList<String>();
                // Creating array of colours to make sure colours scraped from ebay are one of these colours
                String[] colours = {"red", "blue", "pink gold", "purple", "black", "orange", "white", "green", "brown"};

                // Check to make sure phones are only galaxy phones and not random products such as phone cases
                if (title.text().toLowerCase().contains("galaxy")) {
                    // add all phone titles to array of phones
                    phones.add(title.text());
                    String string = "galaxy";
                    // Get model of phone by getting certain words after the string 'galaxy'
                    System.out.println("Samsung G"+title.text().toLowerCase().substring(title.text().toLowerCase().lastIndexOf(string) + 1));
                    System.out.println(phones);
                    String memory = "128GB";

                    // Looping through all the colours
                    for(int j =0; j < colours.length; j++)
                    {
                        // If a match is found of certain colour, then use that colour for the specfic phone model
                        if (title.text().toLowerCase().contains(colours[j])) {
                            // Output all of the retrieved data
                            System.out.println("--------------------------------------------------------");
                            System.out.println("MODEL: Samsung G"+title.text().toLowerCase().substring(title.text().toLowerCase().lastIndexOf(string) + 1));
                            System.out.println("--------------------------------------------------------");
                            System.out.println("DESCRIPTION: " + title.text());
                            System.out.println("PRICE: " + price.text());
                            System.out.println("Image Source: " + images.attr("src"));
                            System.out.println("MANUFACTURER: " + "Samsung");
                            System.out.println("COLOUR: " + colours[j]);
                            System.out.println("MEMORY: " + memory);
                            System.out.println();

                            // Save the data
                            ProductInfo productInfo = new ProductInfo();
                            productInfo.setName("Samsung G"+title.text().toLowerCase().substring(title.text().toLowerCase().lastIndexOf(string) + 1));
                            productInfo.setManufacturer("Samsung");
                            productInfo.setProduct_description(title.text());
                            productInfo.setProduct_img_url(images.attr("src"));

                            PhoneInstance instance = new PhoneInstance();
                            instance.setMemory(memory);
                            instance.setColour(colours[j]);
                            instance.setProductInfo(productInfo);

                            Phone phone = new Phone();
                            phone.setPhoneInstance(instance);
                            phone.setWebsite_url("https://www.ebay.co.uk/sch/i.html?_from=R40&_nkw=samsung+phones&_sacat=0&Brand=Samsung&_dcat=9355&LH_ItemCondition=1000&rt=nc&LH_BIN=1&_ipg=60&_pgn="+ pageNumber);
                            phone.setPrice(price.text());
                            phone.setWebsite_name("Ebay");
                            phone.setWebsite_img("Images/ebay.png");

                            try {
                                // Save all data to the database which is all linked to the phone table
                                dao.saveToDb(phone);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
      } catch (IOException e) {
      e.printStackTrace();
      }
        
}
}
