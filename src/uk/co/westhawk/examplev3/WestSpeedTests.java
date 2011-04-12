// NAME
//      $RCSfile: WestSpeedTests.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.4 $
// CREATED
//      $Date: 2006/01/17 17:43:52 $
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


import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;
import uk.co.westhawk.snmp.stack.*;

/**
 * The WestSpeedTests application uses a couple of StreamEventMonitor objects
 * for speed test.
 *
 * @see StreamEventMonitor
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 1.4 $ $Date: 2006/01/17 17:43:52 $
 */
public class WestSpeedTests extends JComponent implements ActionListener
{
    private static final String     version_id =
        "@(#)$Id: WestSpeedTests.java,v 1.4 2006/01/17 17:43:52 birgit Exp $ Copyright Westhawk Ltd";

    private StreamEventMonitor streamEventMonitor;
    public final static byte[] IffishBytes =
    {
        (byte) (0x00),
    };

    public final static byte[] MgSoftBytes =
    {
        (byte) (0x01),
        (byte) (0x00),
        (byte) (0x00),
        (byte) (0xa1),
        (byte) (0xd4),
        (byte) (0x1e),
        (byte) (0x49),
        (byte) (0x46),
    };
    JButton button = new JButton ("Go");
    JLabel label = new JLabel("Press 'Go' to start");
    
public void init()
{
    this.setLayout(new BorderLayout());
    button.addActionListener(this);
    label.setBackground(Color.white);
    label.setOpaque(true);
    this.add(button, BorderLayout.CENTER);
    this.add(label, BorderLayout.SOUTH);
}

public void actionPerformed(ActionEvent evt)
{
    label.setText("Running ..");
    streamEventMonitor = new StreamEventMonitor("iffish", 10, false);
    streamEventMonitor.run();

    streamEventMonitor = new StreamEventMonitor("iffish", 10, true);
    streamEventMonitor.setUCM("authUserMD5", "AuthPassword",
          "public", IffishBytes, SnmpContextv3Face.MD5_PROTOCOL);
    streamEventMonitor.run();

    streamEventMonitor = new StreamEventMonitor("iffish", 10, true);
    streamEventMonitor.setUCM("authUserSHA1", "AuthPassword", 
          "public", IffishBytes, SnmpContextv3Face.SHA1_PROTOCOL);
    streamEventMonitor.run();
    label.setText("Done.");
}

public static void main(String [] argv)
{
    WestSpeedTests application = new WestSpeedTests();
    JFrame frame = new JFrame();
    frame.setTitle(application.getClass().getName());
    frame.addWindowListener(new WindowAdapter()
    {
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    });
    frame.setSize(new Dimension(150, 100));
    frame.getContentPane().add(application, BorderLayout.CENTER);

    application.init();
    frame.setVisible(true);
}

}
