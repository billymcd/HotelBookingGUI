/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class Hotel {
    private final String name, location;
    private final int rating;
    private final Set<Room> roomList;
    private final Set<Customer> customerList;
    private final Set<Booking> roomBookList, restBookList;
    private Connection conn;
    
    public Hotel(String name, String location, int rating, Connection conn)
    {
        this.roomBookList = new HashSet();
        this.customerList = new HashSet();
        this.roomList = new HashSet();
        this.restBookList= new HashSet();
        this.name=name;
        this.location=location;
        this.rating=rating;
        this.conn=conn;
    }
    
    public void createRoom(int type)
    {
        PreparedStatement statement;
        int roomType=type;
        int roomNo;
        Room room=null;
        String rType="";
        if(roomList.isEmpty())
            roomNo=1;
        else
            roomNo=roomList.size()+1;
        try {
            statement=conn.prepareStatement("INSERT INTO ROOMS VALUES ("+roomNo+", ?, false, ?)");
            switch(roomType)
            {
                case 1:
                    room=new Single(roomNo);
                    roomList.add(room);
                    rType="single";
                    break;
                case 2:
                    room=new Double(roomNo);
                    roomList.add(room);
                    rType="double";
                    break;
                case 3:
                    room=new Suite(roomNo);
                    roomList.add(room);
                    rType="suite";
                    break;
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
            statement.setString(1, rType);
            statement.setInt(2, room.getPrice());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
    }
    
    public Set<Room> getRoomList()
    {
        return roomList;
    }
    
    public Customer createCustomer(String name, String phone, String email)
    {
        int accountNo=0;
        boolean validEmail=emailIsValid(email);
        boolean validPhoneNumber=phoneNumberIsValid(phone);
        if (validEmail==false||validPhoneNumber==false)
            return null;

        if(customerList.isEmpty())
            accountNo=1;
        else
            accountNo=customerList.size()+1;
        Customer cust=new Customer(accountNo, name, email, phone);
        int start=customerList.size();
        customerList.add(cust);
        int end=customerList.size();
        if(start==end)
            cust=null;
        return cust;
    }
    
    public Set<Customer> getCustomerList()
    {
        return customerList;
    }
    
    public void addBooking(Booking newBooking)
    {
        if(newBooking instanceof RoomBooking)
            roomBookList.add((RoomBooking)newBooking);
        if(newBooking instanceof RestaurantBooking)
            restBookList.add((RestaurantBooking)newBooking);
    }
    
    public Set<Booking> getRoomBookList()
    {
        return roomBookList;
    }
    
    public Set<Booking> getRestBookList()
    {
        return restBookList;
    }
    
    public static boolean emailIsValid(String email)
    {
        String emailRegex="^[a-zA-Z0-9_+&*-]+(?:\\."+"[a-zA-Z0-9_+&*-]+)*@"+
                "(?:[a-zA-Z0-9-]+\\.)+[a-z"+"A-Z]{2,7}$";                     
        Pattern pat=Pattern.compile(emailRegex);
        if (email==null)
            return false;
        return pat.matcher(email).matches();
    }
    
    public static boolean phoneNumberIsValid(String phoneNo)
    {
        String phoneNoRegex="^[0-9]*$";                     
        Pattern pat=Pattern.compile(phoneNoRegex);
        if (phoneNo==null)
            return false;
        return pat.matcher(phoneNo).matches();
    }
    
    @Override
    public String toString()
    {
        return name+"\n"+location+"\n"+"Rating: "+rating;
    }
}
