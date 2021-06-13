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
public class Single extends Room {
    
    public Single(int roomNo) // Constructor for a single room
    {
        super(roomNo); // Room created according to room constructor
        this.price=100; // Price for single room is $100
    }
    
    public Single(int roomNo, int price, boolean booked)
    {
        super(roomNo, price, booked);
    }
    
    @Override
    public String toString() // Function prints details of single room
    {
        String roomString;
        roomString="Room no: "+roomNo+"\nRoom type: Single\nStatus: "+status;
        return roomString;
    }
}
