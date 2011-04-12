// NAME
//      $RCSfile: Interfaces.java,v $
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
 * The Interfaces application will ask all interfaces once for their
 * operational state. It uses the InterfaceGetNextPdu to get its information.
 * </p>
 *
 * <p>
 * The host can be configured 
 * in the properties file, 
 * the rest of the parameters is hard coded.
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>Interfaces.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @see uk.co.westhawk.snmp.pdu.InterfaceGetNextPdu
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.5 $ $Date: 2006/01/30 11:36:57 $
 */
public class Interfaces extends JPanel implements Observer
{
    private static final String     version_id =
        "@(#)$Id: Interfaces.java,v 1.5 2006/01/30 11:36:57 birgit Exp $ Copyright Westhawk Ltd";

    /**
     * Use "public" as community name
     */
    public final static String community = "public";
    /**
     * Use 161 as port no
     */
    public final static int port = SnmpContextBasisFace.DEFAULT_PORT;

    private SnmpContext context;
    private JLabel h;
    private JLabel v[];
    private Util util;
    private int count;

    /**
     * Constructor.
     *
     * @param propertiesFilename The name of the properties file. Can be
     * null.
     */
    public Interfaces(String propertiesFilename)
    {
        util = new Util(propertiesFilename, this.getClass().getName());
    }

    public void init () 
    {
        String host = util.getHost();
        String bindAddr = util.getBindAddress();

        h = new JLabel("Interface status of host " + host);
        try 
        {
            // create the context
            context = new SnmpContext(host, port, bindAddr, 
                SnmpContextBasisFace.STANDARD_SOCKET);
            context.setCommunity(community);

            // ask for the number of current interfaces
            int ifCount = InterfaceGetNextPdu.getIfNumber(context);

            setLayout(new GridLayout(ifCount+1, 1));
            add(h);
            v = new JLabel[ifCount];
            for (int i=0; i< ifCount; i++) 
            {
              v[i] = new JLabel("unknown "+i);
              add(v[i]);
            }
        }
        catch (java.io.IOException exc)
        {
            System.out.println("IOException " + exc.getMessage());
            System.exit(0);
        }
        catch (PduException exc) 
        {
            System.out.println("PduException " + exc.getMessage());
            System.exit(0);
        }

    }

    public void start()
    {
        InterfaceGetNextPdu pdu;

        // Send a request to the first interfaces
        // The interface indexes may not be consecutive !!
        count = 0;
        if (context != null) 
        {
            try
            {
                pdu = new InterfaceGetNextPdu(context);
                pdu.addObserver(this);
                pdu.addOids(count);
                pdu.send();
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
     * @param obs the InterfaceGetNextPdu variable
     * @param ov the varbind
     *
     * @see uk.co.westhawk.snmp.pdu.InterfaceGetNextPdu
     * @see uk.co.westhawk.snmp.stack.varbind
     */
    public void update(Observable obs, Object ov)
    {
        // I've got the answer, display it
        // TODO: invokeLater

        int index;
        InterfaceGetNextPdu pdu = (InterfaceGetNextPdu) obs;
        if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
        {
            // display the value
            v[count++].setText(pdu.getIfDescr() + " " + pdu.getIfOperStatusStr());

            // get the index, so we can ask for the next one
            index = pdu.getIfIndex();
        }
        else
        {
            // start again
            count = 0;
            index = 0;
            try
            {
                Thread.sleep(5000);
            }
            catch (java.lang.InterruptedException exc) {}
        }

        try
        {
            pdu = new InterfaceGetNextPdu(context);
            pdu.addObserver(this);
            pdu.addOids(index);
            pdu.send();
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

    public static void main(String[] args)
    {
        String propFileName = null;
        if (args.length > 0)
        {
            propFileName = args[0];
        }
        Interfaces application = new Interfaces(propFileName);
        application.init();
        application.start();

        JFrame frame = new JFrame();
        frame.setTitle(application.getClass().getName());
        frame.getContentPane().add(application, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        frame.setBounds(50, 50, 400, 100);
        frame.setVisible(true);
    }

}
