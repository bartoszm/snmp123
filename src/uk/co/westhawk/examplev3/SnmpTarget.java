// NAME
//      $RCSfile: SnmpTarget.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.5 $
// CREATED
//      $Date: 2006/11/30 17:44:55 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//
/*
 * Copyright (C) 2000 - 2006 by Westhawk Ltd
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
import uk.co.westhawk.snmp.pdu.*;

/**
 * The SnmpTarget class is used in the StreamEventMonitor class. It
 * creates either a SNMPv1 or a SNMPv3 context and offers various get
 * requests.
 *
 * @see StreamEventMonitor
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 1.5 $ $Date: 2006/11/30 17:44:55 $
 */
public class SnmpTarget
{
    private static final String     version_id =
        "@(#)$Id: SnmpTarget.java,v 1.5 2006/11/30 17:44:55 birgit Exp $ Copyright Westhawk Ltd";

    private SnmpContextBasisFace context;
    private BlockPdu pdu;
    private String[] oids;
    private String oid;

public SnmpTarget()
{
    context = null;
    pdu = null;
    oids = null;
    oid = null;
}

/**
 * Sets the SNMPv1 parameters and creates a SnmpContextPool context.
 */
public void setParameters(String host, int port, String community, 
    boolean tcp)
{
    if (context != null)
    {
        context.destroy();
        context = null;
    }
    try
    {
        String type;
        if (tcp)
        {
            type = SnmpContextBasisFace.TCP_SOCKET;
        }
        else
        {
            type = SnmpContextBasisFace.STANDARD_SOCKET;
        }
        SnmpContextPool c = new SnmpContextPool(host, port, type);
        c.setCommunity(community);
        context = c;
    }
    catch (java.io.IOException exc)
    {
        exc.printStackTrace();
    }
}


/**
 * Sets the SNMPv3 parameters and creates a SnmpContextv3Pool context.
 */
public void setParameters(String host, int port, String user, String pass, 
    String contextName, byte[] contextEngineId, int proto, boolean tcp)
{
    if (context != null)
    {
        context.destroy();
        context = null;
    }
    try
    {
        String type;
        if (tcp)
        {
            type = SnmpContextBasisFace.TCP_SOCKET;
        }
        else
        {
            type = SnmpContextBasisFace.STANDARD_SOCKET;
        }
        SnmpContextv3Pool c = new SnmpContextv3Pool(host, port, type);
        c.setAuthenticationProtocol(c.MD5_PROTOCOL);
        c.setUserName(user);
        c.setContextEngineId(contextEngineId);
        c.setContextName(contextName);
        if (pass == null) 
        {
            c.setUseAuthentication(false);
        } 
        else 
        {
            c.setUserAuthenticationPassword(pass);
            c.setUseAuthentication(true);
            c.setAuthenticationProtocol(proto);
        }
        context= c;
    }
    catch (java.io.IOException exc)
    {
        exc.printStackTrace();
    }
}


/**
 * Sets an OID.
 */
public void setObjectID(String oid)
{
    this.oid = oid;
}

/**
 * Sets a list of OIDs.
 */
public void setObjectIDList(String[] oids)
{
    this.oids = oids;
}

/**
 * Performs a (blocked) getRequest with the context created in
 * setParameters.
 *
 * @see BlockPdu#getResponseVariable
 */
public AsnObject snmpGetVariable()
{
    AsnObject result = null;

    if (oid == null) return result;

    try
    {
        pdu = new BlockPdu(context);
        pdu.setPduType(BlockPdu.GET);
        pdu.addOid(oid);
        result = pdu.getResponseVariable();
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
    }

    if (result == null)
    {
        System.err.println("snmpGetVariable(): No Answer " + pdu.toString());
    }

    return result;
}

/**
 * Performs a (blocked) getRequest with the context created in
 * setParameters.
 *
 * @see BlockPdu#getResponseVariables
 */
public AsnObject[] snmpGetVariables()
{
    AsnObject[] results = null;

    if (oids == null) return results;

    try
    {
        pdu = new BlockPdu(context);
        pdu.setPduType(BlockPdu.GET);
        for (int n=0; n < oids.length; n++)
        {
            pdu.addOid(oids[n]);
        }
        results = pdu.getResponseVariables();
    }
    catch(Exception exc)
    {
        results = null;
        exc.printStackTrace();
    }

    if (results == null)
    {
        System.err.println("snmpGetVariables(): No Answer " + pdu.toString());
    }
    return results;
}

/**
 * Performs a (blocked) getRequest with the context created in
 * setParameters.
 *
 * @see BlockPdu#getResponseVariableBinding
 */
public varbind snmpGetVariableBinding()
{
    varbind result = null;

    if (oid == null) return result;
    
    try
    {
        pdu = new BlockPdu(context);
        pdu.setPduType(BlockPdu.GET);
        pdu.addOid(oid);
        result = pdu.getResponseVariableBinding();
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
    }
    
    if (result == null)
    {
        System.err.println("snmpGetVariableBinding(): No Answer " 
            + pdu.toString());
    }
    return result;
}

/**
 * Performs a (blocked) getRequest with the context created in
 * setParameters.
 *
 * @see BlockPdu#getResponseVariableBindings
 */
public varbind[] snmpGetVariableBindings()
{
    varbind[] results = null;
    
    if (oids == null) return results;
    
    try
    {
        pdu = new BlockPdu(context);
        pdu.setPduType(BlockPdu.GET);
        for (int n=0; n < oids.length; n++)
        {
            pdu.addOid(oids[n]);
        }
        results = pdu.getResponseVariableBindings();
    }
    catch (Exception exc)
    {
        results = null;
        exc.printStackTrace();
    }

    if (results == null)
    {
        System.err.println("snmpGetVariableBindings(): No Answer " 
            + pdu.toString());
    }
    return results;
}

/**
 * Performs a (blocked) getNextRequest with the context created in
 * setParameters.
 *
 * @see BlockPdu#getResponseVariableBinding
 */
public varbind snmpGetNextVariableBinding()
{
    varbind result = null;

    if (oid == null) return result;
    
    try
    {
        pdu = new BlockPdu(context);
        pdu.setPduType(BlockPdu.GETNEXT);
        pdu.addOid(oid);
        result = pdu.getResponseVariableBinding();
        if (result != null)
        {
            oid = (result.getOid()).toString();
        }
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
    }
    
    if (result == null)
    {
        System.err.println("snmpGetNextVariableBinding(): No Answer " 
            + pdu.toString());
    }
    return result;
}

/**
 * Performs a (blocked) setRequest with the context created in
 * setParameters.
 *
 * @see BlockPdu#getResponseVariable
 */
public AsnObject snmpSetVariable(AsnObject var)
{
    AsnObject result = null;

    if (oid == null) return result;
    
    try
    {
        pdu = new BlockPdu(context);
        pdu.setPduType(BlockPdu.SET);
        pdu.addOid(oid, var);
        result = pdu.getResponseVariable();
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
    }
    
    if (result == null)
    {
        System.err.println("snmpSetVariable(): No Answer " + pdu.toString());
    }
    return result;    
}

public String toString()
{
    return context.toString();
}

}
