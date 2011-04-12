// NAME
//      $RCSfile: PassiveTrapPduv2.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.8 $
// CREATED
//      $Date: 2006/03/23 14:54:09 $
// COPYRIGHT
//      ERG Group Ltd
// TO DO
//

/*
 * Copyright (C) 2001 by ERG Group Ltd
 * <a href="www.erggroup.com">www.erggroup.com</a>
 *
 * Permission to use, copy, modify, and distribute this software
 * for any purpose and without fee is hereby granted, provided
 * that the above copyright notices appear in all copies and that
 * both the copyright notice and this permission notice appear in
 * supporting documentation.
 * This software is provided "as is" without express or implied
 * warranty.
 * author <a href="mailto:mwaters@erggroup.com">Mike Waters</a>
 */

package uk.co.westhawk.snmp.pdu;
import uk.co.westhawk.snmp.stack.*;

/**
 * This class represents the ASN SNMP v2c (and higher) Trap PDU object
 * that does not create a thread to send itself. It must be used with the
 * context class PassiveSnmpContextv2c. The original purpose of the
 * Passive classes is to allow the stack to be used in environments where
 * thread creation is unwanted, eg database JVMs such as Oracle JServer.
 * See <a href="http://www.ietf.org/rfc/rfc3416.txt">SNMPv2-PDU</a>.
 *
 * <p>
 * See 
 * <a
 * href="../../../../../uk/co/westhawk/nothread/trap/package-summary.html">notes</a>
 * on how to send traps in an Oracle JServer environment.
 * </p>
 *
 * @see PassiveTrapPduv1
 * @since 4_12
 *
 * @author Mike Waters, <a href="www.erggroup.com">ERG Group</a>
 * @version $Revision: 3.8 $ $Date: 2006/03/23 14:54:09 $
 */
public class PassiveTrapPduv2 extends TrapPduv2
{
    private static final String     version_id =
        "@(#)$Id: PassiveTrapPduv2.java,v 3.8 2006/03/23 14:54:09 birgit Exp $ Copyright ERG Group Ltd";

/**
 * Constructor.
 *
 * @param con The context (v2c) of the PDU.
 * This is of type PassiveSnmpContextv2c to ensure that the correct threading
 * behaviour occurs.
 */
public PassiveTrapPduv2(PassiveSnmpContextv2c con)
{
    super(con);

    // this makes the base class PDU believe that the trap is already 
    // awaiting transmission therefore it does not create a transmitter 
    // for this pdu
    added = true;
}

/**
 * Override of the operation in PDU. Send the trap in the
 * callers thread. That is, don't create a sending thread
 * or add it to a queue or anything, just go straight to the socket.
 */
public void addToTrans()
{
    sendme();
}


}
