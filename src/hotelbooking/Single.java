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
    
    public Single(int roomNo)
    {
        super(roomNo);
        this.price=100;
    }
    
    public Single(int roomNo, int price, boolean booked)
    {
        super(roomNo, price, booked);
    }
    
    @Override
    public String toString()
    {
        String roomString;
        roomString="Room no: "+roomNo+"\nRoom type: Single\nStatus: "+status;
        return roomString;
    }
}
