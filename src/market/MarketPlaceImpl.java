package market;

import bankjpa.Account;
import trader.ClientInterface;
import java.rmi.RemoteException;
import bankjpa.Bank;
import bankjpa.RejectedException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author server
 */
public class MarketPlaceImpl extends UnicastRemoteObject implements MarketPlace {
    
    private Map<String, ClientInterface> traders = null;
    private Bank bankObj = null;
    
    private final EntityManagerFactory emFactory;
    
    private static final String YOUR_ITEM_SOLD = "Your item has been sold";
    private static final String ITEM__BOUGHT = "Item Bought!";
    private static final String LOW__BALANCE = "Low Balance";
    private static final String WISH_AVAILABLE = "Wish available: ";
    private String i;
    
    public MarketPlaceImpl(String marketName, String bankName) throws RemoteException {
        super();
        this.traders = new HashMap<>();
     
        try {
            this.bankObj = (Bank) Naming.lookup(bankName);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
        }
        
        emFactory = Persistence.createEntityManagerFactory("bank");
    }
    
    @Override
    public synchronized boolean login(String username, String passw, ClientInterface trader) throws RemoteException {
        
        if(bankObj.findAccount(username)!=null){
            if(bankObj.findAccount(username).getPassw().equals(passw)){
                if(!traders.containsKey(username)){
                    //Keep a volatile list of connected clients
                    traders.put(username, trader);
                    System.out.println("Found " + username);
                    traders.get(username).refreshList(getAllItems());
                    updateUserWishList(username);
                    return true;
                } else {
                    trader.sendNotification(WRONG_PASSWORD);
                }
            }
        }
        return false;
    }
    private static final String WRONG_PASSWORD = "Wrong password";
    
    @Override
    public synchronized ItemList sell(String traderName, Item item) throws RemoteException {
        
        //items.add(item);
        Account myacc = bankObj.findAccount(traderName);
        EntityManager em = null;
        try
        {
            em = beginTransaction();
            ItemList newitem = new ItemList(myacc, item.getName(), item.getPrice());
            
            em.persist(newitem);
            
            List<WishList> currentw = getAllWishes();
            for (WishList wish : currentw) {
                //If new item to be sold matches a wish, send callback
                if (wish.getName().equalsIgnoreCase(item.getName())
                        && wish.getPrice() >= item.getPrice()) {
                    //Send notification to requester and update sat date
                    System.out.println("Sending wish" + "to" + traderName);
                    if(traders.get(wish.getRequester().getName())!=null)
                        traders.get(wish.getRequester().getName()).sendNotification("Wish available: " + wish.getName());
                    System.out.println("Updating wish after sell");
                    
                }
            }
            return newitem;
        } finally
        {
            commitTransaction(em);
            for(String key : traders.keySet()){
                //Check if the user in the "database" is connected
                traders.get(key).refreshList(getAllItems());
            }
        }
    }
    
    
    @Override
    public synchronized void buy(String traderName, String itemName, Float price) throws RemoteException{
        EntityManager em;
        
        
        //Take money from buyer
        if(bankObj.findAccount(traderName).getBalance()>=price){
            em = beginTransaction();
            try {
                bankObj.withdraw(traderName,price);
            } catch (RejectedException ex) {
                Logger.getLogger(MarketPlaceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            traders.get(traderName).sendNotification(ITEM__BOUGHT);
            
            //If the money has been taken from buyer
            List<ItemList> itemSold = em.createNamedQuery("findItemWithNameAndPrice", ItemList.class).setParameter("itemname", itemName).setParameter("price", price).getResultList();
            Account seller = itemSold.get(0).getAccount();
            try {
                bankObj.deposit(seller.getName(), price);
            } catch (RejectedException ex) {
                Logger.getLogger(MarketPlaceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            em.merge(itemSold.get(0));
            em.remove(itemSold.get(0));
            commitTransaction(em);
            
            if(traders.get(seller.getName())!=null)
                traders.get(seller.getName()).sendNotification(YOUR_ITEM_SOLD);
            for(String key : traders.keySet()){
                //Check if the user in the "database" is connected
                traders.get(key).refreshList(getAllItems());
            }
        } else {
            //Not enough money
            traders.get(traderName).sendNotification(LOW__BALANCE);
        }
    }
    
    
    @Override
    public void register(ClientInterface client, String user, String passw) throws RemoteException{
        Account myacc = bankObj.findAccount(user);
        if(myacc!=null){
            client.sendNotification(USERNAME_ALREADY_EXISTS);
        } else {
            try {
                bankObj.newAccount(user, passw);
            } catch (RejectedException ex) {
                Logger.getLogger(MarketPlaceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            client.sendNotification(ACCOUNT_REGISTERED);
        }
    }
    private static final String ACCOUNT_REGISTERED = "Account registered";
    private static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    
    
    
    
    @Override
    public synchronized void wish(String traderName, Wish wish) throws RemoteException {
        boolean found = false;
        List<ItemList> allitems = getAllItems();
        for(ItemList item : allitems){
            if(item.getName().equalsIgnoreCase(wish.getName()) && item.getPrice()<wish.getPrice()){
                if(traders.get(traderName)!=null)
                    traders.get(traderName).sendNotification(WISH_AVAILABLE + wish.getName());
                found = true;
                break;
            }
        }
        if(found==false){
            EntityManager em = null;
            try
            {
                em = beginTransaction();
                
                WishList mywish = new WishList(wish.getName(),wish.getPrice(),bankObj.findAccount(traderName));
                em.persist(mywish);
            } finally
            {
                commitTransaction(em);
                
            }
            updateUserWishList(traderName);
            
        }
        
        
        
        
    }
    
    
    private synchronized void updateUserWishList(String traderName) throws RemoteException{
        List<WishList> tempList = new LinkedList<>();
        List<WishList> allw = getAllWishes();
        for(WishList w : allw){
            if(w.getRequester().getName().equals(traderName)){
                tempList.add(w);
            }
        }
        traders.get(traderName).refreshWishList(tempList);
    }
    
    private EntityManager beginTransaction()
    {
        EntityManager em = emFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        return em;
    }
    
    private void commitTransaction(EntityManager em)
    {
        em.getTransaction().commit();
    }
    
    private List<ItemList> getAllItems() {
        EntityManager em = null;
        try
        {
            em = beginTransaction();
            List<ItemList> all;
            all = em.createNamedQuery("allItem", ItemList.class).getResultList();
            return all;
        }
        finally {
            commitTransaction(em);
        }
    }
    
    private List<WishList> getAllWishes() {
        EntityManager em = null;
        try
        {
            em = beginTransaction();
            List<WishList> all;
            all = em.createNamedQuery("allWishes", WishList.class).getResultList();
            return all;
        }
        finally {
            commitTransaction(em);
        }
    }
    
    @Override
    public List<Integer> getStats(String name) throws RemoteException, RejectedException {
        EntityManager em = null;
        List<Integer> stats = new LinkedList<>();
        try
        {
            em = beginTransaction();
            
            stats.add(bankObj.findAccount(name).getNumBought());
            stats.add(bankObj.findAccount(name).getNumSold());
            return stats;
        } finally
        {
            commitTransaction(em);
        }
    }
    
    @Override
    public void logout(String client) throws RemoteException{
        traders.remove(client);
    }
    
    
}
