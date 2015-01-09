/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package trader;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author fabriziodemaria
 */
public interface ClientInterface extends Remote {
    
    /**
     * Notify the clients to refresh their list of items with the one passed 
     *
     * @param marketList current list of items available in the market 
     */
    public void refreshList(List<market.ItemList> marketList) throws RemoteException;
    
    /**
     * Notify the clients to refresh their list of wished items with the one passed
     *
     * @param wishList current list of wished items, needs a list of wished items
     * for a specific user
     */
    public void refreshWishList(List<market.WishList> wishList) throws RemoteException;
    
    /**
     * Notifications that the receiver sees as text popups
     *
     * @param text message to be displayed
     */
    public void sendNotification(String text) throws RemoteException;
}
