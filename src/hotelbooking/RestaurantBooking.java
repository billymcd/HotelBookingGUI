/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class RestaurantBooking extends Booking { //**********
    private final int time;
    
    public RestaurantBooking(int bookingNo, int customerNo, LocalDate date, int time) // Constructor initialises variables 
    {
        super(bookingNo, customerNo, date); // Booking created according to booking constructor
        this.time=time; // Restaurant booking time set as corresponding input parameter
    }
    
    public int getTime() // Function allows access to private variable
    {
        return time;
    }

    @Override
    public String toString() // Function prints details of restaurant booking
    {
        String bString="Booking Number: "+bookingNo+"\nCustomer Number: "
                +customerNo+"\nDate: "+date+" at "+time+"hrs";
        return bString;
    }
}
