// NAME
//      $RCSfile: propertyDialog2.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.7 $
// CREATED
//      $Date: 2008/12/15 15:52:26 $
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
import uk.co.westhawk.snmp.util.*;
import java.awt.*; 
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;

/**
 *
 * <p>
 * The class propertyDialog is used to set the SNMPv3
 * properties. A user can add an actionListener to be notified when the
 * "Apply" or "OK" button is pressed.
 * </p>
 *
 * <p>
 * The user can configure 
 * <ul>
 * <li>the host name </li>
 * <li>the port number </li>
 * <li>the socket type </li>
 * <li>the local bind address </li>
 * <li>the user name </li>
 * <li>whether to use authentication or not </li>
 * <li>the user authentication password </li>
 * <li>the authentication protocol </li>
 * <li>whether to use privacy or not </li>
 * <li>the user privacy password </li>
 * <li>the context engine id </li>
 * <li>the context name </li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.7 $ $Date: 2008/12/15 15:52:26 $
 */
public class propertyDialog2 extends JDialog 
implements ActionListener, ItemListener, WindowListener
{
    private static final String     version_id =
        "@(#)$Id: propertyDialog2.java,v 1.7 2008/12/15 15:52:26 tpanton Exp $ Copyright Westhawk Ltd";

    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private GridBagLayout gridBagLayout2 = new GridBagLayout();
    private GridBagLayout gridBagLayout3 = new GridBagLayout();
    private GridBagLayout gridBagLayout4 = new GridBagLayout();
    private GridLayout gridLayout1 = new GridLayout();
    private JPanel socketPanel = new JPanel();
    private JPanel aprotocolPanel = new JPanel();
    private JPanel pprotocolPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private ButtonGroup socketGroup = new ButtonGroup();
    private ButtonGroup aprotocolGroup = new ButtonGroup();
    private ButtonGroup pprotocolGroup = new ButtonGroup();
    private JLabel hostLabel = new JLabel();
    private JLabel bindLabel = new JLabel();
    private JLabel portLabel = new JLabel();
    private JLabel contextEngineIdLabel = new JLabel();
    private JLabel contextNameLabel = new JLabel();
    private JLabel userNameLabel = new JLabel();
    private JLabel userAuthPasswLabel = new JLabel();
    private JLabel userPrivPasswLabel = new JLabel();
    private JTextField hostText = new JTextField();
    private JTextField bindText = new JTextField();
    private JTextField portText = new JTextField();
    private JTextField contextEngineIdText = new JTextField();
    private JTextField contextNameText = new JTextField();
    private JTextField userNameText = new JTextField();
    private JTextField userAuthPasswText = new JTextField();
    private JTextField userPrivPasswText = new JTextField();
    private JCheckBox authenticationChoice = new JCheckBox();
    private JCheckBox privacyChoice = new JCheckBox();
    private JCheckBox standardSocketChoice = new JCheckBox();
    private JCheckBox tcpSocketChoice = new JCheckBox();
    private JCheckBox md5ProtocolChoice = new JCheckBox();
    private JCheckBox sha1ProtocolChoice = new JCheckBox();
    private JCheckBox desProtocolChoice = new JCheckBox();
    private JCheckBox aesProtocolChoice = new JCheckBox();
    private JButton okButton = new JButton();
    private JButton applyButton = new JButton();
    private JButton cancelButton = new JButton();
    private JButton fileButton = new JButton();

    protected Vector actionPerformedListener = null;
    protected String host = "hort";
    protected String bindAddr = null;
    protected String port = "" + SnmpContextBasisFace.DEFAULT_PORT;
    protected byte [] contextEngineId = new byte[0];
    protected String contextName = "";

    protected String userName = "authUser";
    protected boolean doAuthentication = true;
    protected boolean doPrivacy = true;

    protected String userAuthPassw = "AuthPassword";
    protected String userPrivPassw = "PrivPassword";
    protected String socketType = SnmpContextv3Face.STANDARD_SOCKET;
    protected int aprotocol = SnmpContextv3Face.MD5_PROTOCOL;
    protected int pprotocol = SnmpContextv3Face.DES_ENCRYPT;
    
    protected JFileChooser fileChooser = null;
    protected Util util;

public propertyDialog2()
{
    this(null, null);
}

public propertyDialog2(JFrame frame)
{
    this(frame, null);
}

public propertyDialog2(JFrame frame, Util u)
{
    super(frame, "Property JDialog v3", false);
    util = u;
    try
    {
        jbInit();

        fillinFromUtil(util);
        actionPerformedListener = new Vector();
        socketGroup.add(standardSocketChoice);
        socketGroup.add(tcpSocketChoice);

        aprotocolGroup.add(md5ProtocolChoice);
        aprotocolGroup.add(sha1ProtocolChoice);
        pprotocolGroup.add(desProtocolChoice);
        pprotocolGroup.add(aesProtocolChoice);        

        if (frame != null)
        {
            Rectangle r = frame.getBounds();
            this.setLocation(r.x, r.y+r.height);
        }
    }
    catch(Exception exc)
    {
        exc.printStackTrace();
    }
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

public byte [] getContextEngineId()
{
    return contextEngineId;
}
public String getContextName()
{
    return contextName;
}
public String getUserName()
{
    return userName;
}
public String getUserAuthPassw()
{
    return userAuthPassw;
}
public String getSocketType()
{
    return socketType;
}
public boolean isAuthentication()
{
    return doAuthentication;
}
public int getAProtocol()
{
    return aprotocol;
}
public boolean isPrivacy()
{
    return doPrivacy;
}
public String getUserPrivPassw()
{
    return userPrivPassw;
}
public int getPProtocol()
{
    return pprotocol;
}
/**
 * Sets the host string.
 *
 * @param newVar the string
 */
public void setHost(String newVar)
{
     host = newVar;
     hostText.setText(host);
}

/**
 * Sets the local bind address
 *
 * @param newVar the string
 * @since 4_14
 */
public void setBindAddress(String newVar)
{
     bindAddr = newVar;
     bindText.setText(bindAddr);
}

/**
 * Sets the port string.
 *
 * @param newVar the string
 */
public void setPort(String newVar)
{
     port = newVar;
     portText.setText(port);
}
public void setSocketType(String newVar)
{
    socketType = newVar;
    if (socketType.equals(SnmpContextv3Face.TCP_SOCKET))
    {
        tcpSocketChoice.setSelected(true);
    }
    else
    {
        standardSocketChoice.setSelected(true);
    }
}

public void setContextEngineId(byte [] newVar)
{
    contextEngineId = newVar;
    String hexString = SnmpUtilities.toHexString(newVar);
    contextEngineIdText.setText(hexString);
}
public void setContextName(String newVar)
{
    contextName = newVar;
    contextNameText.setText(contextName);
}
public void setUserName(String newVar)
{
    userName = newVar;
    userNameText.setText(userName);
}
public void setAuthentication(boolean newVar)
{
    doAuthentication = newVar;
    authenticationChoice.setSelected(doAuthentication);
    userAuthPasswLabel.setEnabled(doAuthentication);
    userAuthPasswText.setEnabled(doAuthentication);
    md5ProtocolChoice.setEnabled(doAuthentication);
    sha1ProtocolChoice.setEnabled(doAuthentication);
}
public void setUserAuthPassw(String newVar)
{
    userAuthPassw = newVar;
    userAuthPasswText.setText(userAuthPassw);
}
public void setAProtocol(int newVar)
{
    aprotocol = newVar;
    if (aprotocol == SnmpContextv3Face.SHA1_PROTOCOL)
    {
        sha1ProtocolChoice.setSelected(true);
    }
    else
    {
        md5ProtocolChoice.setSelected(true);
    }
}

public void setPrivacy(boolean newVar)
{
    doPrivacy = newVar;
    privacyChoice.setSelected(doPrivacy);
    userPrivPasswLabel.setEnabled(doPrivacy);
    userPrivPasswText.setEnabled(doPrivacy);
    desProtocolChoice.setEnabled(doPrivacy);
    aesProtocolChoice.setEnabled(doPrivacy);
}

public void setUserPrivPassw(String newVar)
{
    userPrivPassw = newVar;
    userPrivPasswText.setText(userPrivPassw);
}

public void setPProtocol(int newVar)
{
    pprotocol = newVar;
    if (pprotocol == SnmpContextv3Face.AES_ENCRYPT)
    {
        aesProtocolChoice.setSelected(true);
    }
    else
    {
        desProtocolChoice.setSelected(true);
    }
}

private void jbInit() throws Exception
{
    buttonPanel.setLayout(gridLayout1);
    gridLayout1.setColumns(3);
    hostLabel.setText("Host:");
    hostText.setColumns(15);
    hostText.setText(host);
    bindLabel.setText("Bind:");
    bindText.setColumns(15);
    bindText.setText(bindAddr);
    portLabel.setText("Port:");
    portText.setColumns(15);
    portText.setText(port);
    contextEngineIdLabel.setText("Context Engine Id:");
    contextEngineIdText.setColumns(15);
    String hexString = SnmpUtilities.toHexString(contextEngineId);
    contextEngineIdText.setText(hexString);
    contextNameLabel.setText("Context Name:");
    contextNameText.setColumns(15);
    contextNameText.setText(contextName);
    userNameLabel.setText("User name:");
    userNameText.setColumns(15);
    userNameText.setText(userName);
    userAuthPasswLabel.setText("User Auth Password:");
    userAuthPasswText.setColumns(15);
    userAuthPasswText.setText(userAuthPassw);
    userPrivPasswLabel.setText("User Priv Password:");
    userPrivPasswText.setColumns(15);
    userPrivPasswText.setText(userPrivPassw);
    authenticationChoice.setText("Authentication");
    authenticationChoice.setSelected(doAuthentication);
    authenticationChoice.addItemListener(this);
    privacyChoice.setText("Privacy");
    privacyChoice.setSelected(doPrivacy);
    privacyChoice.addItemListener(this);
    okButton.setText("OK");
    okButton.addActionListener(this);
    applyButton.setText("Apply");
    applyButton.addActionListener(this);
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(this);
    fileButton.setText("Load properties file");
    fileButton.addActionListener(this);
    standardSocketChoice.setText(SnmpContextv3Face.STANDARD_SOCKET);
    tcpSocketChoice.setText(SnmpContextv3Face.TCP_SOCKET);
    md5ProtocolChoice.setText("MD5");
    sha1ProtocolChoice.setText("SHA1");
    desProtocolChoice.setText("DES");
    aesProtocolChoice.setText("AES");

    Container cont = this.getContentPane();
    cont.setLayout(gridBagLayout1);
    cont.add(fileButton, getGridBagConstraints2(0, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, 
            new Insets(2, 2, 2, 2), 0, 0));

    cont.add(hostLabel, getGridBagConstraints2(0, 1, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    cont.add(hostText, getGridBagConstraints2(1, 1, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(portLabel, getGridBagConstraints2(0, 2, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    cont.add(portText, getGridBagConstraints2(1, 2, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(bindLabel, getGridBagConstraints2(0, 3, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    cont.add(bindText, getGridBagConstraints2(1, 3, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(socketPanel, getGridBagConstraints2(0, 4, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(contextEngineIdLabel, getGridBagConstraints2(0, 5, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    cont.add(contextEngineIdText, getGridBagConstraints2(1, 5, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(contextNameLabel, getGridBagConstraints2(0, 6, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    cont.add(contextNameText, getGridBagConstraints2(1, 6, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(userNameLabel, getGridBagConstraints2(0, 7, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    cont.add(userNameText, getGridBagConstraints2(1, 7, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(authenticationChoice, getGridBagConstraints2(0, 8, 2, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(userAuthPasswLabel, getGridBagConstraints2(0, 9, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    cont.add(userAuthPasswText, getGridBagConstraints2(1, 9, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(aprotocolPanel, getGridBagConstraints2(0, 10, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(privacyChoice, getGridBagConstraints2(0, 11, 2, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    cont.add(userPrivPasswLabel, getGridBagConstraints2(0, 12, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    cont.add(userPrivPasswText, getGridBagConstraints2(1, 12, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    cont.add(pprotocolPanel, getGridBagConstraints2(0, 13, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));
    
    cont.add(buttonPanel, getGridBagConstraints2(0, 14, 3, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 5, 5, 5), 0, 0));
    buttonPanel.add(okButton, null);
    buttonPanel.add(applyButton, null);
    buttonPanel.add(cancelButton, null);

    socketPanel.setLayout(gridBagLayout2);
    socketPanel.add(standardSocketChoice, 
            getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    socketPanel.add(tcpSocketChoice, 
            getGridBagConstraints2(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    aprotocolPanel.setLayout(gridBagLayout3);
    aprotocolPanel.add(md5ProtocolChoice, 
            getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    aprotocolPanel.add(sha1ProtocolChoice, 
            getGridBagConstraints2(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

    pprotocolPanel.setLayout(gridBagLayout4);
    pprotocolPanel.add(desProtocolChoice, 
            getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    pprotocolPanel.add(aesProtocolChoice, 
            getGridBagConstraints2(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
    this.addWindowListener(this);
}

public void actionPerformed(ActionEvent evt)
{
    Object src = evt.getSource();
    if (src == okButton)
    {
        okButton_actionPerformed(evt);
    }
    else if (src == applyButton)
    {
        applyButton_actionPerformed(evt);
    }
    else if (src == cancelButton)
    {
        cancelButton_actionPerformed(evt);
    }
    else if (src == fileButton)
    {
        fileButton_actionPerformed(evt);
    }
    else if (src == fileChooser)
    {
        fileChooser_actionPerformed(evt);
    }
}

public void itemStateChanged(ItemEvent evt)
{
    Object src = evt.getSource();
    if (src == authenticationChoice)
    {
        authenticationChoice_itemStateChanged(evt);
    }
    else if (src == privacyChoice)
    {
        privacyChoice_itemStateChanged(evt);
    }
}

public void fillinFromUtil(Util u)
{
    util = u;

    String host = util.getHost();
    String bindAddr = util.getBindAddress();
    int port = util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    String socketType = util.getSocketType();
    byte[] engineId = util.getContextEngineId();
    String contextName = util.getContextName();
    String userName = util.getUserName();
    int auth = util.getUseAuth();
    String authPassw = util.getUserAuthPassword();
    int aproto = util.getAuthProcotol();
    int priv = util.getUsePriv();
    String privPassw = util.getUserPrivPassword();
    int pproto = util.getPrivProcotol();

    setHost(host);
    setBindAddress(bindAddr);
    setPort(""+port);
    setSocketType(socketType);
    setContextEngineId(engineId);
    setContextName(contextName);
    setUserName(userName);
    setAuthentication((auth == 1));
    setUserAuthPassw(authPassw);
    setAProtocol(aproto);
    setPrivacy((priv == 1));
    setUserPrivPassw(privPassw);
    setPProtocol(pproto);
}

void fileChooser_actionPerformed(ActionEvent evt)
{
    String command = evt.getActionCommand();
    if (command.equals(JFileChooser.APPROVE_SELECTION))
    {
        File file = fileChooser.getSelectedFile();
        if (file != null)
        {
            if (util != null)
            {
                util.loadPropfile(file);
            }
            else
            {
                util = new Util(file.getName(), null);
            }
            fillinFromUtil(util);
        }
    }
}

void fileButton_actionPerformed(ActionEvent evt)
{
    if (fileChooser == null)
    {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose properties file:");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new IsPropertiesFilter());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.addActionListener(this);

        File dir = new File(System.getProperty("user.dir"));
        if (util != null)
        {
            File propFile = util.getPropertiesFile();
            if (propFile != null)
            {
                fileChooser.setCurrentDirectory(propFile);
                fileChooser.setSelectedFile(propFile);
            }
            else
            {
                fileChooser.setCurrentDirectory(dir);
            }
        }
        else
        {
            fileChooser.setCurrentDirectory(dir);
        }
    }
    fileChooser.showOpenDialog(this);
}

void cancelButton_actionPerformed(ActionEvent evt)
{
    this.setVisible(false);
}

void okButton_actionPerformed(ActionEvent evt)
{
    this.setVisible(false);
    setAllVars();
    fireActionPerformed(evt);
}

void applyButton_actionPerformed(ActionEvent evt)
{
    setAllVars();
    fireActionPerformed(evt);
}

void authenticationChoice_itemStateChanged(ItemEvent evt)
{
    int state = evt.getStateChange();
    boolean selected = (state == ItemEvent.SELECTED);
    userAuthPasswLabel.setEnabled(selected);
    userAuthPasswText.setEnabled(selected);
    md5ProtocolChoice.setEnabled(selected);
    sha1ProtocolChoice.setEnabled(selected);
}

void privacyChoice_itemStateChanged(ItemEvent evt)
{
    int state = evt.getStateChange();
    boolean selected = (state == ItemEvent.SELECTED);
    userPrivPasswLabel.setEnabled(selected);
    userPrivPasswText.setEnabled(selected);
    aesProtocolChoice.setEnabled(selected);
    desProtocolChoice.setEnabled(selected);
}

private void setAllVars()
{
    host = hostText.getText();
    port = portText.getText();
    bindAddr = bindText.getText();
    if (bindAddr.length() == 0)
    {
        bindAddr = null;
    }

    if (standardSocketChoice.isSelected())
    {
        socketType = SnmpContextv3Face.STANDARD_SOCKET;
    }
    else if (tcpSocketChoice.isSelected())
    {
        socketType = SnmpContextv3Face.TCP_SOCKET;
    }

    contextName = contextNameText.getText();
    userName = userNameText.getText();
    userAuthPassw = userAuthPasswText.getText();
    userPrivPassw = userPrivPasswText.getText();

    doAuthentication = authenticationChoice.isSelected();
    doPrivacy = privacyChoice.isSelected();
    if (md5ProtocolChoice.isSelected())
    {
        aprotocol = SnmpContextv3Face.MD5_PROTOCOL;
    }
    else
    {
        aprotocol = SnmpContextv3Face.SHA1_PROTOCOL;
    }
    if (desProtocolChoice.isSelected())
    {
        pprotocol = SnmpContextv3Face.DES_ENCRYPT;
    }
    else
    {
        pprotocol = SnmpContextv3Face.AES_ENCRYPT;
    }
    String hexString = contextEngineIdText.getText();
    contextEngineId = SnmpUtilities.toBytes(hexString);
}

/**
 * Adds an action listener to be notified when the "Apply" or "OK" button
 * is pressed.
 * @see #removeActionListener
 */
public void addActionListener(ActionListener l)
{
    actionPerformedListener.addElement(l);
}

/**
 * Removes an action listener.
 * @see #addActionListener
 */
public void removeActionListener(ActionListener l)
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
protected void fireActionPerformed(ActionEvent evt)
{
    Vector listeners = (Vector) actionPerformedListener.clone();

    ActionEvent event = new ActionEvent(this, evt.getID(), 
          evt.getActionCommand(), evt.getModifiers());
 
    int sz = listeners.size();
    for (int i=0; i<sz; i++)
    {
        ActionListener l = (ActionListener) listeners.elementAt(i);
        l.actionPerformed(event);
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
 

/**
 * IsPropertiesFilter filters all the properties files.
 * A file is a properties file if it ends with ".properties".
 */

class IsPropertiesFilter extends javax.swing.filechooser.FileFilter
{
    public final static String TAIL = ".properties";

public boolean accept(File test)
{
    boolean ret = false;

    String name = test.getName();
    if (test.isFile()) 
    {
        if (name.endsWith(TAIL))
        {
            ret = true;
        }
    }
    else if (test.isDirectory())
    {
        ret = true;
    }
    return ret;
}

public String getDescription()
{
    return "Properties Files (*.properties)";
}


}

}
