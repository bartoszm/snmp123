// NAME
//      $RCSfile: TestInetCidrRouteTablePdu.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.3 $
// CREATED
//      $Date: 2006/03/23 14:46:47 $
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
import uk.co.westhawk.snmp.util.*;

import java.awt.*; 
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * <p>
 * This class is written to test the IPv6 functionality; net-snmp 5.3.0.1
 * provides experimental implementation for this table.
 * The inetCidrRouteTable replaces the IPv4-specific ipCidrRouteTable,
 * its related objects, and related conformance statements.
 * See <a
 * href="http://www.ietf.org/internet-drafts/draft-ietf-ipv6-rfc2096-update-07.txt">IP-FORWARD-MIB</a>.
 * </p>
 *
 * <p>
 * It walks the tree by creating a new InetCidrRouteTablePdu out off the
 * previous one, and it collects the values of all the addresses. 
 * It also figures out the IPv6 address from the oid.
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
 * <code>TestInetCidrRouteTablePdu.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.3 $ $Date: 2006/03/23 14:46:47 $
 */
public class TestInetCidrRouteTablePdu implements Observer 
{
    private static final String     version_id =
        "@(#)$Id: TestInetCidrRouteTablePdu.java,v 1.3 2006/03/23 14:46:47 birgit Exp $ Copyright Westhawk Ltd";

    private InetCidrRouteTablePdu _pdu;
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
public TestInetCidrRouteTablePdu(String propertiesFilename)
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
    _pdu = new InetCidrRouteTablePdu(_context);
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
 * the Pdu. Figures out the IPv6 address from the oid.
 *
 * @param obs the InetCidrRouteTablePdu variable
 * @param ov the array of varbind (not used)
 *
 * @see InetCidrRouteTablePdu
 * @see uk.co.westhawk.snmp.stack.varbind
 */
public void update(Observable obs, Object ov)
{
    InetCidrRouteTablePdu prev;
    String hashKey;

    if (_pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        prev = _pdu;
        System.out.println("\n" + this.getClass().getName() + ".update:");

        /* This table is indexed on
         INDEX {
              inetCidrRouteDestType,
              inetCidrRouteDest,
              inetCidrRoutePfxLen,
              inetCidrRoutePolicy,
              inetCidrRouteNextHopType,
              inetCidrRouteNextHop
              }
         */

        AsnObjectId inetCidrRouteIfIndexObj = 
                    new AsnObjectId(InetCidrRouteTablePdu.inetCidrRouteIfIndex_OID);
        try
        {
            varbind firstVar = _pdu.getResponseVarbinds()[0];
            AsnObjectId firstOid = firstVar.getOid();
            if (firstOid.startsWith(inetCidrRouteIfIndexObj))
            {
                System.out.println("inetCidrRouteIfIndex oid=" + firstOid.toString());
                int beginIndex = inetCidrRouteIfIndexObj.getSize();

                long inetCidrRouteDestTypeL = firstOid.getElementAt(beginIndex);
                System.out.println("inetCidrRouteDestType=" + inetCidrRouteDestTypeL);

                beginIndex++;
                int endIndex = beginIndex;
                if (inetCidrRouteDestTypeL == 1)
                {
                    // ipv4
                    endIndex = beginIndex + 4;
                }
                else if (inetCidrRouteDestTypeL == 2)
                {
                    // ipv6
                    endIndex = beginIndex + 16;
                }

                if (endIndex > beginIndex)
                {
                    long [] inetCidrRouteDestOid = firstOid.getSubOid(beginIndex, endIndex);
                    byte [] inetCidrRouteDestB = SnmpUtilities.longToByte(inetCidrRouteDestOid);
                    InetAddress iAddr = InetAddress.getByAddress(inetCidrRouteDestB);
                    System.out.println("inetCidrRouteDest=" + iAddr.getHostAddress());
                }
            }
            else
            {
                System.out.println("startsWith is false");
            }
        }
        catch(PduException exc)
        {
            System.out.println("update(): PduException 1 " + exc.getMessage());
        }
        catch(Exception exc)
        {
            System.out.println("update(): " + exc.getClass().getName() 
                + " " + exc.getMessage());
        }
        System.out.println(_pdu.toString());

        _pdu = new InetCidrRouteTablePdu(_context);
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
            System.out.println("update(): PduException 2 " + exc.getMessage());
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
 * <code>TestInetCidrRouteTablePdu.properties</code>, pass the name as first argument.
 */
public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    TestInetCidrRouteTablePdu application = new TestInetCidrRouteTablePdu(propFileName);
    application.init();
    application.start();
}

}
