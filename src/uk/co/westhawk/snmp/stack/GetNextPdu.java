// NAME
//      $RCSfile: GetNextPdu.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.8 $
// CREATED
//      $Date: 2006/01/17 17:43:54 $
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

/**
 * This class represents the SNMP GetNext Pdu.
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.8 $ $Date: 2006/01/17 17:43:54 $
 * @see uk.co.westhawk.snmp.pdu.OneGetNextPdu
 * @see uk.co.westhawk.snmp.pdu.GetNextPdu_vec
 */
public class GetNextPdu extends Pdu 
{
    private static final String     version_id =
        "@(#)$Id: GetNextPdu.java,v 3.8 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";

    /** 
     * Constructor.
     *
     * @param con The context of the Pdu
     */
    public GetNextPdu(SnmpContextBasisFace con) 
    {
        super(con);
        setMsgType(AsnObject.GETNEXT_REQ_MSG);
    }

}
