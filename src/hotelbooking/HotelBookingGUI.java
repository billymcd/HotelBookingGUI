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
import java.util.Set;
import javafx.scene.control.DatePicker;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class HotelBookingGUI extends JPanel
{
    private HotelBooker hotBook;
    public final int PANEL_WIDTH=600;
    public final int PANEL_HEIGHT=600;
    private JPanel welcomePanel, eCustPanel, newCustPanel, bookingPanel, enquiryPanel, 
            northPanel, southPanel, centralPanel, centralBooking, roomBooking, restBooking;
    private JButton loginButton, exitButton, acceptButton, newCustomerButton, 
            existingCustButton, roomButton, restaurantButton, bookingButton, 
            enquiryButton, logoutButton, startDate, endDate, date;
    private JTextField emailField, nameField, newEmailField, phoneField;
    private JLabel welcomeMessage;
    private DatePicker datePicker;
    private JList roomList, restList;
    private String welcome, cust;
    private ButtonListener bListener;
    private Set<RestaurantBooking> bookings;
    private Set<RoomBooking> roomBookings;
    private ListListener lListener;
    
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
                loadList(roomBookings, roomList);
                loadList(bookings, restList);
                repaint();
            }
        }
    }
    
    private class ListListener implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent e) 
        {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        acceptButton=new JButton("Accept");
        acceptButton.addActionListener(bListener);
        bookingButton=new JButton("Booking");
        bookingButton.addActionListener(bListener);
        roomButton=new JButton("Room");
        roomButton.addActionListener(bListener);
        restaurantButton=new JButton("Restaurant");
        restaurantButton.addActionListener(bListener);
        enquiryButton=new JButton("Enquiry");
        enquiryButton.addActionListener(bListener);
        logoutButton=new JButton("Logout");
        logoutButton.addActionListener(bListener);
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
        welcomePanel.add(new JLabel("Placeholder for image"));
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
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(name).addComponent(nameField).addComponent(acceptButton)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(email).addComponent(newEmailField)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(phone).addComponent(phoneField)));
        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(name).addComponent(email).addComponent(phone)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(nameField).addComponent(newEmailField).addComponent(phoneField)).addComponent(acceptButton));
    }
    
    private void bookings()
    {
        bookingPanel=new JPanel(new BorderLayout());
        bookingPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        centralBooking=new JPanel();
        roomBooking=new JPanel();
        restBooking=new JPanel();
        JPanel bbPanel=new JPanel();
        bbPanel.add(roomButton);
        bbPanel.add(restaurantButton);
        bookingPanel.add(bbPanel, BorderLayout.NORTH);
        bookingPanel.add(centralBooking, BorderLayout.CENTER);
//        datePicker=new DatePicker();
    }
    
    private void roomPanel()
    {
        
    }
    
    private void restPanel()
    {
        
    }
    
    private void enquiryPanel()
    {
        enquiryPanel=new JPanel(new BorderLayout());
        roomList=new JList();
        restList=new JList();
        enquiryPanel.add(roomList, BorderLayout.WEST);
        enquiryPanel.add(restList,BorderLayout.EAST);
    }
    
    private void buttonPanel()
    {
        southPanel=new JPanel();
        southPanel.add(newCustomerButton);
        southPanel.add(existingCustButton);
        southPanel.add(exitButton);
    }
    
    private void loadList(Set bookings, JList list)
    {
        Booking[] book=new Booking[bookings.size()];
        bookings.toArray(book);
        String[] bookString=new String[bookings.size()];
        for(int i=0;i<book.length;i++)
        {
            Booking b=book[i];
            String s="Booking no: ";
            s=s.concat(b.getBookingNo()+", Date: "+b.getDate());
            bookString[i]=s;
        }
        list.setListData(bookString);
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
    }
    
    private void loadCustomer()
    {
        String email=emailField.getText();
        emailField.setText("");
        boolean loaded=hotBook.loadCustomer(email);
        if(!loaded)
            JOptionPane.showMessageDialog(eCustPanel, "Email not found, please "
                    + "try again.", "Not Found", JOptionPane.WARNING_MESSAGE);
        else
        {
            updateCentralPanel(welcomePanel);
            buttonChanger(bookingButton, enquiryButton, logoutButton, exitButton);
            cust=hotBook.currentCustomer.toString();
            welcomeMessage.setText(cust);
            bookings=hotBook.hotel.getRestBookList();
            roomBookings=hotBook.hotel.getRoomBookList();
        }
    }
}