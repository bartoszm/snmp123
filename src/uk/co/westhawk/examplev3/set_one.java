// NAME
//      $RCSfile: set_one.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.11 $
// CREATED
//      $Date: 2009/03/05 13:23:35 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2000 - 2006 by Westhawk Ltd
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

package uk.co.westhawk.examplev3;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import uk.co.westhawk.snmp.util.*;
import java.awt.*; 
import javax.swing.*;
import java.util.*;

import java.awt.event.*;

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
 * @see uk.co.westhawk.snmp.stack.SnmpContextv3Pool
 * @see propertyDialog2
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.11 $ $Date: 2009/03/05 13:23:35 $
 */
public class set_one extends JComponent 
implements MouseListener, ActionListener, Observer, WindowListener
{
    private static final String     version_id =
        "@(#)$Id: set_one.java,v 1.11 2009/03/05 13:23:35 birgita Exp $ Copyright Westhawk Ltd";

    public final static String sysContact = "1.3.6.1.2.1.1.4.0";

    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private GridLayout gridLayout1 = new GridLayout();
    private JPanel buttonPanel = new JPanel();
    private JLabel oidLabel = new JLabel();
    private JLabel valueLabel = new JLabel();
    private JLabel messageLabel = new JLabel();
    private JTextField oidText = new JTextField();
    private JTextField valueText = new JTextField();
    private JButton setButton = new JButton();
    private JButton getNextButton = new JButton();
    private JButton getButton = new JButton();

    private propertyDialog2 propDialog;
    private JFrame myFrame;


    private SnmpContextv3Pool context;
    private Pdu pdu;
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
    util = new Util(propertiesFilename, this.getClass().getName());

    try
    {
       // AsnObject.setDebug(15);
       AsnObject.setDebug(1);
        pduInFlight = false;
        jbInit();
    }
    catch(Exception exc)
    {
        exc.printStackTrace();
        setErrorMessage("Exception: " + exc.getMessage());
    }
}

public void init()
{
    createPropertyDialog();
    propDialog.setVisible(true);
    propDialog.toFront();

    String oid = util.getOid(sysContact);
    oidText.setText(oid);

    createContext();
    //sendGetRequest(oidText.getText());
}

