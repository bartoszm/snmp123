// NAME
//      $RCSfile: AsnEncoderv1.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.3 $
// CREATED
//      $Date: 2006/02/09 14:16:36 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1995, 1996 by West Consulting BV
 *
 * Permission to use, copy, modify, and distribute this software
 * for any purpose and without fee is hereby granted, provided
 * that the above copyright notices appear in all copies and that
 * both the copyright notice and this permission notice appear in
 * supporting documentation.
 * This software is provided "as is" without express or implied
 * warranty.
 * author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * original version by hargrave@dellgate.us.dell.com (Jordan Hargrave)
 */

/*
 * Copyright (C) 1996 - 2006 by Westhawk Ltd
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

package uk.co.westhawk.snmp.stack;
import uk.co.westhawk.snmp.util.*;
import java.io.*;
import java.util.*;

/**
 * This class contains the v1 specific methods to encode a Pdu into bytes.
 * We split the original class AsnEncoder into four classes.
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 3.3 $ $Date: 2006/02/09 14:16:36 $
 */
class AsnEncoderv1 extends AsnEncoderBase
{
    private static final String     version_id =
        "@(#)$Id: AsnEncoderv1.java,v 3.3 2006/02/09 14:16:36 birgit Exp $ Copyright Westhawk Ltd";


/**
 * Encode SNMPv1 Trap packet into bytes.
 */
ByteArrayOutputStream EncodeSNMP(SnmpContext context, byte msg_type,
    String enterprise, byte[] IpAddress, int generic_trap, int
    specific_trap, long timeTicks, Enumeration ve)
    throws IOException, EncodingException
{
    ByteArrayOutputStream bout;
    AsnSequence asnTopSeq;

    // Create authentication
    asnTopSeq = new AsnSequence();
    asnTopSeq.add(new AsnInteger(SnmpConstants.SNMP_VERSION_1));
    asnTopSeq.add(new AsnOctets(context.getCommunity()));  // community

    // Create PDU sequence.
    AsnObject asnPduObject = EncodeTrap1Pdu(msg_type, enterprise, 
          IpAddress, generic_trap, specific_trap, timeTicks, ve);

    asnTopSeq.add(asnPduObject);

    if (AsnObject.debug > 10)
    {
        System.out.println("\n" + getClass().getName() + ".EncodeSNMP(): ");
    }
    // Write SNMP object
    bout = new ByteArrayOutputStream();
    asnTopSeq.write(bout);
    return bout;
}

/**
 * Encode Trapv1 PDU itself packet into bytes.
 */
private AsnObject EncodeTrap1Pdu(byte msg_type,
    String enterprise, byte[] IpAddress, int generic_trap, int
    specific_trap, long timeTicks, Enumeration ve)
    throws IOException
{
    AsnObject asnPduObject, asnVBObject;

    // kind of request
    asnPduObject = new AsnSequence(msg_type);
    asnPduObject.add(new AsnObjectId(enterprise));    // enterprise

    // agent-addr (thanks Donnie Love (dlove@idsonline.com) for 
    // pointing out that we should have used IPADDRESS type)
    asnPduObject.add(new AsnOctets(IpAddress, AsnObject.IPADDRESS)); 

    asnPduObject.add(new AsnInteger(generic_trap));   // generic-trap
    asnPduObject.add(new AsnInteger(specific_trap));  // specific-trap
    asnPduObject.add(new AsnUnsInteger(timeTicks));   // time-stap

    // Create VarbindList sequence
    AsnObject asnVBLObject = asnPduObject.add(new AsnSequence());

    // Add variable bindings
    while (ve.hasMoreElements())
    {
        asnVBObject = asnVBLObject.add(new AsnSequence());
        varbind vb = (varbind) ve.nextElement();
        asnVBObject.add(vb.getOid());
        asnVBObject.add(vb.getValue());
    }

    return asnPduObject;
}


/**
 * Encode SNMPv1 packet into bytes.
 */
ByteArrayOutputStream EncodeSNMP(SnmpContext context, byte msg_type,
    int pduId, int errstat, int errind, Enumeration ve)
    throws IOException, EncodingException
{
    ByteArrayOutputStream bout;
    AsnSequence asnTopSeq;

    // Create authentication
    asnTopSeq = new AsnSequence();
    asnTopSeq.add(new AsnInteger(AsnObject.SNMP_VERSION_1));
    asnTopSeq.add(new AsnOctets(context.getCommunity()));  // community

    // Create PDU sequence.
    AsnObject asnPduObject = EncodePdu(msg_type, pduId, errstat, errind, ve);
    asnTopSeq.add(asnPduObject);

    if (AsnObject.debug > 10)
    {
        System.out.println("\n" + getClass().getName() + ".EncodeSNMP(): ");
    }
    // Write SNMP object
    bout = new ByteArrayOutputStream();
    asnTopSeq.write(bout);
    return bout;
}



}
