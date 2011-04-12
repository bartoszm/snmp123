// NAME
//      $RCSfile: OneNTPrintQ.java,v $
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
 * The class OneNTPrintQ 
 * displays the information that is available from the bean.
 * </p>
 *
 * <p>
 * This class is used by testNTPrintQBean class. It uses the
 * OneNTPrintQBean to get its information.
 * </p>
 *
 * @see testNTPrintQBean
 * @see uk.co.westhawk.snmp.beans.OneNTPrintQBean
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.3 $ $Date: 2006/01/17 17:43:55 $
 */
public class OneNTPrintQ extends JDialog 
    implements ActionListener, PropertyChangeListener
{
    private static final String     version_id =
        "@(#)$Id: OneNTPrintQ.java,v 1.3 2006/01/17 17:43:55 birgit Exp $ Copyright Westhawk Ltd";

    JPanel panel1 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel label1 = new JLabel(" ");
    JLabel label2 = new JLabel(" ");
    JLabel nameLabel = new JLabel(" ");
    JLabel numJobLabel = new JLabel(" ");
    JButton cancelButton = new JButton();
    uk.co.westhawk.snmp.beans.OneNTPrintQBean oneNTPrintQBean = new uk.co.westhawk.snmp.beans.OneNTPrintQBean();

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
    public OneNTPrintQ(JFrame fr, String h, String p, String com, String ind, String ival)
    {
        super(fr, "One NT PrintQ", false);

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

        oneNTPrintQBean.setHost(host);
        oneNTPrintQBean.setPort(port);
        oneNTPrintQBean.setCommunityName(community);
        oneNTPrintQBean.setUpdateInterval(interval);
        oneNTPrintQBean.setIndex(index);
        oneNTPrintQBean.action();
    }


    void jbInit() throws Exception
    {
        panel1.setLayout(gridBagLayout1);
        label1.setText(" Name ");
        label2.setText(" Number of Jobs ");
        nameLabel.setBackground(Color.white);
        numJobLabel.setBackground(Color.white);
        nameLabel.setOpaque(true);
        numJobLabel.setOpaque(true);
        oneNTPrintQBean.addPropertyChangeListener(this);
        cancelButton.setText(" Cancel");
        cancelButton.addActionListener(this);
        this.getContentPane().add(panel1);
        panel1.add(label1, getGridBagConstraints2(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(label2, getGridBagConstraints2(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, 
            new Insets(0, 0, 0, 0), 0, 0));

        panel1.add(nameLabel, getGridBagConstraints2(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 10, 0, 0), 0, 0));
        panel1.add(numJobLabel, getGridBagConstraints2(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
            new Insets(0, 10, 0, 0), 0, 0));
        panel1.add(cancelButton, getGridBagConstraints2(0, 2, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, 
            new Insets(10, 10, 10, 10), 0, 0));
    }

    public void propertyChange(PropertyChangeEvent e)
    {
        oneNTPrintQBean_propertyChange(e);
    }

    public void actionPerformed(ActionEvent e)
    {
        cancelButton_actionPerformed(e);
    }

    void cancelButton_actionPerformed(ActionEvent e)
    {
        this.setVisible(false);
        oneNTPrintQBean.setRunning(false);
    }

    void oneNTPrintQBean_propertyChange(PropertyChangeEvent e)
    {
         boolean b;
         String can;

         nameLabel.setText(oneNTPrintQBean.getName());
         numJobLabel.setText(String.valueOf(oneNTPrintQBean.getNumJobs()));
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
