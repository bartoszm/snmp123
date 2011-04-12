// NAME
//      $RCSfile: RequestPduReceivedSupport.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.4 $
// CREATED
//      $Date: 2006/01/17 17:59:33 $
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

import java.util.*;
import uk.co.westhawk.snmp.stack.*;

/**
 * This is a utility class that can be used by classes that support 
 * request pdu listener functionality. 
 * You can use an instance of this class as a member field
 * of your class and delegate various work to it. 
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.4 $ $Date: 2006/01/17 17:59:33 $
 */
public class RequestPduReceivedSupport 
{
    public static final String     version_id =
        "@(#)$Id: RequestPduReceivedSupport.java,v 1.4 2006/01/17 17:59:33 birgit Exp $ Copyright Westhawk Ltd";

    private Object source;
    private transient Vector pduListeners;

/**
 * The constructor.
 *
 * @param src The source (SnmpContext) of the pdu events when they are fired. 
 */
public RequestPduReceivedSupport(Object src)
{
    source = src;
}

/**
 * Removes all the listeners.
 */
public synchronized void empty()
{
    if (pduListeners != null)
    {
        pduListeners.removeAllElements();
    }
}

/**
 * Returns the number of listeners.
 *
 * @return The number of listeners.
 */
public synchronized int getListenerCount()
{
    int c=0;
    if (pduListeners != null)
    {
        c = pduListeners.size();
    }
    return c;
}

/**
 * Adds the specified pdu listener to receive pdus. 
 */ 
public synchronized void addRequestPduListener(RequestPduListener listener)
{
    if (pduListeners == null)
    {
        pduListeners = new Vector (5);
    }
    if (pduListeners.contains(listener) == false)
    {
        pduListeners.addElement(listener);
    }
}

/**
 * Removes the specified pdu listener.
 */
public synchronized void removeRequestPduListener(RequestPduListener listener)
{
    if (pduListeners != null)
    {
        pduListeners.removeElement(listener);
    }
}


/**
 * Fires a decoded pdu event.
 * The event is fired to all listeners, whether they consume it or not.
 * 
 * @param pdu The decoded pdu pdu.
 */
public void fireRequestPduReceived(Pdu pdu, int hostPort)
{
    Vector copyOfListeners = null;
    if (pduListeners != null)
    {
        synchronized (pduListeners)
        {
            copyOfListeners = (Vector) pduListeners.clone();
        }
    }

    if (copyOfListeners != null)
    {
        int sz = copyOfListeners.size();
        for (int i=sz-1; i>=0; i--)
        {
            RequestPduListener listener = (RequestPduListener) copyOfListeners.elementAt(i);

            RequestPduEvent evt = new RequestPduEvent(source, pdu, hostPort);
            listener.requestPduReceived(evt);
        }
    }
}

}
