package bankjpa;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

@SuppressWarnings("serial")
public class BankImpl extends UnicastRemoteObject implements Bank
{
    private EntityManagerFactory emFactory;

    public BankImpl() throws RemoteException
    {
        super();
        emFactory = Persistence.createEntityManagerFactory("bank");
    }

    @Override
    public Account getAccount(String ownerName, EntityManager em)
    {
        if (ownerName == null)
        {
            return null;
        }

        try
        {
            return (Account) em.createNamedQuery("findAccountWithName").
                    setParameter("ownerName", ownerName).getSingleResult();
        } catch (NoResultException noSuchAccount)
        {
            return null;
        }
    }
    
    
    
    public Account newAccount(String name, String password) throws RejectedException
    {
        EntityManager em = null;
        try
        {
            em = beginTransaction();
            List<Account> existingAccounts = em.createNamedQuery("findAccountWithName", Account.class).
                    setParameter("ownerName", name).getResultList();
            if (existingAccounts.size() != 0)
            {
                // account exists, can not be created.
                throw new RejectedException("Rejected: Account for: " + name + " already exists");
            }

            // create account.
            Account account = new Account(name, password, 1000);
            em.persist(account);
            return account;
        } finally
        {
            commitTransaction(em);
        }
    }

    public void deposit(String ownerName, float value) throws RejectedException
    {
        EntityManager em = null;
        try
        {
            em = beginTransaction();

            getAccount(ownerName, em).deposit(value);
        } finally
        {
            commitTransaction(em);
            incSold(ownerName);
        }
    }

    public void withdraw(String ownerName, float value) throws RejectedException
    {
        EntityManager em = null;
        try
        {
            em = beginTransaction();

            getAccount(ownerName, em).withdraw(value);
        } finally
        {
            commitTransaction(em);
            incBought(ownerName);
        }
    }

    public Account findAccount(String ownerName)
    {
        EntityManager em = null;
        try
        {
            em = beginTransaction();

            Account account = getAccount(ownerName, em);
            return account;
        } finally
        {
            commitTransaction(em);
        }
    }

    public void deleteAccount(String name)
    {
        EntityManager em = null;
        try
        {
            em = beginTransaction();

            Account account = getAccount(name, em);
            em.remove(account);

        } finally
        {
            commitTransaction(em);
        }
    }
    
    private void incSold(String name)throws RejectedException{
        EntityManager em = null;
        try
        {
            em = beginTransaction();

            getAccount(name, em).incSold();
        } finally
        {
            commitTransaction(em);
        }
    }
    
    private void incBought(String name)throws RejectedException{
        EntityManager em = null;
        try
        {
            em = beginTransaction();

            getAccount(name, em).incBought();
        } finally
        {
            commitTransaction(em);
        }
    }
    
//    public int getNumSold(String name)throws RejectedException{
//        EntityManager em = null;
//        try
//        {
//            em = beginTransaction();
//
//            return getAccount(name, em).getNumSold();
//        } finally
//        {
//            commitTransaction(em);
//        }
//    } 
//    
//    public int getNumBought(String name)throws RejectedException{
//        EntityManager em = null;
//        try
//        {
//            em = beginTransaction();
//
//            return getAccount(name, em).getNumBought();
//        } finally
//        {
//            commitTransaction(em);
//        }
//    }
    


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

    
}
