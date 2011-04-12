// NAME
//      $RCSfile: OneGetBulkPdu.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.9 $
// CREATED
//      $Date: 2006/01/17 17:43:53 $
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
import uk.co.westhawk.snmp.stack.*;
import java.util.*;

/**
 * <p>
 * The OneGetBulkPdu class performs a getBulkRequest and collects 
 * the response varbinds into a Vector.
 * </p>
 *
 * <p>
 * If no exception occurred whilst receiving the response, the Object to the 
 * update() method of the Observer will be an Vector of
 * varbinds, so they may contains any AsnObject type.
 * If an exception occurred, that exception will be passed as the Object
 * to the update() method.
 * </p>
 *
 * <p>
 * For SNMPv3: The receiver of a request PDU acts as the authoritative engine.
 * </p>
 *
 * @see varbind
 * @see java.util.Vector
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.9 $ $Date: 2006/01/17 17:43:53 $
 */
public class OneGetBulkPdu extends GetBulkPdu 
{
    private static final String     version_id =
        "@(#)$Id: OneGetBulkPdu.java,v 3.9 2006/01/17 17:43:53 birgit Exp $ Copyright Westhawk Ltd";

    /**
     * The GetBulk request is the only request that will return more
     * variables than you've sent.
     */
    java.util.Vector vars;

/**
 * Constructor.
 *
 * @param con The context (v2c or v3) of the PDU
 */
public OneGetBulkPdu(SnmpContextBasisFace con)
{
    super(con);
    vars = new java.util.Vector();
}


/**
 * Returns a vector with the response varbinds.
 */
public java.util.Vector getVarbinds()
{
    return vars;
}


/**
 * The value of the request is set. This will be called by
 * Pdu.fillin().
 *
 * @param n the index of the value
 * @param a_var the value
 * @see Pdu#new_value 
 */
protected void new_value(int n, varbind a_var) 
{
    if (n == 0) 
    {
        vars = new java.util.Vector();
    }
    vars.addElement(a_var);
}

/**
 * This method notifies all observers. 
 * This will be called by Pdu.fillin().
 * 
 * <p>
 * If no exception occurred whilst receiving the response, the Object to the 
 * update() method of the Observer will be an Vector of
 * varbinds, so they may contains any AsnObject type.
 * If an exception occurred, that exception will be passed as the Object
 * to the update() method.
 * </p>
 *
 * @see java.util.Vector
 */
protected void tell_them()  
{
    notifyObservers(vars);
}

}
