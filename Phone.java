package eu.ishaan.webscraping;

import javax.persistence.*;

@Entity
@Table(name="phone")
public class Phone {
    //ID of phone
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    String id;

    // URL of website phone is from
    @Column(name = "website_url")
    String website_url;

    // Price of phone
    @Column(name = "prices")
    String price;

    // Price of phone
    @Column(name = "website_name")
    String website_name;

    // Price of phone
    @Column(name = "website_img")
    String website_img;

    // Instance of phone that phone is related to
    @ManyToOne
    @JoinColumn(name = "phone_instance_id", nullable = false)
    PhoneInstance phoneInstance;

    //Getters and setters
    /** return phone instance of a phone
     * @return the phone instance
     * */
    public PhoneInstance getPhoneInstance() {
        return phoneInstance;
    }
    /** set phone instance of specific product
     * @param phoneInstance the phone instance of a phone
     */
    public void setPhoneInstance(PhoneInstance phoneInstance) {
        this.phoneInstance = phoneInstance;
    }

    /** return ID of a phone
     * @return the ID
     * */
    public String getId() {
        return id;
    }

    /** set ID of specific product
     * @param id the ID of a phone
     */
    public void setId(String id) {
        this.id = id;
    }

    /** return website URL of a phone
     * @return the website URL
     * */
    public String getWebsite_url() {
        return website_url;
    }

    /** set website URL of specific product
     * @param website_url the website URL where a phone is from
     */
    public void setWebsite_url(String website_url) {
        this.website_url = website_url;
    }

    /** return price of a phone
     * @return the price
     * */
    public String getPrice() {
        return price;
    }

    /** set price of specific product and add default price if there is price field is empty
     * @param price the price of a phone
     */
    public void setPrice(String price) {
        if (price.length() > 0) {
            this.price = price;
        } else {
            this.price = "£899.99";
        }
    }

    /** return name of website where phone is from
     * @return the website name
     * */
    public String getWebsite_name() {
        return website_name;
    }

    /** set website name of website where specific product is from
     * @param website_name the name of the website
     */
    public void setWebsite_name(String website_name) {
        this.website_name = website_name;
    }

    /** return logo of website where phone is from
     * @return the website logo
     * */
    public String getWebsite_img() {
        return website_img;
    }

    /** set logo image of website where specific product is from
     * @param website_img the image of the website
     */
    public void setWebsite_img(String website_img) {
        this.website_img = website_img;
    }
}