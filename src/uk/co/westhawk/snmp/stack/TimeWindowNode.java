// NAME
//      $RCSfile: TimeWindowNode.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.10 $
// CREATED
//      $Date: 2006/01/17 17:43:54 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2000 - 2006 by Westhawk Ltd
 * <a href="www.westhawk.co.uk">www.westhawk.co.uk</a>
 *
 * Permission to use, copy, modify, and distribute this software
 * for any purpose and without fee is hereby granted, provided
 * that the above copyright notices appear in all copies and that
 * both the copyright notice and this permission notice appear in
 * supporting documentation.
 * This software is provided "as is" without express or implied
 * warranty.
 */
package uk.co.westhawk.snmp.stack;

/**
 * Node that contains the timeline information of one SNMP Engine ID.
 * This entry has to be filled in via Discovery. 
 * The node will serve as entry in a lookup table.
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.10 $ $Date: 2006/01/17 17:43:54 $
 */
class TimeWindowNode 
{
    private static final String     version_id =
        "@(#)$Id: TimeWindowNode.java,v 3.10 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";

    public final static int maxTime = 2147483647; // 2^31 -1

    private String snmpEngineId;

    private int snmpEngineBoots = 0;
    private int snmpEngineTime = 0;
    private int latestReceivedEngineTime = 0;

/**
 * Constructor.
 *
 * @param engId The engine ID
 */
public TimeWindowNode(String engId)
{
    this(engId, 0, 0);
}

/**
 * Constructor.
 *
 * @param engId The engine ID
 * @param boots The engine boots
 * @param time The engine time
 */
public TimeWindowNode(String engId, int boots, int time)
{
    snmpEngineId = engId;
    snmpEngineBoots = boots;
    snmpEngineTime = time;
    latestReceivedEngineTime = time;
}


/**
 * Returns the snmp engine ID.
 *
 * @return the snmp engine ID
 */
public String getSnmpEngineId()
{
    return snmpEngineId;
}

/**
 * Sets the SNMP engine boots 
 *
 * @param newSnmpEngineBoots The SNMP engine boots
 */
public void setSnmpEngineBoots(int newSnmpEngineBoots)
{
    snmpEngineBoots = newSnmpEngineBoots;
}

/**
 * Returns the SNMP engine boots
 *
 * @return The SNMP engine boots
 */
public int getSnmpEngineBoots()
{
    return snmpEngineBoots;
}

/**
 * Sets the SNMP engine time. It also sets the latest received engine
 * time.
 *
 * @param newSnmpEngineTime The SNMP engine time
 */
public void setSnmpEngineTime(int newSnmpEngineTime)
{
    snmpEngineTime = newSnmpEngineTime;
    latestReceivedEngineTime = newSnmpEngineTime;
}

/**
 * Returns the (estimated) SNMP engine time
 *
 * @return The SNMP engine time
 */
public int getSnmpEngineTime()
{
    return snmpEngineTime;
}

/**
 * Increments the engine time. This mechanisme maintains the loosely
 * time synchronisation.
 */
public synchronized void incrementSnmpEngineTime(int incr)
{
    snmpEngineTime += incr;
    if (snmpEngineTime > maxTime)
    {
        snmpEngineBoots++;
        snmpEngineTime -= maxTime;
    }
}

/**
 * Returns the latest received engine time for an engine ID
 *
 * @return The latest received engine time
 */
public int getLatestReceivedEngineTime()
{
    return latestReceivedEngineTime;
}

/**
 * Returns a string representation of the object.
 * @return The string
 */
public String toString()
{
    StringBuffer buffer = new StringBuffer(this.getClass().getName());
    buffer.append("[");
    buffer.append("engineId=").append(snmpEngineId);
    buffer.append(", engineBoots=").append(snmpEngineBoots);
    buffer.append(", engineTime=").append(snmpEngineTime);
    buffer.append(", latestReceivedEngineTime=").append(latestReceivedEngineTime);
    buffer.append("]");
    return buffer.toString();
}

}
