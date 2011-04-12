// NAME
//      $RCSfile: UsmAgent.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.8 $
// CREATED
//      $Date: 2006/03/23 14:54:10 $
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

package uk.co.westhawk.snmp.stack;

/**
 * This interface provides the SNMPv3 USM (User-Based Security Model)
 * authoritative details.
 *
 * <p>
 * When the stack is used as authoritative SNMP engine it has to send
 * its Engine ID and clock (i.e. Engine Boots and Engine Time) with each 
 * message. 
 * The engine who sends a Response, a Trapv2 or a Report is
 * authoritative.
 * </p>
 *
 * <p>
 * Since this stack has no means in providing this information, this
 * interface has to be implemented by the user.
 * </p>
 *
 * <p>
 * See <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 * </p>
 * @see SnmpContextv3#setUsmAgent
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.8 $ $Date: 2006/03/23 14:54:10 $
 */
public interface UsmAgent
{
    static final String version_id =
        "@(#)$Id: UsmAgent.java,v 3.8 2006/03/23 14:54:10 birgit Exp $ Copyright Westhawk Ltd";

/** 
 * This name ("_myusmagent") is used to create an entry in the TimeWindow
 * for the stack to act as authoritative engine.
 */
public String MYFAKEHOSTNAME = "_myusmagent";

/**
 * Returns the authoritative SNMP Engine ID.
 * It uniquely and unambiguously identifies the SNMP engine, within an 
 * administrative domain. 
 *
 * <p>
 * The Engine ID is the (case insensitive) string representation of a
 * hexadecimal number, without any prefix, for example
 * <b>010000a1d41e4946</b>. 
 * </p>
 * @see uk.co.westhawk.snmp.util.SnmpUtilities#toBytes(String)
 */
public String getSnmpEngineId();

/**
 * Returns the authoritative Engine Boots.
 * It is a count of the number of times the SNMP engine has
 * re-booted/re-initialized since snmpEngineID was last configured.
 */
public int getSnmpEngineBoots();

/**
 * Returns the authoritative Engine Time.
 * It is the number of seconds since the snmpEngineBoots counter was
 * last incremented.
 */
public int getSnmpEngineTime();

/**
 * Returns the value of the usmStatsUnknownEngineIDs counter.
 * The stack needs this when responding to a discovery request. 
 */
public long getUsmStatsUnknownEngineIDs();

/**
 * Returns the value of the usmStatsNotInTimeWindows counter.
 * The stack needs this when responding to a discovery request. 
 */
public long getUsmStatsNotInTimeWindows();

/**
 * Sets the current snmp context.
 */
public void setSnmpContext(SnmpContextv3Basis context);
}

