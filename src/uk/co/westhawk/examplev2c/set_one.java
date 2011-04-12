// NAME
//      $RCSfile: set_one.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.8 $
// CREATED
//      $Date: 2009/03/05 12:34:05 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1996 - 2006 by Westhawk Ltd
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

package uk.co.westhawk.examplev2c;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;

/**
 * <p>
 * The set_one application will display the parameters, as configured in the
 * properties file. It will retrieve the specified MIB variable. 
 * </p>
 *
 * <p>
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>set_one.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * <p>
 * The user can set the required OID and perform a Get or GetNext
 * request. 
 * </p>
 *
 * <p>
 * The user can also set a MIB variable by performing a Set request.
 * By default the value is set as a String type (using AsnOctets),
 * unless the value is a number (AsnInteger will then be used). 
 * </p>
 *
 * @see uk.co.westhawk.snmp.stack.GetPdu
 * @see uk.co.westhawk.snmp.stack.SetPdu
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.8 $ $Date: 2009/03/05 12:34:05 $
 */
public class set_one extends JComponent 
      implements Observer, ActionListener, WindowListener 
{
    private static final String     version_id =
        "@(#)$Id: set_one.java,v 1.8 2009/03/05 12:34:05 birgita Exp $ Copyright Westhawk Ltd";

    /**
     * sysContact is used as default oid
     */
    public final static String sysContact = "1.3.6.1.2.1.1.4.0";

    private String host;
    private int port;
    private String community;
    private String bindAddr;
    private String oid;
    private String value;
    private String socketType;
    private int version = SnmpConstants.SNMP_VERSION_2c;

    private SnmpContextPool     context;
    private JTextField   thost, toid, tcom, tbind, tport, tvalue, ttype;
    private JButton      getButton, setButton, getNextButton;
    private JLabel       lmessage;
    private JComboBox    snmpVersion;
    private JComboBox    socketTypeChoice;

    private Pdu         pdu;
    private boolean     pduInFlight;
    private Util        util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public set_one(String propertiesFilename)
{
    // AsnObject.setDebug(15);
    util = new Util(propertiesFilename, this.getClass().getName());
}



public void init () 
{
    host = util.getHost();
    bindAddr = util.getBindAddress();
    port = util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    socketType = util.getSocketType();
    oid = util.getOid(sysContact);
    community = util.getCommunity();

    pduInFlight = false;
    makeLayout(host, oid, port, community);
    sendGetRequest(host, port, community, oid, version, bindAddr, socketType);
}


public void actionPerformed(ActionEvent evt)
{
    Object src = evt.getSource();
    host = thost.getText();
    port = Integer.valueOf(tport.getText()).intValue();
    community = tcom.getText();
    bindAddr = tbind.getText();
    if (bindAddr.length() == 0)
    {
        bindAddr = null;
    }
    oid = toid.getText();
    socketType = (String) socketTypeChoice.getSelectedItem();

    String item = (String) snmpVersion.getSelectedItem();
    if (item.equals("v2c"))
    {
        version = SnmpConstants.SNMP_VERSION_2c;
    }
    else
    {
        version = SnmpConstants.SNMP_VERSION_1;
    }

    try
    {
        SnmpContextFace context = createContext(version, host, port, bindAddr, socketType);
        if (src == getButton)
        {
            pdu = new GetPdu(context);
            pdu.addOid(oid);
        }
        else if (src == getNextButton)
        {
            pdu = new GetNextPdu(context);
            pdu.addOid(oid);
        }
        else if (src == setButton)
        {
            SetPdu setPdu = new SetPdu(context);
            String value = tvalue.getText();
            AsnObject obj;
            if (Util.isNumber(value))
            {
                obj = new AsnInteger(Util.getNumber(value));
            }
            else
            {
                obj = new AsnOctets(value);
            }
            ttype.setText(obj.getRespTypeString());
            setPdu.addOid(oid, obj);

            pdu = setPdu;
        }
        sendRequest(pdu);
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
        setErrorMessage("Exception: " + exc.getMessage());
    }
}



/**
 * @since 4_14
 */
private void sendGetRequest(String host, int port, String community, 
  String oid, int version, String bindAddr, String socketType)
{
    try
    {
        SnmpContextFace context = createContext(version, host, port, bindAddr, socketType);
        pdu = new GetPdu(context);
        pdu.addOid(oid);
        sendRequest(pdu);
    }
    catch (java.io.IOException exc)
    {
        exc.printStackTrace();
        setErrorMessage("IOException: " + exc.getMessage());
    }
}

private void sendRequest(Pdu pdu)
{
    boolean hadError = false;

    setButton.setEnabled(false);
    getButton.setEnabled(false);
    getNextButton.setEnabled(false);
    try
    {
        if (!pduInFlight)
        {
            pduInFlight = true;
            setMessage("Sending request ..: ");

            tvalue.setText("");
            ttype.setText("");
            pdu.addObserver(this);
            pdu.send();
        }
        else
        {
            setErrorMessage("Pdu still in flight");
        }
    }
    catch (PduException exc)
    {
        exc.printStackTrace();
        setErrorMessage("PduException: " + exc.getMessage());
        hadError = true;
    }
    catch (java.io.IOException exc)
    {
        exc.printStackTrace();
        setErrorMessage("IOException: " + exc.getMessage());
        hadError = true;
    }

    if (hadError == true)
    {
        pduInFlight = false;
        setButton.setEnabled(true);
        getButton.setEnabled(true);
        getNextButton.setEnabled(true);
    }
}


private SnmpContextFace createContext(int version, String host, int port, String
bindAddr, String socketType)
throws java.io.IOException
{
    SnmpContextFace c = null;
    if (version == SnmpConstants.SNMP_VERSION_2c)
    {
        context = new SnmpContextv2cPool(host, port, "bla", bindAddr, socketType);
        // check the Pool does destroy it's previous context
        // check the Pool does indeed destroy it's previous context
        context.setCommunity(community);
        c = context;
    }
    else
    {
        context = new SnmpContextPool(host, port, "bla", bindAddr, socketType);
        // check the Pool does indeed destroy it's previous context
        context.setCommunity(community);
        c = context;
    }
    // context.dumpContexts("Dump 1:");
    return c;
}


/**
 * Implementing the Observer interface. Receiving the response from 
 * the Pdu.
 *
 * @param obs the Pdu variable
 * @param ov the varbind
 *
 * @see uk.co.westhawk.snmp.stack.GetPdu
 * @see uk.co.westhawk.snmp.stack.SetPdu
 * @see uk.co.westhawk.snmp.stack.varbind
 */
public void update(Observable obs, Object ov)
{
    pduInFlight = false;

    // TODO: invokeLater
    setMessage("Received answer");
    if (pdu.getErrorStatus() != AsnObject.SNMP_ERR_NOERROR)
    {
        setErrorMessage(pdu.getErrorStatusString());
    }
    else
    {
        try
        {
            varbind[] vars = pdu.getResponseVarbinds();
            varbind var = vars[0];
            if (var != null)
            {
                AsnObjectId oid = var.getOid();
                toid.setText(oid.toString());

                AsnObject obj = var.getValue();
                tvalue.setText(obj.toString());
                ttype.setText(obj.getRespTypeString());
            }
        }
        catch(uk.co.westhawk.snmp.stack.PduException exc)
        {
            System.out.println("update(): PduException " 
                  + exc.getMessage());
        }
    }
    setButton.setEnabled(true);
    getButton.setEnabled(true);
    getNextButton.setEnabled(true);
}

public void setErrorMessage(String message)
{
    setMessage(message, true);
}

public void setMessage(String message)
{
    setMessage(message, false);
}

public void setMessage(String message, boolean isError)
{
    lmessage.setText(message);
    Color c = Color.white;
    if (isError)
    {
        c = Color.red;
    }
    lmessage.setBackground(c);
}

private void makeLayout(String host, String oid, int port, String com)
{
    JLabel lhost, loid, lport, lcom, lbind, lvalue, lversion, lsocket, ltype;

    // panel for the host, etc
    lhost   = new JLabel("Host: ");
    lport   = new JLabel("Port: ");
    lcom    = new JLabel("Community: ");
    lbind    = new JLabel("Bind: ");
    loid    = new JLabel("OID: ");
    lvalue  = new JLabel("Value: ");
    ltype   = new JLabel("Type: ");
    lversion = new JLabel("Version: ");
    lsocket = new JLabel("Socket: ");

    thost   = new JTextField(host);
    tport   = new JTextField(String.valueOf(port));
    tcom    = new JTextField(com);
    tbind   = new JTextField(bindAddr);
    toid    = new JTextField(oid);
    tvalue  = new JTextField();
    ttype   = new JTextField(oid);

    ttype.setEditable(false);
    ttype.setOpaque(true);
    ttype.setBackground(Color.white);

    snmpVersion = new JComboBox();
    snmpVersion.addItem("v2c");
    snmpVersion.addItem("v1");

    socketTypeChoice = new JComboBox();
    socketTypeChoice.addItem(SnmpContextBasisFace.STANDARD_SOCKET);
    socketTypeChoice.addItem(SnmpContextBasisFace.TCP_SOCKET);

    setButton = new JButton ("Set");
    getButton = new JButton ("Get");
    getNextButton = new JButton ("GetNext");

    lmessage = new JLabel("");
    lmessage.setOpaque(true);
    lmessage.setBackground(Color.white);

    GridBagLayout hostLayout = new GridBagLayout();
    JPanel hostPanel = new JPanel();
    hostPanel.setLayout(hostLayout);
    hostPanel.add(lhost,
        getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(thost,
        getGridBagConstraints2(1, 0, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    hostPanel.add(lport,
        getGridBagConstraints2(0, 1, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(tport,
        getGridBagConstraints2(1, 1, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    hostPanel.add(lcom,
        getGridBagConstraints2(0, 2, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(tcom,
        getGridBagConstraints2(1, 2, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    hostPanel.add(lbind,
        getGridBagConstraints2(0, 3, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(tbind,
        getGridBagConstraints2(1, 3, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    hostPanel.add(lsocket,
        getGridBagConstraints2(0, 4, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(socketTypeChoice,
        getGridBagConstraints2(1, 4, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    hostPanel.add(lversion,
        getGridBagConstraints2(0, 5, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(snmpVersion,
        getGridBagConstraints2(1, 5, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    hostPanel.add(loid,
        getGridBagConstraints2(0, 6, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(toid,
        getGridBagConstraints2(1, 6, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    hostPanel.add(lvalue,
        getGridBagConstraints2(0, 7, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(tvalue,
        getGridBagConstraints2(1, 7, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    hostPanel.add(ltype,
        getGridBagConstraints2(0, 8, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(ttype,
        getGridBagConstraints2(1, 8, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));


    // panel for the buttons
    GridBagLayout buttonLayout = new GridBagLayout();
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(buttonLayout);
    buttonPanel.add(setButton,
        getGridBagConstraints2(0, 0, 1, 1, 0.3, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(getButton,
        getGridBagConstraints2(1, 0, 1, 1, 0.3, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(getNextButton,
        getGridBagConstraints2(2, 0, 1, 1, 0.3, 1.0,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
    

    // the whole panel
    GridBagLayout thisLayout = new GridBagLayout();
    this.setLayout(thisLayout);
    this.add(hostPanel,
        getGridBagConstraints2(0, 0, 1, 1, 1.0, 0.3,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    this.add(buttonPanel,
        getGridBagConstraints2(0, 1, 1, 1, 1.0, 0.3,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    this.add(lmessage,
        getGridBagConstraints2(0, 2, 1, 1, 1.0, 0.3,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    setButton.addActionListener(this);
    getButton.addActionListener(this);
    getNextButton.addActionListener(this);
}

public void freeResources()
{
    if (context != null)
    {
        context.dumpContexts("Dump 2: ");
        context.destroyPool();
    }
}

public static GridBagConstraints getGridBagConstraints2(
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


public void windowOpened(WindowEvent e) {}
public void windowClosed(WindowEvent e) {}
public void windowIconified(WindowEvent e) {}
public void windowDeiconified(WindowEvent e) {}
public void windowActivated(WindowEvent e) {}
public void windowDeactivated(WindowEvent e) {}

public void windowClosing(WindowEvent e)
{
    this.freeResources();
    System.exit(0);
}



public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    set_one application = new set_one(propFileName);

    JFrame frame = new JFrame();
    frame.setTitle(application.getClass().getName());
    frame.getContentPane().add(application, BorderLayout.CENTER);
    application.init();

    frame.addWindowListener(application);
    frame.setSize(new Dimension(500, 250));
    frame.setVisible(true);
}

}
