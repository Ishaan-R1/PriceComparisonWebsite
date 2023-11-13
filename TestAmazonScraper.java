import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import eu.ishaan.webscraping.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;
import java.util.List;


@DisplayName("Test Amazon Scraper")
public class TestAmazonScraper {
    static SessionFactory sessionFactory;
    static AmazonScraper scraper;
    static WebScrapeDao dao;

    @BeforeAll
    static void initAll() {
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
            // Log the exception
            System.err.println("SessionFactory creation failed." + ex);
            ex.printStackTrace();
        }
    }

    @BeforeEach
    void init() {
    }

    @Test
    @DisplayName("Test Scraper")
    void testScraper() throws Exception {
        //Create mock object of DAO
        dao = mock(WebScrapeDao.class);
        // set session factory
        dao.setSessionFactory(sessionFactory);
        scraper = new AmazonScraper();
        // Add DAO to amazon scraper
        scraper.setWebScrapeDao(dao);

        try {
            // Start thread to run amazon scraper
            scraper.start();
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            // Declare an example of a phone model that would be pulled off amazon
            String model = "Samsung Galaxy Z Flip4 5G Smartphone Sim Free Andr";

            // Create list of phones which contains results of a query that searches for specific model in ProductInfo table
            List phoneList = session.createQuery("from ProductInfo where model='"+ model +"'").getResultList();

            // Check to see if that phone model has been saved to the database
            if (phoneList.size() > 0) {
                // If so then test passes
                assertTrue(phoneList.size() > 0);
            } else {
                // Otherwise test fails and content of list is displayed
                fail("Phone not successfully stored. Phone list size: " + phoneList.size() + " in list " + phoneList);
            }
            session.getTransaction().commit();
            session.close();

        } catch (IndexOutOfBoundsException error) {

        }catch (NullPointerException ex) {

        }
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }

}
