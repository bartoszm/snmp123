// NAME
//      $RCSfile: SnmpTarget.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.18 $
// CREATED
//      $Date: 2009/03/05 12:59:50 $
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
package uk.co.westhawk.test;

import java.util.*;
import java.io.*;
import java.beans.*;

import org.w3c.dom.*;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import uk.co.westhawk.snmp.util.*;

/**
 * The SnmpTarget class performs tests specified according to DOM node. 
 * It is the helper class of TestSuite and TrapTestSuite.
 *
 * @see TestSuite
 * @see TrapTestSuite
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.18 $ $Date: 2009/03/05 12:59:50 $
 */
public class SnmpTarget 
{
    private static final String     version_id =
        "@(#)$Id: SnmpTarget.java,v 1.18 2009/03/05 12:59:50 birgita Exp $ Copyright Westhawk Ltd";

    public static final String ADO = "ado";
    public static final String APASSW = "apassw";
    public static final String APROTO = "aproto";
    public static final String AUTH = "auth";
    public static final String BIND = "bind";
    public static final String COMMENT = "comment";
    public static final String COMMUNITY = "community";
    public static final String CONTEXT = "context";
    public static final String GET = "get";
    public static final String GETBULK = "getBulk";
    public static final String GETNEXT = "getNext";
    public static final String HOST = "host";
    public static final String ID = "id";
    public static final String INFORM = "inform";
    public static final String MAX_REP = "max_rep";
    //public static final String MIB = "mib";
    public static final String NAME = "name";
    public static final String NET = "NET";
    //public static final String NO = "no";
    public static final String NON_REP = "non_rep";
    public static final String OID = "oid";
    public static final String PORT = "port";
    public static final String PDO = "pdo";
    public static final String PPROTO = "pproto";
    public static final String PPASSW = "ppassw";
    public static final String PRIV = "priv";
    public static final String REQUEST = "request";
    public static final String SET = "set";
    public static final String SOCKET_TYPE = "socket_type";
    public static final String STD = "STD";
    public static final String TCP = "TCP";
    public static final String TYPE = "type";
    public static final String USERNAME = "username";
    public static final String USM = "usm";
    public static final String VALUE = "value";
    public static final String VARIABLE = "variable";
    public static final String VERSION = "version";
    //public static final String SNMPv1 = "1";
    public static final String SNMPv2c = "2c";
    public static final String SNMPv3 = "3";
    public static final String VNO = "no";
    public static final String YES = "yes";

    private SnmpContextBasisFace _context;
    private PrintWriter _writer;
    private DOMWriter _domWriter;

