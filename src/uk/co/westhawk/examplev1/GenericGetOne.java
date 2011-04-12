// NAME
//      $RCSfile: GenericGetOne.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.2 $
// CREATED
//      $Date: 2007/10/18 09:42:40 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1996 - 2006 by Westhawk Ltd
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

package uk.co.westhawk.examplev1;

import java.applet.*;
import java.util.*;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;

import netscape.javascript.JSObject;


/**
 * <p>
 * The headless GenericGetOne applet will use the GetNextPdu to request
 * a single MIB variable. The idea is to use this (signed!) applet in a
 * webpage via javascript (livescript).
 * If the applet isn't signed, you can only connect back to the web
 * server.
 * </p>
 *
 * <p>
 * The applet will call the javascript function loaded() in method
 * start() to indicate the applet is loaded. You have to define this
 * function, even when empty.
 * </p>
 *
 * <p>
 * In javascript, call the send() method, passing the name of the
 * callback function.
 * The applet will call this javascript function, when receiving the
 * response:</br/>
 * </p>
 * <pre>
 * function &lt;jsCallback&gt;(host, port, comm, requestOid, responseOid, value, error) 
 * {
 * }
 * </pre>
 *
 * <p>
 * See example html file TestGenericGetOne.html
 * </p>
 *
 * @see uk.co.westhawk.snmp.stack.GetNextPdu
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.2 $ $Date: 2007/10/18 09:42:40 $
 */
public class GenericGetOne extends Applet 
{
    private static final String     version_id =
        "@(#)$Id: GenericGetOne.java,v 1.2 2007/10/18 09:42:40 birgita Exp $ Copyright Westhawk Ltd";

    private List _pduList;

/**
 * Constructor.
 *
 */
public GenericGetOne()
{
    _pduList = new ArrayList();
}

/**
 * Send a GetNext request. The answer will be pushed back via
 * function 'jsCallback'.
 *
 * @param host  The host name or address
 * @param port  The port number of the host
 * @param comm  The community name
 * @param oid   The request oid 
 * @param jsCallback The javascript function to call when the response
 * comes in
 *
 * @see #pushValue
 */
public void send(String host, String port, String comm, String oid, String
jsCallback)
{
    if (jsCallback != null)
    {
        try
        {
            printDebug("GenericGetOne.send()");

            synchronized (_pduList)
            {
                int portNo = Integer.parseInt(port);
                PduCallback pduCb = new PduCallback(host, portNo, comm,
                    oid, jsCallback, this);
                _pduList.add(pduCb);

                /*
                 * This method will be called in javascript, which is
                 * not trusted (even though the jar itself is signed).
                 * For that reason we use the invokeLater Runnable, so
                 * the swing thread will 'transfer' this method from an
                 * untrusted to a trusted environment.
                 */
                javax.swing.SwingUtilities.invokeLater(pduCb);
            }
        }
        catch (Exception exc)
        {
            String error = "GenericGetOne.send(): " 
                + exc.getClass().getName()
                + " " + exc.getMessage();
            this.showStatus(error);
            printError(error);
        }
    }
    else
    {
        String error = "GenericGetOne.send(): error jsCallback is null";
        this.showStatus(error);
        printError(error);
    }
}

/**
 * Pushes the value back to javascript by calling setValue. The
 * function (with name passed to send()) has to exist in
 * javascript:<br/>
 * <pre>
 * function &lt;callback&gt;(host, port, comm, requestOid, responseOid, value, error) 
 * {
 * }
 * </pre>
 *
 * @param value The response value of the (latest) request.
 */
protected void pushValue(String host, int port, String comm, 
    String requestOid, String responseOid, String value, String error,
    String jsCallback)
{
    Object[] args = new Object[7];
    args[0] = host;
    args[1] = "" + port;
    args[2] = comm;
    args[3] = requestOid;
    args[4] = responseOid;
    args[5] = value;
    args[6] = error;
    Object ret = getWindow().call(jsCallback, args);
}

public void init () 
{
}

public void start()
{
    invokeLoaded();
}

public synchronized void stop() 
{
    Iterator iter = _pduList.iterator();
    while (iter.hasNext())
    {
        PduCallback pduCb = (PduCallback) iter.next();
        pduCb.destroy();
        iter.remove();
    }
}

protected JSObject getWindow() 
{
    return JSObject.getWindow(this);
}

protected void printError(String text)
{
    System.out.println(text);
}

protected void printDebug(String text)
{
    // System.out.println(text);
}

/**
 * Invokes function loaded() in the javascript. This is called in
 * start().
 *
 * @see #start()
 */
private void invokeLoaded() 
{
    printDebug("calling loaded");
    Object[] args = new Object[0];
    Object ret = this.getWindow().call("loaded", args);
}

protected void removeMe(PduCallback pduCb)
{
    _pduList.remove(pduCb);
}


class PduCallback implements Runnable, Observer
{
    private String _host;
    private int _port;
    private String _comm;
    private String _oid;
    private String _jsCallback;
    private Applet _applet;
    private SnmpContext _context = null;


    public PduCallback(String host, int port, String comm, 
        String oid, String jsCallback, Applet applet)
    {
        _host = host;
        _port = port;
        _comm = comm;
        _oid = oid;
        _jsCallback = jsCallback;
        _applet = applet;
    }

    public void run() 
    {
        String error = null;
        try
        {
            _context = new SnmpContext(_host, _port);
            _context.setCommunity(_comm);

            Pdu pdu = new GetNextPdu(_context);
            pdu.addOid(_oid);
            pdu.addObserver(this);
            pdu.send();
        }
        catch (Exception exc)
        {
            error = exc.getClass().getName() + ": " + exc.getMessage();
        }

        if (error != null)
        {
            String error2 = "GenericGetOne.run(): " + error;
            _applet.showStatus(error2);
            printError(error2);

            finish(null, null, error);
        }
    }

    /**
     * Implementing the Observer interface. Receiving the response from 
     * the Pdu.
     *
     * @param obs the GetNextPdu variable
     * @param ov the varbind
     *
     * @see uk.co.westhawk.snmp.stack.GetNextPdu
     * @see uk.co.westhawk.snmp.stack.varbind
     */
    public void update(Observable obs, Object ov)
    {
        Pdu pdu = (Pdu) obs;

        String responseOid = null;
        String value = null;
        String error = null;

        if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
        {
            try
            {
                varbind[] respVars = pdu.getResponseVarbinds();
                varbind var = respVars[0];
                if (var != null)
                {
                    responseOid = var.getOid().toString();
                    value = var.getValue().toString();
                }
            }
            catch(uk.co.westhawk.snmp.stack.PduException exc)
            {
                error = "PduException " + exc.getMessage();
                _applet.showStatus("GenericGetOne.update(): " + error);
            }
        }
        else
        {
            error = pdu.getErrorStatusString();
            _applet.showStatus("GenericGetOne.update(): " + error);
        }

        finish(responseOid, value, error);
    }

    public void destroy()
    {
        if (_context != null && _context.isDestroyed() == false)
        {
            _context.destroy();
            _context = null;
        }
    }

    public void finish(String responseOid, String value, String error)
    {
        pushValue(_host, _port, _comm, _oid, responseOid, value, error,
            _jsCallback);

        this.destroy();
        removeMe(this);
    }
}

}
