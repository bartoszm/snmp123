//
// NAME
//      $RCSfile: Agent.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.5 $
// CREATED
//      $Date: 2006/11/29 16:23:35 $
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

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

import org.w3c.dom.*;
import uk.co.westhawk.snmp.event.*;
import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.util.*;


/**
 * The goal of the test is to see if the stack receives incoming
 * PDUs properly and can send a reply to a request.
 *
 * The class Agent performs all the tests according to
 * <code>agentIPv4.xml</code>.
 * The xml file defines the configuration of the managers. 
 * The code will create a ListeningContext for each of the 'managers'. 
 * 
 * To keep live simple, it will only send a reply to a GetRequest with
 * a single OID, that is configured in the xml. The reply will be a
 * String as well.
 *
 *
 * This class can be used as applet and application. If run as applet
 * the XML_FILE should be passed as applet parameter.
 *
 * @see SnmpTarget
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.5 $ $Date: 2006/11/29 16:23:35 $
 */
public class Agent extends Applet 
    implements RawPduListener, RequestPduListener, TrapListener,
    UsmAgent, WindowListener
{
    private static final String     version_id =
        "@(#)$Id: Agent.java,v 1.5 2006/11/29 16:23:35 birgit Exp $ Copyright Westhawk Ltd";

    /** 
     * Name of the XML file. Can be overwritten in main and in the html
     * file.
     */
    public static String XML_FILE = "agentIPv4.xml";

    public static final String DOC_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    public static final String DOC_TYPE =
        "<!DOCTYPE agent SYSTEM \"./agent.dtd\">";

    public static final String AGENT = "agent";
    public static final String USM = "usm";
    public static final String ENGINEID = "engineID";
    public static final String ENGINEBOOTS = "engineBoots";
    public static final String NODES = "nodes";
    public static final String SCALAR = "scalar";
    public static final String VALUE = "value";
    public static final String NAME = "name";
    public static final String OID = "oid";
    public static final String TEST = "test";

    private Hashtable _scalarHash;
    private Vector _lcontextList;
    private Vector _scontextList;
    private Vector _testList;

    private String _engineID;
    private int _engineBoots;
    private long _startTime;

    private PrintWriter _writer;
    private boolean _testStarted;

    private XMLtoDOM _xmlToDom;
    private SnmpTarget _target;

    boolean _isStandAlone = false;

    private SnmpContextPool _poolv1;
    private SnmpContextv2cPool _poolv2c;
    private SnmpContextv3Pool _poolv3;

/**
 * The constructor.
 */
public Agent()
{
    //AsnObject.setDebug(15);
    AsnObject.setDebug(0);

    _writer = new PrintWriter(new OutputStreamWriter(System.out), true);
    _testStarted = false;
    _xmlToDom = new XMLtoDOM();
    _xmlToDom.setWriter(_writer);

    _target = new SnmpTarget();
    _target.setWriter(_writer);

    _startTime = (new Date()).getTime();
}

/**
 * Initialises the applet. It looks for the XML_FILE, reads it and
 * analyses it.
 *
 * @see #analyseDocument
 */
public void init()
{
    AppletContext context = null;
    URI documentURI = null;
    Document testDoc = null;
    try
    {
        try
        {
            context = this.getAppletContext();
            // Make sure I've got Unix slashes
            String xml_file = this.getParameter("XML_FILE").replace('\\', '/');
            if (xml_file != null)
            {
                documentURI = new URI(xml_file);
            }
            else
            {
                _writer.println("Agent.init():"
                    + " Missing parameter XML_FILE");
            }
        }
        catch (NullPointerException exc)
        {
            // we are in an application
            // Make sure I've got Unix slashes
            String xml_file = XML_FILE.replace('\\', '/');
            documentURI = new URI(xml_file);
        }

    }
    catch (URISyntaxException exc) 
    { 
        _writer.println("Agent.init(): URISyntaxException"
            + exc.getMessage());
    }


    if (documentURI == null)
    {
        _writer.println("Agent.init():" 
            + " Cannot find " + XML_FILE);
    }
    else
    {
        _writer.println("Agent.init(): XML_FILE " + documentURI);
        // printUriDetails(documentURI);
        testDoc = _xmlToDom.getDocument(documentURI);
        if (testDoc != null)
        {
            analyseDocument(testDoc);
            saveDocument(documentURI, testDoc);
        }
        else
        {
            _writer.println("Agent.init(): Couldn't parse " + documentURI);
        }
    }
}

/**
 * Builds a (node) list of all tests in the document.
 * The tests itself are performed later.
 *
 * @param doc The DOM document.
 */
public void analyseDocument(Document doc)
{
    int l;
    if (doc != null)
    {
        Element agentNode = Util.getTopElementNode(doc);
        if (agentNode != null)
        {
            NodeList engineIDNodes = agentNode.getElementsByTagName(ENGINEID);
            NodeList engineBootsNodes = agentNode.getElementsByTagName(ENGINEBOOTS);

            Node engineIDNode = engineIDNodes.item(0).getFirstChild();
            Node engineBootsNode = engineBootsNodes.item(0).getFirstChild();

            _engineID = engineIDNode.getNodeValue();
            _engineBoots = Integer.parseInt(engineBootsNode.getNodeValue());

            // increment _engineBoots so document can be saved 
            engineBootsNode.setNodeValue("" + (_engineBoots + 1));

            NodeList scalarNodes = agentNode.getElementsByTagName(SCALAR);
            l = scalarNodes.getLength();
            _scalarHash = new Hashtable(l);
            for (int i=0; i<l; i++)
            {
                Node childNode = scalarNodes.item(i);
                short type = childNode.getNodeType();
                String childName = childNode.getNodeName();
                if (type == Node.ELEMENT_NODE && childName.equals(SCALAR))
                {
                    Element element = (Element) childNode;
                    ScalarNode sNode = new ScalarNode(element);
                    _scalarHash.put(sNode._oid, sNode);
                }
            }


            NodeList testNodes = agentNode.getElementsByTagName(TEST);
            l = testNodes.getLength();

            _testList = new Vector(l);
            for (int i=0; i<l; i++)
            {
                Node childNode = testNodes.item(i);
                short type = childNode.getNodeType();
                String childName = childNode.getNodeName();

                if (type == Node.ELEMENT_NODE && childName.equals(TEST))
                {
                    _testList.addElement(childNode);
                }
            }
        }
        else
        {
            _writer.println("Cannot find any node with name " + AGENT);
        }

    }
}


public void saveDocument(URI uri, Document doc)
{
    // check if we can turn the document into a file and write to
    // it.
    boolean success = false;
    String feedback = "";

    String scheme = uri.getScheme();
    if (scheme.startsWith("file"))
    {
        File xmlfileFile = new File(uri);
        if (xmlfileFile != null)
        {
            if (xmlfileFile.canWrite())
            {
                try
                {
                    FileWriter fwriter = new FileWriter(xmlfileFile);
                    PrintWriter pwriter = new PrintWriter (fwriter, true);
                    DOMWriter domWriter = new DOMWriter(true, DOC_HEADER, DOC_TYPE);
                    domWriter.print(doc, pwriter);
                    success = true;
                }
                catch (IOException exc)
                {
                    feedback = "IOException: " + exc.getMessage();
                }
            }
            else
            {
                feedback = "Cannot write to " + uri.toString();
            }
        }
        else
        {
            feedback = "getRawPath() of URI (" + uri + ") is null";
        }
    }
    else
    {
        feedback = "URI scheme is not file, but " + scheme;
    }

    if (success == false)
    {
        _writer.println("Agent.saveDocument(): " + feedback);
    }
}


/**
 * Starts the applet. It starts the thread which will run the tests.
 */
public void start()
{
    if (_testStarted == false
            && 
        _testList != null && _testList.size() > 0)
    {
        _testStarted = true;

        try
        {
            int sz = _testList.size();
            _lcontextList = new Vector(sz);
            _scontextList = new Vector(sz);
            for (int i=0; i<sz; i++)
            {
                Node testNode = (Node) _testList.elementAt(i);

                ListeningContextPool lcontext = _target.createListeningContext(testNode);
                _lcontextList.addElement(lcontext);
                lcontext.addUnhandledRawPduListener(this);
                lcontext.addRawPduListener(this);


                SnmpContextBasisFace scontext = _target.createContext(testNode);
                _scontextList.addElement(scontext);
                if (scontext.getVersion() == SnmpConstants.SNMP_VERSION_3)
                {
                    SnmpContextv3Basis s3context = (SnmpContextv3Basis) scontext;
                    s3context.setUsmAgent(this);
                }
                scontext.addRequestPduListener(this, scontext.getPort());
                scontext.addTrapListener(this);
            }
        }
        catch (IOException exc)
        {
            // the xml file should have the proper settings ..
            _writer.println("Agent.start(): IOException " 
                + exc.getMessage());
            exc.printStackTrace(_writer);
        }
    }
}


public void trapReceived(TrapEvent evt)
{
    _writer.println(getClass().getName() + ".trapReceived():");

    int port = evt.getHostPort();
    Pdu trapPdu = evt.getPdu();
    int reqId = trapPdu.getReqId();
    SnmpContextBasisFace rcontext = trapPdu.getContext();
    int version = rcontext.getVersion();
    String host = rcontext.getHost();

    _writer.println("\ttrap id " + reqId 
        + ", v " + SnmpUtilities.getSnmpVersionString(version)
        + " from host " + host
        + ", sent from port " + port);
    _writer.println("\ttrap " + trapPdu.toString());
}



public void requestPduReceived(RequestPduEvent evt)
{
    Pdu receivedPdu = evt.getPdu();
    _writer.println("\nrequestPduReceived():" 
        + " received decoded request " + receivedPdu.toString());

    // test sending a response back
    if (receivedPdu.getMsgType() == SnmpConstants.GET_REQ_MSG)
    {
        varbind[] varbinds = receivedPdu.getRequestVarbinds();
        if (varbinds != null && varbinds.length == 1)
        {
            varbind var = varbinds[0];
            AsnObjectId oid = var.getOid();
            ScalarNode sNode = (ScalarNode) _scalarHash.get(oid.toString());
            if (sNode != null)
            {
                tryToReply(evt, sNode);
            }
        }
    }
}

public void tryToReply(RequestPduEvent evt, ScalarNode sNode)
{
    Pdu receivedPdu = evt.getPdu();
    try
    {
        SnmpContextBasisFace rcnt = createReplyContext(evt);

        Pdu respPdu = new ResponsePdu(rcnt, receivedPdu);
        respPdu.addOid(sNode._oid, new AsnOctets(sNode._value));
        _writer.println("\tsending response: " + respPdu.toString());
        respPdu.send();
    }
    catch(PduException pexc)
    {
        _writer.println("Agent.tryToReply(): PduException " 
            + pexc.getMessage());
        pexc.printStackTrace(_writer);
    }
    catch(java.io.IOException iexc)
    {
        _writer.println("Agent.tryToReply(): IOException " 
            + iexc.getMessage());
        iexc.printStackTrace(_writer);
    }
}

public SnmpContextBasisFace createReplyContext(RequestPduEvent evt) throws IOException
{
    SnmpContextBasisFace newContext = null;
    int port = evt.getHostPort();
    Pdu pdu = evt.getPdu();
    SnmpContextBasisFace rcontext = pdu.getContext();
    int version = rcontext.getVersion();
    String host = rcontext.getHost();
    String typeSocket = rcontext.getTypeSocket();

    if (version == SnmpConstants.SNMP_VERSION_2c)
    {
        SnmpContextv2c rcontextv2c = (SnmpContextv2c) rcontext;
        _poolv2c = new SnmpContextv2cPool(host, port, rcontextv2c.getCommunity(), typeSocket);
        newContext = _poolv2c;
    }
    else if (version == SnmpConstants.SNMP_VERSION_3)
    {
        SnmpContextv3 rcontextv3 = (SnmpContextv3) rcontext;
        _poolv3 = new SnmpContextv3Pool(host, port, typeSocket);
        rcontextv3.cloneParameters(_poolv3);
        newContext = _poolv3;
    }
    else
    {
        SnmpContext rcontextv1 = (SnmpContext) rcontext;
        _poolv1 = new SnmpContextPool(host, port, rcontextv1.getCommunity(), typeSocket);
        newContext = _poolv1;
    }
    return newContext;
}


public void freeResources()
{
    if (_testStarted == true)
    {
        int len1 = _lcontextList.size();
        for (int i=len1-1; i>=0; i--)
        {
            ListeningContextPool lcontext = (ListeningContextPool) _lcontextList.remove(i);
            lcontext.removeUnhandledRawPduListener(this);
            lcontext.removeRawPduListener(this);
        }

        try
        {
            int len2 = _scontextList.size();
            for (int i=len2-1; i>=0; i--)
            {
                SnmpContextBasisFace scontext = (SnmpContextBasisFace) _scontextList.remove(i);
                scontext.removeRequestPduListener(this, scontext.getPort());
            }
        }
        catch (IOException exc)
        {
            // the xml file should have the proper settings ..
            _writer.println("Agent.freeResources(): IOException " 
                + exc.getMessage());
            exc.printStackTrace(_writer);
        }

        if (_poolv1 != null)
        {
            _poolv1.destroyPool();
        }
        if (_poolv2c != null)
        {
            _poolv2c.destroyPool();
        }
        if (_poolv3 != null)
        {
            _poolv3.destroyPool();
        }

        _testStarted = false;
    }
}


public void rawPduReceived(RawPduEvent evt)
{
    int version = evt.getVersion(); 
    String host = evt.getHostAddress(); 
    _writer.println("\nrawPduReceived():"
        + " received unhandled or raw undecoded pdu v" + version
        + " from host " + host);
}


public java.lang.String getSnmpEngineId()
{
    return _engineID;
}
public int getSnmpEngineBoots()
{
    return _engineBoots;
}
public int getSnmpEngineTime()
{
    long thisTime = (new Date()).getTime();
    long deltaMillis = thisTime - _startTime;
    int seconds = (int) (deltaMillis / 1000);
    return seconds;
}
public long getUsmStatsUnknownEngineIDs()
{
    return 0;
}
public long getUsmStatsNotInTimeWindows()
{
    return 0;
}
public void setSnmpContext(SnmpContextv3Basis context)
{
}

public void windowOpened(WindowEvent e) {}
public void windowClosed(WindowEvent e) {}
public void windowIconified(WindowEvent e) {}
public void windowDeiconified(WindowEvent e) {}
public void windowActivated(WindowEvent e) {}
public void windowDeactivated(WindowEvent e) {}

public void windowClosing(WindowEvent e) 
{
    this.freeResources();
    System.exit(0);
}


/**
 * The main method in order to run this as an application.
 */
public static void main(String[] argv)
{
    try
    {
        int len = argv.length;
        if (len > 0)
        {
            Agent.XML_FILE = argv[0];
        }

        Agent testS = new Agent();
        testS._isStandAlone = true;
        JFrame frame = new JFrame("Agent Test");
        uk.co.westhawk.tablelayout.TableLayout tLayout =
              new uk.co.westhawk.tablelayout.TableLayout();

        frame.addWindowListener(testS);

        java.awt.Dimension dim = new java.awt.Dimension(500, 150);
        frame.setSize(dim);
        frame.setLocation(50, 50);
        frame.getContentPane().setLayout(tLayout);
        frame.setVisible(true);

        testS.init();
        testS.start();
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
        usage();
    }
}

public void printUriDetails(URI uri)
{
    _writer.println("uri.getScheme(): " + uri.getScheme());
    _writer.println("uri.getSchemeSpecificPart(): " + uri.getSchemeSpecificPart());
    _writer.println("uri.getRawSchemeSpecificPart(): " + uri.getRawSchemeSpecificPart());

    _writer.println("uri.getAuthority(): " + uri.getAuthority());
    _writer.println("uri.getRawAuthority(): " + uri.getRawAuthority());
    _writer.println("uri.getUserInfo() : " + uri.getUserInfo() );
    _writer.println("uri.getRawUserInfo(): " + uri.getRawUserInfo());

    _writer.println("uri.getHost(): " + uri.getHost());
    _writer.println("uri.getPort(): " + uri.getPort());
    _writer.println("uri.getPath(): " + uri.getPath());
    _writer.println("uri.getRawPath(): " + uri.getRawPath());

    _writer.println("uri.getQuery(): " + uri.getQuery());
    _writer.println("uri.getRawQuery(): " + uri.getRawQuery());
    _writer.println("uri.getFragment(): " + uri.getFragment());
    _writer.println("uri.getRawFragment(): " + uri.getRawFragment());
}


/**
 * Prints the usage of this application.
 */
public static void usage()
{
    System.err.println("Usage:");
    System.err.println("\t Agent [<xml file>]");
}


class ScalarNode
{
    String _name, _oid, _value;

    public ScalarNode(Element element)
    {
        _name = element.getAttribute(NAME);
        _oid = element.getAttribute(OID);
        _value = Util.getCDataValue(element, VALUE);
    }

    public ScalarNode(String name, String oid, String value)
    {
        _name = name;
        _oid = oid;
        _value = value;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(getClass().getName());
        buffer.append("[");
        buffer.append("name=").append(_name);
        buffer.append(", oid=").append(_oid);
        buffer.append(", value=").append(_value);
        buffer.append("]");
        return buffer.toString();
    }
}

}

