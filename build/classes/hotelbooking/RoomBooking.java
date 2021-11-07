/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class RoomBooking extends Booking {
    private int roomNo, price, occupants;
    private Date departure;
    private long duration;
    
    public RoomBooking(int bookingNo, int customerNo, int roomNo, Date date)
    {
        super(bookingNo, customerNo, date);
        this.roomNo=roomNo;
    }
    
    public RoomBooking(int bookingNo, int customerNo, int roomNo, Date date, Date departure, int price, int occupants, long duration)
    {
        super(bookingNo, customerNo, date);
        this.roomNo=roomNo;
        this.departure=departure;
        this.price=price;
        this.occupants=occupants;
        this.duration=duration;
    }
    
    public void setDeparture(Date departure)
    {
        this.departure=departure;
    }
    
    public Date getDeparture()
    {
        return departure;
    }
    
    public void setDuration()
    {
        long diff=Math.abs(departure.getTime()-date.getTime());
        duration=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
    
    public void setRoom(int roomNo)
    {
        this.roomNo=roomNo;
    }
    
    public int getRoom()
    {
        return roomNo;
    }
    
    public void setOccupants(int occupants)
    {
        this.occupants=occupants;
    }
    
    public int getOccupants()
    {
        return occupants;
    }
    
    public void setPrice(int roomPrice)
    {
        price=roomPrice*(int)duration;
    }
    
    public int getPrice()
    {
        return price;
    }
    
    public long getDuration()
    {
        return duration;
    }
    
    @Override
    public String toString()
    {
        String bString="Booking Number: "+bookingNo+"\nCustomer Number: "+customerNo
                +"\nRoom Number: "+roomNo+"\nDuration: "+date+" - "+departure+" ("
                +duration+" days)"+"\nCost: "+price+"\nOccupants: "+occupants;
        return bString;
    }
}
