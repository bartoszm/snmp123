// NAME
//      $RCSfile: testAscendActiveSessionBean.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.4 $
// CREATED
//      $Date: 2006/01/17 17:43:55 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//


/*
 * Copyright (C) 1998 - 2006 by Westhawk Ltd
 *
 * Permission to use, copy, modify, and distribute this software
 * for any purpose and without fee is hereby granted, provided
 * that the above copyright notices appear in all copies and that
 * both the copyright notice and this permission notice appear in
 * supporting documentation.
 * This software is provided "as is" without express or implied
 * warranty.
 * author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 */
 
package uk.co.westhawk.examplev1;

import java.awt.*; 
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

import uk.co.westhawk.snmp.beans.*;
import uk.co.westhawk.snmp.stack.*;
import java.beans.*;

/**
 * <p>
 * The class testAscendActiveSessionBean demonstrates the use of some of 
 * the beans in the uk.co.westhawk.snmp.beans packages. It shows a UI in 
 * which the user can configure the bean properties.
 * </p>
 *
 * <p>
 * The user can configure the host name, the port number, the community
 * name and the update interval. When the action button is pressed
 * AscendActiveSessionBean is activated. Via the PropertyChangeEvent it
 * will provide the UI with the information about the Active Session.
 * </p>
 *
 * <p>
 * The "Try it" button activates the IsHostReachableBean, who will probe
 * the configured host and signals the UI since when the host was up.
 * </p>
 *
 * @see uk.co.westhawk.snmp.beans.AscendActiveSessionBean
 * @see uk.co.westhawk.snmp.beans.IsHostReachableBean
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.4 $ $Date: 2006/01/17 17:43:55 $
 */
