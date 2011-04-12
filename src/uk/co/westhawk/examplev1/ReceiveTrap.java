// NAME
//      $RCSfile: ReceiveTrap.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.7 $
// CREATED
//      $Date: 2007/10/17 11:19:29 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2001 - 2006 by Westhawk Ltd
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
import java.net.*;
import uk.co.westhawk.snmp.stack.*;    
import uk.co.westhawk.snmp.pdu.*;    
import uk.co.westhawk.snmp.event.*;    
import uk.co.westhawk.snmp.util.*;    

/**
 * <p>
 * The ReceiveTrap receives traps and request pdus. 
 * </p>
 *
 * <p>
 * The host, port, etc can be configured 
 * in the properties file. 
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>ReceiveTrap.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * <p>
 * On UNIX and Linux you will have to run this application as
 * <em>root</em>.
 * </p>
 *
 * @see uk.co.westhawk.snmp.stack.SnmpContext
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.7 $ $Date: 2007/10/17 11:19:29 $
 */
public class ReceiveTrap extends JPanel 
implements RawPduListener, TrapListener, RequestPduListener, Observer
{
    private static final String     version_id =
        "@(#)$Id: ReceiveTrap.java,v 1.7 2007/10/17 11:19:29 birgita Exp $ Copyright Westhawk Ltd";

    private SnmpContext context;
    private Util util;
    private ListeningContextPool defTrap = null;

/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public ReceiveTrap(String propertiesFilename)
{
    //AsnObject.setDebug(6);
    AsnObject.setDebug(0);
    util = new Util(propertiesFilename, this.getClass().getName());
}

public void init () 
{
    String host = util.getHost();
    String bindAddr = util.getBindAddress();
    int port = util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    String socketType = util.getSocketType();
    String community = util.getCommunity();

    try 
    {
        context = new SnmpContext(host, port, bindAddr, socketType);
        context.setCommunity(community);


        // listen on port 162
        defTrap = new ListeningContextPool(ListeningContextPool.DEFAULT_TRAP_PORT, bindAddr, socketType);
        defTrap.addUnhandledRawPduListener(this);
        context.addTrapListener(this, ListeningContextPool.DEFAULT_TRAP_PORT);

        // listen on port 161
        defTrap = new ListeningContextPool(SnmpContextBasisFace.DEFAULT_PORT, bindAddr, socketType);
        defTrap.addUnhandledRawPduListener(this);
        context.addRequestPduListener(this, SnmpContextBasisFace.DEFAULT_PORT);

        System.out.println("ReceiveTrap.init(): " 
            + context.toString());
    }
    catch (java.io.IOException exc)
    {
        System.out.println("ReceiveTrap.init(): IOException " 
            + exc.getMessage());
        exc.printStackTrace();
        System.exit(0);
    }
}

public void update(Observable obs, Object ov)
{
    Pdu pdu = (Pdu) obs;
    System.out.println("ReceiveTrap.update(): " + pdu.toString());
}

public void destroy() 
{ 
    if (context != null)
    {
        context.destroy();
    }
}

public void trapReceived(TrapEvent evt)
{
    System.out.println(getClass().getName() + ".trapReceived():");

    int port = evt.getHostPort();
    Pdu trapPdu = evt.getPdu();
    int reqId = trapPdu.getReqId();
    SnmpContextBasisFace rcontext = trapPdu.getContext();
    int version = rcontext.getVersion();
    String host = rcontext.getHost();

    System.out.println("\ttrap id " + reqId 
        + ", v " + SnmpUtilities.getSnmpVersionString(version)
        + " from host " + host
        + ", sent from port " + port);
    System.out.println("\ttrap " + trapPdu.toString());
}

public void requestPduReceived(RequestPduEvent evt)
{
    System.out.println(getClass().getName() + ".requestPduReceived():");

    int port = evt.getHostPort();
    Pdu pdu = evt.getPdu();
    SnmpContextBasisFace rcontext = pdu.getContext();
    int version = rcontext.getVersion();
    String host = rcontext.getHost();
    int reqId = pdu.getReqId();

    System.out.println("\tpdu id " + reqId 
        + ", v " + SnmpUtilities.getSnmpVersionString(version)
        + " from host " + host
        + ", sent from port " + port);
    System.out.println("\tpdu " + pdu.toString());

    // test sending a response back
    if (pdu.getMsgType() == SnmpConstants.GET_REQ_MSG)
    {
        try
        {
            varbind[] varbinds = pdu.getRequestVarbinds();
            if (varbinds != null && varbinds.length == 1)
            {
                varbind var = varbinds[0];
                AsnObjectId oid = var.getOid();
                if (oid.toString().equals("1.3.6.1.2.1.1.4.0"))
                {
                    SnmpContext rcnt;
                    rcnt = new SnmpContext(context.getHost(), 
                              port, context.getBindAddress(), 
                              context.getTypeSocket());
                    rcnt.setCommunity(context.getCommunity());

                    Pdu respPdu = new ResponsePdu(rcnt, pdu);
                    respPdu.addOid(oid, new AsnOctets("Birgit Arkesteijn, snmp@westhawk.co.uk"));
                    System.out.println("\tsending response: " + respPdu.toString());
                    respPdu.send();
                    try
                    {
                        Thread.currentThread().sleep(1000);
                    }
                    catch (java.lang.InterruptedException exc) {}
                    rcnt.destroy();
                }
            }
        }
        catch(PduException pexc)
        {
            pexc.printStackTrace();
        }
        catch(java.io.IOException iexc)
        {
            iexc.printStackTrace();
        }
    }
}

public void rawPduReceived(RawPduEvent evt)
{
    System.out.println(getClass().getName() + ".rawPduReceived():");

    int port = evt.getHostPort();
    int version = evt.getVersion();
    String host = evt.getHostAddress();

    System.out.println("\traw pdu v " +
        SnmpUtilities.getSnmpVersionString(version)
        + " from host " + host
        + ", sent from port " + port);
}

public static void main (String [] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    ReceiveTrap trap = new ReceiveTrap(propFileName);
    trap.init();
}

}

