// NAME
//      $RCSfile: testDialogChannelStatusBean.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.6 $
// CREATED
//      $Date: 2006/02/02 15:49:36 $
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

import uk.co.westhawk.snmp.beans.*;
import uk.co.westhawk.visual.*;
import uk.co.westhawk.tablelayout.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*; 
import java.util.*;
import java.awt.event.*;

import java.beans.*;

/**
 * <p>
 * The class testDialogChannelStatusBean demonstrates the use of some of 
 * the beans
 * in the uk.co.westhawk.snmp.beans packages. It shows a UI in which the
 * user can configure the bean properties.
 * </p>
 *
 * <p>
 * When triggering the menu button at the background, a property dialog 
 * will popup.
 * The user can configure the host name, the port number, the community
 * name and the update interval. When the OK or Apply button is pressed
 * DialogChannelStatusBean is activated. 
 * </p>
 *
 * <p>
 * The "Try it" button in the dialog activates the IsHostReachableBean, 
 * who will probe
 * the configured host and signals the UI since when the host was up.
 * </p>
 *
 * <p>
 * The UI will show a Swing JTree. The DialogChannelStatusBean will
 * update the tree.
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
 * <code>testDialogChannelStatusBean.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @see uk.co.westhawk.snmp.beans.DialogChannelStatusBean
 * @see uk.co.westhawk.snmp.beans.IsHostReachableBean
 * @see propertyDialog
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.6 $ $Date: 2006/02/02 15:49:36 $
 */
public class testDialogChannelStatusBean extends JPanel 
        implements ActionListener, PropertyChangeListener,
        WindowListener, MouseListener
{
    private static final String     version_id =
        "@(#)$Id: testDialogChannelStatusBean.java,v 1.6 2006/02/02 15:49:36 birgit Exp $ Copyright Westhawk Ltd";

    propertyDialog propDialog;
    JFrame myFrame;

    String name;
    String message;

    GridBagLayout gridBagLayout1;
    JLabel label7;

    JTree tree;

    DialogChannelStatusBean dialogBean;
    private Util      util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public testDialogChannelStatusBean(String propertiesFilename)
{
    //AsnObject.setDebug(15);
    util = new Util(propertiesFilename, this.getClass().getName());

    dialogBean = new DialogChannelStatusBean();

    DefaultTreeSelectionModel gtsm = new DefaultTreeSelectionModel();
    DefaultTreeModel gmodel = new DefaultTreeModel((TreeNode)dialogBean, false);

    gtsm.setSelectionMode(gtsm.SINGLE_TREE_SELECTION);
    tree = new JTree(gmodel);
    tree.setRootVisible(false);
    tree.setSelectionModel(gtsm);

    // This way the bean will update the model by itself,
    // no need for a propertyChangeListener
    dialogBean.setDefaultTreeModel(gmodel);

}

//Initialize the application

public void init()
{
    try
    {
        createPropertyDialog();
        jbInit();
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
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
    testDialogChannelStatusBean application = new testDialogChannelStatusBean(propFileName);

    try
    {
        javax.swing.UIManager.setLookAndFeel(
            javax.swing.UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception exc)
    {
        ;
    }
    JFrame frame = new JFrame();
    frame.setTitle(application.getClass().getName());
    frame.getContentPane().add(application, BorderLayout.CENTER);

    frame.setBounds(50, 50, 300, 400);
    frame.setVisible(true);
    frame.addWindowListener(application);

    application.init();
}

public void actionPerformed(ActionEvent evt)
{
    Object src = evt.getSource();
    if (src == propDialog)
    {
        propDialog_actionPerformed(evt);
    }
}

public void propertyChange(PropertyChangeEvent evt)
{
    Object src = evt.getSource();
    if (src == dialogBean)
    {
        dialogBean_propertyChange(evt);
    }
}

void dialogBean_propertyChange(PropertyChangeEvent evt)
{
    Enumeration e = dialogBean.getChannelIndexes();
    while (e.hasMoreElements())
    {
        Integer index = (Integer) e.nextElement();
        String status = dialogBean.getChannelStatusString(index);
        System.out.println("Channel " + index 
                + " has status " + status);
    }
    System.out.println("\t--");
}

void propDialog_actionPerformed(ActionEvent evt)
{
    String cmd = evt.getActionCommand();
    if (cmd.equals("Cancel") == false)
    {
        String host = propDialog.getHost();
        myFrame.setTitle("Dialogic " + host);
        dialogBean.setHost(host);
        dialogBean.setPort(propDialog.getPort());
        dialogBean.setBindAddress(propDialog.getBindAddress());
        dialogBean.setCommunityName(propDialog.getCommunityName());
        dialogBean.setUpdateInterval(propDialog.getUpdateInterval());

        dialogBean.action();
    }
}

//Component initialization

private void jbInit() throws Exception
{
    TableLayout table = new TableLayout();

    // The bean and the model should keep each other informed
    //dialogBean.addPropertyChangeListener(this);
    //this.setBackground(Color.lightGray);

    JScrollPane spane = new JScrollPane();
    spane.getViewport().add(tree);

    this.addMouseListener(this);
    tree.addMouseListener(this);

    Container cont = this;
    cont.setLayout(table);

    cont.add("0 0", spane);
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
    String comm = util.getCommunity();
    String bindAddr = util.getBindAddress();
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
