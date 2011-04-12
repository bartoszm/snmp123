// NAME
//      $RCSfile: ReceiveTrap.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.9 $
// CREATED
//      $Date: 2008/12/12 14:55:51 $
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
 
package uk.co.westhawk.examplev3;

import java.awt.*; 
import java.util.*;
import java.net.*;
import uk.co.westhawk.snmp.stack.*;    
import uk.co.westhawk.snmp.pdu.*;    
import uk.co.westhawk.snmp.event.*;    
import uk.co.westhawk.snmp.util.*;    

/**
 * <p>
 * The ReceiveTrap receives traps. 
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
 * On UNIX and Linux you will have to run this application as root.
 * </p>
 *
 * @see uk.co.westhawk.snmp.stack.SnmpContextv3
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.9 $ $Date: 2008/12/12 14:55:51 $
 */
public class ReceiveTrap 
implements RequestPduListener, RawPduListener, TrapListener, Observer
{
    private static final String     version_id =
        "@(#)$Id: ReceiveTrap.java,v 1.9 2008/12/12 14:55:51 tpanton Exp $ Copyright Westhawk Ltd";

    private SnmpContextv3 context, context2;
    private Util        util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public ReceiveTrap(String propertiesFilename)
{
    //AsnObject.setDebug(7);
    util = new Util(propertiesFilename, this.getClass().getName());
}

public void init () 
{
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

    try 
    {
        ListeningContextPool defTrap;

        context = new SnmpContextv3(host, port, bindAddr, socketType);
        context.setUserName(userName);
        context.setUseAuthentication((auth==1));
        context.setUserAuthenticationPassword(authPassw);
        context.setAuthenticationProtocol(aproto);
        context.setContextEngineId(engineId);
        context.setContextName(contextName);
        context.setUsePrivacy((priv == 1));
        context.setUserPrivacyPassword(privPassw);
        context.setPrivacyProtocol(pproto);

        // only needed when sending traps and receiving request pdus.
        UsmAgent usmAgent = new SimpleUsmAgent();
        context.setUsmAgent(usmAgent);

        // listen on port 162
        defTrap = new ListeningContextPool(ListeningContextFace.DEFAULT_TRAP_PORT, bindAddr, socketType);
        defTrap.addUnhandledRawPduListener(this);
        context.addTrapListener(this);
        context.addRequestPduListener(this, ListeningContextFace.DEFAULT_TRAP_PORT);

        // listen on port 161
        defTrap = new ListeningContextPool(SnmpContextBasisFace.DEFAULT_PORT, bindAddr, socketType);
        defTrap.addUnhandledRawPduListener(this);
        context.addTrapListener(this, SnmpContextBasisFace.DEFAULT_PORT);
        context.addRequestPduListener(this, SnmpContextBasisFace.DEFAULT_PORT);

        System.out.println("ReceiveTrap.init(): " 
            + context.toString());

        // send a wrong community name, this hopefully causes a
        // authenticationFailure trap. 

        /*
        context2 = (SnmpContextv3)context.clone();
        String name = context2.getContextName();
        name += "_bla"; 
        context2.setContextName(name);

        GetNextPdu pdu = new GetNextPdu(context2, 
            "1.3.6.1.2.1.1.3", this);
        */
    }
    catch (java.io.IOException exc)
    {
        System.out.println("ReceiveTrap.init(): IOException " 
            + exc.getMessage());
        exc.printStackTrace();
        System.exit(0);
    }
    //catch(CloneNotSupportedException exc) {}
    //catch(PduException exc) {}
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
    if (context2 != null)
    {
        context2.destroy();
    }
}

public void trapReceived(TrapEvent evt)
{
    System.out.println();
    System.out.println(getClass().getName() + ".trapReceived():");

    int port = evt.getHostPort();
    Pdu trapPdu = evt.getPdu();
    int reqId = trapPdu.getReqId();
    SnmpContextBasisFace context = trapPdu.getContext();
    int version = context.getVersion();
    String host = context.getHost();

    System.out.println("\ttrap id " + reqId 
        + ", v " + SnmpUtilities.getSnmpVersionString(version)
        + " from host " + host
        + ", sent from port " + port);
    System.out.println("\ttrap " + trapPdu.toString());
}

/**
 * Received a pdu request. 
 * If it is Get(sysContact.0), send an answer back.
 */
public void requestPduReceived(RequestPduEvent evt)
{
    System.out.println();
    System.out.println(getClass().getName() + ".requestPduReceived():");

    int port = evt.getHostPort();
    Pdu pdu = evt.getPdu();
    SnmpContextv3 context = (SnmpContextv3) pdu.getContext();
    int version = context.getVersion();
    String host = context.getHost();
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
                String sysContact0 = "1.3.6.1.2.1.1.4.0";
                if (oid.toString().equals(sysContact0))
                {
                    SnmpContextv3 rcnt;
                    rcnt = new SnmpContextv3(context.getHost(), 
                              port, context.getBindAddress(), context.getTypeSocket());
                    rcnt = (SnmpContextv3) context.cloneParameters(rcnt);

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
    System.out.println();
    System.out.println(getClass().getName() + ".rawPduReceived():");

    int port = evt.getHostPort();
    int version = evt.getVersion();
    String host = evt.getHostAddress();

    System.out.println("\traw pdu v " +
        SnmpUtilities.getSnmpVersionString(version)
        + " from host " + host
        + ", sent from port " + port);
}


public static void main(String[] args)
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