    private PropertyChangeSupport _propertyChangeListeners;
    private int _nrRequests;
    public final static int [] _interval = new int[5000];


/**
 * The constructor.
 */
public SnmpTarget()
{
    _context = null;
    _writer = new PrintWriter(new OutputStreamWriter(System.out), true);
    _domWriter = new DOMWriter(true, "a", "b");
    _propertyChangeListeners = new PropertyChangeSupport(this);
}

/**
 * Sets the writer to be used for all output. If not specified,
 * <code>System.out</code> will be used.
 * @param w The writer
 */
public void setWriter(PrintWriter w)
{
    _writer = w;
}

/**
 * Performs one or more tests specified by node. 
 * The context will be created out off the node and all the tests will be
 * performed.
 *
 * @param node The DOM node
 */
public void performTest(Node node)
{
    _nrRequests = 0;
    try
    {
        if (_context != null)
        {
            _context.destroy();
        }
        _context = createContext(node);

        NodeList childNodes = node.getChildNodes();
        int l = childNodes.getLength();
        for (int i=0; i<l; i++)
        {
            Node childNode = childNodes.item(i);
            short type = childNode.getNodeType();
            String childName = childNode.getNodeName(); 

            if (type == Node.ELEMENT_NODE && childName.equals(REQUEST))
            {
                performRequest((Element) childNode);
            }
        }
    }
    catch (java.io.IOException exc)
    {
        exc.printStackTrace(_writer);
    }
    tellThemWeAreReady();
}

/**
 * Performs one request specified by node.
 * @param node The DOM node
 */
public void performRequest(Element node) 
{
    String requestType = node.getAttribute(TYPE);

    // first get all the OIDS
    Vector vars = getOids(node);
    int sz = vars.size();

    Pdu pdu = null;
    if (requestType.equals(GET))
    {
        pdu = new GetPdu_vec(_context, sz);
        pdu.addObserver(new One());
    }
    else if (requestType.equals(SET))
    {
        pdu = new SetPdu_vec(_context, sz);
        pdu.addObserver(new One());
    }
    else if (requestType.equals(GETBULK))
    {
        GetBulkPdu p = new GetBulkPdu(_context);
        String non_repStr = Util.getCDataValue(node, NON_REP);
        String max_repStr = Util.getCDataValue(node, MAX_REP);

        int non_rep = 0;
        try
        {
            non_rep = Integer.parseInt(non_repStr);
        }
        catch (NumberFormatException exc) { }
        catch (NullPointerException exc) { }

        int max_rep = 0;
        try
        {
            max_rep = Integer.parseInt(max_repStr);
        }
        catch (NumberFormatException exc) { }
        catch (NullPointerException exc) { }

        p.setNonRepeaters(non_rep); 
        p.setMaxRepetitions(max_rep);

        pdu = p;
        pdu.addObserver(new One());
    }
    else if (requestType.equals(INFORM))
    {
        pdu = new InformPdu_vec(_context, sz);
        pdu.addObserver(new One());
    }
    else
    {
        pdu = new GetNextPdu_vec(_context, sz);
        if (requestType.equals(GETNEXT))
        {
            pdu.addObserver(new One());
        }
        else
        {
            // GETWALK
            pdu.addObserver(new Walk());
        }

    }

    for (int i=0; i<sz; i++)
    {
        varbind var = (varbind) vars.elementAt(i);
        pdu.addOid(var);
    }

    try
    {
        _nrRequests ++;
        pdu.send();
    }
    catch(java.io.IOException exc)
    {
        _writer.println("performRequest(): " + _context.toString());
        _writer.println("performRequest(): IOException " + exc.getMessage());

        _nrRequests --;
        tellThemWeAreReady();
    }
    catch (uk.co.westhawk.snmp.stack.PduException exc)
    {
        _writer.println("performRequest(): " + _context.toString());
        _writer.println("performRequest(): PduException " + exc.getMessage());

        _nrRequests --;
        tellThemWeAreReady();
    }
    catch (Exception exc)
    {
        _writer.println("performRequest(): " + _context.toString());
        _writer.println("performRequest(): Exception " + exc.getMessage());

        _nrRequests --;
        tellThemWeAreReady();
    }
}

/**
 * Performs a GetNextRequest with the specified context and OID.
 *
 * @param con The context
 * @param oid The OID
 */
public void performGetNextRequest(SnmpContextBasisFace con, String oid, 
    int [] retry_intervals)
{
    _nrRequests = 1;
    try
    {
        GetNextPdu pdu = new GetNextPdu(con);
        if (retry_intervals != null)
        {
            pdu.setRetryIntervals(retry_intervals);
        }
        pdu.addOid(oid);
        pdu.addObserver(new One());
        pdu.send();
    }
    catch (PduException exc) 
    { 
        _writer.println("performGetNextRequest(): " + con.toString());
        _writer.println("performGetNextRequest(): PduException " + exc.getMessage()); 
        _nrRequests --;
        tellThemWeAreReady();
    }
    catch (java.io.IOException exc) 
    { 
        _writer.println("performGetNextRequest(): " + con.toString());
        _writer.println("performGetNextRequest(): IOException " + exc.getMessage()); 
        _nrRequests --;
        tellThemWeAreReady();
    }
    catch (Exception exc)
    {
        _writer.println("performGetNextRequest(): " + con.toString());
        _writer.println("performGetNextRequest(): Exception " + exc.getMessage());
        _nrRequests --;
        tellThemWeAreReady();
    }
}

/**
 * Returns the varbind list of OIDs specified in node.
 *
 * @param node The DOM node
 */
public Vector getOids(Node node)
{
    NodeList childNodes = node.getChildNodes();
    int l = childNodes.getLength();
    Vector v = new Vector(l);
    for (int i=0; i<l; i++)
    {
        Node childNode = childNodes.item(i);
        short type = childNode.getNodeType();
        String childName = childNode.getNodeName(); 

        if (type == Node.ELEMENT_NODE && childName.equals(OID))
        {
            String variable = Util.getCDataValue(childNode, VARIABLE);
            String value = Util.getCDataValue(childNode, VALUE);
            String comment = Util.getCDataValue(childNode, COMMENT);

            varbind var;
            if (value != null)
            {
                var = new varbind(variable, new AsnOctets(value));
            }
            else
            {
                var = new varbind(variable);
            }
            v.addElement(var);
        }
    }

    return v;
}

/**
 * Returns the trap context specified in node.
 *
 * @param node The DOM node
 */
public ListeningContextPool createListeningContext(Node node) throws IOException
{
    ListeningContextPool tContext = null;
    int portNo = getPort(node, ListeningContextFace.DEFAULT_TRAP_PORT);
    String bindAddr = Util.getCDataValue(node, BIND);
    String typeSocket = getSocketType(node);

    tContext = new ListeningContextPool(portNo, bindAddr, typeSocket);
    return tContext;
}

/**
 * Returns the snmp context specified in node.
 *
 * @param node The DOM node
 */
public SnmpContextBasisFace createContext(Node node) throws IOException
{
    SnmpContextBasisFace cContext = null;

    Element versionNode = (Element) Util.getChildNode(node, VERSION);
    String no = "1";
    if (versionNode != null)
    {
        no = versionNode.getAttribute(VNO);
    }
    String host = Util.getCDataValue(node, HOST);
    int portNo = getPort(node, SnmpContextBasisFace.DEFAULT_PORT);
    String bindAddr = Util.getCDataValue(node, BIND);
    String typeSocket = getSocketType(node);

    if (SNMPv2c.equals(no))
    {
        SnmpContextv2c c = new SnmpContextv2c(host, portNo, bindAddr, typeSocket);
        String comm = Util.getCDataValue(node, COMMUNITY);
        c.setCommunity(comm);
        cContext = c;
    }
    else if (SNMPv3.equals(no))
    {
        SnmpContextv3 c = new SnmpContextv3(host, portNo, bindAddr, typeSocket);
        setUsm(c, node);
        cContext = c;
    }
    else
    {
        SnmpContext c = new SnmpContext(host, portNo, bindAddr, typeSocket);
        String comm = Util.getCDataValue(node, COMMUNITY);
        c.setCommunity(comm);
        cContext = c;
    }
    return cContext;
}

/**
 * Returns the socket type specified in node.
 *
 * @param node The DOM node
 */
public String getSocketType(Node node)
{
    Element typeNode = (Element) Util.getChildNode(node, SOCKET_TYPE);
    String type = STD;
    if (typeNode != null)
    {
        type = typeNode.getAttribute(TYPE);
    }

    String typeSocket = type;
    if (STD.equals(type))
    {
        typeSocket = SnmpContextBasisFace.STANDARD_SOCKET;
    }
    else if (TCP.equals(type))
    {
        typeSocket = SnmpContextBasisFace.TCP_SOCKET;
    }
    return typeSocket;
}

/**
 * Returns the port number specified in node.
 *
 * @param node The DOM node
 * @param defValue The default value
 */
public int getPort(Node node, int defValue)
{
    String port = Util.getCDataValue(node, PORT);
    int portNo = defValue;
    try
    {
        portNo = Integer.parseInt(port);
    }
    catch (NumberFormatException exc) { }
    return portNo;
}


/**
 * Sets the USM properties in the context according to node.
 *
 * @param c The SNMPv3 context
 * @param node The DOM node
 */
public void setUsm(SnmpContextv3Face c, Node node)
{
    Element usmNode = (Element) Util.getChildNode(node, USM);
    if (usmNode != null)
    {

        String userName = Util.getCDataValue(usmNode, USERNAME);
        c.setUserName(userName);

        Element contextNode = (Element) Util.getChildNode(usmNode, CONTEXT);
        if (contextNode != null)
        {
            String contextId = Util.getCDataValue(contextNode, ID);
            String contextName = Util.getCDataValue(contextNode, NAME);

            if (contextId != null)
            {
                byte [] bytes = SnmpUtilities.toBytes(contextId.trim());
                c.setContextEngineId(bytes);
            }
            if (contextName != null)
            {
                c.setContextName(contextName.trim());
            }
        }

        Element authNode = (Element) Util.getChildNode(usmNode, AUTH);
        if (authNode != null)
        {
            String doAuth = authNode.getAttribute(ADO);

            if (YES.equals(doAuth))
            {
                String authProto = Util.getCDataValue(authNode, APROTO);
                String authPassw = Util.getCDataValue(authNode, APASSW);

                c.setUseAuthentication(true);
                if (authProto != null && authProto.charAt(0) == 'S')
                {
                    c.setAuthenticationProtocol(SnmpContextv3Face.SHA1_PROTOCOL);
                }
                else
                {
                    c.setAuthenticationProtocol(SnmpContextv3Face.MD5_PROTOCOL);
                }
                c.setUserAuthenticationPassword(authPassw);
            }
            else
            {
                c.setUseAuthentication(false);
            }
        }

        Element privNode = (Element) Util.getChildNode(usmNode, PRIV);
        if (privNode != null)
        {
            String doPriv = privNode.getAttribute(PDO);

            if (YES.equals(doPriv))
            {
                String privProto = Util.getCDataValue(privNode, PPROTO);
                String privPassw = Util.getCDataValue(privNode, PPASSW);
                
                c.setUsePrivacy(true);
                if (privProto != null && privProto.charAt(0) == 'A')
                {
                    c.setPrivacyProtocol(SnmpContextv3Face.AES_ENCRYPT);
                }
                else
                {
                    c.setPrivacyProtocol(SnmpContextv3Face.DES_ENCRYPT);
                }
                c.setUserPrivacyPassword(privPassw);
            }
            else
            {
                c.setUsePrivacy(false);
            }
        }
    }
}


/**
 * Returns the string representation of the current context.
 */
public String toString()
{
    return _context.toString();
}

/**
 * Fires a property change listener when all the requests have been
 * answered.
 */
protected void tellThemWeAreReady()
{
    if (_nrRequests == 0)
    {
        _propertyChangeListeners.firePropertyChange("Finished", null, null);
    }
} 

/**
 * Adds a property change listener.
 *
 * @param l The listener
 */
public void addPropertyChangeListener(PropertyChangeListener l) 
{
    _propertyChangeListeners.addPropertyChangeListener(l);
}

/**
 * Removes a property change listener.
 *
 * @param l The listener
 */
public void removePropertyChangeListener(PropertyChangeListener l) 
{
    _propertyChangeListeners.removePropertyChangeListener(l);
}

/**
 * The class One will handle the response of one request.
 */
class One implements Observer
{
    public void update(Observable obs, Object ov)
    {
        Vector message = new Vector(5);
        Pdu pdu = (Pdu) obs;
        message.addElement("One.update(): " + pdu.toString());
        if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
        {
            try
            {
                varbind [] vars = pdu.getResponseVarbinds();
                int l = vars.length;

                for (int i=0; i<l; i++)
                {
                    varbind var = vars[i];
                    message.addElement(var.toString());
                }
            }
            catch (uk.co.westhawk.snmp.stack.PduException exc)
            {
                message.addElement("One.update(): PduException " 
                    + exc.getMessage());
            }
        }
        else
        {
            message.addElement("One.update(): " + pdu.getErrorStatusString());
        }
        int sz = message.size();
        synchronized (_writer)
        {
            _writer.println("");
            for (int i=0; i<sz; i++)
            {
                String str = (String) message.elementAt(i);
                _writer.println(str);
            }
        }
        _nrRequests --;
        tellThemWeAreReady();
    }
} // end class One

/**
 * The class Walk will handle the MIB walk.
 */
class Walk implements Observer
{
    boolean _first = true;

