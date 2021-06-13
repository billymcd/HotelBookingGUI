/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.util.*;
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
    private final Set<RoomBooking> roomBookList;
    private final Set<RestaurantBooking> restBookList;
    
    public Hotel(String name, String location, int rating) // Constructor initialises variables
    {
        this.roomBookList = new HashSet();
        this.customerList = new HashSet();
        this.roomList = new HashSet();
        this.restBookList= new HashSet();
        this.scan = new Scanner(System.in);
        this.name=name;
        this.location=location;
        this.rating=rating;
    }
    
    public void createRoom(int type) // Creates a new room of the specified type
    {
        int roomType=type;
        int roomNo=0;
        if(roomList.isEmpty()) // If no rooms in the room list hashset, next room number is 1
            roomNo=1;
        else
            roomNo=roomList.size()+1; // If rooms in the room list hashset, next room number is 1 more than last
        switch(roomType)
        {
            case 1: // Create new single room
                roomList.add(new Single(roomNo));
                break;
            case 2: // Create new double room
                roomList.add(new Double(roomNo));
                break;
            case 3: // Create new suite room
                roomList.add(new Suite(roomNo));
                break;
            default: // If none of the valid room choices are selected, print message
                System.out.println("Invalid selection.");
                break;
        }
    }
    
    public Set<Room> getRoomList() // Function allows access to private variable
    {
        return roomList;
    }
    
    public Customer createCustomer() // Creates a new customer
    {
        int accountNo=0;
        String newPhone="";
        String newEmail="";
        boolean validEmail=false;
        boolean validPhoneNumber=false;
        
        System.out.print("Please enter name: "); // Prompt user to input name
        String newName=scan.next(); // Assign the next scanner token to variable for name
        
        while (newEmail.equals(""))
        {
            System.out.print("Please enter email address: "); // Prompt user to input email address
            newEmail=scan.next(); // Assign the next scanner token to variable for email
            
            validEmail = emailIsValid(newEmail); // Check if email address is valid
            
            if (validEmail == false) // If email not valid, enter sequence
            {
                newEmail ="";
                System.out.println("Not a valid email address, please try again."); // If email is not valid, prompt user to input email again
            }
        }
        
        while (newPhone.equals(""))
        {
            System.out.print("Please enter phone number: "); // Prompt user to input phone number
            newPhone=scan.next(); // Assign the next scanner token to variable for phone number
            
            validPhoneNumber = phoneNumberIsValid(newPhone); // Check if phone number is valid
            
            if (validPhoneNumber == false) // If phone number not valid, enter sequence
            {
                newPhone ="";
                System.out.println("Not a valid phone number, please try again."); // If phone number is not valid, prompt user to input phone number again
            }
        }
        
        if(customerList.isEmpty()) // If no customers in the customer list hashset, next customer account number is 1
            accountNo=1;
        else
            accountNo=customerList.size()+1; // If customers in the customer list hashset, next customer number is 1 more than last
        Customer cust=new Customer(accountNo, newName, newEmail, newPhone); // Create a new customer based on information
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
    } // ******** What if it is null?
    
    public Set<RoomBooking> getRoomBookList() // Function allows access to private variable
    {
        return roomBookList;
    }
    
    public Set<RestaurantBooking> getRestBookList() // Function allows access to private variable
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
