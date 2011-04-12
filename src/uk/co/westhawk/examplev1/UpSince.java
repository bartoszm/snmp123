// NAME
//      $RCSfile: UpSince.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.5 $
// CREATED
//      $Date: 2006/01/30 11:36:57 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1996 - 1998 by Westhawk Ltd (www.westhawk.nl)
 * Copyright (C) 1998 - 2006 by Westhawk Ltd 
 * <a href="www.westhawk.co.uk">www.westhawk.co.uk</a>
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
import java.awt.event.*; 
import javax.swing.*;
import java.util.*;

import java.net.*;

/**
 * <p>
 * The UpSince application will ask a host how long it has been up
 * (sysUpTime). It uses the UpSincePdu to get its information.
 * </p>
 *
 * <p>
 * Unless an exception occurred the Object to the update() method of the
 * Observer will be an Date.
 * In the case of an exception, that exception will be passed.
 * </p>
 *
 * <p>
 * The host can be configured 
 * in the properties file, 
 * the rest of the parameters is hard coded.
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>UpSince.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @see uk.co.westhawk.snmp.pdu.UpSincePdu
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 1.5 $ $Date: 2006/01/30 11:36:57 $
 */
public class UpSince extends JPanel implements Observer
{
    private static final String     version_id =
        "@(#)$Id: UpSince.java,v 1.5 2006/01/30 11:36:57 birgit Exp $ Copyright Westhawk Ltd";

    /**
     * Use 161 as port no
     */
    public final static int port = SnmpContextBasisFace.DEFAULT_PORT;

    private SnmpContext context;
    JLabel h;
    JLabel v;
    Util util;

    /**
     * Constructor.
     *
     * @param propertiesFilename The name of the properties file. Can be
     * null.
     */
    public UpSince(String propertiesFilename)
    {
        util = new Util(propertiesFilename, this.getClass().getName());
    }

    public void init () 
    {
        String host = util.getHost();
        String bindAddr = util.getBindAddress();

        h = new JLabel(host+ " up since: ");
        v = new JLabel(" unknown ");
        setLayout(new GridLayout(2, 1));
        add(h);
        add(v);
        try 
        {
            context = new SnmpContext(host, port, bindAddr,
                SnmpContextBasisFace.STANDARD_SOCKET);
        }
        catch (java.io.IOException exc)
        {
            System.out.println("IOException " + exc.getMessage());
            System.exit(0);
        }
    }

    public void start()
    {
        if (context != null) 
        {
            try
            {
                UpSincePdu up = new UpSincePdu(context, this);
            }
            catch (java.io.IOException exc) 
            {
                System.out.println("start(): IOException " 
                    + exc.getMessage());
            }
            catch (PduException exc) 
            {
                System.out.println("start(): PduException " 
                    + exc.getMessage());
            }
        }
    }

    /**
     * Implementing the Observer interface. Receiving the response from 
     * the Pdu.
     *
     * @param obs the UpSincePdu variable
     * @param ov the date
     *
     * @see uk.co.westhawk.snmp.pdu.UpSincePdu
     */
    public void update(Observable obs, Object ov)
    {
        Pdu pdu = (Pdu) obs;
        if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
        {
            Date dres = (Date) ov;
            if (dres != null)
            {
                // TODO: invokeLater
                v.setText(dres.toString());
            }
        }
        else
        {
            // TODO: invokeLater
            v.setText(pdu.getErrorStatusString());
        }
    }

public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    UpSince application = new UpSince(propFileName);

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
    frame.setBounds(50, 50, 350, 100);
    frame.setVisible(true);

    application.start();
}


}
