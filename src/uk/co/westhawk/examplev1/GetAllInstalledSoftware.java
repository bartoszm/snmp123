// NAME
//      $RCSfile: GetAllInstalledSoftware.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.4 $
// CREATED
//      $Date: 2006/03/23 14:43:43 $
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
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * <p>
 * The GetAllInstalledSoftware application requests the information 
 * of all the currently installed software of a host, using the
 * InstalledSoftwarePdu.
 * This is written to test the DateAndTime functionality. 
 * </p>
 *
 * <p>
 * It walks the tree by creating a new InstalledSoftwarePdu out off the
 * previous one, and it collects the values of all the interfaces. 
 * </p>
 *
 * <p>
 * The information will be printed to System.out .
 * </p>
 *
 * <p>
 * The host, port, community name and sockettype can be configured in the 
 * properties file. 
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>GetAllInstalledSoftware.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.4 $ $Date: 2006/03/23 14:43:43 $
 */
public class GetAllInstalledSoftware implements Observer 
{
    private static final String     version_id =
        "@(#)$Id: GetAllInstalledSoftware.java,v 1.4 2006/03/23 14:43:43 birgit Exp $ Copyright Westhawk Ltd";

    private InstalledSoftwarePdu _pdu;
    private String _host;
    private int _port;
    private SnmpContext _context;
    private Util _util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public GetAllInstalledSoftware(String propertiesFilename)
{
    _util = new Util(propertiesFilename, this.getClass().getName());
}

public void init ()
{
    //AsnObject.setDebug(15);
    _host = _util.getHost();
    String bindAddr = _util.getBindAddress();
    _port = _util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    String socketType = _util.getSocketType();
    String community = _util.getCommunity();

    try
    {
        _context = new SnmpContext(_host, _port, bindAddr, socketType);
        _context.setCommunity(community);
    }
    catch (IOException exc)
    {
        System.out.println("IOException: " + exc.getMessage());
        System.exit(0);
    }
}

public void start()
{
    _pdu = new InstalledSoftwarePdu(_context);
    _pdu.addObserver(this);
    _pdu.addOids(null);

    try
    {
        _pdu.send();
    }
    catch(java.io.IOException exc)
    {
        System.out.println("run(): IOException " + exc.getMessage());
    }
    catch(uk.co.westhawk.snmp.stack.PduException exc)
    {
        System.out.println("run(): PduException " + exc.getMessage());
    }
}


/**
 * Implementing the Observer interface. Receiving the response from 
 * the Pdu.
 *
 * @param obs the InstalledSoftwarePdu variable
 * @param ov the array of varbind (not used)
 *
 * @see InstalledSoftwarePdu
 * @see uk.co.westhawk.snmp.stack.varbind
 */
public void update(Observable obs, Object ov)
{
    InstalledSoftwarePdu prev;
    String hashKey;

    if (_pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        prev = _pdu;
        System.out.println(_pdu.toString());

        _pdu = new InstalledSoftwarePdu(_context);
        _pdu.addObserver(this);
        _pdu.addOids(prev);

        try
        {
            _pdu.send();
        }
        catch(java.io.IOException exc)
        {
            System.out.println("update(): IOException " + exc.getMessage());
        }
        catch(uk.co.westhawk.snmp.stack.PduException exc)
        {
            System.out.println("update(): PduException " + exc.getMessage());
        }
    }
    else
    {
        System.out.println("update(): " + _pdu.getErrorStatusString());
        System.exit(0);
    }
}
    


/**
 * Main. To use a properties file different from 
 * <code>GetAllInstalledSoftware.properties</code>, pass the name as first argument.
 */
public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    GetAllInstalledSoftware application = new GetAllInstalledSoftware(propFileName);
    application.init();
    application.start();
}

}
