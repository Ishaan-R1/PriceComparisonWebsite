
import eu.ishaan.webscraping.Phone;
import eu.ishaan.webscraping.PhoneInstance;
import eu.ishaan.webscraping.ProductInfo;
import eu.ishaan.webscraping.WebScrapeDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.List;

@DisplayName("Test Saving Data To Database")
public class TestWebScrapeDao {
    static SessionFactory sessionFactory;

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
            // Make sure you log the exception, as it might be swallowed
            System.err.println("SessionFactory creation failed." + ex);
            ex.printStackTrace();
        }
    }

    @BeforeEach
    void init() {
    }

    @Test
    @DisplayName("Test Save Phone")
    void testSavePhone() throws Exception {
        //Create instance of WebScrapeDao to test if it saves data to db
        WebScrapeDao dao = new WebScrapeDao();
        // Set session factory
        dao.setSessionFactory(sessionFactory);

        //Create random string to give phone random model name
        String randomPhoneModel = String.valueOf(Math.random());
        Phone phone = new Phone();
        ProductInfo productInfo = new ProductInfo();
        PhoneInstance phoneInstance = new PhoneInstance();
        phone.setWebsite_name("Amazon");

        productInfo.setName(randomPhoneModel);
        productInfo.setProduct_description("description . . .");
        productInfo.setProduct_img_url("img url . . .");
        productInfo.setManufacturer("Samsung");

        phoneInstance.setMemory("128GB");
        phoneInstance.setColour("Blue");
        phoneInstance.setProductInfo(productInfo);

        phone.setWebsite_name("Amazon");
        phone.setPrice("100");
        phone.setWebsite_url("www.Amazon.co.uk");
        phone.setWebsite_img("img");
        phone.setPhoneInstance(phoneInstance);

        //Use WebScrapeDao to save phone to db
        dao.saveToDb(phone);

        //Get a new Session from session factory
        Session session = sessionFactory.getCurrentSession();

        //begin transaction
        session.beginTransaction();

        //Check that phone exists in database
        //Get phone with name provided
        List<ProductInfo> phoneList = session.createQuery("from ProductInfo where model='"+randomPhoneModel+"'").getResultList();

        // Checking to see only one phone is saved with the random name provided
        if(phoneList.size() != 1)
           fail("Phone not successfully stored. Phone list size: " + phoneList.size() + " in list " + phoneList);

        //Commit transaction to save to database
        session.getTransaction().commit();

        //Close session
        session.close();
    }


    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }
}