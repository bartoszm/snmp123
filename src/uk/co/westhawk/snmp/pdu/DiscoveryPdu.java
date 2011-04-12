// NAME
//      $RCSfile: DiscoveryPdu.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.15 $
// CREATED
//      $Date: 2006/11/29 16:12:50 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2000 - 2006 by Westhawk Ltd
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

package uk.co.westhawk.snmp.pdu;
import uk.co.westhawk.snmp.stack.*;
import java.util.*;

/**
 * This class is used to perform the SNMPv3 USM discovery.
 * This PDU cannot have any OIDs.
 *
 * <p>
 * See <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 * </p>
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.15 $ $Date: 2006/11/29 16:12:50 $
 */
public class DiscoveryPdu extends GetPdu
{
    private static final String     version_id =
        "@(#)$Id: DiscoveryPdu.java,v 3.15 2006/11/29 16:12:50 birgit Exp $ Copyright Westhawk Ltd";

    private SnmpContextv3Face context;

/**
 * Constructor.
 *
 * @param cntxt The v3 context of the PDU
 */
public DiscoveryPdu(SnmpContextv3Face cntxt)
{
    super(cntxt);
    context = cntxt;
}

/**
 * Cannot add any OID. This method is overwritten to prevent users from
 * adding any OID.
 *
 * @exception IllegalArgumentException A discovery PDU cannot have any
 * OID.
 */
public void addOid(String oid)
throws IllegalArgumentException
{
    throw new IllegalArgumentException("DiscoveryPdu cannot have OID");
}

/** 
 * Cannot add any OID. This method is overwritten to prevent users from
 * adding any OID.
 *
 * @exception IllegalArgumentException A discovery PDU cannot have any
 * OID.
 * @since 4_12
 */
public void addOid(String oid, AsnObject val) 
{
    throw new IllegalArgumentException("DiscoveryPdu cannot have OID");
}

/** 
 * Cannot add any OID. This method is overwritten to prevent users from
 * adding any OID.
 *
 * @exception IllegalArgumentException A discovery PDU cannot have any
 * OID.
 * @since 4_12
 */
public void addOid(AsnObjectId oid, AsnObject val) 
{
    throw new IllegalArgumentException("DiscoveryPdu cannot have OID");
}

/**
 * Cannot add any OID. This method is overwritten to prevent users from
 * adding any OID.
 *
 * @exception IllegalArgumentException A discovery PDU cannot have any
 * OID.
 */
public void addOid(varbind var)
throws IllegalArgumentException
{
    throw new IllegalArgumentException("DiscoveryPdu cannot have OID");
}

/**
 * Cannot add any OID. This method is overwritten to prevent users from
 * adding any OID.
 *
 * @exception IllegalArgumentException A discovery PDU cannot have any
 * OID.
 * @since 4_12
 */
public void addOid(AsnObjectId oid) 
{
    throw new IllegalArgumentException("DiscoveryPdu cannot have OID");
}

/**
 * Sends the PDU.
 * Note that all properties of the context have to be set before this
 * point.
 */
public boolean send() throws java.io.IOException, PduException
{
    if (added == false)
    {
        // Moved this statement from the constructor because it
        // conflicts with the way the SnmpContextXPool works.
        added = context.addDiscoveryPdu(this);
    }
    Enumeration vbs = reqVarbinds.elements();
    encodedPacket = context.encodeDiscoveryPacket(msg_type, getReqId(),
        getErrorStatus(), getErrorIndex(), vbs, snmpv3MsgId);
    addToTrans();
    return added;
}

}
