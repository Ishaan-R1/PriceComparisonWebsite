package eu.ishaan.webscraping;

import javax.persistence.*;

@Entity
@Table(name="phone_instance")
public class PhoneInstance {
    //ID of phoneInstance
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    String id;

    // device memory of phone
    @Column(name = "memory")
    String memory;

    // colour of phone
    @Column(name = "colour")
    String colour;

    //Information of phone that phone is linked to
    @ManyToOne
    @JoinColumn(name="product_info_id", nullable=false)
    ProductInfo product_info;


    //Getters and setters
    /** return information of a phone
     * @return the information
     * */
    public ProductInfo getProductInfo() { return product_info; }

    /** set information of specific product
     * @param productInfo the information of a phone
     */
    public void setProductInfo(ProductInfo productInfo) { this.product_info = productInfo; }

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

    /** return memory storage of a phone
     * @return the memory
     * */
    public String getMemory() {
        return memory;
    }
    /** set memory of specific product
     * @param memory the storage of a phone
     */
    public void setMemory(String memory) {
        this.memory = memory;
    }

    /** return colour of a phone
     * @return the colour
     * */
    public String getColour() {
        return colour;
    }

    /** set colour of specific product
     * @param colour the colour of a phone
     */
    public void setColour(String colour) {
        this.colour = colour;
    }
}



