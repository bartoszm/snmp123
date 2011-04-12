// NAME
//      $RCSfile: SimpleUsmAgent.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.5 $
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

package uk.co.westhawk.examplev3;

import uk.co.westhawk.snmp.stack.*;

/**
 * This implementation of UsmAgent just returns static, constant values.
 *
 * <p>
 * Users are advised and encouraged to provide a better, more accurate
 * implementation of UsmAgent.
 * </p>
 *
 * <p>
 * See <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 * </p>
 * @see SnmpContextv3
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.5 $ $Date: 2006/03/23 14:54:09 $
 */
public class SimpleUsmAgent implements UsmAgent
{
    static final String version_id =
        "@(#)$Id: SimpleUsmAgent.java,v 1.5 2006/03/23 14:54:09 birgit Exp $ Copyright Westhawk Ltd";

    private String _snmpEngineId = "800007e580fd791162bfae0042";
    private int _boots = 1;
    private int _time = 1;
    private int _usmStatsUnknownEngineIDs = 0;
    private int _usmStatsNotInTimeWindows = 0;


public SimpleUsmAgent()
{
}

public void setSnmpEngineId(String snmpEngineId)
{
    _snmpEngineId = snmpEngineId;
}
public String getSnmpEngineId()
{
    return _snmpEngineId;
}

public void setSnmpEngineBoots(int boots)
{
    _boots = boots;
}
public int getSnmpEngineBoots()
{
    return _boots;
}

/**
 * Returns the authoritative Engine Time. If the discovery failed,
 * <em>1</em> will be returned.
 *
 * @return The Engine Time
 */
public void setSnmpEngineTime(int time)
{
    _time = time;
}
public int getSnmpEngineTime()
{
    return _time;
}

/**
 * Sets the SNMP context. It will do a discovery if needed.
 */
public void setSnmpContext(SnmpContextv3Basis c) 
{
}

public void setUsmStatsUnknownEngineIDs(int usmStatsUnknownEngineIDs)
{
    _usmStatsUnknownEngineIDs = usmStatsUnknownEngineIDs;
}
public long getUsmStatsUnknownEngineIDs()
{
    return _usmStatsUnknownEngineIDs;
}

public void setUsmStatsNotInTimeWindows(int usmStatsNotInTimeWindows)
{
    _usmStatsNotInTimeWindows = usmStatsNotInTimeWindows;
}
public long getUsmStatsNotInTimeWindows()
{
    return _usmStatsNotInTimeWindows;
}

}
