package bankjpa;

import javax.persistence.*;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "deleteAccountWithName",
                query = "DELETE FROM Account acct WHERE acct.accname LIKE :ownerName"
        ),
        @NamedQuery(
                name = "findAccountWithName",
                query = "SELECT acct FROM Account acct WHERE acct.accname LIKE :ownerName",
                lockMode = LockModeType.OPTIMISTIC
        )
})

@Entity(name = "Account")
public class Account implements Serializable
{
    private static final long serialVersionUID = -4302632166699642491L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountId;

    @Column(name = "balance", nullable = false)
    private float balance;

    @Column(name = "accname", nullable = false)
    private String accname;
    
    @Column(name = "password", nullable = false)
    private String password;
    
     @Column(name = "numBought", nullable = false)
    private int numBought;
     
      @Column(name = "numSold", nullable = false)
    private int numSold;

    
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    public Account()
    {
        this(null, null, 0);
    }

    public Account(String accname, String password, float balance)
    {
        this.password = password;
        this.accname = accname;
        this.balance = balance;
        this.numBought = 0;
        this.numSold = 0;
    }
    
    public String getPassw(){
        return this.password;
    }

    public float getBalance()
    {
        return balance;
    }
    
    public String getName(){
        return this.accname;
    }
    
    public void incSold(){
        this.numSold = this.numSold + 1;
    }
    
    public void incBought(){
        this.numBought = this.numBought + 1;
    }
    
    public int getNumSold(){
        return this.numSold;
    } 
    
    public int getNumBought(){
        return this.numBought;
    }
    

    public void deposit(float value) throws RejectedException
    {
        if (value < 0)
        {
            throw new RejectedException("Rejected: Account " + this.accname +
                    ": Illegal value: " + value);
        }

        balance += value;
        System.out.println("Transaction: Account " + this.accname + ": deposit: $" +
                value + ", balance: $" + balance);
    }

    public void withdraw(float value) throws RejectedException
    {
        if (value < 0)
        {
            throw new RejectedException("Rejected: Account " + this.accname +
                    ": Illegal value: " + value);
        }

        if ((balance - value) < 0)
        {
            throw new RejectedException("Rejected: Account " + this.accname +
                    ": Negative balance on withdraw: " + (balance - value));
        }

        balance -= value;
        System.out.println("Transaction: Account " + this.accname + ": withdraw: $" +
                value + ", balance: $" + balance);
    }

    public String toString()
    {
        return "Account for " + this.accname + " has balance $" + balance;
    }

    public EntityManager withdraw(Float price, EntityManager em) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}