/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class RoomBooking extends Booking {
    private int roomNo, price, occupants;
    private LocalDate departure;
    private long duration;
    
    public RoomBooking(int bookingNo, int customerNo, int roomNo, LocalDate date) // Constructor initialises variables 
    {
        super(bookingNo, customerNo, date); // Room created according to room constructor
        this.roomNo=roomNo; // Room number set as corresponding input parameter
    }
    
    public RoomBooking(int bookingNo, int customerNo, int roomNo, LocalDate date, LocalDate departure, int price, int occupants, long duration) // Constructor initialises variables 
    {
        super(bookingNo, customerNo, date); // Room created according to room constructor
        this.roomNo=roomNo; // Room number set as corresponding input parameter
        this.departure=departure;
        this.price=price;
        this.occupants=occupants;
        this.duration=duration;
    }
    
    public void setDeparture(LocalDate departure) // Function allows user to change private variable
    {
        this.departure=departure;
    }
    
    public LocalDate getDeparture() // Function allows access to private variable
    {
        return departure;
    }
    
    public void setDuration() // Function allows user to set stay duration as days between arrival and departure dates
    {
        duration=ChronoUnit.DAYS.between(this.date, this.departure);
    }
    
    public void setRoom(int roomNo) // Function allows user to change private variable
    {
        this.roomNo=roomNo;
    }
    
    public int getRoom() // Function allows access to private variable
    {
        return roomNo;
    }
    
    public void setOccupants(int occupants) // Function allows user to change private variable
    {
        this.occupants=occupants;
    }
    
    public int getOccupants() // Function allows access to private variable
    {
        return occupants;
    }
    
    public void setPrice(int roomPrice) // Function allows user to set price by multiplying a night's rate by stay duration
    {
        price=roomPrice*(int)duration;
    }
    
    public int getPrice() // Function allows access to private variable
    {
        return price;
    }
    
    public long getDuration()
    {
        return duration;
    }
    
    @Override
    public String toString() // Function prints details of room booking
    {
        String bString="Booking Number: "+bookingNo+"\nCustomer Number: "+customerNo
                +"\nRoom Number: "+roomNo+"\nDuration: "+date+" - "+departure+" ("
                +duration+" days)"+"\nCost: "+price+"\nOccupants: "+occupants;
        return bString;
    }
}
