/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cmk2481
 */
public class HotelBookerTest {
    public static Connection connection;
    public static String url="jdbc:derby:PDCHotel;create=true";
    
    public HotelBookerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        try {
            connection=DriverManager.getConnection(url, "vsf2319", "Hotel123");
        } catch (SQLException ex) {
            Logger.getLogger(HotelBookerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of newCustomer method, of class HotelBooker.
     */
    @Test
    public void testNewCustomer() {
        String name = "Rhea";
        String phone = "0210234567";
        String email = "rhea@gmail.com";
        
        HotelBooker instance = new HotelBooker("test", "test", 5);
        boolean expResult = false;
        boolean result = instance.newCustomer(name, phone, email);
        
       assertEquals(expResult, result);
    }

    /**
     * Test of loadCustomer method, of class HotelBooker.
     */
    @Test
    public void testLoadCustomer() {
        String email = "rhea@gmail.com";
        
        HotelBooker instance = new HotelBooker("test", "test", 5);
        boolean expResult = true;
        boolean result = instance.loadCustomer(email);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of createRoomBooking method, of class HotelBooker.
     */
    @Test
    public void testCreateRoomBooking() {
        Date start = Date.valueOf(LocalDate.of(2021, 05, 10));
        Date end = Date.valueOf(LocalDate.of(2021, 07, 12));
        String type = "Single";
        String occupants = "4";
        HotelBooker instance = new HotelBooker("test", "test", 5);
        boolean expResult = true;
        boolean result = instance.createRoomBooking(start, end, type, occupants);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of createRoomBooking method, of class HotelBooker.
     */
    @Test
    public void testCreateRoomBookingWrong() {
        Date start = Date.valueOf(LocalDate.of(2021, 05, 10));
        Date end = Date.valueOf(LocalDate.of(2021, 05, 10));
        String type = "Single";
        String occupants = "4";
        HotelBooker instance = new HotelBooker("test", "test", 5);
        boolean expResult = false;
        boolean result = instance.createRoomBooking(start, end, type, occupants);
        assertEquals(expResult, result);
    }

    /**
     * Test of createRestaurantBooking method, of class HotelBooker.
     */
    @Test
    public void testCreateRestaurantBooking() {
        Date date = Date.valueOf(LocalDate.of(2021, 07, 10));
        String time = "12:00";
        HotelBooker instance = new HotelBooker("test", "test", 5);
        boolean expResult = true;
        boolean result = instance.createRestaurantBooking(date, time);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of createRestaurantBooking method, of class HotelBooker.
     */
    @Test
    public void testCreateRestaurantBookingWrong() {
        Date date = Date.valueOf(LocalDate.of(2021, 05, 10));
        String time = "12:00";
        HotelBooker instance = new HotelBooker("test", "test", 5);
        boolean expResult = true;
        boolean result = instance.createRestaurantBooking(date, time);
        assertEquals(expResult, result);
    } 
}