private void jbInit() throws Exception
{
    oidLabel.setText("OID:");
    this.setLayout(gridBagLayout1);
    valueLabel.setText("Value:");
    oidText.setColumns(15);
    valueText.setColumns(15);
    buttonPanel.setLayout(gridLayout1);
    gridLayout1.setColumns(3);
    setButton.setText("Set");
    setButton.addActionListener(this);
    getNextButton.setText("GetNext");
    getNextButton.addActionListener(this);
    getButton.setText("Get");
    getButton.addActionListener(this);
    messageLabel.setText(" ");
    messageLabel.setBackground(Color.white);
    messageLabel.setOpaque(true);
    this.addMouseListener(this);
    this.add(oidLabel, propertyDialog2.getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(5, 5, 5, 5), 0, 0));
    this.add(valueLabel, propertyDialog2.getGridBagConstraints2(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(5, 5, 5, 5), 0, 0));
    this.add(oidText, propertyDialog2.getGridBagConstraints2(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    this.add(valueText, propertyDialog2.getGridBagConstraints2(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    this.add(buttonPanel, propertyDialog2.getGridBagConstraints2(0, 2, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(5, 5, 5, 5), 0, 0));
    buttonPanel.add(setButton, null);
    buttonPanel.add(getButton, null);
    buttonPanel.add(getNextButton, null);
    this.add(messageLabel, propertyDialog2.getGridBagConstraints2(0, 3, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
}

/**
 * Creates a new Pdu and send a request.
 */
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
            String oid = oidText.getText();
            if (src == getButton)
            {
                System.out.println("Get " + oid);
                pdu = new GetPdu(context);
                pdu.addOid(oid);
            }
            else if (src == getNextButton)
            {
                System.out.println("GetNext " + oid);
                pdu = new GetNextPdu(context);
                pdu.addOid(oid);
            }
            else if (src == setButton)
            {
                SetPdu setPdu = new SetPdu(context);
                String value = valueText.getText();
                AsnObject obj;
                System.out.println("Set " + oid + "=" + value);
                if (Util.isNumber(value))
                {
                    obj = new AsnInteger(Util.getNumber(value));
                }
                else
                {
                    obj = new AsnOctets(value);
                }
                setPdu.addOid(oid, obj);

                pdu = setPdu;
            }
            sendRequest(pdu);
        }
        // context.dumpContexts("Dump 1:");
    }
    catch (Exception exc)
    {
        if (context != null)
        {
            // context.dumpContexts("Dump 1:");
        }
        exc.printStackTrace();
        setErrorMessage("Exception: " + exc.getMessage());
    }
}

void propDialog_actionPerformed(ActionEvent evt)
{
    String cmd = evt.getActionCommand();
    if (cmd.equals("Cancel") == false)
    {
        createContext();
        if (context != null)
        {
            sendGetRequest(oidText.getText());
        }
    }
    else
    {
        //System.exit(0);
    }
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

/**
 * Will popup the property dialog if the popup menu button was
 * triggered.
 */
public void mouseEvt(MouseEvent evt)
{
    if (evt.isPopupTrigger())
    {
        propDialog.setVisible(true);
        propDialog.toFront();
        evt.consume();
    }
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
    messageLabel.setText(message);
    Color c = Color.white;
    if (isError)
    {
        c = Color.red;
    }
    messageLabel.setBackground(c);
}

private void createPropertyDialog()
{
    myFrame = getFrame(this);
    propDialog = new propertyDialog2(myFrame, util);

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

private void createContext()
{
    String host = propDialog.getHost();
    String portStr = propDialog.getPort();
    int port = SnmpContextBasisFace.DEFAULT_PORT;
    try
    {
        port = Integer.valueOf(portStr).intValue();
    }
    catch (NumberFormatException exc) { }
    String socketType = propDialog.getSocketType();
    String bindAddr = propDialog.getBindAddress();

    byte [] contextEngineId = propDialog.getContextEngineId();
    String contextName = propDialog.getContextName();
    String userName = propDialog.getUserName();

    boolean useAuthentication = propDialog.isAuthentication();
    String userAuthPassw = propDialog.getUserAuthPassw();
    int aprotocol = propDialog.getAProtocol();

    boolean usePrivacy = propDialog.isPrivacy();
    String userPrivPassw = propDialog.getUserPrivPassw();
    int pprotocol = propDialog.getPProtocol();
    
    myFrame.setTitle("One Set Operation - " + host);

    if (context != null)
    {
        context.destroy();
    }
    try
    {
        context = new SnmpContextv3Pool(host, port, bindAddr, socketType);
        context.setContextEngineId(contextEngineId);
        context.setContextName(contextName);
        context.setUserName(userName);
        context.setUseAuthentication(useAuthentication);
        context.setUserAuthenticationPassword(userAuthPassw);
        context.setAuthenticationProtocol(aprotocol);
        context.setUsePrivacy(usePrivacy);
        context.setUserPrivacyPassword(userPrivPassw);
        context.setPrivacyProtocol(pprotocol);

        // context.dumpContexts("Dump 2:");
    }
    catch (java.io.IOException exc)
    {
        if (context != null)
        {
            // context.dumpContexts("Dump 2:");
        }
        exc.printStackTrace();
        setErrorMessage("IOException: " + exc.getMessage());
        context = null;
    }

}

private void sendGetRequest(String oid)
{
    System.out.println("Get " + oid);
    pdu = new GetPdu(context);
    pdu.addOid(oid);
    sendRequest(pdu);
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

            valueText.setText("");

            pdu.addObserver(this);
            pdu.send();

            /*
            context.dumpContexts("Dump 8:");
            context.toString();
            context.dumpContexts("Dump 9:");

            // test the Pool does indeed destroy 
            String userName = context.getUserName();
            context.setUserName("bla");
            context.toString();
            context.dumpContexts("Dump 10:");

            context.setUserName(userName);
            context.toString();
            context.dumpContexts("Dump 11:");
            */
        }
        else
        {
            setErrorMessage("Pdu still in flight");
        }
    }
    catch (PduException exc)
    {
        if (context != null)
        {
            // context.dumpContexts("Dump 3:");
        }
        exc.printStackTrace();
        setErrorMessage("PduException: " + exc.getMessage());
        hadError = true;
    }
    catch (java.io.IOException exc)
    {
        if (context != null)
        {
            // context.dumpContexts("Dump 4:");
        }
        exc.printStackTrace();
        setErrorMessage("IOException: " + exc.getMessage());
        hadError = true;
    }
    catch (Exception exc)
    {
        if (context != null)
        {
            // context.dumpContexts("Dump 5:");
        }
        exc.printStackTrace();
        setErrorMessage("Exception: " + exc.getMessage());
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

public void update(Observable obs, Object ov)
{
    pduInFlight = false;

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
            if (vars != null)
            {
                varbind var = vars[0];
                if (var != null)
                {
                    AsnObjectId oid = var.getOid();
                    oidText.setText(Util.printOid(var.getOid()));

                    AsnObject obj = var.getValue();
                    valueText.setText(obj.toString());
                }
            }
        }
        catch(uk.co.westhawk.snmp.stack.PduException exc)
        {
            System.out.println("update(): PduException " 
                  + exc.getMessage());
            // context.dumpContexts("Dump 6:");
        }
    }

    setButton.setEnabled(true);
    getButton.setEnabled(true);
    getNextButton.setEnabled(true);
}

public void windowActivated(WindowEvent evt) { }
public void windowDeactivated(WindowEvent evt) { }
public void windowClosed(WindowEvent evt) { }
public void windowIconified(WindowEvent evt) { }
public void windowDeiconified(WindowEvent evt) { }
public void windowOpened(WindowEvent evt) { }
public void windowClosing(WindowEvent evt)
{
    // context.dumpContexts("Dump 7:");
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
    frame.addWindowListener(application);
    frame.setBounds(50, 50, 500, 150);
    frame.pack();
    frame.setVisible(true);
    
    application.init();
}

}

