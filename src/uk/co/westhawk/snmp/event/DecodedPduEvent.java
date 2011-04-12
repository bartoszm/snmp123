// NAME
//      $RCSfile: DecodedPduEvent.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.5 $
// CREATED
//      $Date: 2006/02/09 14:30:18 $
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
package uk.co.westhawk.snmp.event;

import uk.co.westhawk.snmp.stack.*;


/**
 * The DecodedPduEvent class. This class is delivered when a pdu is received.
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.5 $ $Date: 2006/02/09 14:30:18 $
 */
public abstract class DecodedPduEvent extends java.util.EventObject  
{
    private static final String     version_id =
        "@(#)$Id: DecodedPduEvent.java,v 1.5 2006/02/09 14:30:18 birgit Exp $ Copyright Westhawk Ltd";

    protected boolean consumed = false;

    private int hostPort = -1;
    private Pdu pdu;

/** 
 * The constructor for a decoded pdu event. The SnmpContext classes
 * will fire decoded pdu events.
 *
 * @param source The source (SnmpContext) of the event
 * @param p The pdu
 * @param prt The remote port number of the host where the pdu came from
 *
 * @see #getHostPort()
 * @see #getPdu()
 */
public DecodedPduEvent(Object source, Pdu p, int prt) 
{
    super(source);
    pdu = p;
    hostPort = prt;
}



/**
 * The remote port number of the host where the pdu came from. 
 *
 * @return The remote port number of the host or -1.
 *
 */
public int getHostPort()
{
    return hostPort;
}


/**
 * The pdu. The pdu is part of a decoded pdu event.
 *
 * @return The decoded Pdu.
 */
public Pdu getPdu()
{
    return pdu;
}

/**
 * Consumes this event so that it will not be sent to any other
 * listeners.
 */
public void consume()
{
    consumed = true;
}

/**
 * Returns whether or not this event has been consumed.
 * @see #consume 
 */
public boolean isConsumed()
{
    return consumed;
}

}
