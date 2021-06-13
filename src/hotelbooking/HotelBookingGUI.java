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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author Billy McCarthy-Dowd
 */
public class HotelBookingGUI extends JPanel
{
    public final int PANEL_WIDTH=600;
    public final int PANEL_HEIGHT=600;
    private JPanel welcomePanel;
    private JPanel eCustPanel;
    private JPanel newCustPanel;
    private JPanel bookingPanel;
    private JPanel enquiryPanel;
    private JPanel northPanel;
    private JButton loginButton;
    private JButton exitButton;
    private JButton acceptButton;
    private JButton newCustomerButton;
    private JButton existingCustButton;
    private JTextField emailField;
    private JTextField nameField;
    private JTextField newEmailField;
    private JTextField phoneField;
    private JLabel welcomeMessage;
    private JButton roomButton;
    private JButton restaurantButton;
    private JButton enquiryButton;
    
    public HotelBookingGUI()
    {
        super(new BorderLayout());
        
        //setting up central panels; welcome, account creation/login, bookings, enquiries
        welcomePanel=new JPanel();
        welcomePanel.add(new JLabel("Placeholder for image"));
        
        eCustPanel=new JPanel();
        eCustPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        emailField=new JTextField();
        loginButton=new JButton("Login");
        GroupLayout eCustLayout=new GroupLayout(eCustPanel);
        eCustPanel.setLayout(eCustLayout);
        eCustLayout.setAutoCreateGaps(true);
        eCustLayout.setAutoCreateContainerGaps(true);
        eCustLayout.setHorizontalGroup(eCustLayout.createSequentialGroup().addComponent(emailField).addComponent(loginButton).addGroup(eCustLayout.createParallelGroup(GroupLayout.Alignment.LEADING)));
        eCustLayout.setVerticalGroup(eCustLayout.createSequentialGroup().addGroup(eCustLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(emailField).addComponent(loginButton)));
        
        newCustPanel=new JPanel();
        eCustPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        nameField=new JTextField();
        newEmailField=new JTextField();
        phoneField=new JTextField();
        acceptButton=new JButton("Accept");
        GroupLayout nCustLayout=new GroupLayout(newCustPanel);
        newCustPanel.setLayout(nCustLayout);
        nCustLayout.setAutoCreateGaps(true);
        nCustLayout.setAutoCreateContainerGaps(true);
        nCustLayout.setHorizontalGroup(nCustLayout.createSequentialGroup().addComponent(nameField).addComponent(newEmailField).addGroup(nCustLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(phoneField).addComponent(acceptButton)));
        nCustLayout.setVerticalGroup(nCustLayout.createSequentialGroup().addGroup(nCustLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(nameField).addComponent(newEmailField).addComponent(phoneField)).addComponent(acceptButton));
        
        //setting up top panel
        welcomeMessage=new JLabel("Welcome to the PDC Hotel Booking System");
        welcomeMessage.setHorizontalAlignment(SwingConstants.CENTER);
        
        //setting up bottom panel
        JPanel southPanel=new JPanel();
        exitButton=new JButton("Exit");
        newCustomerButton=new JButton("New Customer");
        existingCustButton=new JButton("Existing Customer");
        southPanel.add(newCustomerButton);
        southPanel.add(existingCustButton);
        southPanel.add(exitButton);
        
        //adding subpanels to primary panel
        this.add(welcomePanel, BorderLayout.CENTER);
        this.add(eCustPanel, BorderLayout.CENTER);
        this.add(newCustPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
        this.add(welcomeMessage, BorderLayout.NORTH);
    }
    
    public static void main(String[] args) 
    {
        String url="jdbc:derby://localhost:1527/ PDCHotel; create=true";
        try {
            Connection conn=DriverManager.getConnection(url, "vsf2319", "Hotel123");
        } catch (SQLException ex) {
            System.out.println("SQLException: "+ex.getMessage());
        }
        
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
}
