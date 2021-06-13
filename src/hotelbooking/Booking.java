/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.sql.Date;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public abstract class Booking {
    protected int bookingNo, customerNo;
    protected Date date;
    
    public Booking(int bookingNo, int customerNo, Date date) // Constructor initialises variables
    {
        this.bookingNo=bookingNo;
        this.customerNo=customerNo;
        this.date=date;
    }
    
    public void setDate(Date date) // Function allows user to change protected variable
    {
        this.date=date; 
    }
    
    public Date getDate() // Function allows access to protected variable
    {
        return date;
    }
    
    public int getCustomer()
    {
        return customerNo;
    }
    
    public int getBookingNo()
    {
        return bookingNo;
    }
    
    @Override
    public boolean equals(Object o) // Checks if this room is equal to inputted parameter
    {
        if (o==null) // If o doesn't refer to an object, this room doesn't equal o
        {
            return false;
        }
        if (!(o instanceof Booking)) //If o isn't a room object, this room doesn't equal o
        {
            return false;
        }
        Booking other=(Booking)o;
        return (this.bookingNo==other.bookingNo); // Check if this room equals inputted room (by checking if room number is the same), and return answer
    }
    @Override
    public int hashCode() // Assign a different hashcode for every room
    {
        int hashCode = 1;
        hashCode = 180 * hashCode + this.bookingNo;
        return hashCode;
    }
    
    @Override
    public abstract String toString(); // Child classes print different things
}
