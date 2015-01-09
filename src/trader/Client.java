package trader;

import bankjpa.RejectedException;
import java.lang.reflect.InvocationTargetException;
import market.MarketPlace;
import market.Item;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import market.ItemList;
import market.Wish;
import market.WishList;

public class Client extends UnicastRemoteObject implements ClientInterface {
    private static final String USAGE = "java bankrmi.Client <bank_url>";
    private static final String DEFAULT_BANK_NAME = "Mar1";
    MarketPlace marketobj;
    private String marketname;
    String clientname;
    GUI myView = null;
    
    void sellitem(String name, Float value) throws RemoteException {
        Item newitem = new Item(name, value, clientname);
        marketobj.sell(clientname, newitem);
    }

    void wishitem(String name, Float value) throws RemoteException {
        Wish newwish = new Wish(name, value, clientname);
        marketobj.wish(clientname, newwish);
    }

    void buyOperation(Object name, Object price) throws RemoteException {
        Float b = (float)price;
        marketobj.buy(clientname, (String) name, b);
    }

    void disconnect() throws RemoteException {
        marketobj.logout(clientname);
    }

    void register(String user, String passw) throws RemoteException {
        marketobj.register(this, user,passw);
    }

    void showStats() throws RemoteException, RejectedException, InterruptedException, InvocationTargetException {
        List<Integer> stats = marketobj.getStats(clientname);
        myView.notifyUser("Sold: " + stats.get(1) + "; Bought: " + stats.get(0));
    }
    
    
    
    static enum CommandName {
        newAccount, getAccount, deleteAccount, deposit, withdraw, balance, quit, help, list;
    };
    
    public Client(String marketName, GUI myView) throws RemoteException {
        
        this.myView = myView;
        this.marketname = marketName;
        try {
            marketobj = (MarketPlace)Naming.lookup(marketname);
        } catch (Exception e) {
            System.out.println("The runtime failed: " + e.getMessage());
            myView.connectionToBankFailed();
            System.exit(0);
        }
        
        System.out.println("Connected to market: " + marketname);
        
        
    }
    
    public Client(GUI myView) throws RemoteException {
        this(DEFAULT_BANK_NAME, myView);
    }
    
    void login(String text, String passw) throws RemoteException, InterruptedException {
        
        if(marketobj.login(text, passw, this)){
            clientname=text;
            myView.validateLogin(true,text);
        } else {
            myView.validateLogin(false,text);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public synchronized void refreshList(List<ItemList> marketList) throws RemoteException {
        int k = 0;
        myView.clearMarketTable();
        for(ItemList i : marketList){
            myView.addMarketRow(i.getName(),k,0);
            myView.addMarketRow(i.getPrice(),k,1);
            k++;
        }
    }

    @Override
    public synchronized void refreshWishList(List<WishList> wishList) throws RemoteException {
        int k = 0;
        myView.clearWishTable();
        for(WishList i : wishList){
            myView.addWishRow(i.getName(),k,0);
            myView.addWishRow(i.getPrice(),k,1);
            k++;
        }
    }

    
    @Override
    public synchronized void sendNotification(String text) throws RemoteException{
        try {
            myView.notifyUser(text);
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}