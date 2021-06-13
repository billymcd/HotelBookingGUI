/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class HotelBooker {
    private final Hotel hotel;
    private Customer currentCustomer;
    private String response;
    private Room currentRoom;
    private Booking currentBooking;
    private Connection conn;

    public HotelBooker(String name, String location, int rating) // Constructor initialises variable
    {
        this.hotel = new Hotel(name, location, rating);
    }
    
    public HotelBooker(String name, String location, int rating, Connection conn) // Constructor initialises variable
    {
        this(name, location, rating);
        this.conn=conn;
    }
    
    public static void main(String[] args) 
    {
        Connection conn=null;
        String url="jdbc:derby://localhost:1527/ PDCHotel; create=true";
        try {
            conn=DriverManager.getConnection(url, "vsf2319", "Hotel123");
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
        
        Scanner scan = new Scanner(System.in); // Initialise new scanner
        int running=1;
        int customerLoaded=0;
        HotelBooker hotBook=new HotelBooker("PDC Hotel", "Auckland", 5, conn);
        if(!hotBook.loadHotel()) //*****
        {
            System.out.println("Initialising first time setup.");
            hotBook.setupRooms(50, 25, 10);
        }
        
        while(running==1) // While program running //*****
        {
            hotBook.currentCustomer=null;
            customerLoaded=0;
            System.out.println("Welcome to PDC Hotel!"); // Welcome user
            System.out.println("");
            
            while(customerLoaded==0) // While customer not loaded, enter sequence
                customerLoaded=hotBook.checkCustomer();
            hotBook.response=null; // Reset response
            
            int selection=0;
            while(selection==0)
            {
                System.out.println("What would you like to do? ");
                System.out.println("(1) Book a room");
                System.out.println("(2) Book into the restaurant");
                System.out.println("(3) Enquire on an existing booking");
                System.out.println("Or enter any other number to return to the main menu.");
                try{
                    selection=scan.nextInt(); // Scan user input
                }catch(InputMismatchException e){
                    System.out.println("Invalid response, please try again."); // If user input not a whole number, prompt user to input again
                    scan.next();
                }
                System.out.println("");
            }
            
            while(selection==1&&customerLoaded==1) // While booking type is unchanged and customer loaded, enter sequence //****
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
                    enquiry=scan.nextInt(); // Scan user input
                }catch(InputMismatchException e){
                    System.out.println("Invalid response, please try again.\n"); // If user input not a whole number, prompt user to input again
                    scan.next();
                }
                System.out.println("");
                selection=hotBook.bookingEnquiry(enquiry, custNo);
            }
        }
        hotBook.saveHotel();
    }
    
    public void setupRooms(int singles, int doubles, int suites) // Depending of number of each type of room specified, create new rooms
    {
        for(int i=0;i<singles;i++) // Create new single rooms
            this.hotel.createRoom(1);
        for(int i=0;i<doubles;i++) // Create new double rooms
            this.hotel.createRoom(2);
        for(int i=0;i<suites;i++) // Create new suite rooms
            this.hotel.createRoom(3);
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
                this.loadCustomer();
                if(currentCustomer!=null) // If current customer loaded, set variable to match
                    customerLoaded=1;
                break;
            case "n": // If user indicates they haven't booked before
                currentCustomer=this.newCustomer(); // Create new customer and set as current customer
                if(currentCustomer!=null) // If current customer loaded, set variable to match
                    customerLoaded=1;
                break;
            case "q": // If user indicates they want to quit
                this.saveHotel();
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
        while(custCheck==0) // Enter sequence to create new customer
        {
            System.out.println("Create new customer account:");
            System.out.println("----------------------------");
            cust=this.hotel.createCustomer(); // Create a new customer
            if(cust==null) // If new customer instance unchanged
            {   
                System.out.println("");
                System.out.print("Account creation failed, enter Y to try again,"
                        + " anything else to cancel: ");
                String cResponse=scan.next().toLowerCase(); // Read user input
                if(!cResponse.equals("y"))
                    custCheck=1; // If user cancels, don't try to create customer account again
                System.out.println("");
            }
            else
            {
                System.out.println("");
                System.out.println("Customer account created!");
                System.out.println("-------------------------");
                System.out.println(cust.toString()); // Print customer details
                System.out.println("");
                custCheck=1; // Account created, so exit sequence
            }
        }
        return cust;
    }
    
    public void loadCustomer() // Load customer and set as current customer
    {
        Scanner scan = new Scanner(System.in);
        int emailCheck=0;
        while(emailCheck==0) // Enter sequence to load customer
        {
            System.out.print("Please enter email address for your account: ");
            String email=scan.next(); // Scan user input
            System.out.println("");
            Iterator itr=this.hotel.getCustomerList().iterator(); // Initialise iterator to look through customer list
            while(itr.hasNext()) // Look through customer list
            {
                Object element=itr.next();
                Customer cust=(Customer)element;
                if(email.equals(cust.getEmail())) // If user email matches any in customer list, customer account is loaded
                {
                    System.out.println("Customer account loaded!");
                    System.out.println("------------------------");
                    this.currentCustomer=cust; // Set current customer as customer found
                    System.out.println(this.currentCustomer.toString()); // Print customer details
                    emailCheck=1; // Customer loaded, so exit sequence
                }
            }
            if(emailCheck==0) // If customer not loaded
            {
                System.out.print("Email not found, type 'y' to"
                        + " try again, anything else to cancel: ");
                String retry=scan.next().toLowerCase(); //Sacn user input
                if(!retry.equals("y")) // If user cancels, don't try to load customer again
                    emailCheck=1;
            }
            System.out.println("");
        }
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
            roomType=scan.nextInt(); // Scan user input
        }catch(InputMismatchException e){
            System.out.println("Invalid response, please try again.\n"); // If user input not a whole number, prompt user to input again
        }
        if(roomType>0&&roomType<4)
        {
            available=this.checkAvailability(roomType); // Check if the type of room the user wants is available
            if(available==false) // If desired room not available
                System.out.println("Sorry, no rooms of that type are available.");
            else // If desired room available //*****
            {
                currentBooking=this.createRoomBooking(currentCustomer.getAccount(), currentRoom.getRoomNo()); // Create room booking
                System.out.println("Booking created!");
                System.out.println("----------------");
                System.out.println(currentBooking.toString()); // Print room booking details
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
        this.currentRoom=null;
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
                        this.currentRoom=room;
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
                        this.currentRoom=room;
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
                        this.currentRoom=room;
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
        LocalDate date=LocalDate.of(2000, 1, 1); // Initialise arrival date to 1 January 2000 //*****
        LocalDate depart=LocalDate.of(2000, 1, 1); // Initialise departure date to 1 January 2000 //*****
        Set<RoomBooking> bList=this.hotel.getRoomBookList(); 
        if(bList.isEmpty()) // If no room bookings in room booking list hashset, next room booking number is 1
            bookingNo=1;
        else
            bookingNo=bList.size()+1; // If room bookings in the room booking list hashset, next room booking number is 1 more than last
        while(date.isBefore(LocalDate.now())) // While arrival date is before current day, enter sequence
        {
            System.out.print("What date would you like the booking for? (YYYY-MM-DD) ");
            dateS=scan.next(); // Scan user input
            try{
                date=LocalDate.parse(dateS); // Try to get the user input as a valid date
            }catch(DateTimeParseException e){
                System.out.println("Please enter a valid date."); // Print if date invalid
                dateS="";
            }
            if(date.isBefore(LocalDate.now())&&!dateS.isEmpty()) // If date before current date entered
                System.out.println("Date must not be earlier than today's date.");
        }
        while(!depart.isAfter(date)) // While departure date before arrival date
        {
            System.out.print("Please enter departure date. (YYYY-MM-DD) ");
            dateD=scan.next(); //  Scan user input
            try{
                depart=LocalDate.parse(dateD); // Try to get the user input as a valid date
            }catch(DateTimeParseException e){
                System.out.println("Please enter a valid date."); // Print if date invalid
                dateD="";
            }
            if(!depart.isAfter(date)&&!dateD.isEmpty()) // If departure date before arrival date entered
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
//        this.currentBooking=newBook; // Set room booking created as current booking
        this.currentRoom.setBookingStatus(true); // Change booking status of room to booked
        hotel.addBooking(newBook);
        return newBook; // Return room booking created
    }
    
    public int restaurantBookingDetails()
    {
        RestaurantBooking booking=this.createRestaurantBooking(currentCustomer.getAccount()); // Create new restaurant booking
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
        LocalDate date=LocalDate.of(2000, 1, 1); // Set date to 1 January 2000
        Set<RestaurantBooking> bList=this.hotel.getRestBookList(); 
        
        if(bList.isEmpty()) // If no restaurant bookings in restaurant booking list hashset, next restaurant booking number is 1
            bookingNo=1; 
        else
            bookingNo=bList.size()+1; // If restaurant bookings in the restaurant booking list hashset, next restaurant booking number is 1 more than last
        
        while(date.isBefore(LocalDate.now())) // While booking date is before current date, enter sequence
        {
            System.out.print("What date would you like the booking for? (YYYY-MM-DD) ");
            dateS=scan.next(); // Scan user input
            try{
                date=LocalDate.parse(dateS); // Try to get the user input as a valid date
            }catch(DateTimeParseException e){
                System.out.println("Please enter a valid date."); // Print if date invalid
                dateS="";
            }
            if(date.isBefore(LocalDate.now())&&!dateS.isEmpty()) // If date before current date entered
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
                this.bookingEnquiry(1, custNo);
                this.bookingEnquiry(2, custNo);
                break;
            case 4:
                break;
            default:
                completed=3;
        }
        return completed;
    }
    
    public void saveHotel()
    {
        PrintWriter pw=null;
        File file=null;
        try 
        { // Save the hotel to a file //*****
            Set<Room> roomList=hotel.getRoomList();
            Set<Customer> customerList=hotel.getCustomerList();
            Set<RoomBooking> roomBookList=hotel.getRoomBookList();
            Set<RestaurantBooking> restBookList=hotel.getRestBookList();
            file=new File("Rooms.txt"); // Try to save to "PDC Hotel" file
            pw=new PrintWriter(new FileOutputStream(file)); //*****
            for(Room room : roomList) // Write each room object in the room list hashset to the file
            {
                if(room instanceof Single)
                    pw.write("Single,");
                else if(room instanceof Double)
                    pw.write("Double,");
                else
                    pw.write("Suite,");
                pw.write(room.getRoomNo()+",");
                pw.write(room.getPrice()+",");
                pw.write(room.getBookingStatus()+"");
                pw.println();
            }
            pw.close();
            file=new File("Customers.txt");
            pw=new PrintWriter(new FileOutputStream(file)); //*****
            for(Customer customer : customerList) // Write each customer object in the customer list hashset to the file
            {
                pw.write(customer.getAccount()+",");
                pw.write(customer.getName()+",");
                pw.write(customer.getEmail()+",");
                pw.write(customer.getPhone());
                pw.println();
            }
            pw.close();
            file=new File("RoomBookings.txt");
            pw=new PrintWriter(new FileOutputStream(file)); //*****
            for(RoomBooking book : roomBookList) // Write each customer object in the customer list hashset to the file
            {
                pw.write(book.getBookingNo()+",");
                pw.write(book.getCustomer()+",");
                pw.write(book.getRoom()+",");
                pw.write(book.getDate()+",");
                pw.write(book.getDeparture()+",");
                pw.write(book.getPrice()+",");
                pw.write(book.getOccupants()+",");
                pw.write(book.getDuration()+"");
                pw.println();
            }
            pw.close();
            file=new File("RestaurantBookings.txt");
            pw=new PrintWriter(new FileOutputStream(file)); //*****
            for(RestaurantBooking booking : restBookList) // Write each restaurant booking object in the restaurant booking list hashset to the file
            {
                pw.write(booking.getBookingNo()+",");
                pw.write(booking.getCustomer()+",");
                pw.write(booking.getDate()+",");
                pw.write(booking.getTime()+"");
                pw.println();
            }
            pw.close();
        } catch (FileNotFoundException ex) { // If file not found
            System.out.println("File "+file.getName()+" save failed.");
        } 
    }
    
    public boolean loadHotel() // Create the hotel from a file //*******
    {
        boolean loaded=true; // Assume hotel can be loaded from file
        File file=null;
        BufferedReader br=null;
        try 
        {
            file=new File("Rooms.txt");
            br=new BufferedReader(new FileReader(file));
            String line=null;
            while((line=br.readLine())!=null)
            {
                String type="";
                int roomNo=0;
                int price=0;
                boolean status=true;
                StringTokenizer st=new StringTokenizer(line, "\n,");
                while(st.hasMoreTokens())
                {
                    type=st.nextToken();
                    roomNo=Integer.parseInt(st.nextToken());
                    price=Integer.parseInt(st.nextToken());
                    status=st.nextToken().equals("true");
                }
                if(type.equals("Single"))
                    hotel.getRoomList().add(new Single(roomNo, price, status));
                if(type.equals("Double"))
                    hotel.getRoomList().add(new Double(roomNo, price, status));
                if(type.equals("Suite"))
                    hotel.getRoomList().add(new Suite(roomNo, price, status));
            }
            br.close();
            file=new File("Customers.txt");
            br=new BufferedReader(new FileReader(file));
            line=null;
            while((line=br.readLine())!=null)
            {
                int account=0;
                String name="";
                String email="";
                String phone="";
                StringTokenizer st=new StringTokenizer(line, "\n,");
                while(st.hasMoreTokens())
                {
                    account=Integer.parseInt(st.nextToken());
                    name=st.nextToken();
                    email=st.nextToken();
                    phone=st.nextToken();
                }
                hotel.getCustomerList().add(new Customer(account, name, email, phone));
            }
            br.close();
            file=new File("RoomBookings.txt");
            br=new BufferedReader(new FileReader(file));
            line=null;
            while((line=br.readLine())!=null)
            {
                int bookingNo=0;
                int customerNo=0;
                int roomNo=0;
                LocalDate date=null;
                LocalDate departure=null;
                int price=0;
                int occupants=0;
                long duration=0;
                StringTokenizer st=new StringTokenizer(line, "\n,");
                while(st.hasMoreTokens())
                {
                    bookingNo=Integer.parseInt(st.nextToken());
                    customerNo=Integer.parseInt(st.nextToken());
                    roomNo=Integer.parseInt(st.nextToken());
                    date=LocalDate.parse(st.nextToken());
                    departure=LocalDate.parse(st.nextToken());
                    price=Integer.parseInt(st.nextToken());
                    occupants=Integer.parseInt(st.nextToken());
                    duration=Long.parseLong(st.nextToken());
                }
                hotel.getRoomBookList().add(new RoomBooking(bookingNo, customerNo, roomNo, date, departure, price, occupants, duration));
            }
            br.close();
            file=new File("RestaurantBookings.txt");
            br=new BufferedReader(new FileReader(file));
            line=null;
            while((line=br.readLine())!=null)
            {
                int bookingNo=0;
                int customerNo=0;
                LocalDate date=null;
                int time=0;
                StringTokenizer st=new StringTokenizer(line, "\n,");
                while(st.hasMoreTokens())
                {
                    bookingNo=Integer.parseInt(st.nextToken());
                    customerNo=Integer.parseInt(st.nextToken());
                    date=LocalDate.parse(st.nextToken());
                    time=Integer.parseInt(st.nextToken());
                }
                hotel.getRestBookList().add(new RestaurantBooking(bookingNo, customerNo, date, time));
            }
            br.close();
        } catch (FileNotFoundException ex) // If file not found, indicate file not loaded
        {
            loaded=false;
        } catch (IOException ex) //******
        {
            loaded=false;
        }
        return loaded; // Return loaded variable
    }
}
