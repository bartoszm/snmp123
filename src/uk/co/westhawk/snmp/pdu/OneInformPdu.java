// NAME
//      $RCSfile: OneInformPdu.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.5 $
// CREATED
//      $Date: 2006/02/09 14:20:09 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2002 - 2006 by Westhawk Ltd
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
 * The OneInformPdu class will inform a manager about one 
 * object (OIDs), based on the Inform request.
 * </p>
 *
 * <p>
 * This class represents the SNMP Inform Request PDU. Inform Requests
 * are sent between managers. It is a kind of 'acknowlegded' trap since
 * the receiving end should send a Response PDU as reply.
 * The varbind list has the same elements as the TrapPduv2.
 * </p>
 *
 * <p>
 * Note, this PDU should be sent to port 162 (the default trap port) by
 * default. You will have to create a SnmpContext with the
 * ListeningContextFace.DEFAULT_TRAP_PORT as parameter!
 * </p>
 *
 * <p>
 * For SNMPv3: The receiver of an inform PDU acts as the authoritative engine.
 * </p>
 *
 * @deprecated  As of 4_14, just use {@link InformPdu} 
 * @see InformPdu_vec
 * @see varbind
 * @see ListeningContextFace#DEFAULT_TRAP_PORT
 * @since 4_12
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.5 $ $Date: 2006/02/09 14:20:09 $
 */
public class OneInformPdu extends InformPdu 
{
    private static final String     version_id =
        "@(#)$Id: OneInformPdu.java,v 3.5 2006/02/09 14:20:09 birgit Exp $ Copyright Westhawk Ltd";

    varbind var;

/**
 * Constructor.
 *
 * @param con The context (v2c or v3) of the PDU
 */
public OneInformPdu(SnmpContextBasisFace con)
{
    super(con);
}

/**
 * Constructor that will send the request immediately. No Observer
 * is set.
 *
 * @param con the SnmpContextBasisFace
 * @param oid the oid 
 */
public OneInformPdu(SnmpContextBasisFace con, String oid) 
throws PduException, java.io.IOException
{
    this(con, oid, null);
}

/**
 * Constructor that will send the request immediately. 
 *
 * @param con the SnmpContextBasisFace
 * @param oid the oid 
 * @param o the Observer that will be notified when the answer is received
 */
public OneInformPdu(SnmpContextBasisFace con, String oid, Observer o) 
throws PduException, java.io.IOException
{
    super(con);
    if (o != null) 
    {
        addObserver(o);
    }
    addOid(oid);
    send();
}

/**
 * The value of the request is set. This will be called by
 * InformPdu.fillin().
 *
 * @param n the index of the value
 * @param a_var the value
 * @see Pdu#new_value 
 */
protected void new_value(int n, varbind a_var) 
{
    if (n == 0) 
    {
        var = a_var;
    }
}

/**
 * This method notifies all observers. 
 * This will be called by InformPdu.fillin().
 * 
 * <p>
 * Unless an exception occurred the Object to the update() method of the
 * Observer will be a varbind, so any AsnObject type can be returned.
 * In the case of an exception, that exception will be passed.
 * </p>
 */
protected void tell_them()  
{
    notifyObservers(var);
}

}
