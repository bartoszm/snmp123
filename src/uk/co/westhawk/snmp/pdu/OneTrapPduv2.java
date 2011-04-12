// NAME
//      $RCSfile: OneTrapPduv2.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.9 $
// CREATED
//      $Date: 2006/03/23 14:54:09 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2001 - 2006 by Westhawk Ltd
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
import java.util.*;
import java.io.*;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.util.*;

/**
 * This class represents the ASN SNMPv2c (and higher) Trap PDU object. 
 * See <a href="http://www.ietf.org/rfc/rfc1157.txt">RFC1157-SNMP</a>.
 *
 * <p>
 * For SNMPv3: The sender of a trap PDU acts as the authoritative engine.
 * </p>
 *
 * @deprecated  As of 4_14, just use {@link TrapPduv2} 
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.9 $ $Date: 2006/03/23 14:54:09 $
 */
public class OneTrapPduv2 extends TrapPduv2 
{
    private static final String     version_id =
        "@(#)$Id: OneTrapPduv2.java,v 3.9 2006/03/23 14:54:09 birgit Exp $ Copyright Westhawk Ltd";


/** 
 * Constructor.
 *
 * @param con The context (v2c, v3) of the OneTrapPduv2
 * @see SnmpContext
 */
public OneTrapPduv2(SnmpContextBasisFace con) 
{
    super(con);
}


}
