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
public abstract class Room {
    protected boolean booked;
    protected int roomNo, price;
    protected String status;
    
    public Room(int roomNo)
    {
        this.booked=false;
        this.status="Available";
        this.roomNo=roomNo;
    }
    
    public Room(int roomNo, int price, boolean booked)
    {
        this.roomNo=roomNo;
        this.price=price;
        this.booked=booked;
    }
    
    public int getRoomNo()
    {
        return roomNo;
    }
    
    public int getPrice()
    {
        return price;
    }
    
    public boolean getBookingStatus()
    {
        return booked;
    }
    
    public void setBookingStatus(boolean booked)
    {
        this.booked=booked;
        if(booked)
        {
            status="Booked";
        }
        else
        {
            status="Available";
        }
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o==null)
        {
            return false;
        }
        if (!(o instanceof Room))
        {
            return false;
        }
        Room other=(Room)o;
        return (this.roomNo==other.roomNo);
    }
    @Override
    public int hashCode()
    {
        int hashCode = 1;
        hashCode = 180 * hashCode + this.roomNo;
        return hashCode;
    }
    
    @Override
    public abstract String toString();
}
