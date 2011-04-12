// NAME
//      $RCSfile: SendInform.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.8 $
// CREATED
//      $Date: 2008/12/12 14:55:51 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2002 - 2006 by Westhawk Ltd
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
import uk.co.westhawk.snmp.util.*;

/**
 * <p>
 * The SendInform application sends an inform using the InformPdu. 
 * </p>
 *
 * <p>
 * All parameters can be configured 
 * in the properties file. 
 * The value for sysUpTime and snmpTrapOID are hard coded.
 * </p>
 * <p>
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>SendInform.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * <p><em>
 * <font color="red">
 * Note:
 * The stack so far only supports <u>sending</u> an Inform. Receiving an Inform
 * and replying with a Response is NOT yet supported!
 * </font>
 * </em></p>
 *
 * @see uk.co.westhawk.snmp.stack.InformPdu
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.8 $ $Date: 2008/12/12 14:55:51 $
 */
public class SendInform implements Observer
{
    private static final String     version_id =
        "@(#)$Id: SendInform.java,v 1.8 2008/12/12 14:55:51 tpanton Exp $ Copyright Westhawk Ltd";

    public final static String sysUpTime   = "1.3.6.1.2.1.1.3.0";
    public final static String sysContact  = "1.3.6.1.2.1.1.4.0";

    /**
     * The authoritative identification of the notification currently 
     * being sent. This variable occurs as the second varbind in every 
     * SNMPv2-Trap-PDU and InformRequest-PDU.
     */
    public final static String snmpTrapOID = "1.3.6.1.6.3.1.1.4.1.0";

    public final static String coldStart              = "1.3.6.1.6.3.1.1.5.1";
    public final static String warmStart              = "1.3.6.1.6.3.1.1.5.2";
    public final static String linkDown               = "1.3.6.1.6.3.1.1.5.3";
    public final static String linkUp                 = "1.3.6.1.6.3.1.1.5.4";
    public final static String authenticationFailure  = "1.3.6.1.6.3.1.1.5.5";
    public final static String egpNeighborLoss        = "1.3.6.1.6.3.1.1.5.6";


    private SnmpContextv3 context;
    private InformPdu pdu;
    private Util        util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public SendInform(String propertiesFilename)
{
    AsnObject.setDebug(6);
    util = new Util(propertiesFilename, this.getClass().getName());
}

public void init () 
{
    String host = util.getHost();
    String bindAddr = util.getBindAddress();
    int port = util.getPort(ListeningContextFace.DEFAULT_TRAP_PORT);
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

        pdu = new InformPdu(context);
        pdu.addOid(sysUpTime, new AsnUnsInteger(5));
        pdu.addOid(snmpTrapOID, new AsnObjectId(warmStart));

        System.out.println(pdu.toString());
        pdu.addObserver(this);
        pdu.send();
    }
    catch (java.io.IOException exc)
    {
        System.out.println("IOException " + exc.getMessage());
    }
    catch(uk.co.westhawk.snmp.stack.PduException exc)
    {
        System.out.println("PduException " + exc.getMessage());
    }
    //System.exit(0);
}





/**
 * Implementing the Observer interface. Receiving the response from 
 * the Inform Pdu.
 *
 * @param obs the InformPdu variable
 * @param ov the varbind
 *
 * @see uk.co.westhawk.snmp.stack.InformPdu
 * @see uk.co.westhawk.snmp.stack.varbind
 * @see SnmpConstants#SNMP_VAR_ENDOFMIBVIEW
 */
public void update(Observable obs, Object ov)
{
    if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        try
        {
            varbind[] vars = pdu.getResponseVarbinds();
            int sz = vars.length;
            System.out.println("update(): " + sz + " varbinds");
            for (int i=0; i<sz; i++)
            {
                varbind var = (varbind) vars[i];
                System.out.println(i + " " + Util.printOid(var.getOid()) 
                    + ": " + var.getValue());
            }
        }
        catch(uk.co.westhawk.snmp.stack.PduException exc)
        {
            System.out.println("update(): PduException " + exc.getMessage());
        }
    }
    else
    {
        System.out.println("update(): " + pdu.getErrorStatusString());
    }
    System.exit(0);
}



public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    SendInform application = new SendInform(propFileName);
    application.init();
}

}
