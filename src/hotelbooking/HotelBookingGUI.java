/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbooking;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

//image obtained for free from the following url
//https://www.pexels.com/photo/bedroom-door-entrance-guest-room-271639/

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class HotelBookingGUI extends JPanel
{
    private HotelBooker hotBook;
    public final int PANEL_WIDTH=600;
    public final int PANEL_HEIGHT=400;
    private JPanel welcomePanel, eCustPanel, newCustPanel, bookingPanel, enquiryPanel, 
            northPanel, southPanel, centralPanel, roomBooking, restBooking;
    private JButton loginButton, exitButton, newCustomerButton, existingCustButton,
            acceptRoom, bookingButton, enquiryButton, logoutButton, acceptRest, acceptCust;
    private JTextField emailField, nameField, newEmailField, phoneField, 
            bookingDetails, occupants;
    private JLabel welcomeMessage;
    private JDatePanelImpl datePanel, startPanel, departPanel;
    private JDatePickerImpl datePicker, startPicker, departPicker;
    private BookingTableModel model;
    private JTable bookings;
    private String welcome, cust, roomType, time;
    private ButtonListener bListener;
    private List<Booking> restBookings, roomBookings;
    private ImageIcon welcomePic;
    private JTabbedPane centralBooking;
    private JComboBox roomPicker, timePicker;
    private ComboBoxListener cbListener;
    
    public HotelBookingGUI()
    {
        super(new BorderLayout());
        hotBook=new HotelBooker("PDC Hotel", "Auckland", 5);
        setupButtons();
        setupPanels();
        add(centralPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
    }
    
    public static void main(String[] args) 
    {
        HotelBookingGUI hotelPanel=new HotelBookingGUI();
        JFrame frame=new JFrame("PDC Hotel Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(hotelPanel);
        frame.pack();
        Toolkit tk=Toolkit.getDefaultToolkit();
        Dimension d=tk.getScreenSize();
        int screenHeight=d.height;
        int screenWidth=d.width;
        frame.setLocation(new Point((screenWidth/2)-(frame.getWidth()/2),(screenHeight/2)-(frame.getHeight()/2)));
        frame.setVisible(true);
    }
    //listener for all the buttons, invoking helper methods to shrink the class size considerably
    private class ButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if(e.getSource().equals(exitButton))
                System.exit(0);
            if(e.getSource().equals(newCustomerButton))
            {
                updateCentralPanel(newCustPanel);
                repaint();
            }
            if(e.getSource().equals(existingCustButton))
            {
                updateCentralPanel(eCustPanel);
                repaint();
            }
            if(e.getSource().equals(loginButton))
            {
                loadCustomer();
                repaint();
            }
            if(e.getSource().equals(logoutButton))
            {
                logout();
                repaint();
            }
            if(e.getSource().equals(bookingButton))
            {
                updateCentralPanel(bookingPanel);
                repaint();
            }
            if(e.getSource().equals(enquiryButton))
            {
                updateCentralPanel(enquiryPanel);
                repaint();
            }
            if(e.getSource().equals(acceptCust))
            {
                createCustomer();
                repaint();
            }
            if(e.getSource().equals(acceptRoom))
            {
                createRoomBooking();
                repaint();
            }
            if(e.getSource().equals(acceptRest))
            {
                createRestBooking();
                repaint();
            }
        }
    }
    
    private class ComboBoxListener implements ItemListener
    {
        @Override
        public void itemStateChanged(ItemEvent e) 
        {
            if(e.getSource().equals(roomPicker))
            {
                if(e.getStateChange()==ItemEvent.SELECTED)
                {
                    roomType=roomPicker.getSelectedItem().toString();
                    if(!hotBook.checkAvailability(roomType))
                    {
                        JOptionPane.showMessageDialog(bookingPanel, "No "+roomType+" rooms are"
                                +" available.", "Not Available", JOptionPane.WARNING_MESSAGE);
                        roomType=null;
                    }
                }
            }
            if(e.getSource().equals(timePicker))
                if(e.getStateChange()==ItemEvent.SELECTED)
                    time=timePicker.getSelectedItem().toString();
        }
    }
    //method to change the buttons after logout
    private void buttonChanger(JButton button1, JButton button2, JButton button3)
    {
        southPanel.removeAll();
        southPanel.add(button1);
        southPanel.add(button2);
        southPanel.add(button3);
        southPanel.validate();
    }
    //same as above but for login
    private void buttonChanger(JButton button1, JButton button2, JButton button3, JButton button4)
    {
        buttonChanger(button1, button2, button3);
        southPanel.add(button4);
        southPanel.validate();
    }
    //change panel displayed in center based on selections
    private void updateCentralPanel(JPanel panel)
    {
        centralPanel.removeAll();
        centralPanel.add(panel);
        centralPanel.validate();
    }
    //method to set up all the buttons and their listeners
    private void setupButtons()
    {
        bListener=new ButtonListener();
        exitButton=new JButton("Exit");
        exitButton.addActionListener(bListener);
        newCustomerButton=new JButton("New Customer");
        newCustomerButton.addActionListener(bListener);
        existingCustButton=new JButton("Existing Customer");
        existingCustButton.addActionListener(bListener);
        loginButton=new JButton("Login");
        loginButton.addActionListener(bListener);
        acceptRoom=new JButton("Accept");
        acceptRoom.addActionListener(bListener);
        acceptRest=new JButton("Accept");
        acceptRest.addActionListener(bListener);
        bookingButton=new JButton("Booking");
        bookingButton.addActionListener(bListener);
        enquiryButton=new JButton("Enquiry");
        enquiryButton.addActionListener(bListener);
        logoutButton=new JButton("Logout");
        logoutButton.addActionListener(bListener);
        acceptCust=new JButton("Accept");
        acceptCust.addActionListener(bListener);
    }
    //calls methods to set up each of the main panels
    private void setupPanels()
    {
        topPanel();
        centralPanel();
        existingCustomer();
        newCustomer();
        bookings();
        enquiryPanel();
        buttonPanel();
    }
    
    private void topPanel()
    {
        northPanel=new JPanel();
        welcome="Welcome to the PDC Hotel Booking System";
        welcomeMessage=new JLabel(welcome);
        welcomeMessage.setHorizontalAlignment(SwingConstants.CENTER);
        northPanel.add(welcomeMessage);
    }
    
    private void centralPanel()
    {
        centralPanel=new JPanel();
        welcomePanel=new JPanel();
        welcomePanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        welcomePic=new ImageIcon("Images/stock-hotel-image.jpg");
        welcomePanel.add(new JLabel(welcomePic));
        centralPanel.add(welcomePanel);
    }
    
    private void existingCustomer()
    {
        eCustPanel=new JPanel(new BorderLayout());
        eCustPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        emailField=new JTextField();
        JLabel email=new JLabel("Email: ");
        GroupLayout layout=new GroupLayout(eCustPanel);
        eCustPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(email).addComponent(emailField)).addComponent(loginButton));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(email).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emailField).addComponent(loginButton)));
    }
    
    private void newCustomer()
    {
        newCustPanel=new JPanel();
        newCustPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        nameField=new JTextField();
        newEmailField=new JTextField();
        phoneField=new JTextField();
        JLabel name=new JLabel("Name: ");
        JLabel email=new JLabel("Email: ");
        JLabel phone=new JLabel("Phone no: ");
        GroupLayout layout=new GroupLayout(newCustPanel);
        newCustPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(name).addComponent(nameField).addComponent(acceptCust)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(email).addComponent(newEmailField)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(phone).addComponent(phoneField)));
        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(name).addComponent(email).addComponent(phone)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameField).addComponent(newEmailField).addComponent(phoneField)).addComponent(acceptCust));
    }
    
    private void bookings()
    {
        bookingPanel=new JPanel();
        bookingPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        centralBooking=new JTabbedPane();
        cbListener=new ComboBoxListener();
        setupDatePanels();
        roomPanel();
        restPanel();
        centralBooking.addTab("Room Booking", roomBooking);
        centralBooking.addTab("Restaurant Booking", restBooking);
        bookingPanel.add(centralBooking);
    }
    //setup for the date panels used for making bookings
    private void setupDatePanels()
    {
        SqlDateModel model1=new SqlDateModel();
        SqlDateModel model2=new SqlDateModel();
        SqlDateModel model3=new SqlDateModel();
        Properties p=new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        datePanel=new JDatePanelImpl(model1, p);
        startPanel=new JDatePanelImpl(model2, p);
        departPanel=new JDatePanelImpl(model3, p);
    }
    //room booking panel setup
    private void roomPanel()
    {
        roomBooking=new JPanel(new BorderLayout());
        occupants=new JTextField(2);
        roomPicker=new JComboBox(new String[] {"Single", "Double", "Suite"});
        roomPicker.setSelectedIndex(-1);
        roomPicker.addItemListener(cbListener);
        startPicker=new JDatePickerImpl(startPanel, new DateLabelFormatter());
        departPicker=new JDatePickerImpl(departPanel, new DateLabelFormatter());
        JPanel top=new JPanel();
        JPanel bot=new JPanel();
        top.add(new JLabel("Arrival: "));
        top.add(startPicker);
        top.add(new JLabel("Departure: "));
        top.add(departPicker);
        roomBooking.add(top, BorderLayout.NORTH);
        bot.add(new JLabel("Room type: "));
        bot.add(roomPicker);
        bot.add(new JLabel("No. of occupants: "));
        bot.add(occupants);
        bot.add(acceptRoom);
        roomBooking.add(bot);
    }
    //restaurant booking panel setup
    private void restPanel()
    {
        restBooking=new JPanel();
        String[] times=setupTimes(1100, 2300);
        timePicker=new JComboBox(times);
        timePicker.setSelectedIndex(-1);
        timePicker.addItemListener(cbListener);
        datePicker=new JDatePickerImpl(datePanel, new DateLabelFormatter());
        restBooking.add(new JLabel("Date: "));
        restBooking.add(datePicker);
        restBooking.add(new JLabel("Time: "));
        restBooking.add(timePicker);
        restBooking.add(acceptRest);
    }
    //helper method to setup an array of strings representing times for restaurant bookings
    private String[] setupTimes(int open, int close)
    {
        int totalOpen=close-open;
        int availableTimes=totalOpen/25+1;
        int hours=totalOpen/100;
        String[] times=new String[availableTimes];
        int index=0;
        int hour=open/100;
        for(int i=0;i<hours;i++)
        {
            for(int j=0;j<4;j++)
            {
                if(j==0)
                    times[index]=hour+":00";
                else
                    times[index]=hour+":"+j*15;
                index++;
            }
            hour++;
        }
        times[index]=hour+":00";
        return times;
    }
    
    private void enquiryPanel()
    {
        tableSetup();
        enquiryPanel=new JPanel();
        bookingDetails=new JTextField();
        bookingDetails.setEditable(false);
        bookingDetails.setSize(250, 100);
        JLabel label=new JLabel("Booking details:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        enquiryPanel.add(new JScrollPane(bookings));
    }
    
    private void buttonPanel()
    {
        southPanel=new JPanel();
        southPanel.add(newCustomerButton);
        southPanel.add(existingCustButton);
        southPanel.add(exitButton);
    }
    //set of actions to take when a customer logs out; ie resetting all the fields etc.
    private void logout()
    {
        hotBook.currentCustomer=null;
        updateCentralPanel(welcomePanel);
        buttonChanger(newCustomerButton, existingCustButton, exitButton);
        welcomeMessage.setText(welcome);
        emailField.setText("");
        nameField.setText("");
        newEmailField.setText("");
        phoneField.setText("");
    }
    //load a returning customer
    private void loadCustomer()
    {
        String email=emailField.getText();
        boolean loaded=hotBook.loadCustomer(email);
        if(!loaded)
            JOptionPane.showMessageDialog(eCustPanel, "Email not found, please "
                    + "try again.", "Not Found", JOptionPane.WARNING_MESSAGE);
        else
        {
            login();
            emailField.setText("");
        }
    }
    //create a new customer
    private void createCustomer()
    {
        String name=nameField.getText();
        String email=newEmailField.getText();
        String phone=phoneField.getText();
        boolean created=hotBook.newCustomer(name, phone, email);
        if(!created)
            JOptionPane.showMessageDialog(newCustPanel, "Failed to create account. Please check "
                    +"input and try again.", "Account not created", JOptionPane.WARNING_MESSAGE);
        else
        {
            login();
            nameField.setText("");
            newEmailField.setText("");
            phoneField.setText("");
        }
    }
    //actions to take on login, ie ensuring all fields are set for the current customer
    private void login()
    {
        updateCentralPanel(welcomePanel);
        buttonChanger(bookingButton, enquiryButton, logoutButton, exitButton);
        cust=hotBook.currentCustomer.toString();
        welcomeMessage.setText(cust);
        restBookings=hotBook.currentCustBookings("rest");
        roomBookings=hotBook.currentCustBookings("room");
        model.removeAll();
        model.add(roomBookings);
        model.add(restBookings);
    }
    
    private void createRoomBooking()
    {
        Date start=(Date)startPicker.getModel().getValue();
        Date end=(Date)departPicker.getModel().getValue();
        String occupantNo=occupants.getText();
        boolean created=hotBook.createRoomBooking(start, end, roomType, occupantNo);
        if(!created)
            JOptionPane.showMessageDialog(welcomePanel, "Cannot create booking, please check "
                    +"details and try again.", "Booking failed", JOptionPane.WARNING_MESSAGE);
        else
        {
            startPicker.getModel().setSelected(false);
            departPicker.getModel().setSelected(false);
            occupants.setText("");
            roomPicker.setSelectedIndex(-1);
            login();
            updateCentralPanel(enquiryPanel);
        }
    }
    
    private void createRestBooking()
    {
        Date date=(Date)datePicker.getModel().getValue();
        hotBook.createRestaurantBooking(date, time);
        datePicker.getModel().setSelected(false);
        timePicker.setSelectedIndex(-1);
        login();
        updateCentralPanel(enquiryPanel);
    }
    //formatter class for JDatePicker
    public class DateLabelFormatter extends AbstractFormatter 
    {
        private String datePattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
        
        @Override
        public Object stringToValue(String text) throws ParseException 
        {
            return dateFormatter.parseObject(text);
        }
        
        @Override
        public String valueToString(Object value) throws ParseException 
        {
            if (value != null) 
            {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
    //class to set a model for the table displaying customers bookings
    public static class BookingTableModel extends AbstractTableModel {

        protected static final String[] COLUMN_NAMES = {"Booking #", "Date", "Type", "Cost", "Time"};

        private List<Booking> rowData;

        public BookingTableModel() {
            rowData = new ArrayList<>(25);
        }

        public void add(Booking... booking) {
            add(Arrays.asList(booking));
        }

        public void add(List<Booking> booking) {
            rowData.addAll(booking);
            fireTableDataChanged();
        }
        
        public void removeAll()
        {
            rowData.clear();
        }

        @Override
        public int getRowCount() {
            return rowData.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        public Booking getBookingAt(int row) {
            return rowData.get(row);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Booking booking = getBookingAt(rowIndex);
            Object value = null;
            switch (columnIndex) {
                case 0:
                    value = booking.getBookingNo();
                    break;
                case 1:
                    value = booking.getDate();
                    break;
                case 2:
                    if(booking instanceof RoomBooking)
                        value = "Room";
                    if(booking instanceof RestaurantBooking)
                        value = "Restaurant";
                    break;
                case 3:
                    if(booking instanceof RoomBooking)
                        value = ((RoomBooking)booking).getPrice();
                    else
                        value = "-";
                    break;
                case 4:
                    if(booking instanceof RestaurantBooking)
                        value = ((RestaurantBooking) booking).getTime();
                    else
                        value = "-";
                    break;
            }
            return value;
        }
    }
    //set up the bookings enquiry table
    private void tableSetup()
    {
        model=new BookingTableModel();
        bookings = new JTable(model);
        bookings.setShowGrid(false);
        bookings.setShowHorizontalLines(false);
        bookings.setShowVerticalLines(false);
        bookings.setRowMargin(0);
        bookings.setIntercellSpacing(new Dimension(0, 0));
        bookings.setFillsViewportHeight(true);
        TableRowSorter<BookingTableModel> sorter = new TableRowSorter<>(model);
        bookings.setRowSorter(sorter);
    }
}