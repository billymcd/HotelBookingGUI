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
public class Double extends Room {
    
    public Double(int roomNo) // Constructor for a double room
    {
        super(roomNo); // Room created according to room constructor
        this.price=175; // Price for double room is $175
    }
    
    public Double(int roomNo, int price, boolean booked)
    {
        super(roomNo, price, booked);
    }
    
    @Override
    public String toString() // Function prints details of double room
    {
        String roomString;
        roomString="Room no: "+roomNo+"\nRoom type: Double\nStatus: "+status;
        return roomString;
    }
}
