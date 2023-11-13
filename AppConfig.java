package eu.ishaan.webscraping;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfig {
    SessionFactory sessionFactory;

    @Bean
    public ManageWebScrapers manageWebScrapers() {
        ManageWebScrapers manageWebScrapers = new ManageWebScrapers();

        // Create array list to store all scrapers
        List<WebScrape> scrapeArrayList = new ArrayList();
        scrapeArrayList.add(johnLewisScraper());
        scrapeArrayList.add(freemansScraper());
        scrapeArrayList.add(veryScraper());
        scrapeArrayList.add(argosScraper());
        scrapeArrayList.add(amazonScraper());
        scrapeArrayList.add(ebayScraper());

        manageWebScrapers.setWebScrapeArrayList(scrapeArrayList);

        return manageWebScrapers;
    }
    @Bean
    public WebScrape johnLewisScraper(){
        WebScrape johnLewisScraper = new JohnLewisScraper();
        johnLewisScraper.setWebScrapeDao(webScrapeDao());
        // run john lewis scraper with crawl delay of 1 second
        johnLewisScraper.setCrawDelay(1000);
        return johnLewisScraper;
    }
    @Bean
    public WebScrape argosScraper(){
        WebScrape argosScraper = new ArgosScraper();
        argosScraper.setWebScrapeDao(webScrapeDao());
        // run argos scraper with crawl delay of 1 second
        argosScraper.setCrawDelay(1000);
        return argosScraper;
    }
    @Bean
    public WebScrape amazonScraper(){
        WebScrape amazonScraper = new AmazonScraper();
        amazonScraper.setWebScrapeDao(webScrapeDao());
        // run amazon scraper with crawl delay of 1 second
        amazonScraper.setCrawDelay(1000);
        return amazonScraper;
    }
    @Bean
    public WebScrape freemansScraper(){
        WebScrape freemansScraper = new FreemansScraper();
        freemansScraper.setWebScrapeDao(webScrapeDao());
        // run freemans scraper with crawl delay of 1 second
        freemansScraper.setCrawDelay(1000);
        return freemansScraper;
    }
    @Bean
    public WebScrape veryScraper(){
        WebScrape veryScraper = new VeryScraper();
        veryScraper.setWebScrapeDao(webScrapeDao());
        // run very scraper with crawl delay of 1 second
        veryScraper.setCrawDelay(1000);
        return veryScraper;
    }
    @Bean
    public WebScrape ebayScraper(){
        WebScrape ebayScraper = new EbayScraper();
        ebayScraper.setWebScrapeDao(webScrapeDao());
        // run ebay scraper with crawl delay of 1 second
        ebayScraper.setCrawDelay(1000);
        return ebayScraper;
    }
    @Bean
    public WebScrapeDao webScrapeDao(){
        WebScrapeDao webScrapeDao = new WebScrapeDao();
        // start session factory
        webScrapeDao.setSessionFactory(sessionFactory());
        return webScrapeDao;
    }
    @Bean
    public SessionFactory sessionFactory() {
        if(sessionFactory == null){//Build sessionFatory once only
            try {
                //Create a builder for the standard service registry
                StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();

                //Load configuration from hibernate configuration file.
                //Here we are using a configuration file that specifies Java annotations.
                standardServiceRegistryBuilder.configure("hibernate.cfg.xml");

                //Create the registry that will be used to build the session factory
                StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
                try {
                    //Create the session factory - this is the goal of the init method.
                    sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
                }
                catch (Exception e) {
                        /* The registry would be destroyed by the SessionFactory,
                            but we had trouble building the SessionFactory, so destroy it manually */
                    System.err.println("Session Factory build failed.");
                    e.printStackTrace();
                    StandardServiceRegistryBuilder.destroy( registry );
                }
                //Ouput result
                System.out.println("Session factory built.");
            }
            catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("SessionFactory creation failed." + ex);
                ex.printStackTrace();
            }
        }
        return sessionFactory;
    }

}
