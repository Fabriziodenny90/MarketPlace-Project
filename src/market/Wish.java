package market;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author server
 */
public class Wish implements Serializable {

    private String name;
    private float price;
    private String requester;


    public Wish(String name, float price, String requester) {
        this.name = name;
        this.price = price;
        this.requester = requester;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Name:" + name + "\n"
                + "Price: " + price + "\n"
                + "Requester: " + requester + "\n";
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getRequester() {
        return requester;
    }
    
}
