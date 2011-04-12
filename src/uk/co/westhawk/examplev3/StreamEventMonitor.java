// NAME
//      $RCSfile: StreamEventMonitor.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.4 $
// CREATED
//      $Date: 2006/11/30 13:55:19 $
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

import java.util.*;
import uk.co.westhawk.snmp.stack.*;

/**
 * The StreamEventMonitor class performs a system subtree walk with
 * either SNMPv1 or SNMPv3. This class is used by WestSpeedTests.
 *
 * @see SnmpTarget
 * @see WestSpeedTests
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 1.4 $ $Date: 2006/11/30 13:55:19 $
 */
public class StreamEventMonitor implements Runnable
{
    private static final String     version_id =
        "@(#)$Id: StreamEventMonitor.java,v 1.4 2006/11/30 13:55:19 birgit Exp $ Copyright Westhawk Ltd";

    /** 
     * The system OID.
     */
    public final static String OID = "1.3.6.1.2.1.1";
    public final static boolean doTCP = false;
    private Thread thread;
    private String targetHost;
    private int count;
    private boolean doV3;
    private String userName = SnmpContextv3Face.Default_UserName;
    private String password = "";
    private String contextName = SnmpContextv3Face.Default_ContextName;
    private byte[] contextEngId = new byte[0];
    private int protocol = SnmpContextv3Face.MD5_PROTOCOL;

/**
 * The constructor.
 *
 * If SNMPv3 should be used, setUCM() should be called as well.
 *
 * @param h The host
 * @param loops The number of time the subtree walk should be performed
 * @param v3 The boolean indicating if SNMPv3 (true) should be used or not.
 *
 * @see #setUCM
 */
public StreamEventMonitor(String h, int loops, boolean v3)
{
    targetHost = h;
    doV3 = v3;
    count = loops;
}

/**
 * Set the UCM parameters for SNMPv3.
 */
public void setUCM(String uname, String passw, String contextN, 
    byte[] contextI, int proto)
{
    userName = uname;
    password = passw;
    contextName = contextN;
    contextEngId = contextI;
    protocol = proto;
}

/**
 * Runs the configured number of subtree walks. It prints the result and
 * then sleeps one second.
 */
public void run()
{
    SnmpTarget target = new SnmpTarget();
    if (doV3)
    {
        target.setParameters(targetHost,
        SnmpContextBasisFace.DEFAULT_PORT, userName, password,
            contextName, contextEngId, protocol, doTCP);
    }
    else
    {
        target.setParameters(targetHost,
        SnmpContextBasisFace.DEFAULT_PORT, "public", doTCP);
    }
    while ((count --) > 0)
    {
        Date then = new Date();
        Vector results = walkSubTree(target, OID);
        Date now = new Date();
        System.out.println("---------------------- "
            + targetHost + " " + OID 
            + " SNMPv" + (doV3 ? "3 " + userName  : "1")
            + " " + count
            + " -----------------");
        int no = results.size();
        double delay = (double) (now.getTime() - then.getTime())/1000.0;
        double rate = ((double)no)/delay;
        System.out.println("pdus = "+ no + ", delay = "+ delay
            +" (sec), rate = " +rate + " (pdu/sec)");

        for (int i=0; i<no; i++)
        {
            System.out.println(results.elementAt(i).toString());
        }
        try
        {
            Thread.sleep(1000);   
        }
        catch (InterruptedException e)
        {
        }
    }
}

/**
 * Performs the actual subtree walk. 
 *
 * @param target The configured snmp target
 * @param startOID The oid where the walks start
 */
public static Vector walkSubTree(SnmpTarget target, String startOID)
{
    Vector results;
    varbind element;
    
    varbind startVarBind = new varbind(startOID);
    AsnObjectId asnStartOID = startVarBind.getOid();
    results = new Vector();
    boolean endOfSubTree = false;
    target.setObjectID(startOID);

    while(!endOfSubTree)
    {
        // Get next element in MIB
        element = target.snmpGetNextVariableBinding();
        if (element == null) 
        {
            endOfSubTree = true;
        }
        else
        {
            AsnObjectId asnCurrentOID = element.getOid();
            
            // Check whether oid is in the subtree
            if (asnCurrentOID.startsWith(asnStartOID))
            {
                results.addElement(element);
                //System.out.println(element.toString());    
            }    
            else
            {
                endOfSubTree = true;    
            }
        }
    }
    return results;
}

}
