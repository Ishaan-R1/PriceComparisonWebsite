package eu.ishaan.webscraping;

/**
 * Manage scrapers and set craw delay for each scraper
 */
public class WebScrape extends Thread {
    int crawDelay = 1000;
    boolean runThread;
    WebScrapeDao webScrapeDao;

    /** return web scrape data access object
     * @return the DAO
     * */
    public WebScrapeDao getWebScrapeDao() {
        return webScrapeDao;
    }
    /** set web scrape data access object
     * @param dao the dao that send the data to the database
     * */
    public void setWebScrapeDao(WebScrapeDao dao) {
        this.webScrapeDao = dao;
    }
    /** return crawl delay
     * @return the crawl delay
     * */
    public int getCrawDelay() {
        return crawDelay;
    }
    /** set crawl delay
     * @param crawDelay the craw delay between the scraper
     * */
    public void setCrawDelay(int crawDelay) {
        this.crawDelay = crawDelay;
    }

}