    public Walk()
    {
        _first = true;
    }

    public void update(Observable obs, Object ov)
    {
        boolean finished = true;
        Pdu pdu = (Pdu) obs;
        Vector message = new Vector(5);
        if (_first == true)
        {
            message.addElement("");
            message.addElement("Walk.update(): " + pdu.toString());
            _first = false;
        }
        if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
        {
            try
            {
                boolean anyEndOfMibs = false;
                varbind [] vars = pdu.getResponseVarbinds();
                int l = vars.length;

                pdu = new GetNextPdu_vec(_context, l); 
                for (int i=0; i<l; i++)
                {
                    varbind var = vars[i];
                    message.addElement(var.toString());

                    AsnObject obj = var.getValue(); 
                    anyEndOfMibs = (anyEndOfMibs || 
                          obj.getRespType() == AsnObject.SNMP_VAR_ENDOFMIBVIEW);
                    pdu.addOid(var.getOid().toString());
                }

                if (l>0 && anyEndOfMibs == false)
                {
                    pdu.addObserver(this);
                    pdu.send();
                    finished = false;
                }
            }
            catch(java.io.IOException exc)
            {
                message.addElement("Walk.update(): " + pdu.toString());
                message.addElement("Walk.update(): IOException " + exc.getMessage());
            }
            catch (uk.co.westhawk.snmp.stack.PduException exc)
            {
                message.addElement("Walk.update(): " + pdu.toString());
                message.addElement("Walk.update(): PduException " + exc.getMessage());
            }
        }
        else
        {
            message.addElement("Walk.update(): " + pdu.toString());
            message.addElement("Walk.update(): " + pdu.getErrorStatusString());
        }
        int sz = message.size();
        synchronized (_writer)
        {
            for (int i=0; i<sz; i++)
            {
                String str = (String) message.elementAt(i);
                _writer.println(str);
            }
        }
        if (finished == true)
        {
            _nrRequests --;
            tellThemWeAreReady();
        }
    }
} // end class Walk

} // end class SnmpTarget


