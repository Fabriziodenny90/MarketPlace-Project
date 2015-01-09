/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package market;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author server
 */
public class Item implements Serializable {
    private String buyer;
    private String seller;
    private float price;
    private String name;
    private Date sellDate;
    
    public Item(String name, float price, String seller){
        this.name=name;
        this.price=price;
        this.seller=seller;
    }

    public Item(String string, Integer integer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setSellDate(Date date) {
        this.sellDate=date;
  }

    public String getName() {
        return name;
  }

   public float getPrice() {
        return price;
  }

    public String getBuyer() {
        return buyer;
  }

    public String getSeller() {
        return seller;
  }

    void setBuyer(String traderName) {
        this.buyer=traderName;
 }
    
}
