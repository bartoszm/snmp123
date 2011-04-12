// NAME
//      $RCSfile: GraphDataSet.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.7 $
// CREATED
//      $Date: 2006/01/17 17:43:54 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1998 - 2006 by Westhawk Ltd
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

package uk.co.westhawk.visual;
import java.beans.*;
import java.util.*;

/**
 * <p>
 * The class GraphDataSet holds the int data, representing a line on 
 * a Graph.
 * </p>
 *
 * <p>
 * Only a fixed number of elements will be saved, that is called the
 * capacity. The oldest value is removed each time the capacity would
 * overflow.
 * </p>
 *
 * <p>
 * The capacity can not be change dynamically, only via the constructor.
 * </p>
 *
 * @see Graph
 * @see BareGraph
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.7 $ $Date: 2006/01/17 17:43:54 $
 */
public class GraphDataSet extends Object
{
    private static final String     version_id =
        "@(#)$Id: GraphDataSet.java,v 1.7 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";

    private int capacity = 100;
    private int [] values;
    private int index = 0;

    private Vector propertyChangeListener = null;


/**
 * The default constructor. The capacity will be by default
 * <em>100</em>.
 */
public GraphDataSet()
{
    this (100);
}

/**
 * The constructor with the capacity.
 *
 * @param cap the capacity of this data set
 */
public GraphDataSet(int cap)
{
    capacity = 100;
    values = new int[capacity];
    propertyChangeListener = new Vector();
}

/**
 * Returns the capacity. The capacity is the number of elements that
 * this data set can hold. 
 *
 * @return the capacity 
 */
public int getCapacity()
{
    return capacity;
}

/**
 * Returns the current number of elements. This is always less or equal
 * to the capacity.
 *
 * @return the size 
 */
public int getSize()
{
    return index;
}

/**
 * <p>
 * Adds a value to the data set. If the capacity is reached, the oldest
 * value will be removed. 
 * </p>
 *
 * <p>
 * The oldest value will always the first
 * element, the newest value always the last element.
 * This methods fires a propertyChangeEvent.
 * </p>
 *
 * @param v the new element (int)
 */
public void addElement(int v)
{
    synchronized(values)
    {
        int newcount = index + 1;
        if (newcount > capacity)
        {
            shiftValues();
        }
        values[index++] = v;
    }

    firePropertyChange("Values", null, new Integer(v));
}

/**
 * Returns the values of this data set. The length of this array will
 * always be the capacity. To get the actual number, call getSize().
 *
 * @return all the elements 
 * @see #getSize
 * @see #getCapacity
 */
public int [] getValues()
{
   return values;
}

private void shiftValues()
{
    int oldData[] = values;
    values = new int [capacity];

    System.arraycopy(oldData, 1, values, 0, capacity-1);
    index = capacity-1;
}

/**
 * Add a property change listener.
 * This methods fires a propertyChangeEvent.
 *
 * @see #removePropertyChangeListener
 */
public void addPropertyChangeListener(PropertyChangeListener l)
{
    synchronized(propertyChangeListener)
    {
        propertyChangeListener.addElement(l);
    }

    if (index > 0)
    {
        firePropertyChange("Values", null, new Integer(values[index-1]));
    }
}

/**
 * Remove a property change listener.
 * @see #addPropertyChangeListener
 */
public void removePropertyChangeListener(PropertyChangeListener l)
{
    synchronized(propertyChangeListener)
    {
        propertyChangeListener.removeElement(l);
    }
}
 
/**
 * Fire a property event. This is done each time a new element is added
 * and when adding a listener.
 *
 * @param property this will be <em>Values</em>
 * @param old_v this is not used
 * @param new_v the new (added) value as Integer 
 *
 * @see #removePropertyChangeListener
 * @see #addPropertyChangeListener
 * @see PropertyChangeEvent
 * @see PropertyChangeListener
 */
protected void firePropertyChange(String property, Object old_v, Object new_v)
{
    Vector listeners;
    synchronized(propertyChangeListener)
    {
        listeners = (Vector) propertyChangeListener.clone();
    }

    PropertyChangeEvent event = new PropertyChangeEvent(this, 
          property, old_v, new_v);
 
    int sz = listeners.size();
    for (int i=0; i<sz; i++)
    {
        PropertyChangeListener l = (PropertyChangeListener) 
                listeners.elementAt(i);
        l.propertyChange(event);
    }
}

}
