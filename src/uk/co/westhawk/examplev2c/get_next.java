// NAME
//      $RCSfile: get_next.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.6 $
// CREATED
//      $Date: 2006/01/30 11:37:15 $
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
 
package uk.co.westhawk.examplev2c;

import java.awt.Graphics;
import java.awt.Event;
import java.util.*;
import java.net.*;
import uk.co.westhawk.snmp.stack.*;    
import uk.co.westhawk.snmp.pdu.*;    

/**
 * <p>
 * The get_next application does a MIB tree walk, using the GetNextPdu. 
 * It will start with the OID as configured in the properties file.
 * This application uses SNMP v2c.
 * </p>
 *
 * <p>
 * It walks the tree by creating a new GetNextPdu out off the
 * previous one. All information will be printed to System.out.
 * </p>
 *
 * <p>
 * The host, port, oid and community name can be configured 
 * in the properties file. 
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>get_next.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @see uk.co.westhawk.snmp.stack.GetNextPdu
 * @see uk.co.westhawk.snmp.stack.SnmpContextv2c
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.6 $ $Date: 2006/01/30 11:37:15 $
 */
public class get_next implements Observer
{
    private static final String     version_id =
        "@(#)$Id: get_next.java,v 1.6 2006/01/30 11:37:15 birgit Exp $ Copyright Westhawk Ltd";

    final static String sysUpTime = "1.3.6.1.2.1.1.3";

    private SnmpContextv2c context;
    private Util        util;


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


public void init () 
{
    String host = util.getHost();
    String bindAddr = util.getBindAddress();
    int port = util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    String socketType = util.getSocketType();
    String oid = util.getOid(sysUpTime);
    String community = util.getCommunity();

    try 
    {
        context = new SnmpContextv2c(host, port, bindAddr, socketType);
        context.setCommunity(community);

        GetNextPdu pdu = new GetNextPdu(context);
        pdu.addObserver(this);
        pdu.addOid(oid);
        pdu.send();
    }
    catch (java.io.IOException exc)
    {
        System.out.println("IOException " + exc.getMessage());
        System.exit(0);
    }
    catch(uk.co.westhawk.snmp.stack.PduException exc)
    {
        System.out.println("PduException " + exc.getMessage());
        System.exit(0);
    }
}

/**
 * Implementing the Observer interface. Receiving the response from 
 * the Pdu.
 * Note, what is different from SNMPv1 is that it tests for a (new) 
 * <code>'end of MIB view'</code> element.
 *
 * @param obs the GetNextPdu variable
 * @param ov the varbind
 *
 * @see uk.co.westhawk.snmp.stack.GetNextPdu
 * @see uk.co.westhawk.snmp.stack.varbind
 * @see SnmpConstants#SNMP_VAR_ENDOFMIBVIEW
 */
public void update(Observable obs, Object ov)
{
    boolean isFinished = false;
    GetNextPdu pdu = (GetNextPdu) obs;
    if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        try
        {
            varbind[] vars = pdu.getResponseVarbinds();
            varbind var = vars[0];
            AsnObject obj = var.getValue();
            if (obj.getRespType() != AsnObject.SNMP_VAR_ENDOFMIBVIEW)
            {
                System.out.println(var.toString());

                pdu = new GetNextPdu(context);
                pdu.addObserver(this);
                pdu.addOid(var.getOid().toString());
                pdu.send();
            }
            else
            {
                isFinished = true;
            }
        }
        catch(java.io.IOException exc)
        {
            System.out.println("update(): IOException " 
                  + exc.getMessage());
            isFinished = true;
        }
        catch(uk.co.westhawk.snmp.stack.PduException exc)
        {
            System.out.println("update(): PduException " 
                  + exc.getMessage());
            isFinished = true;
        }
    }
    else
    {
        isFinished = true;
    }

    if (isFinished == true)
    {
        System.exit(0);
    }
}


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
