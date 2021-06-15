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
import java.util.Calendar;
import java.util.Properties;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    private JList roomList, restList;
    private DefaultListModel roomListModel, restListModel;
    private String welcome, cust, roomType, time;
    private ButtonListener bListener;
    private Set<Booking> restBookings, roomBookings;
    private ListListener lListener;
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
        
        //adding subpanels to primary panel
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
//                System.out.println(roomList);
//                String[] s=(String[])restList.getSelectedValues();
//                System.out.println(s[0]);
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
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
    }
    
    private class ListListener implements ListSelectionListener
            {
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        System.out.println("First index: " + listSelectionEvent.getFirstIndex());
        System.out.println(", Last index: " + listSelectionEvent.getLastIndex());
        boolean adjust = listSelectionEvent.getValueIsAdjusting();
        System.out.println(", Adjusting? " + adjust);
        if (!adjust) {
          JList list = (JList) listSelectionEvent.getSource();
          int selections[] = list.getSelectedIndices();
          Object selectionValues[] = list.getSelectedValues();
          for (int i = 0, n = selections.length; i < n; i++) {
            if (i == 0) {
              System.out.println(" Selections: ");
            }
            System.out.println(selections[i] + "/" + selectionValues[i] + " ");
          }
        }
      }
    }
//    {
//        @Override
//        public void valueChanged(ListSelectionEvent e) 
//        {
//            if(e.getSource().equals(roomList))
//            {
//                System.out.println("click");
//                String selection=(String)roomList.getSelectedValue();
//                System.out.println(selection);
//            }
//        }
//    }
    
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
                time=(String)timePicker.getSelectedItem();
        }
    }
    
    private void buttonChanger(JButton button1, JButton button2, JButton button3)
    {
        southPanel.removeAll();
        southPanel.add(button1);
        southPanel.add(button2);
        southPanel.add(button3);
        southPanel.validate();
    }
    
    private void buttonChanger(JButton button1, JButton button2, JButton button3, JButton button4)
    {
        buttonChanger(button1, button2, button3);
        southPanel.add(button4);
        southPanel.validate();
    }
    
    private void updateCentralPanel(JPanel panel)
    {
        centralPanel.removeAll();
        centralPanel.add(panel);
        centralPanel.validate();
    }
    
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
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(email).addComponent(emailField)).addComponent(loginButton));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(email).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(emailField).addComponent(loginButton)));
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
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(name).addComponent(nameField).addComponent(acceptCust)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(email).addComponent(newEmailField)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(phone).addComponent(phoneField)));
        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(name).addComponent(email).addComponent(phone)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(nameField).addComponent(newEmailField).addComponent(phoneField)).addComponent(acceptCust));
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
    
    private void setupDatePanels()
    {
        SqlDateModel model=new SqlDateModel();
        SqlDateModel model2=new SqlDateModel();
        SqlDateModel model3=new SqlDateModel();
        Properties p=new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        datePanel=new JDatePanelImpl(model, p);
        startPanel=new JDatePanelImpl(model2, p);
        departPanel=new JDatePanelImpl(model3, p);
    }
    
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
    
    private void restPanel()
    {
        restBooking=new JPanel();
        String[] times=setupTimes(1100, 2300);
        timePicker=new JComboBox(times);
        timePicker.addItemListener(cbListener);
        datePicker=new JDatePickerImpl(datePanel, new DateLabelFormatter());
        restBooking.add(new JLabel("Date: "));
        restBooking.add(datePicker);
        restBooking.add(new JLabel("Time: "));
        restBooking.add(timePicker);
        restBooking.add(acceptRest);
    }
    
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
        setupLists();
        enquiryPanel=new JPanel(new BorderLayout());
        bookingDetails=new JTextField();
        bookingDetails.setEditable(false);
        JTabbedPane enquiryPanels=new JTabbedPane();
        JPanel bot=new JPanel();
        JLabel label=new JLabel("Booking details:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        bot.add(label);
        bot.add(bookingDetails);
        enquiryPanels.add("Room Bookings", new JScrollPane(roomList));
        enquiryPanels.add("Restaurant Bookings", new JScrollPane(restList));
        enquiryPanel.add(enquiryPanels, BorderLayout.NORTH);
//        enquiryPanel.add(bot, BorderLayout.SOUTH);
        enquiryPanel.add(label, BorderLayout.CENTER);
        enquiryPanel.add(bookingDetails, BorderLayout.SOUTH);
        
    }
    
    private void setupLists()
    {
        roomListModel=new DefaultListModel();
        roomList=new JList(roomListModel);
        roomList.addListSelectionListener(lListener);
        roomList.setPreferredSize(new Dimension(250, 100));
        restListModel=new DefaultListModel();
        restList=new JList(restListModel);
        restList.addListSelectionListener(lListener);
        restList.setPreferredSize(new Dimension(250, 100));
    }
    
    private void buttonPanel()
    {
        southPanel=new JPanel();
        southPanel.add(newCustomerButton);
        southPanel.add(existingCustButton);
        southPanel.add(exitButton);
    }
    
    private void loadList(Set<Booking> bookings, JList list)
    {
        DefaultListModel model;
        if(list.equals(roomList))
            model=roomListModel;
        else
            model=restListModel;
        for(Booking booking : bookings)
            model.addElement(booking);
    }
    
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
        roomListModel.clear();
        restListModel.clear();
    }
    
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
    
    private void login()
    {
        updateCentralPanel(welcomePanel);
        buttonChanger(bookingButton, enquiryButton, logoutButton, exitButton);
        cust=hotBook.currentCustomer.toString();
        welcomeMessage.setText(cust);
        restBookings=hotBook.currentCustBookings("rest");
        roomBookings=hotBook.currentCustBookings("room");
        loadList(roomBookings, roomList);
        loadList(restBookings, restList);
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
            login();
            startPicker.getModel().setSelected(false);
            departPicker.getModel().setSelected(false);
            occupants.setText("");
            roomPicker.setSelectedIndex(-1);
        }
    }
    
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
}