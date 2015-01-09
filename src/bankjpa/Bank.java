package bankjpa;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import javax.persistence.EntityManager;

public interface Bank extends Remote
{
    public Account newAccount(String ownerName, String password) throws RemoteException, RejectedException;

    public Account findAccount(String ownerName) throws RemoteException;
    
    public Account getAccount(String ownerName, EntityManager em) throws RemoteException;

    public void deleteAccount(String ownerName) throws RemoteException;

    public void deposit(String ownerName, float value) throws RemoteException, RejectedException;

    public void withdraw(String ownerName, float value) throws RemoteException, RejectedException;
   
   
}