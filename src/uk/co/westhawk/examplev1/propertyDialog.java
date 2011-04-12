// NAME
//      $RCSfile: propertyDialog.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.6 $
// CREATED
//      $Date: 2006/11/30 13:57:09 $
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
import java.beans.*;

import uk.co.westhawk.snmp.beans.*;
import uk.co.westhawk.snmp.stack.*;

/**
 * <p>
 * The class propertyDialog is used to set the bean
 * properties. A user can add an actionListener to be notified when the
 * "Apply" or "OK" button is pressed.
 * </p>
 *
 * <p>
 * The user can configure the host name, the port number, the community
 * name, the socket type and the update interval. 
 * </p>
 *
 * <p>
 * The "Try it" button activates the IsHostReachableBean, who will probe
 * the configured host and signals the UI since when the host was up.
 * </p>
 *
 * @see uk.co.westhawk.snmp.beans.IsHostReachableBean
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.6 $ $Date: 2006/11/30 13:57:09 $
 */
public class propertyDialog extends JDialog 
      implements ActionListener, PropertyChangeListener, WindowListener
{
    private static final String     version_id =
        "@(#)$Id: propertyDialog.java,v 1.6 2006/11/30 13:57:09 birgit Exp $ Copyright Westhawk Ltd";

    protected GridBagLayout gridBagLayout1;
    protected JLabel hostLabel;
    protected JLabel portLabel;
    protected JLabel commLabel;
    protected JLabel intLabel;
    protected JLabel bindLabel;
  
    protected JTextField hostText;
    protected JTextField bindText;
    protected JTextField portText;
    protected JTextField communityText;
    protected JTextField intervalText;
   
    protected JLabel messageLabel;
    protected IsHostReachableBean reachableBean;
    
    protected JButton tryButton;
    protected JButton okButton;
    protected JButton cancelButton;
    protected JButton applyButton;

    protected String host;
    protected String bindAddr;
    protected String port;
    protected String community;
    protected String interval;
    protected String socketType;

    private JPanel socketPanel = new JPanel();
    private ButtonGroup socketGroup = new ButtonGroup();
    private JCheckBox standardSocketChoice = new JCheckBox();
    private JCheckBox tcpSocketChoice = new JCheckBox();

    protected Vector actionPerformedListener = null;

/**
 * The constructor to create a non-modal JDialog with the title "Property
 * JDialog".
 *
 * @param frame the parent frame
 */
public propertyDialog(JFrame frame)
{
    super (frame, "Property JDialog", false);
    actionPerformedListener = new Vector();
    jbInit();

    socketGroup.add(standardSocketChoice);
    socketGroup.add(tcpSocketChoice);
    standardSocketChoice.setSelected(true);

    if (frame != null)
    {
        Rectangle r = frame.getBounds();
        this.setLocation(r.x, r.y+r.height);
    }
}

/**
 * Sets the host string.
 *
 * @param s the string
 */
public void setHost(String s)
{
     host = s;
     hostText.setText(host);
}

/**
 * Sets the local bind address
 *
 * @param s the string
 * @since 4_14
 */
public void setBindAddress(String s)
{
     bindAddr = s;
     bindText.setText(bindAddr);
}

/**
 * Sets the port string.
 *
 * @param s the string
 */
public void setPort(String s)
{
     port = s;
     portText.setText(port);
}

/**
 * Sets the community name string.
 *
 * @param s the string
 */
public void setCommunityName(String s)
{
     community = s;
     communityText.setText(community);
}

/**
 * Sets the update interval string.
 *
 * @param s the string
 */
public void setUpdateInterval(String s)
{
     interval = s;
     intervalText.setText(interval);
}

/**
 * Sets the socket type string.
 *
 * @param newVar the socket type
 */
public void setSocketType(String newVar)
{
    socketType = newVar;
    if (socketType.equals(SnmpContextBasisFace.TCP_SOCKET))
    {
        tcpSocketChoice.setSelected(true);
    }
    else
    {
        standardSocketChoice.setSelected(true);
    }
}


/**
 * Returns the socket type string
 *
 * @return the socket type
 */
public String getSocketType()
{
    return socketType;
}

/**
 * Returns the host string
 *
 * @return the string
 */
public String getHost()
{
    return host;
}

/**
 * Returns the local bind address
 *
 * @return the string
 * @since 4_14
 */
public String getBindAddress()
{
    return bindAddr;
}

/**
 * Returns the port string
 *
 * @return the string
 */
public String getPort()
{
    return port;
}

/**
 *
 * Returns the community name string
 *
 * @return the string
 */
public String getCommunityName()
{
    return community;
}

/**
 *
 * Returns the update interval string
 *
 * @return the string
 */
public String getUpdateInterval()
{
    return interval;
}

void tryButton_actionPerformed(ActionEvent e)
{
    messageLabel.setText(" ");
    reachableBean.setHost(hostText.getText());
    reachableBean.setPort(portText.getText());
    reachableBean.setCommunityName(communityText.getText());

    bindAddr = bindText.getText();
    if (bindAddr.length() == 0)
    {
        bindAddr = null;
    }
    reachableBean.setBindAddress(bindAddr);

    if (standardSocketChoice.isSelected())
    {
        reachableBean.setSocketType(SnmpContextBasisFace.STANDARD_SOCKET);
    }
    else if (tcpSocketChoice.isSelected())
    {
        reachableBean.setSocketType(SnmpContextBasisFace.TCP_SOCKET);
    }

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


void reachableBean_propertyChange(PropertyChangeEvent e)
{
    messageLabel.setText(reachableBean.getMessage());
}


void cancelButton_actionPerformed(ActionEvent e)
{
    this.setVisible(false);

    fireActionPerformed(e.getID(), e.getActionCommand(),
          e.getModifiers());
}


void okButton_actionPerformed(ActionEvent e)
{
    this.setVisible(false);

    host = hostText.getText();
    bindAddr = bindText.getText();
    if (bindAddr.length() == 0)
    {
        bindAddr = null;
    }
    port = portText.getText();
    community = communityText.getText();
    interval = intervalText.getText();

    if (standardSocketChoice.isSelected())
    {
        socketType = SnmpContextBasisFace.STANDARD_SOCKET;
    }
    else if (tcpSocketChoice.isSelected())
    {
        socketType = SnmpContextBasisFace.TCP_SOCKET;
    }

    fireActionPerformed(e.getID(), e.getActionCommand(),
          e.getModifiers());
}


void applyButton_actionPerformed(ActionEvent e)
{
    host = hostText.getText();
    bindAddr = bindText.getText();
    if (bindAddr.length() == 0)
    {
        bindAddr = null;
    }
    port = portText.getText();
    community = communityText.getText();
    interval = intervalText.getText();

    if (standardSocketChoice.isSelected())
    {
        socketType = SnmpContextBasisFace.STANDARD_SOCKET;
    }
    else if (tcpSocketChoice.isSelected())
    {
        socketType = SnmpContextBasisFace.TCP_SOCKET;
    }
    fireActionPerformed(e.getID(), e.getActionCommand(),
          e.getModifiers());
}


/**
 * Implements the ActionListener
 */
public void actionPerformed(ActionEvent e)
{
    Object src = e.getSource();
    if (src == tryButton)
    {
        tryButton_actionPerformed(e);
    }
    else if (src == okButton)
    {
        okButton_actionPerformed(e);
    }
    else if (src == applyButton)
    {
        applyButton_actionPerformed(e);
    }
    else if (src == cancelButton)
    {
        cancelButton_actionPerformed(e);
    }
}


/**
 * Implements the PropertyChangeListener
 */
public void propertyChange(PropertyChangeEvent e)
{
    Object src = e.getSource();
    if (src == reachableBean)
    {
        reachableBean_propertyChange(e);
    }
}

private void jbInit() 
{
    gridBagLayout1 = new GridBagLayout();
    hostLabel = new JLabel(" Server: ");
    portLabel = new JLabel(" Port: ");
    commLabel = new JLabel(" Community Name: ");
    intLabel = new JLabel(" Update Interval: ");
    bindLabel = new JLabel(" Bind Address: ");

    host = "localhost";
    port = "" + SnmpContextBasisFace.DEFAULT_PORT;
    community = "public";
    interval = "5000";
    bindAddr = null;
    socketType = SnmpContextBasisFace.STANDARD_SOCKET;

    hostText = new JTextField(host);
    bindText = new JTextField(bindAddr);
    portText = new JTextField(port);
    communityText = new JTextField(community);
    intervalText = new JTextField(interval);

    standardSocketChoice.setText(SnmpContextv3Face.STANDARD_SOCKET);
    tcpSocketChoice.setText(SnmpContextv3Face.TCP_SOCKET);           

    messageLabel = new JLabel(" ");
    reachableBean = new IsHostReachableBean();

    tryButton = new JButton("Try connection");
    okButton = new JButton("OK");
    applyButton = new JButton("Apply");
    cancelButton = new JButton("Cancel");

    hostText.setBackground(Color.white);
    bindText.setBackground(Color.white);
    portText.setBackground(Color.white);
    communityText.setBackground(Color.white);
    intervalText.setBackground(Color.white);
    messageLabel.setBackground(Color.white);
    messageLabel.setOpaque(true);

    tryButton.addActionListener(this);
    okButton.addActionListener(this);
    applyButton.addActionListener(this);
    cancelButton.addActionListener(this);

    reachableBean.addPropertyChangeListener(this);
    this.getContentPane().setLayout(gridBagLayout1);
    this.setBackground(Color.lightGray);

    Container cont = this.getContentPane();

    cont.add(hostLabel, 
        getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));
    cont.add(hostText, 
        getGridBagConstraints2(1, 0, 2, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));

    cont.add(portLabel, 
        getGridBagConstraints2(0, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));
    cont.add(portText, 
        getGridBagConstraints2(1, 1, 2, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));

    cont.add(commLabel, 
        getGridBagConstraints2(0, 2, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));
    cont.add(communityText, 
        getGridBagConstraints2(1, 2, 2, 1, 1.0, 0.0
        ,GridBagConstraints.EAST, GridBagConstraints.BOTH, 
        new Insets(5, 5, 5, 5), 0, 0));

    cont.add(bindLabel, 
        getGridBagConstraints2(0, 3, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));
    cont.add(bindText, 
        getGridBagConstraints2(1, 3, 2, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));

    cont.add(intLabel, 
        getGridBagConstraints2(0, 4, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));
    cont.add(intervalText, 
        getGridBagConstraints2(1, 4, 2, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));

    cont.add(socketPanel, 
        getGridBagConstraints2(0, 5, 3, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
        new Insets(10, 10, 10, 10), 0, 0));

    cont.add(tryButton, 
        getGridBagConstraints2(0, 6, 3, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
        new Insets(10, 10, 10, 10), 0, 0));

    cont.add(messageLabel, 
        getGridBagConstraints2(0, 7, 3, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
        new Insets(10, 10, 10, 10), 0, 0));

    cont.add(okButton, 
        getGridBagConstraints2(0, 8, 1, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
        new Insets(10, 2, 10, 2), 0, 0));
    cont.add(applyButton, 
        getGridBagConstraints2(1, 8, 1, 1, 0.5, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
        new Insets(10, 2, 10, 2), 0, 0));
    cont.add(cancelButton, 
        getGridBagConstraints2(2, 8, 1, 1, 0.5, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
        new Insets(10, 2, 10, 2), 0, 0));

    socketPanel.add(standardSocketChoice, null);
    socketPanel.add(tcpSocketChoice, null);
    this.addWindowListener(this);
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

 
/**
 * Adds an action listener to be notified when the "Apply" or "OK" button
 * is pressed.
 * @see #removeActionListener
 */
public synchronized void addActionListener(ActionListener l)
{
    actionPerformedListener.addElement(l);
}

/**
 * Removes an action listener.
 * @see #addActionListener
 */
public synchronized void removeActionListener(ActionListener l)
{
    actionPerformedListener.removeElement(l);
}
 
/**
 * Fires a action event when the "Apply" or "OK"
 * button is pressed.
 *
 * @see #removeActionListener
 * @see #addActionListener
 * @see ActionEvent
 * @see ActionListener
 */
protected void fireActionPerformed(int id, String comm, int modif)
{
    Vector listeners;
    synchronized(this)
    {
        listeners = (Vector) actionPerformedListener.clone();
    }

    ActionEvent event = new ActionEvent(this, id, comm, modif);
 
    int sz = listeners.size();
    for (int i=0; i<sz; i++)
    {
        ActionListener l = (ActionListener) listeners.elementAt(i);
        l.actionPerformed(event);
    }
}

public void windowActivated(WindowEvent evt)
{
}
public void windowDeactivated(WindowEvent evt)
{
}
public void windowClosing(WindowEvent evt)
{
    ActionEvent evt2 = new ActionEvent(
        cancelButton,                 // source
        ActionEvent.ACTION_PERFORMED, // id
        cancelButton.getText()        // command
        );
    cancelButton_actionPerformed(evt2);
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
