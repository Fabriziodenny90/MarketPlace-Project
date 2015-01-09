/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package market;

import bankjpa.Account;
import java.io.Serializable;
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
@NamedQuery(
                name = "allWishes",
                query = "SELECT a FROM WishList a",
                lockMode = LockModeType.OPTIMISTIC
        )
        
//        @NamedQuery(
//                name = "removeItemWithNameAndPrice",
//                query = "DELETE FROM ItemList a WHERE a.itemname LIKE :itemname AND a.price LIKE :price"
//        ),
//        
//        @NamedQuery(
//                name = "findItemWithNameAndPrice",
//                query = "SELECT a FROM ItemList a WHERE a.itemname LIKE :itemname AND a.price = :price",
//                lockMode = LockModeType.OPTIMISTIC
//        )
})



/**
 *
 * @author fabriziodemaria
 */
@Entity
public class WishList implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "itemname", nullable = false)
    private String itemname;
    
    @Column(name = "price", nullable = false)
    private float price;

    @OneToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "requester", nullable = false)
    private Account requester;
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    public WishList() {
    }

    public WishList(String itemname, Float price, Account requester){
        this.itemname=itemname;
        this.price=price;
        this.requester=requester;
    }
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public float getPrice(){
        return this.price;
    }
    
    public String getName(){
        return this.itemname;
    }
    
    public Account getRequester(){
        return this.requester;
    } 

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishList)) {
            return false;
        }
        WishList other = (WishList) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "market.WishList[ id=" + id + " ]";
    }
    
}
