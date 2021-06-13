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
    private final String name, email;
    private long phoneNo;
    private final int accountNo;
    
    public Customer(int accountNo, String name, String email, long phoneNo) // Constructor initialises variables
    {
        this.accountNo=accountNo;
        this.name=name;
        this.email=email;
        this.phoneNo=phoneNo;
    }
    
    public int getAccount() // Function allows access to private variable
    {
        return accountNo;
    }
    
    public String getName() // Function allows access to private variable
    {
        return name;
    }
    
    public String getEmail() // Function allows access to private variable
    {
        return email;
    }
    
    public long getPhone() // Function allows access to private variable
    {
        return phoneNo;
    }
    
    @Override
    public boolean equals(Object o) // Checks if this customer is equal to inputted parameter
    {
        if (o==null) // If o doesn't refer to an object, this customer doesn't equal o
        {
            return false;
        }
        if (!(o instanceof Customer)) //If o isn't a customer object, this customer doesn't equal o
        {
            return false;
        }
        Customer other=(Customer)o;
        return (this.email.equals(other.email)); // Check if this customer equals inputted customer (by checking if email is the same), and return answer
    }
    @Override
    public int hashCode() // Assign a different hashcode for every customer
    {
        int hashCode = 1;
        hashCode = 180 * hashCode + this.email.hashCode(); //*****************
        return hashCode;
    }
    
    @Override
    public String toString() // Function prints details of customer
    {
        String customerString;
        customerString="Account Number: "+accountNo+"\nName: "+name+"\nEmail: "
                +email+"\nPhone: "+phoneNo;
        return customerString;
    }
}