public class testAscendActiveSessionBean extends JPanel 
        implements ActionListener, PropertyChangeListener,
        WindowListener
{
    private static final String     version_id =
        "@(#)$Id: testAscendActiveSessionBean.java,v 1.4 2006/01/17 17:43:55 birgit Exp $ Copyright Westhawk Ltd";

    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel label1 = new JLabel(" ");
    JLabel label2 = new JLabel(" ");
    JLabel label3 = new JLabel(" ");
    JLabel label4 = new JLabel(" ");
    JLabel label6 = new JLabel(" ");
    JLabel label7 = new JLabel(" ");
    JLabel label8 = new JLabel(" ");
    JLabel label9 = new JLabel(" ");
    JTextField hostText = new JTextField();
    JTextField portText = new JTextField();
    JTextField communityText = new JTextField();
    JTextField intervalText = new JTextField();
    JLabel userNameLabel = new JLabel(" ");
    JLabel userIPAddressLabel = new JLabel(" ");
    JLabel userSubnetMaskLabel = new JLabel(" ");
    JLabel currentServiceLabel = new JLabel(" ");
    JLabel messageLabel = new JLabel(" ");
    JButton tryButton = new JButton();
    JButton actionButton = new JButton();
    AscendActiveSessionBean sessionBean = new AscendActiveSessionBean();
    IsHostReachableBean reachableBean = new IsHostReachableBean();

    //Construct the application
    
    public testAscendActiveSessionBean()
    {
    }
//Initialize the application
    
    public void init()
    {
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//Component initialization
    
    private void jbInit() throws Exception
    {
        label1.setText("Host");
        label2.setText("Port");
        label3.setText("Community");
        label4.setText("Interval");

        hostText.setText("hands.westhawk.co.uk");
        hostText.setBackground(Color.white);
        hostText.addActionListener(this);
        portText.setText("" + SnmpContextBasisFace.DEFAULT_PORT);
        portText.setBackground(Color.white);
        portText.addActionListener(this);
        communityText.setText("public");
        communityText.setBackground(Color.white);
        communityText.addActionListener(this);
        intervalText.setText("4000");
        intervalText.setBackground(Color.white);
        intervalText.addActionListener(this);

        tryButton.setText("Try connection");
        tryButton.addActionListener(this);
        actionButton.setText("Action");
        actionButton.addActionListener(this);

        label6.setText("User Name ");
        label7.setText("User IP Address ");
        label8.setText("User Subnet Mask ");
        label9.setText("Current Service ");

        userNameLabel.setBackground(Color.yellow);
        userIPAddressLabel.setBackground(Color.yellow);
        userSubnetMaskLabel.setBackground(Color.yellow);
        currentServiceLabel.setBackground(Color.yellow);
        messageLabel.setBackground(Color.white);

        userNameLabel.setOpaque(true);
        userIPAddressLabel.setOpaque(true);
        userSubnetMaskLabel.setOpaque(true);
        currentServiceLabel.setOpaque(true);
        messageLabel.setOpaque(true);

        reachableBean.addPropertyChangeListener(this);
        sessionBean.setPort(SnmpContextBasisFace.DEFAULT_PORT);
        sessionBean.setHost("ns.westhawk.co.uk");
        sessionBean.addPropertyChangeListener(this);
        sessionBean.addPropertyChangeListener(this);

        this.setLayout(gridBagLayout1);
        this.setSize(400,300);

        this.add(label1, 
            getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(label2, 
            getGridBagConstraints2(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(label3, 
            getGridBagConstraints2(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(label4, 
            getGridBagConstraints2(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(tryButton, 
            getGridBagConstraints2(0, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(10, 10, 10, 10), 0, 0));
        this.add(label6, 
            getGridBagConstraints2(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(label7, 
            getGridBagConstraints2(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(label8, 
            getGridBagConstraints2(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(label9, 
            getGridBagConstraints2(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(messageLabel, 
            getGridBagConstraints2(0, 9, 2, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, 
            new Insets(10, 2, 10, 2), 0, 0));

        this.add(hostText, 
            getGridBagConstraints2(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(portText, 
            getGridBagConstraints2(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(communityText, 
            getGridBagConstraints2(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(intervalText, 
            getGridBagConstraints2(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(actionButton, 
            getGridBagConstraints2(1, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 
            new Insets(10, 10, 10, 10), 0, 0));
        this.add(userNameLabel, 
            getGridBagConstraints2(1, 5, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(userIPAddressLabel, 
            getGridBagConstraints2(1, 6, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(userSubnetMaskLabel, 
            getGridBagConstraints2(1, 7, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(currentServiceLabel, 
            getGridBagConstraints2(1, 8, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
            new Insets(2, 2, 2, 2), 0, 0));
    }

//Main method

    public static void main(String[] args)
    {
        testAscendActiveSessionBean application = new testAscendActiveSessionBean();
        application.init();

        JFrame frame = new JFrame();
        frame.setTitle(application.getClass().getName());
        frame.getContentPane().add(application, BorderLayout.CENTER);
        frame.setSize(500, 220);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
        frame.setVisible(true);
        frame.addWindowListener(application);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if (src == hostText)
        {
            hostText_actionPerformed(e);
        }
        else if (src == portText)
        {
            portText_actionPerformed(e);
        }
        else if (src == communityText)
        {
            communityText_actionPerformed(e);
        }
        else if (src == intervalText)
        {
            intervalText_actionPerformed(e);
        }
        else if (src == tryButton)
        {
            tryButton_actionPerformed(e);
        }
        else if (src == actionButton)
        {
            actionButton_actionPerformed(e);
        }
    }

    public void propertyChange(PropertyChangeEvent e)
    {
        Object src = e.getSource();
        if (src == reachableBean)
        {
            reachableBean_propertyChange(e);
        }
        else if (src == sessionBean)
        {
            sessionBean_propertyChange(e);
        }
    }

    void hostText_actionPerformed(ActionEvent e)
    {
        sessionBean.setHost(hostText.getText());
    }

    void portText_actionPerformed(ActionEvent e)
    {
        sessionBean.setPort(portText.getText());
    }

    void communityText_actionPerformed(ActionEvent e)
    {
        sessionBean.setCommunityName(communityText.getText());
    }

    void intervalText_actionPerformed(ActionEvent e)
    {
        sessionBean.setUpdateInterval(intervalText.getText());
    }

    void tryButton_actionPerformed(ActionEvent e)
    {
        messageLabel.setText(" ");
        reachableBean.setHost(hostText.getText());
        reachableBean.setPort(portText.getText());
        reachableBean.setCommunityName(communityText.getText());

        try
        {
            reachableBean.action();
        }
        catch (java.io.IOException exc)
        {
            messageLabel.setText("IOException " + exc.getMessage());
        }
        catch (uk.co.westhawk.snmp.stack.PduException exc)
        {
            messageLabel.setText("PduException " + exc.getMessage());
        }
    }

    void sessionBean_propertyChange(PropertyChangeEvent e)
    {
         userNameLabel.setText(String.valueOf(sessionBean.getUserName()));
         userIPAddressLabel.setText(String.valueOf(sessionBean.getUserIPAddress()));
         userSubnetMaskLabel.setText(sessionBean.getUserSubnetMask());
         currentServiceLabel.setText(sessionBean.getCurrentService());
    }

    void reachableBean_propertyChange(PropertyChangeEvent e)
    {
        messageLabel.setText(reachableBean.getMessage());
    }

    void actionButton_actionPerformed(ActionEvent e)
    {
        messageLabel.setText(" ");
        userNameLabel.setText(" ");
        userIPAddressLabel.setText(" ");
        userSubnetMaskLabel.setText(" ");
        currentServiceLabel.setText(" ");

        sessionBean.setHost(hostText.getText());
        sessionBean.setPort(portText.getText());
        sessionBean.setCommunityName(communityText.getText());
        sessionBean.setUpdateInterval(intervalText.getText());

        sessionBean.action();
    }

    GridBagConstraints getGridBagConstraints2(
            int x, int y, int w, int h, double wx, double wy,
            int anchor, int fill,
            Insets ins, int ix, int iy)
    {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = x;
        gc.gridy = y;
        gc.gridwidth = w;
        gc.gridheight = h;
        gc.weightx = wx;
        gc.weighty = wy;
        gc.anchor = anchor;
        gc.fill = fill;
        gc.insets = ins;
        gc.ipadx = ix;
        gc.ipady = iy;

        return gc;
    }

public void windowActivated(WindowEvent evt)
{
}
public void windowDeactivated(WindowEvent evt)
{
}
public void windowClosing(WindowEvent evt)
{
    System.exit(0);
}
public void windowClosed(WindowEvent evt)
{
}
public void windowIconified(WindowEvent evt)
{
}
public void windowDeiconified(WindowEvent evt)
{
}
public void windowOpened(WindowEvent evt)
{
}
 
}
