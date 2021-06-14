/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class HotelBooker {
    public final Hotel hotel;
    public Customer currentCustomer;
    private String response;
    private Room currentRoom;
    private Booking currentBooking;
    private Connection conn=null;

    public HotelBooker(String name, String location, int rating)
    {
        dbSetup();
        hotel = new Hotel(name, location, rating, conn);
        loadHotel();
    }
    
    public static void main(String[] args) 
    {
        Scanner scan = new Scanner(System.in);
        int running=1;
        int customerLoaded=0;
        HotelBooker hotBook=new HotelBooker("PDC Hotel", "Auckland", 5);
        hotBook.loadHotel();
        
        while(running==1)
        {
            hotBook.currentCustomer=null;
            customerLoaded=0;
            System.out.println("Welcome to PDC Hotel!");
            System.out.println("");
            
            while(customerLoaded==0)
                customerLoaded=hotBook.checkCustomer();
            hotBook.response=null; // Response variable is reused, so clearing it
            
            int selection=0;
            while(selection==0)
            {
                System.out.println("What would you like to do? ");
                System.out.println("(1) Book a room");
                System.out.println("(2) Book into the restaurant");
                System.out.println("(3) Enquire on an existing booking");
                System.out.println("Or enter any other number to return to the main menu.");
                try{
                    selection=scan.nextInt();
                }catch(InputMismatchException e){
                    System.out.println("Invalid response, please try again.");
                    scan.next();
                }
                System.out.println("");
            }
            
            while(selection==1&&customerLoaded==1)
                selection=hotBook.roomBookingDetails();
            while(selection==2&&customerLoaded==1)
                selection=hotBook.restaurantBookingDetails();
            while(selection==3&&customerLoaded==1)
            {
                int enquiry=0;
                int custNo=hotBook.currentCustomer.getAccount();
                System.out.println("What type of bookings would you like to enquire on? ");
                System.out.println("(1) Room");
                System.out.println("(2) Restaurant");
                System.out.println("(3) Both");
                System.out.println("(4) Return to main menu");
                try{
                    enquiry=scan.nextInt();
                }catch(InputMismatchException e){
                    System.out.println("Invalid response, please try again.\n");
                    scan.next();
                }
                System.out.println("");
                selection=hotBook.bookingEnquiry(enquiry, custNo);
            }
        }
    }
    //Used to create new rooms in bulk, primarily on first time running
    public void setupRooms(int singles, int doubles, int suites)
    {
        for(int i=0;i<singles;i++)
            hotel.createRoom(1);
        for(int i=0;i<doubles;i++)
            hotel.createRoom(2);
        for(int i=0;i<suites;i++)
            hotel.createRoom(3);
    }
    
    public int checkCustomer()
    {
        Scanner scan = new Scanner(System.in);
        int customerLoaded=0;
        System.out.print("Have you booked with us before? (Y/N) (Q to quit) ");
        response=scan.next().toLowerCase(); // Scan user input and store to response variable
        System.out.println("");
        switch(response)
        {
            case "y": // If user indicates they have booked before
//                loadCustomer();
                if(currentCustomer!=null) // If current customer loaded, set variable to match
                    customerLoaded=1;
                break;
            case "n": // If user indicates they haven't booked before
                currentCustomer=newCustomer(); // Create new customer and set as current customer
                if(currentCustomer!=null) // If current customer loaded, set variable to match
                    customerLoaded=1;
                break;
            case "q": // If user indicates they want to quit
                System.exit(0);
                break;
            default:
                System.out.println("Invalid response, please try again."); // If no defined answer entered, print line
                break;
        }
        return customerLoaded;
    }
    
    public Customer newCustomer() // Create a new customer
    {
        Scanner scan = new Scanner(System.in);
        Customer cust=null;
        int custCheck=0;
        //Continually loop until either a customer is created, or user cancels
        while(custCheck==0)
        {
            System.out.println("Create new customer account:");
            System.out.println("----------------------------");
            cust=hotel.createCustomer();
            if(cust==null)
            {   
                System.out.println("");
                System.out.print("Account creation failed, enter Y to try again,"
                        + " anything else to cancel: ");
                String cResponse=scan.next().toLowerCase();
                if(!cResponse.equals("y"))
                    custCheck=1;
                System.out.println("");
            }
            else
            {
                //Output details if account created successfully
                System.out.println("");
                System.out.println("Customer account created!");
                System.out.println("-------------------------");
                System.out.println(cust.toString());
                System.out.println("");
                custCheck=1;
            }
        }
        if(cust!=null)
        {
            PreparedStatement statement;
            try {
                statement=conn.prepareStatement("INSERT INTO CUSTOMERS VALUES (?, ?, ?, ?)");
                statement.setInt(1, cust.getAccount());
                statement.setString(2, cust.getName());
                statement.setString(3, cust.getEmail());
                statement.setLong(4, cust.getPhone());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: "+ex.getMessage());
            }
        }
        return cust;
    }
    
    //Method to search and load an existing customer from the database
    public boolean loadCustomer(String email)
    {
        Iterator itr=hotel.getCustomerList().iterator();
        while(itr.hasNext())
        {
            Object element=itr.next();
            Customer cust=(Customer)element;
            if(email.equals(cust.getEmail()))
            {
                currentCustomer=cust;
                return true;
            }
        }
        return false;
    }
    
    public int roomBookingDetails()
    {
        Scanner scan = new Scanner(System.in);
        int completed=1;
        int roomType=0;
        boolean available=false;
        System.out.println("Here are the types of rooms:");
        System.out.println("(1) Single"+"\n(2) Double"+"\n(3) Suite"+"\n");
        System.out.print("What type of room would you like to book? (1/2/3) (4 for main menu) ");
        try{
            roomType=scan.nextInt();
        }catch(InputMismatchException e){
            System.out.println("Invalid response, please try again.\n");
        }
        if(roomType>0&&roomType<4)
        {
            available=checkAvailability(roomType); // Check if the type of room the user wants is available
            if(available==false)
                System.out.println("Sorry, no rooms of that type are available.");
            else
            {
                currentBooking=createRoomBooking(currentCustomer.getAccount(), currentRoom.getRoomNo());
                System.out.println("Booking created!");
                System.out.println("----------------");
                System.out.println(currentBooking.toString());
                System.out.println("");
                completed=0;
            }
        }
        else if(roomType==4)
            completed=0; //return to main menu
        else
            System.out.println("Invalid response, please try again.\n");
        return completed;
    }
    
    public boolean checkAvailability(int type) // Check the availability of specified type of room
    {
        currentRoom=null;
        boolean available=false;
        Iterator itr=hotel.getRoomList().iterator(); // Initialise iterator to look through room list
        switch(type)
        {
            case 1: // User wants to check if single room is available
                while(itr.hasNext()&&available==false) // Look through room list
                {
                    Room room=(Room)itr.next();
                    if(room instanceof Single && room.getBookingStatus()==false) // If single room and available, set room as current room
                    {
                        available=true;
                        currentRoom=room;
                    }
                }
                break;
            case 2: // User wants to check if double room is available
                while(itr.hasNext()&&available==false) // Look through room list
                {
                    Room room=(Room)itr.next();
                    if(room instanceof Double && room.getBookingStatus()==false) // If double room and available, set room as current room 
                    {
                        available=true;
                        currentRoom=room;
                    }
                }
                break;
            case 3: // User wants to check if suite room is available
                while(itr.hasNext()&&available==false) // Look through room list
                {
                    Room room=(Room)itr.next();
                    if(room instanceof Suite && room.getBookingStatus()==false) // If suite room and available, set room as current room
                    {
                        available=true;
                        currentRoom=room;
                    }
                }
                break;
            default:
                System.out.println("Invalid response, please try again."); // If no defined room type selected, print line
                break;
        }
        return available; // Return availability status
    }
    
    public RoomBooking createRoomBooking(int customerNo, int roomNo) // Create a room booking
    {
        Scanner scan = new Scanner(System.in);
        int bookingNo=0;
        int occupants=0;
        String dateS;
        String dateD;
        Date date=Date.valueOf("2000-1-1"); // Initialise arrival date to 1 January 2000 //*****
        Date depart=Date.valueOf("2000-1-1"); // Initialise departure date to 1 January 2000 //*****
        Set<RoomBooking> bList=hotel.getRoomBookList(); 
        if(bList.isEmpty()) // If no room bookings in room booking list hashset, next room booking number is 1
            bookingNo=1;
        else
            bookingNo=bList.size()+1; // If room bookings in the room booking list hashset, next room booking number is 1 more than last
        while(date.before(Calendar.getInstance().getTime())) // While arrival date is before current day, enter sequence
        {
            System.out.print("What date would you like the booking for? (YYYY-MM-DD) ");
            dateS=scan.next(); // Scan user input
            try{
                date=Date.valueOf(dateS); // Try to get the user input as a valid date
            }catch(DateTimeParseException e){
                System.out.println("Please enter a valid date."); // Print if date invalid
                dateS="";
            }
            if(date.before(Calendar.getInstance().getTime())&&!dateS.isEmpty()) // If date before current date entered
                System.out.println("Date must not be earlier than today's date.");
        }
        while(!depart.after(date)) // While departure date before arrival date
        {
            System.out.print("Please enter departure date. (YYYY-MM-DD) ");
            dateD=scan.next(); //  Scan user input
            try{
                depart=Date.valueOf(dateD); // Try to get the user input as a valid date
            }catch(DateTimeParseException e){
                System.out.println("Please enter a valid date."); // Print if date invalid
                dateD="";
            }
            if(!depart.after(date)&&!dateD.isEmpty()) // If departure date before arrival date entered
                System.out.println("Departure must be later than arrival.");
        }
        RoomBooking newBook=new RoomBooking(bookingNo, customerNo, roomNo, date); // Create room booking from the information
        // ***** Add booking to hotel bookings
        while(occupants==0)
        {
            System.out.print("How many occupants will there be? ");
            try{
                occupants=scan.nextInt(); // Scan user input for occupant number
            }catch(InputMismatchException e){
                System.out.println("Invalid response."); // If whole number not entered
                scan.next();
            }
        }
        System.out.println("");
        newBook.setDeparture(depart);
        newBook.setDuration();
        newBook.setPrice(currentRoom.getPrice());
        newBook.setOccupants(occupants);
        currentRoom.setBookingStatus(true); // Change booking status of room to booked
        hotel.addBooking(newBook);
        
        PreparedStatement statement;
        try {
            statement=conn.prepareStatement("INSERT INTO ROOM_BOOKINGS VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, bookingNo);
            statement.setInt(2, customerNo);
            statement.setInt(3, roomNo);
            statement.setDate(4, date);
            statement.setDate(5, depart);
            statement.setInt(6, newBook.getPrice());
            statement.setInt(7, occupants);
            statement.setLong(8, newBook.getDuration());
            statement.executeUpdate();
            statement.close();
            //Update room status in the database
            statement=conn.prepareStatement("UPDATE ROOMS SET BOOKINGSTATUS=? WHERE ROOMNO=?");
            statement.setBoolean(1, true);
            statement.setInt(2, roomNo);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
        
        return newBook; // Return room booking created
    }
    
    public int restaurantBookingDetails()
    {
        RestaurantBooking booking=createRestaurantBooking(currentCustomer.getAccount()); // Create new restaurant booking
        System.out.println("\nBooking successful!");
        System.out.println("-------------------");
        System.out.println(booking.toString()); // Print restaurant booking details
        System.out.println("");
        return 0;
    }
    
    public RestaurantBooking createRestaurantBooking(int customerNo) // Create a restaurant booking
    {
        Scanner scan = new Scanner(System.in);
        int bookingNo=0;
        String dateS;
        int time=0;
        Date date=Date.valueOf("2000-1-1"); // Set date to 1 January 2000
        Set<RestaurantBooking> bList=hotel.getRestBookList(); 
        
        if(bList.isEmpty()) // If no restaurant bookings in restaurant booking list hashset, next restaurant booking number is 1
            bookingNo=1; 
        else
            bookingNo=bList.size()+1; // If restaurant bookings in the restaurant booking list hashset, next restaurant booking number is 1 more than last
        
        while(date.before(Calendar.getInstance().getTime())) // While booking date is before current date, enter sequence
        {
            System.out.print("What date would you like the booking for? (YYYY-MM-DD) ");
            dateS=scan.next(); // Scan user input
            try{
                date=Date.valueOf(dateS); // Try to get the user input as a valid date
            }catch(DateTimeParseException e){
                System.out.println("Please enter a valid date."); // Print if date invalid
                dateS="";
            }
            if(date.before(Calendar.getInstance().getTime())&&!dateS.isEmpty()) // If date before current date entered
                System.out.println("Date must not be earlier than today's date.");
        }
        
        while(time<1100||time>2300) // While time earlier than 11 am or later than 11 pm, enter sequence
        {
            System.out.print("What time would you like to book for? (24hr format) ");
            try{
                time=scan.nextInt(); // Scan user input
            }catch(InputMismatchException e){
                System.out.println("Invalid input, please try again."); // Print if whole number not entered //****
            }
            if(time<1100||time>2300) // If time earlier than 11 am or later than 11 pm, prompt user to enter time again
                System.out.println("We accept bookings between 1100-2300, please"
                        + " enter a valid time. ");
        }
        RestaurantBooking newBook=new RestaurantBooking(bookingNo, customerNo, date, time); // Create restaurant booking from the information
        //*** Add other things like end of room booking?
        hotel.addBooking(newBook);
        
        PreparedStatement statement;
        try {
            statement=conn.prepareStatement("INSERT INTO RESTAURANT_BOOKINGS VALUES (?, ?, ?, ?)");
            statement.setInt(1, bookingNo);
            statement.setInt(2, customerNo);
            statement.setDate(3, date);
            statement.setInt(4, time);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(HotelBooker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return newBook; // Return restaurant booking created
    }
    
    public int bookingEnquiry(int enquiry, int custNo)
    {
        int completed=0;
        Iterator itr;
        int bookingCount=0;
        switch(enquiry)
        {
            case 1:
                bookingCount=0;
                Set<RoomBooking> roomList=hotel.getRoomBookList();
                itr=roomList.iterator();
                while(itr.hasNext())
                {
                    Object element=itr.next();
                    RoomBooking book=(RoomBooking)element;
                    if(book.getCustomer()==custNo)
                    {
                        System.out.println(book.toString());
                        bookingCount++;
                        System.out.println("");
                    }
                }
                switch (bookingCount) 
                {
                    case 0:
                        System.out.println("No room bookings found.\n");
                        break;
                    case 1:
                        System.out.println("You have 1 room booking with us.\n");
                        break;
                    default:
                        System.out.println("You have "+bookingCount+" room bookings with us.\n");
                        break;
                }
                break;
            case 2:
                bookingCount=0;
                Set<RestaurantBooking> restList=hotel.getRestBookList();
                itr=restList.iterator();
                while(itr.hasNext())
                {
                    Object element=itr.next();
                    RestaurantBooking book=(RestaurantBooking)element;
                    if(book.getCustomer()==custNo)
                    {
                        System.out.println(book.toString());
                        bookingCount++;
                        System.out.println("");
                    }
                }
                switch (bookingCount) 
                {
                    case 0:
                        System.out.println("No restaurant bookings found.");
                        break;
                    case 1:
                        System.out.println("You have 1 restaurant booking with us.");
                        break;
                    default:
                        System.out.println("You have "+bookingCount+" restaurant bookings with us.");
                        break;
                }
                break;
            case 3:
                bookingEnquiry(1, custNo);
                bookingEnquiry(2, custNo);
                break;
            case 4:
                break;
            default:
                completed=3;
        }
        return completed;
    }
    
    //Run on start up to load all saved data from the database
    public boolean loadHotel()
    {
        boolean loaded=true;
        
        Statement statement;
        try {
            statement=conn.createStatement();
            ResultSet rs=statement.executeQuery("SELECT * FROM ROOMS");
            while(rs.next())
            {
                int roomNo=rs.getInt("roomNo");
                String type=rs.getString("type");
                boolean status=rs.getBoolean("bookingstatus");
                int price=rs.getInt("price");
                if(type.equalsIgnoreCase("single"))
                    hotel.getRoomList().add(new Single(roomNo, price, status));
                else if(type.equalsIgnoreCase("double"))
                    hotel.getRoomList().add(new Double(roomNo, price, status));
                else if(type.equals("suite"))
                    hotel.getRoomList().add(new Suite(roomNo, price, status));
            }
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
        //If there were no rooms to load, run method to populate the set
        if(hotel.getRoomList().isEmpty())
        {
            System.out.println("Initialising first time setup.");
            setupRooms(50, 25, 10);
        }
        
        try {
            statement=conn.createStatement();
            ResultSet rs=statement.executeQuery("SELECT * FROM CUSTOMERS");
            while(rs.next())
            {
                int account=rs.getInt("custno");
                String name=rs.getString("name");
                String email=rs.getString("email");
                int phone=rs.getInt("phoneno");
                hotel.getCustomerList().add(new Customer(account, name, email, phone));
            }
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
        
        try {
            statement=conn.createStatement();
            ResultSet rs=statement.executeQuery("SELECT * FROM ROOM_BOOKINGS");
            while(rs.next())
            {
                int bookingNo=rs.getInt("bookingno");
                int customerNo=rs.getInt("custno");
                int roomNo=rs.getInt("roomno");
                Date date=rs.getDate("date");
                Date departure=rs.getDate("departure");
                int price=rs.getInt("price");
                int occupants=rs.getInt("occupants");
                long duration=rs.getLong("duration");
                hotel.getRoomBookList().add(new RoomBooking(bookingNo, customerNo, roomNo, date, departure, price, occupants, duration));
            }
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
        
        try {
            statement=conn.createStatement();
            ResultSet rs=statement.executeQuery("SELECT * FROM RESTAURANT_BOOKINGS");
            while(rs.next())
            {
                int bookingNo=rs.getInt("bookingno");
                int customerNo=rs.getInt("custno");
                Date date=rs.getDate("date");
                int time=rs.getInt("time");
                hotel.getRestBookList().add(new RestaurantBooking(bookingNo, customerNo, date, time));
            }
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
        return loaded; // Return loaded variable
    }
    
    private void dbSetup()
    {
        //establishes connection to the database, creates the tables if they don't already exist
        String url="jdbc:derby:PDCHotel;create=true";
        try {
            conn=DriverManager.getConnection(url, "vsf2319", "Hotel123");
            Statement statement=conn.createStatement();
            if(!tableCheck("rooms"))
                statement.execute("CREATE TABLE rooms (roomno INT NOT NULL, type VARCHAR(10),"
                        + " bookingstatus BOOLEAN, price INT, PRIMARY KEY (roomno))");
            if(!tableCheck("customers"))
                statement.execute("CREATE TABLE customers (custno INT NOT NULL, name VARCHAR(25),"
                        + " email VARCHAR(50), phoneno BIGINT, PRIMARY KEY (custno))");
            if(!tableCheck("room_bookings"))
                statement.execute("CREATE TABLE room_bookings (bookingno INT NOT NULL, custno INT,"
                        + " roomno INT, date DATE, departure DATE, price INT, occupants INT,"
                        + " duration BIGINT, PRIMARY KEY (bookingno))");
            if(!tableCheck("restaurant_bookings"))
                statement.execute("CREATE TABLE restaurant_bookings (bookingno INT NOT NULL,"
                        + " custno INT, date DATE, time INT, PRIMARY KEY (bookingno))");
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
    }
    
    private boolean tableCheck(String table)
    {
        //helper method to create tables during initial db setup
        boolean exists=false;
        try {
            DatabaseMetaData dbmd=conn.getMetaData();
            ResultSet rs=dbmd.getTables(null, null, null, null);
            while(rs.next())
            {
                String tab=rs.getString("TABLE_NAME");
                if(tab.equalsIgnoreCase(table))
                    exists=true;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
        return exists;
    }
}
