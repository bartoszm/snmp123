// NAME
//      $RCSfile: OneBlockOperation.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.6 $
// CREATED
//      $Date: 2006/11/30 17:44:24 $
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

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;

import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;

import java.util.*;

/**
 * <p>
 * The class OneBlockOperation demonstrates the use of the BlockPdu
 * class and the SnmpContextPool class.
 * <p>
 *
 * </p>
 * The user can set the required OID and perform a Get or GetNext
 * request. The user can also set a MIB variable to a new String value
 * by performing a Set request.
 * </p>
 *
 * <p>
 * The host, port, oid and community name can be configured in the 
 * properties file. 
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>OneBlockOperation.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @see uk.co.westhawk.snmp.pdu.BlockPdu
 * @see uk.co.westhawk.snmp.stack.SnmpContextPool
 * @see propertyDialog
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.6 $ $Date: 2006/11/30 17:44:24 $
 */
public class OneBlockOperation extends JPanel 
        implements ActionListener, MouseListener
{
    private static final String     version_id =
        "@(#)$Id: OneBlockOperation.java,v 1.6 2006/11/30 17:44:24 birgit Exp $ Copyright Westhawk Ltd";

    public final static String sysContact = "1.3.6.1.2.1.1.4.0";

    private SnmpContextPool context;
    private BlockPdu pdu;

    private propertyDialog propDialog;
    private JFrame myFrame;

    private JTextField toid, tvalue;
    private JLabel    lmessage;
    private JButton   getButton, setButton, getNextButton;
    private Util      util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public OneBlockOperation(String propertiesFilename)
{
    //AsnObject.setDebug(15);
    util = new Util(propertiesFilename, this.getClass().getName());
}

public void init()
{
    try
    {
        createPropertyDialog();
        jbInit();

        propDialog.setVisible(true);
        propDialog.toFront();

        createContext(propDialog.getHost(), propDialog.getPort(), 
            propDialog.getCommunityName(), propDialog.getBindAddress(),
            propDialog.getSocketType());
        if (context != null)
        {
            sendGetRequest(toid.getText());
        }
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
        lmessage.setText("Exception: " + exc.getMessage());
        lmessage.setBackground(Color.red);
    }
}

public void actionPerformed(ActionEvent evt)
{
    Object src = evt.getSource();

    try
    {
        if (src == propDialog)
        {
            propDialog_actionPerformed(evt);
        }
        else if (context != null)
        {
            pdu = new BlockPdu(context);
            if (src == setButton)
            {
                pdu.setPduType(BlockPdu.SET);
                pdu.addOid(toid.getText(), new AsnOctets(tvalue.getText()));
            }
            else if (src == getButton)
            {
                pdu.setPduType(BlockPdu.GET);
                pdu.addOid(toid.getText());
            }
            else if (src == getNextButton)
            {
                pdu.setPduType(BlockPdu.GETNEXT);
                pdu.addOid(toid.getText());
            }
            sendRequest(pdu);
        }
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
        lmessage.setText("Exception: " + exc.getMessage());
        lmessage.setBackground(Color.red);
    }
}

void propDialog_actionPerformed(ActionEvent evt)
{
    String cmd = evt.getActionCommand();
    if (cmd.equals("Cancel") == false)
    {
        createContext(propDialog.getHost(), propDialog.getPort(), 
              propDialog.getCommunityName(), propDialog.getBindAddress(),
              propDialog.getSocketType());
        if (context != null)
        {
            sendGetRequest(toid.getText());
        }
    }
    else
    {
        //System.exit(0);
    }
}

private void createContext(String host, String portStr, String comm, 
      String bindAddr, String socketType)
{
    int port = SnmpContextBasisFace.DEFAULT_PORT;
    try
    {
        port = Integer.valueOf(portStr).intValue();
    }
    catch (NumberFormatException exc) { }
    createContext(host, port, comm, bindAddr, socketType);
}

private void createContext(String host, int port, String comm, 
      String bindAddr, String socketType)
{
    myFrame.setTitle("Set One  - " + host);

    if (context != null)
    {
        context.destroy();
    }
    try
    {
        context = new SnmpContextPool(host, port, comm, bindAddr, socketType);
        context.dumpContexts("Dump 1:");
    }
    catch (java.io.IOException exc)
    {
        lmessage.setText("IOException: " + exc.getMessage());
        lmessage.setBackground(Color.red);
        System.out.println("createContext: " + exc.getMessage());
        context = null;
    }
}

private void sendGetRequest(String oid)
{
    pdu = new BlockPdu(context);
    pdu.setPduType(BlockPdu.GET);
    pdu.addOid(oid);
    sendRequest(pdu);
}

private void sendRequest(BlockPdu pdu)
{
    setButton.setEnabled(false);
    getButton.setEnabled(false);
    getNextButton.setEnabled(false);
    lmessage.setText("Sending request ..");
    lmessage.setBackground(Color.white);
    try
    {
        varbind var = pdu.getResponseVariableBinding();
        AsnObjectId oid = var.getOid();
        AsnObject res = var.getValue();
        if (res != null)
        {
            toid.setText(oid.toString());
            tvalue.setText(res.toString());
            lmessage.setText("Received aswer ");
            lmessage.setBackground(Color.white);
        }
        else
        {
            lmessage.setText("Received no aswer ");
            lmessage.setBackground(Color.red);
        }
    }
    catch (PduException exc)
    {
        lmessage.setText("PduException: " + exc.getMessage());
        lmessage.setBackground(Color.red);
        exc.printStackTrace();
    }
    catch (java.io.IOException exc)
    {
        lmessage.setText("IOException: " + exc.getMessage());
        lmessage.setBackground(Color.red);
        exc.printStackTrace();
    }
    setButton.setEnabled(true);
    getButton.setEnabled(true);
    getNextButton.setEnabled(true);
}

private void jbInit() throws Exception
{
    JLabel loid, lvalue;

    loid    = new JLabel("OID: ");
    lvalue  = new JLabel("Value: ");
    lmessage = new JLabel("");
    lmessage.setOpaque(true);

    String oid = util.getOid(sysContact);
    toid    = new JTextField(oid);
    tvalue  = new JTextField();

    setButton = new JButton ("Set");
    getButton = new JButton ("Get");
    getNextButton = new JButton ("GetNext");

    Vector sw = new Vector(2);
    sw.addElement(setButton);
    sw.addElement(getButton);

    GridBagLayout grid = new GridBagLayout();
    Container cont = this;
    cont.setLayout(grid);

    addToGridBag(grid, cont, loid,          0, 0, 0.33, 1.0);
    addToGridBag(grid, cont, toid,          1, 0, 2, 1, 0.33, 1.0);
    addToGridBag(grid, cont, lvalue,        0, 1, 0.33, 1.0);
    addToGridBag(grid, cont, tvalue,        1, 1, 2, 1, 0.33, 1.0);
    addToGridBag(grid, cont, setButton,     0, 2, 0.33, 1.0);
    addToGridBag(grid, cont, getButton,     1, 2, 0.33, 1.0);
    addToGridBag(grid, cont, getNextButton, 2, 2, 0.33, 1.0);
    addToGridBag(grid, cont, lmessage,      0, 3, 3, 1, 0.33, 1.0);

    lmessage.setBackground(Color.white);

    setButton.addActionListener(this);
    getButton.addActionListener(this);
    getNextButton.addActionListener(this);
    this.addMouseListener(this);
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
    String bindAddr = util.getBindAddress();
    String port = "" + util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    String socketType = util.getSocketType();
    String comm = util.getCommunity();

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
    if (socketType != null)
    {
        propDialog.setSocketType(socketType);
    }
    if (bindAddr != null)
    {
        propDialog.setBindAddress(bindAddr);
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


public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    OneBlockOperation application = new OneBlockOperation(propFileName);

    JFrame frame = new JFrame();
    frame.setTitle(application.getClass().getName());
    frame.getContentPane().add(application, BorderLayout.CENTER);

    frame.addWindowListener(new WindowAdapter()
    {
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    });
    frame.setBounds(50, 50, 400, 150);
    frame.setVisible(true);

    application.init();
}

public static void addToGridBag(GridBagLayout grid, Container cont, 
    Component comp, 
    int x, int y, int gw, int gh, double wx, double wy, int fill, 
    int anchor) 
{
    // now the constraints
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = x;
    gbc.gridy = y;
    gbc.weightx = wx;
    gbc.weighty = wy;
    gbc.fill = fill;
    gbc.gridwidth = gw;
    gbc.gridheight = gh;
    gbc.anchor = anchor;

    cont.add(comp);   
    grid.setConstraints(comp, gbc);
}

public static void addToGridBag(GridBagLayout grid, Container cont, 
    Component comp, 
    int x, int y, int gx, int gy, double wx, double wy) 
{
    addToGridBag(grid, cont, comp, x, y, gx, gy, wx, wy,
        GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
}

public static void addToGridBag(GridBagLayout grid, Container cont, 
    Component comp, 
    int x, int y, double wx, double wy) 
{
    addToGridBag(grid, cont, comp, x, y, wx, wy, GridBagConstraints.HORIZONTAL);
}

public static void addToGridBag(GridBagLayout grid, Container cont, 
    Component comp, 
    int x, int y, double wx, double wy, int fill) 
{
    addToGridBag(grid, cont, comp, x, y, wx, wy, 
          fill, GridBagConstraints.CENTER);
}

public static void addToGridBag(GridBagLayout grid, Container cont, 
    Component comp, 
    int x, int y, double wx, double wy, int fill, int anchor) 
{
    addToGridBag(grid, cont, comp, x, y, 1, 1, wx, wy, 
          fill, anchor);
}

}
