package eu.ishaan.webscraping;

import javax.persistence.*;

@Entity
@Table(name="product_info")
public class ProductInfo {

    //Id of the productInfo
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    String id;

    // model of the phone
    @Column(name = "model")
    String name;

    // description of the phone
    @Column(name = "description")
    String product_description;

    // image url of the phones image
    @Column(name = "img_url")
    String product_img_url;

    // manufacturer of the phone
    @Column(name = "manufacturer")
    String manufacturer;

    public String toString() {
        return "id: " + id + "; name: " + name + "; description: " + product_description + "; image: " + product_img_url + "; manufacturer: " + manufacturer;
    }

    //Getters and setters
    /** return ID of product info
     * @return the ID
     * */
    public String getId() {
        return id;
    }
    /** set ID of product info
     * @param id the id of the specific phone
     */
    public void setId(String id) {
        this.id = id;
    }

    /** return model name of product info
     * @return the model name
     * */
    public String getName() {
        return name;
    }
    /** set model name of product info and check it has less than 50 characters
     * @param name the name of the specific phone
     */
    public void setName(String name) {
        if (name.length() <= 50) {
            this.name = name;
        } else {
            this.name = name.substring(0, 50);
        }
    }
    /** return description of product
     * @return the description
     * */
    public String getProduct_description() {
        return product_description;
    }

    /** set description of product
     * @param product_description the name of the specific phone
     */
    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }
    /** return image url of product
     * @return the image url
     * */
    public String getProduct_img_url() {
        return product_img_url;
    }

    /** set image url for a product
     * @param product_img_url the image url for the specific phone
     */
    public void setProduct_img_url(String product_img_url) {
        if (product_img_url.length() > 0) {
            this.product_img_url = product_img_url;
        } else {
            this.product_img_url = "https://st3.depositphotos.com/1322515/35964/v/600/depositphotos_359648638-stock-illustration-image-available-icon.jpg";
        }
    }
    /** return manufacturer of product
     * @return the manufacturer
     * */
    public String getManufacturer() {
        return manufacturer;
    }

    /** set the manufacturer for a product
     * @param manufacturer the manufacturer for a phone
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

}
