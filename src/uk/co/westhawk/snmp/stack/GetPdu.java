// NAME
//      $RCSfile: GetPdu.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.3 $
// CREATED
//      $Date: 2006/01/17 17:59:34 $
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
 * author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 */

package uk.co.westhawk.snmp.stack;

/**
 * This class represents the (public) SNMP Get Pdu.
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.3 $ $Date: 2006/01/17 17:59:34 $
 * @see uk.co.westhawk.snmp.pdu.OneGetPdu
 * @see uk.co.westhawk.snmp.pdu.GetPdu_vec
 */
public class GetPdu extends Pdu 
{
    private static final String     version_id =
        "@(#)$Id: GetPdu.java,v 3.3 2006/01/17 17:59:34 birgit Exp $ Copyright Westhawk Ltd";

    /** 
     * Constructor.
     *
     * @param con The context of the Pdu
     */
    public GetPdu(SnmpContextBasisFace con) 
    {
        super(con);
        setMsgType(AsnObject.GET_REQ_MSG);
    }

}
