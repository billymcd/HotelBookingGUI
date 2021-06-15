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
public class RestaurantBooking extends Booking { //**********
    private final String time;
    
    public RestaurantBooking(int bookingNo, int customerNo, Date date, String time)
    {
        super(bookingNo, customerNo, date);
        this.time=time;
    }
    
    public String getTime()
    {
        return time;
    }

    @Override
    public String toString()
    {
        String bString="Booking Number: "+bookingNo+"\nCustomer Number: "
                +customerNo+"\nDate: "+date+" at "+time+"hrs";
        return bString;
    }
}
