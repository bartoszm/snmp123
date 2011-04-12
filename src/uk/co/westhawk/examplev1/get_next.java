// NAME
//      $RCSfile: get_next.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.5 $
// CREATED
//      $Date: 2006/01/26 16:11:45 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1996 - 2006 by Westhawk Ltd
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

import java.util.*;

import uk.co.westhawk.snmp.stack.*;    
import uk.co.westhawk.snmp.pdu.*;    

/**
 * <p>
 * The get_next application does a MIB tree walk, using the GetNextPdu. 
 * It will start with the OID as configured in the properties file.
 * </p>
 *
 * <p>
 * It walks the tree by creating a new GetNextPdu out off the
 * previous one. All information will be printed to System.out.
 * </p>
 *
 * <p>
 * The host, port, oid and community name can be configured in the 
 * properties file. 
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>get_next.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @see uk.co.westhawk.snmp.stack.GetNextPdu
 * @see Util
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.5 $ $Date: 2006/01/26 16:11:45 $
 */
public class get_next implements Observer
{
    private static final String     version_id =
        "@(#)$Id: get_next.java,v 1.5 2006/01/26 16:11:45 birgit Exp $ Copyright Westhawk Ltd";

    final static String sysUpTime = "1.3.6.1.2.1.1.3";

    private SnmpContext context;
    private Util util;

/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public get_next(String propertiesFilename)
{
    util = new Util(propertiesFilename, this.getClass().getName());
}

public void init() 
{
    //AsnObject.setDebug(15);
    String host = util.getHost();
    String bindAddr = util.getBindAddress();
    int port = util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    String community = util.getCommunity();
    String socketType = util.getSocketType();
    String oid = util.getOid(sysUpTime);

    try 
    {
        context = createContext(host, port, socketType, bindAddr, community);
        System.out.println("init(): " + context.toString());

        GetNextPdu pdu = createPdu(context, new varbind(oid), this);
        pdu.send();
    }
    catch (java.io.IOException exc)
    {
        System.out.println("init(): IOException " + exc.getMessage());
        exc.printStackTrace();
        if (context != null)
        {
            context.destroy();
        }
        System.exit(1);
    }
    catch (uk.co.westhawk.snmp.stack.PduException exc)
    {
        System.out.println("init(): PduException " + exc.getMessage());
        exc.printStackTrace();
        if (context != null)
        {
            context.destroy();
        }
        System.exit(1);
    }
}


protected SnmpContext createContext(String host, int port, String socketType, 
String bindAddr, String community)
throws java.io.IOException
{
    SnmpContext con = new SnmpContext(host, port, bindAddr, socketType);
    con.setCommunity(community);
    return con;
}


protected GetNextPdu createPdu(SnmpContext context, varbind var, Observer obs)
{
    GetNextPdu newPdu = new GetNextPdu(context);
    newPdu.addOid(var);
    newPdu.addObserver(obs);
    return newPdu;
}


/**
 * Implementing the Observer interface. Receiving the response from 
 * the Pdu.
 *
 * @param obs the GetNextPdu variable
 * @param ov the varbind
 *
 * @see uk.co.westhawk.snmp.stack.GetNextPdu
 * @see uk.co.westhawk.snmp.stack.varbind
 */
public void update(Observable obs, Object ov)
{
    //System.out.println(context.getDebugString());
    GetNextPdu pdu = (GetNextPdu) obs;
    if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        try
        {
            varbind[] vars = pdu.getResponseVarbinds();
            varbind var = vars[0];
            System.out.println(var.toString());

            pdu = createPdu(context, new varbind(var.getOid()), this);
            pdu.send();
        }
        catch(java.io.IOException exc)
        {
            System.out.println("update(): IOException " 
                  + exc.getMessage());
        }
        catch(uk.co.westhawk.snmp.stack.PduException exc)
        {
            System.out.println("update(): PduException " 
                  + exc.getMessage());
        }
    }
    else
    {
        System.out.println("update(): " + pdu.getErrorStatusString());
        context.destroy();
        System.exit(0);
    }
}


/**
 * Main. To use a properties file different from 
 * <code>get_next.properties</code>, pass the name as first argument.
 */
public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    get_next application = new get_next(propFileName);
    application.init();
}

}
