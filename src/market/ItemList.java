/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package market;

import bankjpa.Account;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Version;

@NamedQueries({
//        @NamedQuery(
//                name = "deleteItemWithName",
//                query = "DELETE FROM ItemList a WHERE a.id LIKE :itemID"
//        ),
//        @NamedQuery(
//                name = "findItemWithName",
//                query = "SELECT a FROM ItemList a WHERE a.id LIKE :itemID",
//                lockMode = LockModeType.OPTIMISTIC
//        )
        @NamedQuery(
                name = "allItem",
                query = "SELECT a FROM ItemList a",
                lockMode = LockModeType.OPTIMISTIC
        ),
        
        @NamedQuery(
                name = "removeItemWithNameAndPrice",
                query = "DELETE FROM ItemList a WHERE a.itemname LIKE :itemname AND a.price LIKE :price"
        ),
        
        @NamedQuery(
                name = "findItemWithNameAndPrice",
                query = "SELECT a FROM ItemList a WHERE a.itemname LIKE :itemname AND a.price = :price",
                lockMode = LockModeType.OPTIMISTIC
        )
        
        
})

/**
 *
 * @author fabriziodemaria
 */
@Entity(name = "ItemList")
public class ItemList implements Serializable {
    
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "itemname", nullable = false)
    private String itemname;
    
    @Column(name = "price", nullable = false)
    private float price;

    @OneToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "account", nullable = false)
    private Account account;
    

//    @Column(name = "seller", nullable = false)
//    private String seller;
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    public ItemList() {
    }

    ItemList(Account seller, String name, float price) {
        itemname = name;
        this.price = price;
        this.account = seller;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.itemname;
    }

    public float getPrice() {
        return this.price;
    }
    
    public Account getAccount(){
        return this.account;
    }

    
    
}
