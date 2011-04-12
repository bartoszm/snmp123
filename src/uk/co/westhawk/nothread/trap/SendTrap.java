// NAME
//      $RCSfile: SendTrap.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.3 $
// CREATED
//      $Date: 2006/01/17 17:43:54 $
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
package uk.co.westhawk.nothread.trap;

import uk.co.westhawk.snmp.stack.*;    
import uk.co.westhawk.snmp.pdu.*;    

/**
 * <p>
 * The SendTrap application sends a PassiveTrapPduv2 in an Oracle JServ
 * environment. 
 * </p>
 *
 * <p>
 * The host, port, oid, community name, etc, are passed by the Oracle 
 * trigger to the method.
 * </p>
 *
 * <p>
 * See 
 * <a href="./package-summary.html">notes</a>
 * on how to send traps in an Oracle JServer environment.
 * </p>
 *
 * @see uk.co.westhawk.snmp.pdu.PassiveTrapPduv2 
 * @see uk.co.westhawk.snmp.stack.PassiveSnmpContextv2c 
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.3 $ $Date: 2006/01/17 17:43:54 $
 */
public class SendTrap 
{
    private static final String     version_id =
        "@(#)$Id: SendTrap.java,v 1.3 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";

    public final static String sysUpTime   = "1.3.6.1.2.1.1.3.0";
    public final static String snmpTrapOID = "1.3.6.1.6.3.1.1.4.1.0";

    public final static String coldStart              = "1.3.6.1.6.3.1.1.5.1";
    public final static String warmStart              = "1.3.6.1.6.3.1.1.5.2";
    public final static String linkDown               = "1.3.6.1.6.3.1.1.5.3";
    public final static String linkUp                 = "1.3.6.1.6.3.1.1.5.4";
    public final static String authenticationFailure  = "1.3.6.1.6.3.1.1.5.5";
    public final static String egpNeighborLoss        = "1.3.6.1.6.3.1.1.5.6";


/**
 * Sends one v2c trap. 
 * One can only call static methods from within Oracle. Sad, but true.
 *
 * <p>
 * The parameters are passed via the Oracle trigger;
 * they are the insert values of the scott.trap table.
 * </p>
 *
 * @param host The hostname
 * @param port The port number, usually 162
 * @param comm The community name
 * @param upTime The value for sysUpTime.0
 * @param trapOid The value for snmpTrapOID.0
 *
 * @return The result of the send. <code>OK</code>, if all went well, 
 *      an error message if not. This value will be stored in the
 *      scott.trap.result field.
 */
public static String doSendTrap(String host, int port, String comm, 
    long upTime, String trapOid)
{
    PassiveSnmpContextv2c context;
    PassiveTrapPduv2 pdu;
    StringBuffer result = new StringBuffer("");

    try 
    {
        context = new PassiveSnmpContextv2c(host, port);
        context.setCommunity(comm);

        pdu = new PassiveTrapPduv2(context);
        pdu.addOid(sysUpTime, new AsnUnsInteger(upTime));
        pdu.addOid(snmpTrapOID, new AsnObjectId(trapOid));
        pdu.send();
        context.destroy();
        result.append("OK");
    }
    catch (java.io.IOException exc)
    {
        result.append("IOException ").append(exc.getMessage());
    }
    catch(uk.co.westhawk.snmp.stack.PduException exc)
    {
        result.append("PduException ").append(exc.getMessage());
    }
    catch (Exception exc)
    {
        result.append("Exception ").append(exc.getMessage());
    }

    int len = result.length();
    if (len > 500)
    {
        result.setLength(500);
    }

    return result.toString();
}

}
