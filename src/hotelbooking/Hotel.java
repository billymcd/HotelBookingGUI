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
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class Hotel {
    private final Scanner scan;
    private final String name, location;
    private final int rating;
    private final Set<Room> roomList;
    private final Set<Customer> customerList;
    private final Set<Booking> roomBookList;
    private final Set<Booking> restBookList;
    private Connection conn;
    
    public Hotel(String name, String location, int rating, Connection conn)
    {
        this.roomBookList = new HashSet();
        this.customerList = new HashSet();
        this.roomList = new HashSet();
        this.restBookList= new HashSet();
        this.scan = new Scanner(System.in);
        this.name=name;
        this.location=location;
        this.rating=rating;
        this.conn=conn;
    }
    
    public void createRoom(int type)
    {
        PreparedStatement statement;
        int roomType=type;
        int roomNo=0;
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
    
    public Customer createCustomer()
    {
        int accountNo=0;
        String newPhone="";
        String newEmail="";
        boolean validEmail=false;
        boolean validPhoneNumber=false;
        
        System.out.print("Please enter name: ");
        String newName=scan.next();
        
        while (newEmail.equals(""))
        {
            System.out.print("Please enter email address: ");
            newEmail=scan.next();
            
            validEmail = emailIsValid(newEmail);
            
            if (validEmail == false)
            {
                newEmail ="";
                System.out.println("Not a valid email address, please try again.");
            }
        }
        
        while (newPhone.equals(""))
        {
            System.out.print("Please enter phone number: ");
            newPhone=scan.next();
            
            validPhoneNumber = phoneNumberIsValid(newPhone);
            
            if (validPhoneNumber == false)
            {
                newPhone ="";
                System.out.println("Not a valid phone number, please try again.");
            }
        }
        long phone=Long.parseLong(newPhone);
        
        if(customerList.isEmpty()) // If no customers in the customer list hashset, next customer account number is 1
            accountNo=1;
        else
            accountNo=customerList.size()+1; // If customers in the customer list hashset, next customer number is 1 more than last
        Customer cust=new Customer(accountNo, newName, newEmail, phone); // Create a new customer based on information
        int start=customerList.size(); // Check current size of customer list hashset and assign to start variable
        customerList.add(cust); // Add customer to customer list hashset
        int end=customerList.size(); // Check size of customer list hashset and assign to end variable
        if(start==end) // If customer list hastset size has not changed, then no new customer //******
            cust=null;
        return cust; // Return cust variable
    }
    
    public Set<Customer> getCustomerList() // Function allows access to private variable
    {
        return customerList;
    }
    
    public void addBooking(Booking newBooking) // Add booking specified to corresponding hashset
    {
        if(newBooking instanceof RoomBooking) // Add room booking to room booking hashset
            roomBookList.add((RoomBooking)newBooking);
        if(newBooking instanceof RestaurantBooking) // Add restaurant booking to restaurant booking hashset
            restBookList.add((RestaurantBooking)newBooking);
    }
    
    public Set<Booking> getRoomBookList() // Function allows access to private variable
    {
        return roomBookList;
    }
    
    public Set<Booking> getRestBookList() // Function allows access to private variable
    {
        return restBookList;
    }
    
    public static boolean emailIsValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+"[a-zA-Z0-9_+&*-]+)*@"+
                "(?:[a-zA-Z0-9-]+\\.)+[a-z"+"A-Z]{2,7}$";                     
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
    
    public static boolean phoneNumberIsValid(String phoneNo)
    {
        String phoneNoRegex = "^[0-9]*$";                     
        Pattern pat = Pattern.compile(phoneNoRegex);
        if (phoneNo == null)
            return false;
        return pat.matcher(phoneNo).matches();
    }
    
    @Override
    public String toString()
    {
        return name+"\n"+location+"\n"+"Rating: "+rating;
    }
}
