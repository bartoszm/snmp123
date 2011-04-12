// NAME
//      $RCSfile: OneNTService.java,v $
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
import uk.co.westhawk.snmp.beans.*;
import uk.co.westhawk.snmp.pdu.*;
import uk.co.westhawk.snmp.stack.*;
import java.beans.*;

/**
 * <p>
 * The class OneNTService 
 * displays the information that is available from the bean.
 * </p>
 *
 * <p>
 * This class is used by testNTServiceNamesBean class. It uses the
 * OneNTServiceBean to get its information.
 * </p>
 *
 * @see testNTServiceNamesBean
 * @see uk.co.westhawk.snmp.beans.OneNTServiceBean
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.3 $ $Date: 2006/01/17 17:43:55 $
 */
public class OneNTService extends JDialog 
    implements ActionListener, PropertyChangeListener
{
    private static final String     version_id =
        "@(#)$Id: OneNTService.java,v 1.3 2006/01/17 17:43:55 birgit Exp $ Copyright Westhawk Ltd";

    JPanel panel1 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel label1 = new JLabel(" ");
    JLabel label2 = new JLabel(" ");
    JLabel label3 = new JLabel(" ");
    JLabel label4 = new JLabel(" ");
    JLabel label5 = new JLabel(" ");
    JLabel nameLabel = new JLabel(" ");
    JLabel instalLabel = new JLabel(" ");
    JLabel operatingLabel = new JLabel(" ");
    JLabel canUninstLabel = new JLabel(" ");
    JLabel canPauzeLabel = new JLabel(" ");
    JButton cancelButton = new JButton();
    uk.co.westhawk.snmp.beans.OneNTServiceBean oneNTServiceBean = new uk.co.westhawk.snmp.beans.OneNTServiceBean();

    String host, port, community, interval;
    String index;

    
    /**
     * Constructor
     *
     * @param h the hostname
     * @param p the port no
     * @param com the community name
     * @param ind the index
     * @param ival the update interval
     *
     */
    public OneNTService(JFrame fr, String h, String p, String com, String ind, String ival)
    {
        super(fr, "One NT Service", false);

        host = h;
        port = p;
        community = com;
        index = ind;
        interval = ival;

        this.setTitle("Host " + host);
        try
        {
            jbInit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        this.setVisible(true);
        this.setSize(400, 150);

        oneNTServiceBean.setHost(host);
        oneNTServiceBean.setPort(port);
        oneNTServiceBean.setCommunityName(community);
        oneNTServiceBean.setUpdateInterval(interval);
        oneNTServiceBean.setIndex(index);
        oneNTServiceBean.action();
    }


    void jbInit() throws Exception
    {
        panel1.setLayout(gridBagLayout1);
        label1.setText(" Name ");
        label2.setText(" Installed  State ");
        label3.setText(" Operating State ");
        label4.setText(" Can Be Uninstalled ");
        label5.setText(" Can Be Paused ");
        nameLabel.setBackground(Color.white);
        instalLabel.setBackground(Color.white);
        operatingLabel.setBackground(Color.white);
        canUninstLabel.setBackground(Color.white);
        canPauzeLabel.setBackground(Color.white);
        nameLabel.setOpaque(true);
        instalLabel.setOpaque(true);
        operatingLabel.setOpaque(true);
        canUninstLabel.setOpaque(true);
        canPauzeLabel.setOpaque(true);
        oneNTServiceBean.addPropertyChangeListener(this);
        cancelButton.setText(" Cancel");
        cancelButton.addActionListener(this);
        this.getContentPane().add(panel1);
        panel1.add(label1, getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(label2, getGridBagConstraints2(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(label3, getGridBagConstraints2(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(label4, getGridBagConstraints2(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(label5, getGridBagConstraints2(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(nameLabel, getGridBagConstraints2(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(instalLabel, getGridBagConstraints2(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(operatingLabel, getGridBagConstraints2(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(canUninstLabel, getGridBagConstraints2(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(canPauzeLabel, getGridBagConstraints2(1, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(cancelButton, getGridBagConstraints2(0, 5, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, 
            new Insets(10, 10, 10, 10), 0, 0));
    }

    public void propertyChange(PropertyChangeEvent e)
    {
        oneNTServiceBean_propertyChange(e);
    }

    public void actionPerformed(ActionEvent e)
    {
        cancelButton_actionPerformed(e);
    }

    void cancelButton_actionPerformed(ActionEvent e)
    {
        this.setVisible(false);
        oneNTServiceBean.setRunning(false);
    }

    void oneNTServiceBean_propertyChange(PropertyChangeEvent e)
    {
         boolean b;
         String can;

         nameLabel.setText(oneNTServiceBean.getName());
         instalLabel.setText(oneNTServiceBean.getInstalledState());
         operatingLabel.setText(oneNTServiceBean.getOperatingState());

         b = oneNTServiceBean.getCanBeUninstalled();
         if (b)
         {
            can = "Yes";
         }
         else
         {
            can = "No";
         }
         canUninstLabel.setText(can);

         b = oneNTServiceBean.getCanBePaused();
         if (b)
         {
            can = "Yes";
         }
         else
         {
            can = "No";
         }
         canPauzeLabel.setText(can);
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
