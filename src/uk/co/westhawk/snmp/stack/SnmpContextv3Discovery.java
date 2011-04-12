// NAME
//      $RCSfile: SnmpContextv3Discovery.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.11 $
// CREATED
//      $Date: 2009/03/05 13:12:50 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2005 - 2006 by Westhawk Ltd
 * <a href="www.westhawk.co.uk">www.westhawk.co.uk</a>
 *
 * Permission to use, copy, modify, and distribute this software
 * for any purpose and without fee is hereby granted, provided
 * that the above copyright notices appear in all copies and that
 * both the copyright notice and this permission notice appear in
 * supporting documentation.
 * This software is provided "as is" without express or implied
 * warranty.
 */

package uk.co.westhawk.snmp.stack;

import java.net.*;
import java.io.*;
import java.util.*;

import uk.co.westhawk.snmp.pdu.*;
import uk.co.westhawk.snmp.util.*;
import uk.co.westhawk.snmp.event.*;
import uk.co.westhawk.snmp.beans.*;

/**
 * This class contains the SNMP v3 discovery context that is used by
 * UsmBeingDiscoveredBean, when this stack is being discovered.
 * Most of the work is done by SnmpContextv3Basis.
 *
 * <p>
 * Now that the stack can send traps and receive requests, 
 * it needs to be able to act as an
 * authoritative SNMP engine. This is done via the interface UsmAgent.
 * The DefaultUsmAgent is not guaranteed to work; agents (or rather 
 * authoritative engines) <em>should</em> provide a better implementation.
 * </p>
 *
 * @see DefaultUsmAgent
 * @see uk.co.westhawk.snmp.beans.UsmBeingDiscoveredBean
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.11 $ $Date: 2009/03/05 13:12:50 $
 */
public class SnmpContextv3Discovery extends SnmpContextv3Basis
{
    private static final String     version_id =
        "@(#)$Id: SnmpContextv3Discovery.java,v 3.11 2009/03/05 13:12:50 birgita Exp $ Copyright Westhawk Ltd";


/**
 * Constructor.
 *
 * @param host The host to which the PDU will be sent
 * @param port The port where the SNMP server will be
 * @see AbstractSnmpContext#AbstractSnmpContext(String, int)
 */
public SnmpContextv3Discovery(String host, int port) throws java.io.IOException
{
    super(host, port);
}

/**
 * Constructor.
 * Parameter typeSocketA should be either STANDARD_SOCKET, TCP_SOCKET or a
 * fully qualified classname.
 *
 * @param host The host to which the Pdu will be sent
 * @param port The port where the SNMP server will be
 * @param typeSocketA The local address the server will bind to
 *
 * @see AbstractSnmpContext#AbstractSnmpContext(String, int, String)
 */
public SnmpContextv3Discovery(String host, int port, String typeSocketA) 
throws java.io.IOException
{
    super(host, port, typeSocketA);
}

/**
 * Constructor.
 * Parameter typeSocketA should be either STANDARD_SOCKET, TCP_SOCKET or a
 * fully qualified classname.
 *
 * @param host The host to which the PDU will be sent
 * @param port The port where the SNMP server will be
 * @param bindAddress The local address the server will bind to
 * @param typeSocketA The type of socket to use. 
 *
 * @see AbstractSnmpContext#AbstractSnmpContext(String, int, String, String)
 * @see SnmpContextBasisFace#STANDARD_SOCKET
 * @see SnmpContextBasisFace#TCP_SOCKET
 * @since 4_14
 */
public SnmpContextv3Discovery(String host, int port, String bindAddress, String typeSocketA) 
throws java.io.IOException
{
    super(host, port, bindAddress, typeSocketA);
}


/**
 * Processes an incoming Discovery (and only Discovery) PDU. 
 * <p>
 * See <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 * </p>
 *
 * @see #rawPduReceived
 */
public Pdu processIncomingPdu(byte [] message) 
throws DecodingException, IOException
{
    String msg = checkContextSanity();
    if (msg != null)
    {
        throw new DecodingException(msg);
    }
    int l = message.length;
    byte [] copyOfMessage = new byte[l];
    System.arraycopy(message, 0, copyOfMessage, 0, l);

    AsnDecoderv3 rpdu = new AsnDecoderv3();
    ByteArrayInputStream in = new ByteArrayInputStream(message);
    AsnSequence asnTopSeq = rpdu.DecodeSNMPv3(in);
    int msgId = rpdu.getMsgId(asnTopSeq);
    AsnPduSequence pduSeq = rpdu.processSNMPv3(this, asnTopSeq, copyOfMessage, true);

    Pdu pdu = null;
    if (pduSeq != null)
    {
        byte type = pduSeq.getRespType();
        if (type == SnmpConstants.GET_REQ_MSG && pduSeq.isSnmpv3Discovery() == true)
        {
            pdu = new uk.co.westhawk.snmp.stack.GetPdu(this);
        }
        else
        {
            /*
            These cannot be sent as discovery pdu;
            SnmpConstants.GETNEXT_REQ_MSG
            SnmpConstants.SET_REQ_MSG
            SnmpConstants.GETBULK_REQ_MSG
            SnmpConstants.INFORM_REQ_MSG
            SnmpConstants.GET_RSP_MSG
            SnmpConstants.GET_RPRT_MSG
            SnmpConstants.TRPV2_REQ_MSG
            */

            if (AsnObject.debug > 3)
            {
                System.out.println(getClass().getName() 
                    + ".ProcessIncomingPdu(): PDU received with type " 
                    + pduSeq.getRespTypeString()
                    + ". Ignoring it.");
            }
        }

        if (pdu != null)
        {
            pdu.fillin(pduSeq);
            pdu.snmpv3MsgId = new Integer(msgId);
        }
    }
    return pdu;
}

/**
 * Returns a clone of this SnmpContextv3.
 *
 * @exception CloneNotSupportedException Thrown when the constructor
 * generates an IOException
 */
public Object clone() throws CloneNotSupportedException
{
    SnmpContextv3Discovery clContext = null;
    try
    {
        clContext = new SnmpContextv3Discovery(hostname, hostPort, bindAddr, typeSocket);
        clContext = (SnmpContextv3Discovery) cloneParameters(clContext);
    }
    catch (java.io.IOException exc)
    {
        throw new CloneNotSupportedException("IOException " 
            + exc.getMessage());
    }
    return clContext;
}

}
