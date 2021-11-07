/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class Customer {
    private final String name, email, phoneNo;
    private final int accountNo;
    
    public Customer(int accountNo, String name, String email, String phoneNo)
    {
        this.accountNo=accountNo;
        this.name=name;
        this.email=email;
        this.phoneNo=phoneNo;
    }
    
    public int getAccount()
    {
        return accountNo;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public String getPhone()
    {
        return phoneNo;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o==null)
        {
            return false;
        }
        if (!(o instanceof Customer))
        {
            return false;
        }
        Customer other=(Customer)o;
        return (this.email.equals(other.email));
    }
    @Override
    public int hashCode()
    {
        int hashCode = 1;
        hashCode = 180 * hashCode + this.email.hashCode();
        return hashCode;
    }
    
    @Override
    public String toString()
    {
        String customerString;
        customerString="Name: "+name+", Account Number: "+accountNo+", Email: "
                +email+", Phone: "+phoneNo;
        return customerString;
    }
}
