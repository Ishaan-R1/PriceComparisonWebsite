package eu.ishaan.webscraping;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Manage web scrapers by running each thread
 */
public class ManageWebScrapers {
    WebScrapeDao dao;
    List<WebScrape> webScrapeArrayList;

    /**
     * Initialise DAO to save phone data to database
     */
    ManageWebScrapers() {
        dao = new WebScrapeDao();
        dao.init();
    }

    /** Method to loop through all scrapers and start each one */
    public void scrapePhones() {
        try {
            for(WebScrape scraper : webScrapeArrayList) {
                scraper.start();
            }
        } catch (IndexOutOfBoundsException error) {

        }catch (NullPointerException ex) {

        }
    }
    /** Return array list of web scrapers
     * @return the scrapers
     * */
    public List<WebScrape> getWebScrapeArrayList() {
        return webScrapeArrayList;
    }

    /** Set an array list of web scrapers
     * @param webScrapeArrayList the array list where scrapers are stored
     * */
    public void setWebScrapeArrayList(List<WebScrape> webScrapeArrayList) {
        this.webScrapeArrayList = webScrapeArrayList;
    }
    }

