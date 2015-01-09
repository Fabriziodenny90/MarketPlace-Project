package market;

import bankjpa.Bank;
import bankjpa.BankImpl;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Initiates a market remote class.
 *
 * @author
 */
public class Server {
    
    public Server(String marketName, String bankName) {
        try {
            
            Bank bankobj = new BankImpl();
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(bankName, bankobj);
            System.out.println(bankName + " server is ready.");
            MarketPlace markobj = new MarketPlaceImpl(marketName, bankName);
            registry.rebind(marketName, markobj);
            System.out.println(marketName + " server is ready");
            
        } catch (Exception e) {
            System.out.println("Server:Error rebinding");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        
//        if (args.length != 2) {
//            System.out.println("error wrong num args");
//            System.exit(1);
//        }
//        
//        System.out.println(args[0]);
//        System.out.println(args[1]);
//        
//        String marketName = args[0];
//        String bankName = args[1];
        
        new Server("Mar1", "Nordea");
    }
}
