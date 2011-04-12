// NAME
//      $RCSfile: OneInterface.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.3 $
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
import java.beans.*;

import uk.co.westhawk.snmp.beans.*;
import uk.co.westhawk.snmp.pdu.*;
import uk.co.westhawk.snmp.stack.*;

/**
 * <p>
 * The class OneInterface 
 * displays the information that is available from the bean.
 * </p>
 *
 * <p>
 * This class is used by testInterfaceBean class. It uses the
 * OneInterfaceBean to get its information.
 * </p>
 *
 * @see testInterfaceBean
 * @see uk.co.westhawk.snmp.beans.OneInterfaceBean
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.3 $ $Date: 2006/01/17 17:43:55 $
 */
public class OneInterface extends JDialog 
    implements ActionListener, PropertyChangeListener
{
    private static final String     version_id =
        "@(#)$Id: OneInterface.java,v 1.3 2006/01/17 17:43:55 birgit Exp $ Copyright Westhawk Ltd";

    JPanel panel1 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel label1 = new JLabel(" ");
    JLabel interfaceLabel = new JLabel(" ");
    JLabel label3 = new JLabel(" ");
    JLabel label4 = new JLabel(" ");
    JLabel label5 = new JLabel(" ");
    JLabel speedLabel = new JLabel(" ");
    JLabel descriptionLabel = new JLabel(" ");
    JLabel statusLabel = new JLabel(" ");
    JButton cancelButton = new JButton();
    uk.co.westhawk.snmp.beans.OneInterfaceBean oneInterfaceBean = new uk.co.westhawk.snmp.beans.OneInterfaceBean();

    String host, port, community, interval;
    int index;

    
    /**
     * Constructor
     *
     * @param h the hostname
     * @param p the port no
     * @param com the community name
     * @param ind the interface index
     * @param ival the update interval
     *
     */
    public OneInterface(JFrame fr, String h, String p, String com, int ind, String ival)
    {
        super(fr, "One Interface", false);

        host = h;
        port = p;
        community = com;
        index = ind;
        interval = ival;

        this.setTitle("Host " + host + ", Interface " + index);
        try
        {
            jbInit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        interfaceLabel.setText(String.valueOf(index));
        this.setSize(400, 150);
        this.setVisible(true);

        oneInterfaceBean.setHost(host);
        oneInterfaceBean.setPort(port);
        oneInterfaceBean.setCommunityName(community);
        oneInterfaceBean.setUpdateInterval(interval);
        oneInterfaceBean.setIndex(index);
        oneInterfaceBean.action();
    }


    void jbInit() throws Exception
    {
        panel1.setLayout(gridBagLayout1);
        label1.setText(" Interface No");
        label3.setText(" Speed");
        label4.setText(" Description");
        label5.setText(" Status");
        interfaceLabel.setBackground(Color.white);
        speedLabel.setBackground(Color.white);
        descriptionLabel.setBackground(Color.white);
        statusLabel.setBackground(Color.white);
        cancelButton.setText(" Cancel");
        interfaceLabel.setOpaque(true);
        speedLabel.setOpaque(true);
        descriptionLabel.setOpaque(true);
        statusLabel.setOpaque(true);
        oneInterfaceBean.addPropertyChangeListener(this);
        cancelButton.addActionListener(this);
        this.getContentPane().add(panel1);
        panel1.add(label1, 
            getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(10, 0, 0, 0), 0, 0));
        panel1.add(label3, 
            getGridBagConstraints2(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(label4, 
            getGridBagConstraints2(0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(label5, 
            getGridBagConstraints2(0, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));

        panel1.add(interfaceLabel, 
            getGridBagConstraints2(1, 0, 1, 1, 1.0, 0.0, 
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
            new Insets(10, 10, 0, 0), 0, 0));
        panel1.add(speedLabel, 
            getGridBagConstraints2(1, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 10, 0, 0), 0, 0));
        panel1.add(descriptionLabel, 
            getGridBagConstraints2(1, 2, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 10, 0, 0), 0, 0));
        panel1.add(statusLabel, 
            getGridBagConstraints2(1, 3, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 10, 0, 0), 0, 0));
        panel1.add(cancelButton, 
            getGridBagConstraints2(0, 4, 2, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, 
            new Insets(10, 10, 10, 10), 0, 0));
    }

    public void propertyChange(PropertyChangeEvent e)
    {
        oneInterfaceBean_propertyChange(e);
    }

    public void actionPerformed(ActionEvent e)
    {
        cancelButton_actionPerformed(e);
    }

    void cancelButton_actionPerformed(ActionEvent e)
    {
        this.setVisible(false);
        oneInterfaceBean.setRunning(false);
    }

    void oneInterfaceBean_propertyChange(PropertyChangeEvent e)
    {
         speedLabel.setText(String.valueOf(oneInterfaceBean.getSpeed()));
         statusLabel.setText(oneInterfaceBean.getOperStatusString());
         descriptionLabel.setText(oneInterfaceBean.getDescription());
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
}
