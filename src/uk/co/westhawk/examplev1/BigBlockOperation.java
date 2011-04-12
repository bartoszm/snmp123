// NAME
//      $RCSfile: BigBlockOperation.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.6 $
// CREATED
//      $Date: 2006/11/30 17:44:24 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//


/*
 * Copyright (C) 1998 - 2006 by Westhawk Ltd
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

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;

import java.awt.*; 
import java.awt.event.*;

import java.io.*;
import java.util.*;

/**
 * <p>
 * The class BigBlockOperation tests the performance of the BlockPdu and
 * SnmpContextPool by building a big (or long) SNMP request. 
 * </p>
 *
 * <p>
 * This class asks for the number of interfaces and builds a request
 * asking all the interfaces in one request.
 * </p>
 *
 * @see uk.co.westhawk.snmp.pdu.BlockPdu
 * @see uk.co.westhawk.snmp.stack.SnmpContextPool
 * @see uk.co.westhawk.snmp.beans.IsHostReachableBean
 * @see propertyDialog
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.6 $ $Date: 2006/11/30 17:44:24 $
 */
public class BigBlockOperation implements ActionListener
{
    private static final String     version_id =
        "@(#)$Id: BigBlockOperation.java,v 1.6 2006/11/30 17:44:24 birgit Exp $ Copyright Westhawk Ltd";

    public final static String ifNumber = "1.3.6.1.2.1.2.1.0";
    public final static String ifEntry  = "1.3.6.1.2.1.2.2.1";
    public final static int noIfEntry = 22;

    private SnmpContextPool context;
    private BlockPdu pdu;

    private propertyDialog propDialog;
    private Util util;

/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public BigBlockOperation(String propertiesFilename)
{
    //AsnObject.setDebug(7);
    util = new Util(propertiesFilename, this.getClass().getName());
}


public void init()
{
    try
    {
        createPropertyDialog();
        propDialog.setVisible(true);
        propDialog.toFront();

        /*
        */
        createContext(propDialog.getHost(), propDialog.getPort(), 
            propDialog.getCommunityName(), propDialog.getBindAddress(),
            propDialog.getSocketType());
        if (context != null)
        {
            sendGetRequest();
        }
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
    }
}

public void actionPerformed(ActionEvent evt)
{
    Object src = evt.getSource();

    try
    {
        if (src == propDialog)
        {
            propDialog_actionPerformed(evt);
        }
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
    }
}

void propDialog_actionPerformed(ActionEvent evt)
{
    String cmd = evt.getActionCommand();
    if (cmd.equals("Cancel") == false)
    {
        createContext(propDialog.getHost(), propDialog.getPort(), 
              propDialog.getCommunityName(), propDialog.getBindAddress(),
              propDialog.getSocketType());
        if (context != null)
        {
            sendGetRequest();
        }
    }
    else
    {
        System.exit(0);
    }
}

private void createContext(String host, String portStr, String comm,
    String bindAddr, String socketType)
{
    int port = SnmpContextBasisFace.DEFAULT_PORT;
    try
    {
        port = Integer.valueOf(portStr).intValue();
    }
    catch (NumberFormatException exc) { }

    if (context != null)
    {
        context.destroy();
        //context.dumpContexts("destroy");
    }
    try
    {
        context = new SnmpContextPool(host, port, bindAddr, socketType);
        context.setCommunity(comm);
        //context.dumpContexts("create:");
        System.out.println(context);
    }
    catch (java.io.IOException exc)
    {
        exc.printStackTrace();
        context = null;
    }
}


private void createPropertyDialog()
{
    propDialog = new propertyDialog(null);

    String host = util.getHost();
    String bindAddr = util.getBindAddress();
    String port = "" + util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    String socketType = util.getSocketType();
    String comm = util.getCommunity();

    if (host != null)
    {
        propDialog.setHost(host);
    }
    if (port != null)
    {
        propDialog.setPort(port);
    }
    if (comm != null)
    {
        propDialog.setCommunityName(comm);
    }
    if (socketType != null)
    {
        propDialog.setSocketType(socketType);
    }
    if (bindAddr != null)
    {
        propDialog.setBindAddress(bindAddr);
    }
    propDialog.addActionListener(this);
    propDialog.pack();
}

private void sendGetRequest()
{
    try
    {
        pdu = new BlockPdu(context);
        pdu.setPduType(BlockPdu.GET);
        pdu.addOid(ifNumber);
        AsnObject res = pdu.getResponseVariable();

        if (res != null)
        {
            int ifCount = ((AsnInteger)res).getValue();
            System.out.println("ifCount " + ifCount);

            pdu = new BlockPdu(context);
            pdu.setPduType(BlockPdu.GETNEXT);
            for (int no=0; no<ifCount; no++)
            {
                for (int i=1; i<=noIfEntry; i++)
                {
                    String oid = ifEntry + "." + i + "." + no;
                    pdu.addOid(oid);
                }
            }
            sendRequest(pdu);
        }
        else
        {
            System.out.println("Received no answer");
        }
    }
    catch (Exception exc)
    {
        exc.printStackTrace();
    }
}

private void sendRequest(BlockPdu pdu) throws PduException, IOException
{
    varbind [] var = pdu.getResponseVariableBindings();
    if (var != null)
    {
        int sz = var.length;
        System.out.println("Received answer " + sz);
        for (int i=0; i<sz; i++)
        {
            AsnObjectId oid = var[i].getOid();
            AsnObject res = var[i].getValue();
            System.out.println(oid.toString() + ": " + res.toString());
        }
    }
    else
    {
        System.out.println("Received no answer");
    }
}

public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    BigBlockOperation application = new BigBlockOperation(propFileName);
    application.init();
}


}
