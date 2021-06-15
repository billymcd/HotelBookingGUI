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
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public final class HotelBooker {
    public final Hotel hotel;
    public Customer currentCustomer;
    private Room currentRoom;
    private Connection conn=null;

    public HotelBooker(String name, String location, int rating)
    {
        dbSetup();
        hotel = new Hotel(name, location, rating, conn);
        loadHotel();
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
    
    //creates a new customer from the parameters passed in, returns null if any input is invalid
    public boolean newCustomer(String name, String phone, String email)
    {
        Customer cust=hotel.createCustomer(name, phone, email);
        if(cust!=null)
        {
            PreparedStatement statement;
            try {
                statement=conn.prepareStatement("INSERT INTO CUSTOMERS VALUES (?, ?, ?, ?)");
                statement.setInt(1, cust.getAccount());
                statement.setString(2, cust.getName());
                statement.setString(3, cust.getEmail());
                statement.setString(4, cust.getPhone());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: "+ex.getMessage());
            }
            currentCustomer=cust;
            return true;
        }
        return false;
    }
    
    //Method to search and load an existing customer from the database
    public boolean loadCustomer(String email)
    {
        Iterator itr=hotel.getCustomerList().iterator();
        while(itr.hasNext())
        {
            Object element=itr.next();
            Customer cust=(Customer)element;
            if(email.equalsIgnoreCase(cust.getEmail()))
            {
                currentCustomer=cust;
                return true;
            }
        }
        return false;
    }
    
    public boolean checkAvailability(String type)
    {
        currentRoom=null;
        boolean available=false;
        Iterator itr=hotel.getRoomList().iterator();
        if(type.equalsIgnoreCase("single"))
        {
            while(itr.hasNext()&&available==false)
            {
                Room room=(Room)itr.next();
                if(room instanceof Single && room.getBookingStatus()==false)
                {
                    available=true;
                    currentRoom=room;
                }
            }
        }
        else if(type.equalsIgnoreCase("double"))
        {
            while(itr.hasNext()&&available==false)
            {
                Room room=(Room)itr.next();
                if(room instanceof Double && room.getBookingStatus()==false)
                {
                    available=true;
                    currentRoom=room;
                }
            }
        }
        else if(type.equalsIgnoreCase("suite"))
        {
            while(itr.hasNext()&&available==false)
            {
                Room room=(Room)itr.next();
                if(room instanceof Suite && room.getBookingStatus()==false)
                {
                    available=true;
                    currentRoom=room;
                }
            }
        }
        return available;
    }
    
    public boolean createRoomBooking(Date start, Date end, String type, String occupants)
    {
        int bookingNo;
        int occ;
        try{
            occ=Integer.parseInt(occupants);
        }catch(NumberFormatException e){
            return false;
        }
        if(start.before(Calendar.getInstance().getTime()))
            return false;
        if(end.before(start))
            return false;
        
        Set<Booking> bList=hotel.getRoomBookList(); 
        if(bList.isEmpty())
            bookingNo=1;
        else
            bookingNo=bList.size()+1;
        RoomBooking newBook=new RoomBooking(bookingNo, currentCustomer.getAccount(), 
                currentRoom.getRoomNo(), start);
        newBook.setDeparture(end);
        newBook.setDuration();
        newBook.setPrice(currentRoom.getPrice());
        newBook.setOccupants(occ);
        currentRoom.setBookingStatus(true);
        hotel.addBooking(newBook);
        
        commitRoomBooking(bookingNo, start, end, newBook, occ);
        return true;
    }
        
    public RestaurantBooking createRestaurantBooking(Date date, String time)
    {
        int bookingNo;
        Set<Booking> bList=hotel.getRestBookList(); 
        
        if(bList.isEmpty())
            bookingNo=1; 
        else
            bookingNo=bList.size()+1;
        RestaurantBooking newBook=new RestaurantBooking(bookingNo, currentCustomer.getAccount(), date, time);
        hotel.addBooking(newBook);
        
        commitNewRestBooking(bookingNo, date, time);
        return newBook;
    }
    
    //Run on start up to load all saved data from the database
    public void loadHotel()
    {
        loadRooms();
        loadCustomers();
        loadRoomBookings();
        loadRestBookings();
        //If there's no rooms to load ie first time running, generate a new set of rooms
        if(hotel.getRoomList().isEmpty())
        {
            System.out.println("Initialising first time setup.");
            setupRooms(50, 25, 10);
        }
    }
    
    //establishes connection to the database, creates the tables if they don't already exist
    private void dbSetup()
    {
        String url="jdbc:derby:PDCHotel;create=true";
        try {
            conn=DriverManager.getConnection(url, "vsf2319", "Hotel123");
            Statement statement=conn.createStatement();
            if(!tableCheck("rooms"))
                statement.execute("CREATE TABLE rooms (roomno INT NOT NULL, type VARCHAR(10),"
                        + " bookingstatus BOOLEAN, price INT, PRIMARY KEY (roomno))");
            if(!tableCheck("customers"))
                statement.execute("CREATE TABLE customers (custno INT NOT NULL, name VARCHAR(50),"
                        + " email VARCHAR(50), phoneno VARCHAR(15), PRIMARY KEY (custno))");
            if(!tableCheck("room_bookings"))
                statement.execute("CREATE TABLE room_bookings (bookingno INT NOT NULL, custno INT,"
                        + " roomno INT, date DATE, departure DATE, price INT, occupants INT,"
                        + " duration BIGINT, PRIMARY KEY (bookingno))");
            if(!tableCheck("restaurant_bookings"))
                statement.execute("CREATE TABLE restaurant_bookings (bookingno INT NOT NULL,"
                        + " custno INT, date DATE, time VARCHAR(5), PRIMARY KEY (bookingno))");
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
    }
    
    //helper method to create tables during initial db setup
    private boolean tableCheck(String table)
    {
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
    
    public List<Booking> currentCustBookings(String type)
    {
        List<Booking> set=new LinkedList<>();
        int account=currentCustomer.getAccount();
        Iterator itr=null;
        if(type.equalsIgnoreCase("room"))
            itr=hotel.getRoomBookList().iterator();
        if(type.equalsIgnoreCase("rest"))
            itr=hotel.getRestBookList().iterator();
        if(itr!=null)
        {
            while(itr.hasNext())
            {
                Booking book=(Booking)itr.next();
                if(book.customerNo==account)
                    set.add(book);
            }
            return set;
        }
        return null;
    }
    
    private void commitRoomBooking(int bookingNo, Date start, Date end, RoomBooking booking, int occupants)
    {
        PreparedStatement statement;
        try {
            statement=conn.prepareStatement("INSERT INTO ROOM_BOOKINGS VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, bookingNo);
            statement.setInt(2, currentCustomer.getAccount());
            statement.setInt(3, currentRoom.getRoomNo());
            statement.setDate(4, start);
            statement.setDate(5, end);
            statement.setInt(6, booking.getPrice());
            statement.setInt(7, occupants);
            statement.setLong(8, booking.getDuration());
            statement.executeUpdate();
            statement.close();
            //Update room status in the database
            statement=conn.prepareStatement("UPDATE ROOMS SET BOOKINGSTATUS=? WHERE ROOMNO=?");
            statement.setBoolean(1, true);
            statement.setInt(2, currentRoom.getRoomNo());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
    }
    
    private void commitNewRestBooking(int bookingNo, Date date, String time)
    {
        PreparedStatement statement;
        try {
            statement=conn.prepareStatement("INSERT INTO RESTAURANT_BOOKINGS VALUES (?, ?, ?, ?)");
            statement.setInt(1, bookingNo);
            statement.setInt(2, currentCustomer.getAccount());
            statement.setDate(3, date);
            statement.setString(4, time);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(HotelBooker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadRooms()
    {
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
    }
    
    private void loadCustomers()
    {
        Statement statement;
        try {
            statement=conn.createStatement();
            ResultSet rs=statement.executeQuery("SELECT * FROM CUSTOMERS");
            while(rs.next())
            {
                int account=rs.getInt("custno");
                String name=rs.getString("name");
                String email=rs.getString("email");
                String phone=rs.getString("phoneno");
                hotel.getCustomerList().add(new Customer(account, name, email, phone));
            }
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
    }
    
    private void loadRoomBookings()
    {
        Statement statement;
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
    }
    
    private void loadRestBookings()
    {
        Statement statement;
        try {
            statement=conn.createStatement();
            ResultSet rs=statement.executeQuery("SELECT * FROM RESTAURANT_BOOKINGS");
            while(rs.next())
            {
                int bookingNo=rs.getInt("bookingno");
                int customerNo=rs.getInt("custno");
                Date date=rs.getDate("date");
                String time=rs.getString("time");
                hotel.getRestBookList().add(new RestaurantBooking(bookingNo, customerNo, date, time));
            }
            statement.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
    }
}
