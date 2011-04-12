// NAME
//      $RCSfile: DisplayString.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.3 $
// CREATED
//      $Date: 2006/01/30 11:37:15 $
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
 * The DisplayString application tests the display (print)
 * of DisplayString. It will display the parameters, as configured in the 
 * properties file. 
 * It will show the (unicode) value as specified in the properties file
 * in the UI.
 * The UI will enable you to do a set with this value. The returned
 * value should be exactly the same as original unicode one.
 * </p>
 *
 * <p>
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>DisplayString.properties</code>. If this file does not exist, the
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
 * @version $Revision: 1.3 $ $Date: 2006/01/30 11:37:15 $
 */
public class DisplayString extends JComponent 
      implements Observer, ActionListener, AsnOctetsPrintableFace
{
    private static final String     version_id =
        "@(#)$Id: DisplayString.java,v 1.3 2006/01/30 11:37:15 birgit Exp $ Copyright Westhawk Ltd";

    /**
     * sysLocation is used as default oid
     */
    public final static String sysLocation = "1.3.6.1.2.1.1.6.0";

    private String host;
    private int port;
    private String community;
    private String oid;
    private String value;
    private String socketType;
    private String bindAddr;
    private int version = SnmpConstants.SNMP_VERSION_2c;

    private SnmpContextFace context;
    private JTextField   thost, toid, tcom, tport, tvalue;
    private JButton      getButton, setButton, getNextButton;
    private JLabel       lmessage;
    private JComboBox    snmpVersion;

    private Pdu         pdu;
    private boolean     pduInFlight;
    private Util        util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public DisplayString(String propertiesFilename)
{
    AsnObject.setDebug(6);
    util = new Util(propertiesFilename, this.getClass().getName());
}



public void init () 
{
    host = util.getHost();
    port = util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    bindAddr = util.getBindAddress();
    socketType = util.getSocketType();
    oid = util.getOid(sysLocation);
    community = util.getCommunity();
    value = util.getProperty("value");

    pduInFlight = false;
    makeLayout(host, oid, port, community, value);
    // sendGetRequest(host, port, community, oid, version);
}


public void actionPerformed(ActionEvent evt)
{
    Object src = evt.getSource();
    host = thost.getText();
    port = Integer.valueOf(tport.getText()).intValue();
    community = tcom.getText();
    oid = toid.getText();

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
        context = createContext(version, host, port, community, bindAddr, socketType);

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
                byte [] utf8bytes = value.getBytes("UTF-8");
                AsnOctets utfoctet = new AsnOctets(utf8bytes, SnmpConstants.ASN_OCTET_STR);
                obj = utfoctet;
                System.out.println("actionPerformed(): value " + value 
                      + ", " + utfoctet.toHex());
            }
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

private void sendGetRequest(String host, int port, String comm, 
  String oid, int version)
{
    try
    {
        context = createContext(version, host, port, comm, bindAddr, socketType);

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

private SnmpContextFace createContext(int version, String host, int port, 
    String comm, String bindAddr, String socketType)
throws java.io.IOException
{
    SnmpContextFace c;
    if (version == SnmpConstants.SNMP_VERSION_2c)
    {
        c = new SnmpContextv2cPool(host, port, comm, bindAddr, socketType);
    }
    else
    {
        c = new SnmpContextPool(host, port, comm, bindAddr, socketType);
    }
    //((SnmpContextPool)context).dumpContexts("Dump 1:");
    return c;
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

public boolean isPrintable(byte[] value)
{
    return true;
}

public String toInternationalDisplayString(byte[] value)
{
    // I'm expecting Unicode, so this should work with a 'normal' String
    String ret = null;
    try
    {
        ret = new String(value, "UTF-8");
    }
    catch (java.io.UnsupportedEncodingException exc)
    {
        ret = new String(value);
    }
    return ret;
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
                if (obj instanceof AsnOctets)
                {
                    AsnOctets oct = (AsnOctets) obj;
                    tvalue.setText(oct.toString(this));
                }
                else
                {
                    tvalue.setText(obj.toString());
                }
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

private void makeLayout(String host, String oid, int port, String com,
String value)
{
    JLabel lhost, loid, lport, lcom, lvalue, lversion;

    // panel for the host, etc
    lhost   = new JLabel("Host: ");
    lport   = new JLabel("Port: ");
    lcom    = new JLabel("Community: ");
    loid    = new JLabel("OID: ");
    lvalue  = new JLabel("Value: ");
    lversion = new JLabel("Version: ");

    thost   = new JTextField(host);
    tport   = new JTextField(String.valueOf(port));
    tcom    = new JTextField(com);
    toid    = new JTextField(oid);
    tvalue  = new JTextField(value);

    snmpVersion = new JComboBox();
    snmpVersion.addItem("v2c");
    snmpVersion.addItem("v1");

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

    hostPanel.add(loid,
        getGridBagConstraints2(0, 3, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(toid,
        getGridBagConstraints2(1, 3, 1, 1, 1.0, 0.1,
            GridBagConstraints.EAST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    hostPanel.add(lvalue,
        getGridBagConstraints2(0, 4, 1, 1, 0.0, 0.1,
            GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(2, 2, 2, 2), 0, 0));
    hostPanel.add(tvalue,
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



    // panel for the buttons
    setButton = new JButton ("Set");
    getButton = new JButton ("Get");
    getNextButton = new JButton ("GetNext");

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
    lmessage = new JLabel("");
    lmessage.setBackground(Color.white);


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



public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    DisplayString application = new
    DisplayString(propFileName);

    JFrame frame = new JFrame();
    frame.setTitle(application.getClass().getName());
    frame.getContentPane().add(application, BorderLayout.CENTER);
    application.init();
    frame.addWindowListener(new WindowAdapter()
    {
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    });
    frame.setSize(new Dimension(500, 200));
    frame.setVisible(true);
}

}
