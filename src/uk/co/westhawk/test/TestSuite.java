//
// NAME
//      $RCSfile: TestSuite.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.16 $
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
import uk.co.westhawk.snmp.stack.*;


/**
 * The class TestSuite performs all the tests according to
 * <code>testIPv4.xml</code>.
 * This class can be used as applet and application. If run as applet
 * the XML_FILE should be passed as applet parameter.
 *
 * @see SnmpTarget
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.16 $ $Date: 2007/10/17 11:13:57 $
 */
public class TestSuite extends Applet 
    implements PropertyChangeListener, Runnable
{
    private static final String     version_id =
        "@(#)$Id: TestSuite.java,v 1.16 2007/10/17 11:13:57 birgita Exp $ Copyright Westhawk Ltd";

    /** 
     * Name of the XML file. Can be overwritten in main and in the html
     * file.
     */
    public static String XML_FILE = "testIPv4.xml";

    public static final String DOC_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    public static final String DOC_TYPE =
        "<!DOCTYPE tests SYSTEM \"./test.dtd\">";

    public static final String TESTS = "tests";
    public static final String TEST = "test";

    private Vector _testList;
    private PrintWriter _writer;
    private boolean _testStarted;
    private boolean _testInFlight;

    private XMLtoDOM _xmlToDom;
    private SnmpTarget _target;
    private Thread _me;

    private int _testNo = 0;
    boolean _isStandAlone = false;


/**
 * The constructor.
 */
public TestSuite()
{
    //AsnObject.setDebug(15);
    AsnObject.setDebug(0);

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
                _writer.println("TestSuite.init():"
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
        _writer.println("TestSuite.init(): URISyntaxException"
            + exc.getMessage());
    }


    if (documentURI == null)
    {
        _writer.println("TestSuite.init():" 
            + " Cannot find " + XML_FILE);
    }
    else
    {
        _writer.println("TestSuite.init(): XML_FILE " + documentURI);
        // printUriDetails(documentURI);
        testDoc = _xmlToDom.getDocument(documentURI);
        if (testDoc != null)
        {
            analyseDocument(testDoc);
        }
        else
        {
            _writer.println("TestSuite.init(): Couldn't parse " + documentURI);
        }
    }
}


/**
 * Starts the applet. It starts the thread which will run the tests.
 * @see #run
 */
public void start()
{
    if (_testStarted == false && _testList != null && _testList.size() > 0)
    {
        _testStarted = true;
        _me = new Thread(this, "TestSuite");
        _me.setPriority(Thread.MIN_PRIORITY);
        _me.start();
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

    if (_isStandAlone == true)
    {
        System.exit(0);
    }
}

/**
 * Receives the property change event, indicating that the test has finished.
 */
public void propertyChange(PropertyChangeEvent evt)
{
    _testInFlight = false;
}

/**
 * Performs the next test. It takes the next node from the list and
 * passes it to the SnmpTarget to perform it.
 */
private void nextTest()
{
    _writer.println("\nTestSuite.nextTest(): Starting test " + _testNo);
    _testInFlight = true;

    Node testNode = (Node) _testList.elementAt(_testNo);
    _target.performTest(testNode);
    _testNo--;
}

/**
 * Builds a (node) list of all tests in the document.
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
                if (type == Node.ELEMENT_NODE && childName.equals(TEST))
                {
                    _testList.addElement(childNode);
                }
            }

            _testNo = _testList.size()-1;
            if (_testNo < 0)
            {
                _testNo = 0;
            }
        }
        else
        {
            _writer.println("Cannot find any node with name " + TESTS);
        }

    }
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
            TestSuite.XML_FILE = argv[0];
        }

        TestSuite testS = new TestSuite();
        testS._isStandAlone = true;
        JFrame frame = new JFrame("TestSuite Test");
        uk.co.westhawk.tablelayout.TableLayout tLayout =
              new uk.co.westhawk.tablelayout.TableLayout();

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
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
    System.err.println("\t TestSuite [<xml file>]");
}

}

