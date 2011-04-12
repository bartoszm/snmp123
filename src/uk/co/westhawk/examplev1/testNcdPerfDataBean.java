// NAME
//      $RCSfile: testNcdPerfDataBean.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.5 $
// CREATED
//      $Date: 2006/01/30 11:36:57 $
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
import uk.co.westhawk.visual.*;
import java.beans.*;

/**
 * <p>
 * The class testNcdPerfDataBean demonstrates the use of some of the beans
 * in the uk.co.westhawk.snmp.beans packages. It shows a UI in which the
 * user can configure the bean properties.
 * </p>
 *
 * <p>
 * When triggering the menu button at the background, a property dialog 
 * will popup.
 * The user can configure the host name, the port number, the community
 * name and the update interval. When the OK or Apply button is pressed
 * NcdPerfDataBean is activated. Via the PropertyChangeEvent it
 * will provide the UI with the list of current names.
 * </p>
 *
 * <p>
 * The "Try it" button in the dialog activates the IsHostReachableBean, 
 * who will probe
 * the configured host and signals the UI since when the host was up.
 * </p>
 *
 * <p>
 * The values of the property dialog can also be configured via 
 * the properties file. 
 * The action() method still has to be called, either via java
 * code, or by pressing the button in the dialog.
 * </p>
 *
 * <p>
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>testNcdPerfDataBean.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * <p>
 * The speed will be shown as a Graph, its values displayed as 10log.
 * The memory will also be shown as a Graph, but with normal values
 * displayed.
 * </p>
 *
 * @see uk.co.westhawk.snmp.beans.NcdPerfDataBean
 * @see uk.co.westhawk.snmp.beans.IsHostReachableBean
 * @see uk.co.westhawk.visual.Graph
 * @see propertyDialog
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.5 $ $Date: 2006/01/30 11:36:57 $
 */
