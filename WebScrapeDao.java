package eu.ishaan.webscraping;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

/**
 * DAO to start session and save web scraper phone data to database
 */
public class WebScrapeDao {
    SessionFactory sessionFactory;

    /** Save phone data to database
     * @param phone the phone data to store in database
     * @throws Exception If data cannot be stored
     * */
    public void saveToDb(Phone phone) throws Exception {
        //Get a new Session instance from the session factory and start transaction
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        /* The commented out code is my attempt at eliminating duplicates for products that are from the same shop,
           however sometimes generates errors and sometimes runs successfully -
           current database is run using this code as shown in video demonstration
        */
        // Attempt to remove duplicates by checking if data already exists in product_info table - checks if there is a model with the same description and img url
        String queryStr = "from ProductInfo where model='" + phone.getPhoneInstance().getProductInfo().getName() + "' and description='" + phone.getPhoneInstance().getProductInfo().getProduct_description() + "' and img_url='" + phone.getPhoneInstance().getProductInfo().getProduct_img_url() + "'";
        List<ProductInfo> productInfoList = session.createQuery(queryStr).getResultList();
//         If a phone matches the query then store in an array list
//        if(productInfoList.size() == 1) {
//            System.out.println("Already exists");
//            // Update description, manufacturer and img url if needed
//            productInfoList.get(0).setProduct_description(phone.getPhoneInstance().getProductInfo().getProduct_description());
//            productInfoList.get(0).setManufacturer(phone.getPhoneInstance().getProductInfo().getManufacturer());
//            productInfoList.get(0).setProduct_img_url(phone.getPhoneInstance().getProductInfo().getProduct_img_url());
//
////            Set mapped product info in phone
//            phone.getPhoneInstance().setProductInfo(productInfoList.get(0));
//        } else if (productInfoList.size() == 0) {
            System.out.println("NEW ENTRY");
            // save data to db
            session.saveOrUpdate(phone.getPhoneInstance().getProductInfo());
//        }

        // Attempt to remove duplicates by checking if data already exists in phone_instance table - checks if there is a phone with the same memory, colour and product info ID
        queryStr = "from PhoneInstance where memory='" + phone.getPhoneInstance().getMemory() + "' and colour='" + phone.getPhoneInstance().getColour() + "' and product_info_id='" + phone.getPhoneInstance().getProductInfo().getId() + "'";
        List<PhoneInstance> phoneInstanceList = session.createQuery(queryStr).getResultList();
//        if(phoneInstanceList.size() == 1) {
//            phoneInstanceList.get(0).setMemory(phone.getPhoneInstance().getMemory());
//            phoneInstanceList.get(0).setColour(phone.getPhoneInstance().getColour());
//
//            //Set mapped phone instance in phone
//            phone.setPhoneInstance(phoneInstanceList.get(0));
//        }
//        else if (phoneInstanceList.size() == 0) {
            System.out.println("NEW ENTRY");
            // save data to db
            session.saveOrUpdate(phone.getPhoneInstance());
//        }

        // Attempt to remove duplicates by checking if data already exists in phone table - checks if there is a phone with the same website url, price and phone instance ID
        queryStr = "from Phone where website_url='" + phone.getWebsite_url() + "' and price='" + phone.getPrice() + "' and phone_instance_id='" + phone.getPhoneInstance().getId() + "'" ;
        List<Phone> phoneList = session.createQuery(queryStr).getResultList();
//        if(phoneList.size() == 1) {
//            System.out.println("Already exists");
//
//            // Update url, price, name and img if needed
//            phoneList.get(0).setWebsite_url(phone.getWebsite_url());
//            phoneList.get(0).setPrice(phone.getPrice());
//            phoneList.get(0).setWebsite_name(phone.getWebsite_name());
//            phoneList.get(0).setWebsite_img(phone.getWebsite_img());
//        } else if (phoneList.size() == 0) {
            System.out.println("NEW ENTRY");
            // save data to db

            session.saveOrUpdate(phone);
//        }

        session.getTransaction().commit();
        //Close the session and release database connection
        session.close();
    }
    /** Initialize session factory to send data to database using hibernate */
    public void init() {
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
        }
    }
    /** return session factory
     * @return the session factory
     * */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    /** set session factory
     * @param sessionFactory the session factory
     * */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

