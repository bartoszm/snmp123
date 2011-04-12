// NAME
//      $RCSfile: get_next.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.6 $
// CREATED
//      $Date: 2008/12/12 14:55:51 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1996 - 1998 by Westhawk Ltd (www.westhawk.nl)
 * Copyright (C) 1998 - 2006 by Westhawk Ltd (www.westhawk.co.uk)
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

import java.awt.Graphics;
import java.awt.Event;
import java.util.*;
import java.net.*;

import uk.co.westhawk.snmp.stack.*;    
import uk.co.westhawk.snmp.pdu.*;    
import uk.co.westhawk.snmp.util.*;    

/**
 * <p>
 * The get_next application does a MIB tree walk, using the BlockPdu. 
 * It will start with the OID as configured in the properties file.
 * </p>
 *
 * <p>
 * It walks the tree by creating a new BlockPdu out off the
 * previous one. All information will be printed to
 * <code>System.out</code>.
 * </p>
 *
 * <p>
 * All parameters can be configured 
 * in the properties file. 
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>get_next.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @see uk.co.westhawk.snmp.pdu.BlockPdu
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.6 $ $Date: 2008/12/12 14:55:51 $
 */
public class get_next 
{
    private static final String     version_id =
        "@(#)$Id: get_next.java,v 1.6 2008/12/12 14:55:51 tpanton Exp $ Copyright Westhawk Ltd";

    final static String sysUpTime = "1.3.6.1.2.1.1.3";

    private SnmpContextv3Pool context;
    private BlockPdu pdu;
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
        context = new SnmpContextv3Pool(host, port, bindAddr, socketType);
        context.setUserName(userName);
        context.setUseAuthentication((auth==1));
        context.setUserAuthenticationPassword(authPassw);
        context.setAuthenticationProtocol(aproto);
        context.setContextEngineId(engineId);
        context.setContextName(contextName);
        context.setUsePrivacy((priv == 1));
        context.setUserPrivacyPassword(privPassw);
        context.setPrivacyProtocol(pproto);

        pdu = new BlockPdu(context);
        pdu.setPduType(BlockPdu.GETNEXT);
        pdu.addOid(oid);
    }
    catch (java.io.IOException exc)
    {
        System.out.println("IOException " + exc.getMessage());
        System.exit(0);
    }
    catch (Exception exc)
    {
        System.out.println("Exception " + exc.getMessage());
        System.exit(0);
    }
}

public void start()
{
    boolean running=true;
    try
    {
        while (running)
        {
            varbind var = pdu.getResponseVariableBinding();
            if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
            {
                AsnObject obj = var.getValue();
                if (obj != null 
                        && 
                    obj.getRespType() != AsnObject.SNMP_VAR_ENDOFMIBVIEW)
                {
                    AsnObjectId oid = var.getOid();
                    System.out.println(var.toString());

                    pdu = new BlockPdu(context);
                    pdu.setPduType(BlockPdu.GETNEXT);
                    pdu.addOid(oid.toString());
                }
                else
                {
                    running = false;
                }
            }
            else
            {
                running = false;
            }
        }
    }
    catch (PduException exc)
    {
        exc.printStackTrace();
        System.out.println("PduException: " + exc.getMessage());
        running = false;
    }
    catch (java.io.IOException exc)
    {
        exc.printStackTrace();
        System.out.println("IOException: " + exc.getMessage());
        running = false;
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
    application.start();
}


}