public class testNcdPerfDataBean extends JPanel 
        implements ActionListener, PropertyChangeListener,
        WindowListener, MouseListener
{
    private static final String     version_id =
        "@(#)$Id: testNcdPerfDataBean.java,v 1.5 2006/01/30 11:36:57 birgit Exp $ Copyright Westhawk Ltd";

    propertyDialog propDialog;
    JFrame myFrame;

    int speed;
    int memory;
    String name;
    String message;

    GridBagLayout gridBagLayout1;
    JLabel label5;
    JLabel label6;
    JLabel label7;

    Graph memGr;
    GraphDataSet memData;

    Graph speedGr;
    GraphDataSet speedData;

    JLabel nameLabel;
    JLabel messageLabel;

    NcdPerfDataBean ncdBean;
    private Util      util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public testNcdPerfDataBean(String propertiesFilename)
{
    //AsnObject.setDebug(15);
    util = new Util(propertiesFilename, this.getClass().getName());

    speedData = new GraphDataSet();
    speedGr = new Graph(speedData);
    speedGr.setMax(1000000);
    speedGr.setMin(0);
    speedGr.setName("Speed");
    speedGr.setUnit("(log10 (b/s))");
    speedGr.setAxes(true);
    speedGr.setLog(true);

    memData = new GraphDataSet();
    memGr = new Graph(memData);
    memGr.setMax(50000);
    memGr.setMin(50);
    memGr.setName("Memory");
    memGr.setUnit("(Kb)");
    // memGr.setAxes(false);
    // memGr.setFont(new Font("lucida", Font.PLAIN, 8));

    nameLabel = new JLabel(" ");
    messageLabel = new JLabel(" ");

    ncdBean = new NcdPerfDataBean();
}

//Initialize the application

public void init()
{
    try
    {
        createPropertyDialog();
        jbInit();
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
}

//Main method

public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    testNcdPerfDataBean application = new testNcdPerfDataBean(propFileName);

    JFrame frame = new JFrame();
    frame.setTitle(application.getClass().getName());
    frame.getContentPane().add(application, BorderLayout.CENTER);

    frame.setBounds(50, 50, 350, 300);
    application.init();
    frame.pack();

    frame.setVisible(true);
    frame.addWindowListener(application);

}

public void actionPerformed(ActionEvent e)
{
    Object src = e.getSource();
    if (src == propDialog)
    {
        propDialog_actionPerformed(e);
    }
}

public void propertyChange(PropertyChangeEvent e)
{
    Object src = e.getSource();
    if (src == ncdBean)
    {
        ncdBean_propertyChange(e);
    }
}

void ncdBean_propertyChange(PropertyChangeEvent e)
{
    String property = e.getPropertyName();
    
    if (property.equals(ncdBean.speedPropertyName))
    {
        speed = ((Integer) e.getNewValue()).intValue();
        speedData.addElement(speed);
    }
    else if (property.equals(ncdBean.memoryPropertyName))
    {
        memory = ((Integer) e.getNewValue()).intValue();
        memory = (int)(memory/1000);

        memData.addElement(memory);
    }
    else if (property.equals(ncdBean.userPropertyName))
    {
        name = (String) e.getNewValue();
        nameLabel.setText(name);
    }
    else 
    {
        // ncdBean.messagePropertyName
        message = (String) e.getNewValue();
        messageLabel.setText(message);
    }
}

void propDialog_actionPerformed(ActionEvent evt)
{
    String cmd = evt.getActionCommand();
    if (cmd.equals("Cancel") == false)
    {
        nameLabel.setText(" ");
        messageLabel.setText(" ");

        String host = propDialog.getHost();
        myFrame.setTitle("NCD " + host);
        ncdBean.setHost(host);
        ncdBean.setPort(propDialog.getPort());
        ncdBean.setBindAddress(propDialog.getBindAddress());
        ncdBean.setCommunityName(propDialog.getCommunityName());
        ncdBean.setUpdateInterval(propDialog.getUpdateInterval());

        ncdBean.action();
    }
    else
    {
        //System.exit(0);
    }
}


//Component initialization

private void jbInit() throws Exception
{
    gridBagLayout1 = new GridBagLayout();
    label5 = new JLabel(" ");
    label6 = new JLabel(" ");
    label7 = new JLabel(" ");

    label5.setText(" Speed ");
    label6.setText(" Memory ");
    label7.setText(" User name ");

    nameLabel.setBackground(Color.white);
    messageLabel.setBackground(Color.white);

    nameLabel.setOpaque(true);
    messageLabel.setOpaque(true);

    ncdBean.addPropertyChangeListener(this);
    this.addMouseListener(this);

    this.setLayout(gridBagLayout1);
    this.setBackground(Color.lightGray);

    this.add(label5, 
        getGridBagConstraints2(0, 0,  1, 1,  0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(label6, 
        getGridBagConstraints2(0, 1,  1, 1,  0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(label7, 
        getGridBagConstraints2(0, 2,  1, 1,  0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(messageLabel, 
        getGridBagConstraints2(0, 3,  2, 1,  0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
        new Insets(10, 5, 10, 5), 0, 0));

    this.add(speedGr, 
        getGridBagConstraints2(1, 0,  1, 1,  1.0, 0.5
        ,GridBagConstraints.EAST, GridBagConstraints.BOTH, 
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(memGr, 
        getGridBagConstraints2(1, 1,  1, 1,  1.0, 0.5
        ,GridBagConstraints.EAST, GridBagConstraints.BOTH, 
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(nameLabel, 
        getGridBagConstraints2(1, 2,  1, 1,  1.0, 0.0
        ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 
        new Insets(5, 5, 5, 5), 0, 0));
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
 
public void mouseClicked(MouseEvent evt)
{
}
public void mouseEntered(MouseEvent evt)
{
}
public void mouseExited(MouseEvent evt)
{
}
public void mousePressed(MouseEvent evt)
{
    mouseEvt(evt);
}
public void mouseReleased(MouseEvent evt)
{
    mouseEvt(evt);
}

public void mouseEvt(MouseEvent evt)
{
    if (evt.isPopupTrigger())
    {
        propDialog.setVisible(true);
        propDialog.toFront();
        evt.consume();
    }
}

private void createPropertyDialog()
{
    myFrame = getFrame(this);
    propDialog = new propertyDialog(myFrame);

    String host = util.getHost();
    String port = util.getPort();
    String bindAddr = util.getBindAddress();
    String comm = util.getCommunity();
    String interv = util.getProperty(Util.INTERVAL);

    if (host != null)
    {
        propDialog.setHost(host);
    }
    if (port != null)
    {
        propDialog.setPort(port);
    }
    if (comm != null)
    {
        propDialog.setCommunityName(comm);
    }
    if (bindAddr != null)
    {
        propDialog.setBindAddress(bindAddr);
    }
    if (interv != null)
    {
        propDialog.setUpdateInterval(interv);
    }
    propDialog.addActionListener(this);
    propDialog.pack();
}

public static JFrame getFrame(Component c)
{
    if(c instanceof JFrame)
        return (JFrame)c;
 
    while((c = c.getParent()) != null)
    {
        if(c instanceof JFrame)
            return (JFrame)c;
    }
    return null;
}

}
