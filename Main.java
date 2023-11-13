package eu.ishaan.webscraping;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main( String[] args )
    {

        try {
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            System.out.println("Starting Scrapers");
            ManageWebScrapers manager = (ManageWebScrapers) context.getBean("manageWebScrapers");

            // Call function to start thread and run all scrapers
            manager.scrapePhones();

        } catch (Exception e) {

        }
    }
}