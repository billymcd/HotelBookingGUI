/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.io.Serializable;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public abstract class Room implements Serializable {
    protected boolean booked;
    protected int roomNo, price;
    protected String status;
    
    public Room(int roomNo) // Constructor initialises variables
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
    
    public int getRoomNo() // Function allows access to protected variable
    {
        return roomNo;
    }
    
    public int getPrice() // Function allows access to protected variable
    {
        return price;
    }
    
    public boolean getBookingStatus() // Function allows access to protected variable
    {
        return booked;
    }
    
    public void setBookingStatus(boolean booked) // Function allows user to change protected variable
    {
        this.booked=booked; // Set booking status to inputted parameter
        if(booked) // Change status to booked or available depending on input parameter
        {
            status="Booked";
        }
        else
        {
            status="Available";
        }
    }
    
    @Override
    public boolean equals(Object o) // Checks if this room is equal to inputted parameter
    {
        if (o==null) // If o doesn't refer to an object, this room doesn't equal o
        {
            return false;
        }
        if (!(o instanceof Room)) //If o isn't a room object, this room doesn't equal o
        {
            return false;
        }
        Room other=(Room)o;
        return (this.roomNo==other.roomNo); // Check if this room equals inputted room (by checking if room number is the same), and return answer
    }
    @Override
    public int hashCode() // Assign a different hashcode for every room
    {
        int hashCode = 1;
        hashCode = 180 * hashCode + this.roomNo;
        return hashCode;
    }
    
    @Override
    public abstract String toString(); // Child classes print different things
}
