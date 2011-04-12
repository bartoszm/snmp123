//
// NAME
//      $RCSfile: XMLtoDOM.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.8 $
// CREATED
//      $Date: 2006/01/17 17:43:54 $
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
                    
import java.io.*;
import java.util.*;
import java.net.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/**
 * The class XMLtoDOM creates a DOM (Document Object Model) from XML
 * input. This XML input is received via a URL.
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.8 $ $Date: 2006/01/17 17:43:54 $
 */
public class XMLtoDOM 
{
    private static final String     version_id =
        "@(#)$Id: XMLtoDOM.java,v 1.8 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";

    final static String WARNING       = "Warning";
    final static String ERROR         = "Error";
    final static String FATAL_ERROR   = "Fatal Error";

    private ErrorStorer ef;

    // The document say there is a
    // org.apache.xerces.parsers.RevalidatingDOMParser, but it isn't in
    // the jar file???
    //private DOMParser   parser;
    private DocumentBuilder _builder;
    private String      _uri;
    private PrintWriter _writer;

/**
 *  The constructor.
 */
public XMLtoDOM()
{
    DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
    _writer = new PrintWriter(new OutputStreamWriter(System.out), true);

    try
    {
        _builder = fac.newDocumentBuilder();
        ef = new ErrorStorer();
        _builder.setErrorHandler(ef);
    }
    catch (ParserConfigurationException exc)
    {
        _writer.println("XMLtoDOM.XMLtoDOM(): ParserConfigurationException " 
            + exc.getMessage());
    }
}

/**
 * Sets the writer to be used for all output.
 * @param writer The writer
 */
public void setWriter(PrintWriter writer)
{
    _writer = writer;
}

/**
 * Translates a URI into a DOM document. To get the data from the
 * servlet the 'get' method is used. 
 * The URI should have the following format:
 * <pre>
 *      http://host/servlet?k1=v1&amp;k2=v2&amp;k3=v3
 * </pre>
 * 
 * @param uri the URI 
 *
 * @return the DOM document
 */
public Document getDocument(URI uri) 
{
    Document doc = null;
    _uri = uri.toString();
    if (_uri != null) 
    {
        try 
        {
            ef.resetErrors();
            ef.setURI(_uri);
            doc = _builder.parse(uri.toString());
        } 
        catch (Exception exc) 
        {
            _writer.println("XMLtoDOM.getDocument(): " + _uri
                  + " " + exc.getMessage());
        }
    }
    return doc;
}

/**
 * Reads the answer from a URL via the HTTP GET method.
 * @return the input stream from the URL to read the answer
 */
private Reader readFromUrl (URL url)
{
    InputStreamReader inStream = null;

    try
    {
        URLConnection conn = url.openConnection();

        // No cache may be used, else we might get an old value !!
        conn.setUseCaches(false);

        // After long fiddling with Netscape, I found out that the
        // content-type has to be set, else the servlet somehow cannot
        // decode the parameters!!!!
        /*
        conn.setRequestProperty("Content-type",
                            "application/x-www-form-urlencoded");
        */

        // Since I only do a GET, I only need Input.
        conn.setDoInput(true);
        conn.setDoOutput(false);

        // Read the answer
        inStream = new InputStreamReader (conn.getInputStream());
    }
    catch (IOException exc)
    {
        _writer.println("XMLtoDOM.readFromUrl(): IOException: " +
        exc.getMessage());
    }
    catch (Exception exc)
    {
        _writer.println("XMLtoDOM.readFromUrl(): Exception: " + exc.getMessage());
        exc.printStackTrace(_writer);
    }
    return inStream;
}



/**
 * The ErrorStorer maps Nodes to errors. It receives a reference
 * to the ErrorTreeFactory in the Constructor.
 *
 * <p>
 * When error is called, it asks the
 * ErrorTreeFactory for the current node, and uses this as the
 * "key" of a Hashtable, with the error as a value. The error
 * value is wrapped up nicely in an ParseError object.
 * </p>
 *
 * <p>
 * It is used in the XML Tutorial to illustrate how to implement
 * the ErrorListener to provide error storage for later reference.
 * </p>
 */
class ErrorStorer implements ErrorHandler
{
    //
    // Data
    //
    Hashtable errorNodes = null;
    String _uri = null;

/**
 * Constructor
 */
public ErrorStorer()
{
}

protected boolean errorHandlingEnabled()
{
    return true;
}

/**
 * Sets the uri so it can be mentioned in any error message.
 */
void setURI(String uri)
{
    _uri = uri;
}

/**
 * The client is is allowed to get a reference to the Hashtable,
 * and so could corrupt it, or add to it...
 */
public Hashtable getErrorNodes()
{
    return errorNodes;
}

/**
 * The ParseError object for the node key is returned.
 * If the node doesn't have errors, null is returned.
 */
public Object getError(Node node)
{
    if (errorNodes == null)
        return null;
    return errorNodes.get(node);
}

/**
 * Reset the error storage.
 */
public void resetErrors()
{
    if (errorNodes != null)
        errorNodes.clear();
}

/**
 * Prints a warning message.
 */
public void warning(SAXParseException exc)
{
    handleError(exc, XMLtoDOM.WARNING);
}

/**
 * Prints a error message.
 */
public void error(SAXParseException exc)
{
    handleError(exc, XMLtoDOM.ERROR);
}

/**
 * Prints a fatal error message.
 */
public void fatalError(SAXParseException exc) throws SAXException
{
    handleError(exc, XMLtoDOM.FATAL_ERROR);
}

/**
 * Prints a message.
 */
protected void handleError(SAXParseException exc, String type)
{
    _writer.println("ErrorStorer.handleError(): ");

    String str = "\t" + type
        + " at line number " + exc.getLineNumber()
        + ": " + exc.getMessage()
        + " (" + _uri + ")";

    _writer.println(str);

}

} // end class ErrorStorer


} // end class XMLtoDOM

