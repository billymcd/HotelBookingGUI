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
    
    public Booking(int bookingNo, int customerNo, Date date)
    {
        this.bookingNo=bookingNo;
        this.customerNo=customerNo;
        this.date=date;
    }
    
    public void setDate(Date date)
    {
        this.date=date; 
    }
    
    public Date getDate()
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
    public boolean equals(Object o)
    {
        if (o==null)
        {
            return false;
        }
        if (!(o instanceof Booking))
        {
            return false;
        }
        Booking other=(Booking)o;
        return (this.bookingNo==other.bookingNo);
    }
    @Override
    public int hashCode()
    {
        int hashCode = 1;
        hashCode = 180 * hashCode + this.bookingNo;
        return hashCode;
    }
    
    @Override
    public abstract String toString();
}
