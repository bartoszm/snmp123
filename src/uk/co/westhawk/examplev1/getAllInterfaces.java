// NAME
//      $RCSfile: getAllInterfaces.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.7 $
// CREATED
//      $Date: 2006/02/02 15:49:36 $
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
import java.util.*;
import java.text.*;
import java.io.*;
import java.net.*;

/**
 * <p>
 * The getAllInterfaces application requests the interface information 
 * of all the current interfaces of a host, using the InterfaceGetNextPdu.
 * </p>
 *
 * <p>
 * It walks the tree by creating a new InterfaceGetNextPdu out off the
 * previous one, and it collects the values of all the interfaces. Since
 * the application is Runnable, it will send requests continuously.
 * </p>
 *
 * <p>
 * The information will be printed to System.out .
 * </p>
 *
 * <p>
 * The host, port, oid and community name can be configured in the 
 * properties file. 
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>getAllInterfaces.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 *
 * @see uk.co.westhawk.snmp.pdu.InterfaceGetNextPdu
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.7 $ $Date: 2006/02/02 15:49:36 $
 */
public class getAllInterfaces implements Observer, Runnable
{
    private static final String     version_id =
        "@(#)$Id: getAllInterfaces.java,v 1.7 2006/02/02 15:49:36 birgit Exp $ Copyright Westhawk Ltd";

    /**
     * Use 10 (sec) as interval 
     */
    public final static long sleepTime = 10000;

    private InterfaceGetNextPdu pdu;
    private Hashtable allInt;

    private String host;
    private int port;
    private SnmpContext context;

    private String  hashKeyPart;
    private boolean mayLoopStart;
    private SimpleDateFormat dateFormat;
    private Date lastUpdateDate;
    private Util util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public getAllInterfaces(String propertiesFilename)
{
    util = new Util(propertiesFilename, this.getClass().getName());
}

public void init ()
{
    //AsnObject.setDebug(15);
    host = util.getHost();
    String bindAddr = util.getBindAddress();
    port = util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    String socketType = util.getSocketType();
    String community = util.getCommunity();

    hashKeyPart = new String(host+port);

    try
    {
        context = new SnmpContext(host, port, bindAddr, socketType);
        context.setCommunity(community);

        allInt = new Hashtable();
        mayLoopStart = true;

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        lastUpdateDate = new Date();
    }
    catch (IOException exc)
    {
        System.out.println("IOException: " + exc.getMessage());
        System.exit(0);
    }
}

public void start()
{
    Thread me = new Thread(this);
    me.setPriority(Thread.MIN_PRIORITY);
    me.start();
}


public void run()
{
    while (context != null)
    {
        if (mayLoopStart == true)
        {
            mayLoopStart = false;
            pdu = new InterfaceGetNextPdu(context);
            pdu.addObserver(this);
            pdu.addOids(null);

            try
            {
                pdu.send();
            }
            catch(java.io.IOException exc)
            {
                System.out.println("run(): IOException " + exc.getMessage());
            }
            catch(uk.co.westhawk.snmp.stack.PduException exc)
            {
                System.out.println("run(): PduException " + exc.getMessage());
            }
        }

        try
        {
            Thread.sleep(sleepTime);
        } 
        catch (InterruptedException ix)
        {
            ;
        }
    }
}

/**
 * Implementing the Observer interface. Receiving the response from 
 * the Pdu.
 *
 * @param obs the InterfaceGetNextPdu variable
 * @param ov the array of varbind (not used)
 *
 * @see uk.co.westhawk.snmp.pdu.InterfaceGetNextPdu
 * @see uk.co.westhawk.snmp.stack.varbind
 */
public void update(Observable obs, Object ov)
{
    InterfaceGetNextPdu prev;
    String hashKey;

    if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        lastUpdateDate = new Date();
        hashKey = hashKeyPart+pdu.getIfIndex();

        if ((prev = (InterfaceGetNextPdu)allInt.get(hashKey)) != null)
        {
           pdu.getSpeed(prev);
        }
        allInt.put(hashKey, pdu);

        prev = pdu;
        pdu = new InterfaceGetNextPdu(context);
        pdu.addObserver(this);
        pdu.addOids(prev);

        try
        {
            pdu.send();
        }
        catch(java.io.IOException exc)
        {
            System.out.println("update(): IOException " + exc.getMessage());
        }
        catch(uk.co.westhawk.snmp.stack.PduException exc)
        {
            System.out.println("update(): PduException " + exc.getMessage());
        }
    }
    else
    {
        mayLoopStart = true;
        showAllInterface();
    }
}
    
void showAllInterface()
{
    InterfaceGetNextPdu tpdu;
    Enumeration e = allInt.elements();

    System.out.println("Host " + host + " port " + port);
    System.out.println(allInt.size());
    while (e.hasMoreElements()) 
    {
        tpdu = (InterfaceGetNextPdu) e.nextElement();
        int index    = tpdu.getIfIndex();
        long speed    = tpdu.getSpeed();
        String descr = tpdu.getIfDescr();
        String operSt = tpdu.getIfOperStatusStr();

        System.out.println(index + ", " + speed + ", " 
              + descr + ", " + operSt);
    }
    System.out.println(dateFormat.format(lastUpdateDate));
    System.out.println();
}

void showAllInterface(int index)
{
    InterfaceGetNextPdu tpdu;
    String hashKey;

    hashKey = hashKeyPart+index;

    System.out.println("Host " + host + " port " + port);
    if ((tpdu = (InterfaceGetNextPdu)allInt.get(hashKey)) != null)
    {
        long speed = tpdu.getSpeed();
        String descr = tpdu.getIfDescr();
        String operSt = tpdu.getIfOperStatusStr();
        System.out.println(index + ", " + speed + ", " + descr + ", " + operSt);
    }
    else
    {
        System.out.println(index);
    }
}

/**
 * Main. To use a properties file different from 
 * <code>getAllInterfaces.properties</code>, pass the name as first argument.
 */
public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    getAllInterfaces application = new getAllInterfaces(propFileName);
    application.init();
    application.start();
}

}
