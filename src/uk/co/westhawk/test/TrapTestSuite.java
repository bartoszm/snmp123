//
// NAME
//      $RCSfile: TrapTestSuite.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.15 $
// CREATED
//      $Date: 2007/10/17 11:13:57 $
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
import java.beans.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

import org.w3c.dom.*;
import uk.co.westhawk.snmp.event.*;
import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;


/**
 * The class TrapTestSuite tests the trap functionality of this stack.
 * It performs all the tests according to <code>trap.xml</code>.
 * This class can be used as applet and application. If run as applet
 * the XML_FILE should be passed as applet parameter.
 *
 * <p>
 * I've configured all the SNMP agents involved so they send a trap
 * when there is an authentication failure (like a wrong community name). 
 * The xml file lists all the agents I'm testing against. 
 * I'm going to send every one of these agents a request with a wrong 
 * authentication, and then see if I can handle the trap.
 * </p>
 *
 * <p>
 * On Unix and Linux systems this applet or application has to run as
 * <em>root</em>.
 * </p>
 *
 * @see SnmpTarget
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.15 $ $Date: 2007/10/17 11:13:57 $
 */
public class TrapTestSuite extends Applet 
    implements PropertyChangeListener, Runnable, RawPduListener, 
    TrapListener, WindowListener
{
    private static final String     version_id =
        "@(#)$Id: TrapTestSuite.java,v 1.15 2007/10/17 11:13:57 birgita Exp $ Copyright Westhawk Ltd";

    /** 
     * Name of the XML file. Can be overwritten in main and in the html
     * file.
     */
    public static String XML_FILE = "trapIPv4.xml";

    public static final String DOC_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    public static final String DOC_TYPE =
        "<!DOCTYPE tests SYSTEM \"./trap.dtd\">";

    public static final String TRAPS = "traps";
    public static final String TEST = "test";
    public static final String DEFAULT = "default";

    public final static String sysUpTime = "1.3.6.1.2.1.1.3";

    private int [] zero_retry = {500};

    private Node _defaultNode;
    private Vector _contextList;
    private Vector _testList;
    private PrintWriter _writer;
    private boolean _testStarted;
    private boolean _testInFlight;

    private XMLtoDOM _xmlToDom;
    private SnmpTarget _target;
    private Thread _me;
    private ListeningContextPool _trapContext = null;

    private int _testNo = 0;
    boolean _isStandAlone = false;


/**
 * The constructor.
 */
public TrapTestSuite()
{
    //AsnObject.setDebug(6);
    //AsnObject.setDebug(15);

    _writer = new PrintWriter(new OutputStreamWriter(System.out), true);
    _testStarted = false;
    _testInFlight = false;
    _xmlToDom = new XMLtoDOM();
    _xmlToDom.setWriter(_writer);

    _target = new SnmpTarget();
    _target.setWriter(_writer);
    _target.addPropertyChangeListener(this);
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
                _writer.println("TrapTestSuite.init():"
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
        _writer.println("TrapTestSuite.init(): URISyntaxException"
            + exc.getMessage());
    }


    if (documentURI == null)
    {
        _writer.println("TrapTestSuite.init():" 
            + " Cannot find " + XML_FILE);
    }
    else
    {
        _writer.println("TrapTestSuite.init(): XML_FILE " + documentURI);
        // printUriDetails(documentURI);
        testDoc = _xmlToDom.getDocument(documentURI);
        if (testDoc != null)
        {
            analyseDocument(testDoc);
        }
        else
        {
            _writer.println("TrapTestSuite.init(): Couldn't parse " + documentURI);
        }
    }
}



/**
 * Starts the applet. All the contexts are created first. Next the
 * thread which will run the tests is created.
 * @see #run
 */
public void start()
{
    if (_testStarted == false && _defaultNode != null 
            && 
        _testList != null && _testList.size() > 0)
    {
        _testStarted = true;

        try
        {
            _trapContext = _target.createListeningContext(_defaultNode);
            _trapContext.addUnhandledRawPduListener(this);

            int sz = _testList.size();
            _contextList = new Vector(sz);
            for (int i=0; i<sz; i++)
            {
                Node testNode = (Node) _testList.elementAt(i);
                SnmpContextBasisFace context = _target.createContext(testNode);
                _contextList.addElement(context);
                context.addTrapListener(this);
            }
            
            _testNo = _contextList.size()-1;
            if (_testNo < 0)
            {
                _testNo = 0;
            }

            _me = new Thread(this, "TrapTestSuite");
            _me.setPriority(Thread.MIN_PRIORITY);
            _me.start();
        }
        catch (IOException exc)
        {
            // the xml file should have the proper settings ..
            _writer.println("TrapTestSuite.start(): IOException " 
                + exc.getMessage());
            exc.printStackTrace(_writer);
        }
    }
}

/**
 * Runs the test. It launches one test, waits for it to be finished and
 * starts the next one.
 */
public void run()
{
    while (_testNo >= 0)
    {
        if (_testInFlight == false)
        {
            nextTest();
        }
        try
        {
            _me.sleep(2000);
        }
        catch (InterruptedException exc) {}
    }

    _writer.println("waiting for the last test to finish ..");
    while (_testInFlight == true)
    {
        try
        {
            _me.sleep(5000);
        }
        catch (InterruptedException exc) {}
    }

    _writer.println("** Finished all Tests. **");
}


public void freeResources()
{
    if (_testStarted == true)
    {
        if (_trapContext != null)
        {
            _trapContext.destroyPool();
        }

        int sz = _contextList.size();
        for (int i=0; i<sz; i++)
        {
            SnmpContextBasisFace context = 
                  (SnmpContextBasisFace) _contextList.elementAt(i);
            context.destroy();
        }
        _testStarted = false;
    }
}


/**
 * Receives a trap event.
 */
public void trapReceived(TrapEvent evt)
{
    Pdu pdu = evt.getPdu();
    _writer.println("\ntrapReceived():" 
        + " received decoded trap " + pdu.toString());
}

/**
 * Receives a rawpdu event.
 */
public void rawPduReceived(RawPduEvent evt)
{
    int version = evt.getVersion(); 
    String host = evt.getHostAddress(); 
    _writer.println("\nrawPduReceived():"
        + " received unhandled undecoded pdu v " + version
        + " from host " + host);
}

/**
 * Receives the property change event, indicating that the test has finished.
 */
public void propertyChange(PropertyChangeEvent evt)
{
    _testInFlight = false;
}

/**
 * Send a request with a wrong authentication to force a trap.
 */
private void nextTest()
{
    _writer.println("\nTrapTestSuite.nextTest(): Starting test " + _testNo);
    _testInFlight = true;

    SnmpContextBasisFace wrongContext = null;
    SnmpContextBasisFace context = (SnmpContextBasisFace) _contextList.elementAt(_testNo);
    try
    {
        if (context instanceof SnmpContext)
        {
            SnmpContext c = (SnmpContext) ((SnmpContext)context).clone();
            String com = c.getCommunity();
            com += "_bla";
            c.setCommunity(com);
            wrongContext = c;
        }
        else if (context instanceof SnmpContextv2c)
        {
            SnmpContextv2c c = (SnmpContextv2c) ((SnmpContextv2c)context).clone();
            String com = c.getCommunity();
            com += "_bla";
            c.setCommunity(com);
            wrongContext = c;
        }
        else if (context instanceof SnmpContextv3)
        {
            SnmpContextv3 c = (SnmpContextv3) ((SnmpContextv3)context).clone();
            String name = c.getContextName();
            name += "_bla";
            c.setContextName(name);
            wrongContext = c;
        }

        _target.performGetNextRequest(wrongContext, sysUpTime, zero_retry);
    }
    catch (CloneNotSupportedException exc)
    {
        _writer.println("TrapTestSuite.nextTest(): CloneNotSupportedException " 
            + exc.getMessage());
    }
    _testNo--;
}

/**
 * Builds a list of all tests in the document.
 * The tests itself are performed later.
 *
 * @param doc The DOM document.
 */
public void analyseDocument(Document doc)
{
    if (doc != null)
    {
        Node testsNode = Util.getTopElementNode(doc);
        if (testsNode != null)
        {
            NodeList childNodes = testsNode.getChildNodes();
            int l = childNodes.getLength();

            _testList = new Vector(l);
            for (int i=0; i<l; i++)
            {
                Node childNode = childNodes.item(i);
                short type = childNode.getNodeType();
                String childName = childNode.getNodeName();
                if (type == Node.ELEMENT_NODE)
                {
                    if (childName.equals(TEST))
                    {
                        _testList.addElement(childNode);
                    }
                    else if (childName.equals(DEFAULT))
                    {
                        _defaultNode = childNode;
                    }
                }
            }

        }
        else
        {
            _writer.println("Cannot find any node with name " + TRAPS);
        }

    }
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
            TrapTestSuite.XML_FILE = argv[0];
        }

        TrapTestSuite testS = new TrapTestSuite();
        testS._isStandAlone = true;
        JFrame frame = new JFrame("TrapTestSuite Test");
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

/**
 * Prints the usage of this application.
 */
public static void usage()
{
    System.err.println("Usage:");
    System.err.println("\t TrapTestSuite [<xml file>]");
}

}

