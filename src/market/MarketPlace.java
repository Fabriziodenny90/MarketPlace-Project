package market;

import bankjpa.RejectedException;
import trader.ClientInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * KTH - ID2212, Homework2
 * EventHandler.java
 * Purpose: MarketPlace remote interface.
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 17/11/2014
 */
public interface MarketPlace extends Remote {
    
    /**
     * Register accept connections from clients and look for the 
     * username in the userlist (later with database)
     *
     * @param username is the nickname of the user 
     * @param trader is the client's remote interface needed for callingback operations
     */
    public boolean login(String username, String passw, ClientInterface trader) throws RemoteException; 
    
    /**
     * Handle selling requestes from users
     *
     * @param traderName who is selling the item 
     * @param item the serializable object Item with all the info of the item to be sold
     */
    public ItemList sell(String traderName, Item item) throws RemoteException;
    
    /**
     * Handle wish requests from users
     *
     * @param traderName is the nickname of the user  
     * @param wish is the serializable object Wish with the info of the item to be wished
     */
    public void wish(String traderName, Wish wish) throws RemoteException;
    
    /**
     * Handle users' requests for buying an item
     *
     * @param traderName is the nickname of the user 
     * @param itemName the name identifying the item in the item list of the market
     * @param price the price of the item being bought (needed for lookup in the items list)
     */
    public void buy(String traderName, String itemName, Float price) throws RemoteException;
    
    public void logout(String client) throws RemoteException;

    public void register(ClientInterface clientInterface, String user, String passw) throws RemoteException;
    
    public List getStats(String name)throws RemoteException, RejectedException;
}