// NAME
//      $RCSfile: TrapReceivedSupport.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.6 $
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
package uk.co.westhawk.snmp.event;

import java.util.*;
import uk.co.westhawk.snmp.stack.*;

/**
 * This is a utility class that can be used by classes that support trap
 * listener functionality. 
 * You can use an instance of this class as a member field
 * of your class and delegate various work to it. 
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.6 $ $Date: 2006/01/17 17:43:53 $
 */
public class TrapReceivedSupport 
{
    public static final String     version_id =
        "@(#)$Id: TrapReceivedSupport.java,v 1.6 2006/01/17 17:43:53 birgit Exp $ Copyright Westhawk Ltd";

    private Object source;
    private transient Vector trapListeners;

/**
 * The constructor.
 *
 * @param src The source (SnmpContext) of the trap events when they are fired. 
 */
public TrapReceivedSupport(Object src)
{
    source = src;
}

/**
 * Removes all the listeners.
 */
public synchronized void empty()
{
    if (trapListeners != null)
    {
        trapListeners.removeAllElements();
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
    if (trapListeners != null)
    {
        c = trapListeners.size();
    }
    return c;
}

/**
 * Adds the specified trap listener to receive traps. 
 */ 
public synchronized void addTrapListener(TrapListener listener)
{
    if (trapListeners == null)
    {
        trapListeners = new Vector (5);
    }
    if (trapListeners.contains(listener) == false)
    {
        trapListeners.addElement(listener);
    }
}

/**
 * Removes the specified trap listener.
 */
public synchronized void removeTrapListener(TrapListener listener)
{
    if (trapListeners != null)
    {
        trapListeners.removeElement(listener);
    }
}


/**
 * Fires a decoded trap event.
 * The event is fired to all listeners, whether they consume it or not.
 * This behaviour is different from the undecoded trap event.
 * 
 * @param pdu The decoded trap pdu.
 */
public void fireTrapReceived(Pdu pdu, int hostPort)
{
    Vector copyOfListeners = null;
    if (trapListeners != null)
    {
        synchronized (trapListeners)
        {
            copyOfListeners = (Vector) trapListeners.clone();
        }
    }

    if (copyOfListeners != null)
    {
        int sz = copyOfListeners.size();
        for (int i=sz-1; i>=0; i--)
        {
            TrapListener listener = (TrapListener) copyOfListeners.elementAt(i);

            TrapEvent evt = new TrapEvent(source, pdu, hostPort);
            listener.trapReceived(evt);
        }
    }
}

}
