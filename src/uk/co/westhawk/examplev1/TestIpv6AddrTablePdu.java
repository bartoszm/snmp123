// NAME
//      $RCSfile: TestIpv6AddrTablePdu.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.4 $
// CREATED
//      $Date: 2006/03/23 14:54:05 $
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
 * The TestIpv6AddrTablePdu application requests the information 
 * of all the IPv6 address in the host's IPV6-MIB, using the
 * Ipv6AddrTablePdu.
 * This is written to test the IPv6 functionality. 
 * See <a href="http://www.ietf.org/rfc/rfc2465.txt">IPV6-TC and IPV6-MIB</a>.
 * </p>
 *
 * <p>
 * It walks the tree by creating a new Ipv6AddrTablePdu out off the
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
 * <code>TestIpv6AddrTablePdu.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.4 $ $Date: 2006/03/23 14:54:05 $
 */
public class TestIpv6AddrTablePdu implements Observer 
{
    private static final String     version_id =
        "@(#)$Id: TestIpv6AddrTablePdu.java,v 1.4 2006/03/23 14:54:05 birgit Exp $ Copyright Westhawk Ltd";

    private Ipv6AddrTablePdu _pdu;
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
public TestIpv6AddrTablePdu(String propertiesFilename)
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
    _pdu = new Ipv6AddrTablePdu(_context);
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
 * @param obs the Ipv6AddrTablePdu variable
 * @param ov the array of varbind (not used)
 *
 * @see Ipv6AddrTablePdu
 * @see uk.co.westhawk.snmp.stack.varbind
 */
public void update(Observable obs, Object ov)
{
    Ipv6AddrTablePdu prev;
    String hashKey;

    if (_pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        prev = _pdu;
        System.out.println("\n" + this.getClass().getName() + ".update:");

        // get the 'tail' of the oid, that consists of the
        // ipv6IfIndex and ipv6AddrAddress
        AsnObjectId ipv6AddrPfxLengthObj = 
                    new AsnObjectId(Ipv6AddrTablePdu.ipv6AddrPfxLength_OID);
        try
        {
            varbind firstVar = _pdu.getResponseVarbinds()[0];
            AsnObjectId firstOid = firstVar.getOid();
            if (firstOid.startsWith(ipv6AddrPfxLengthObj))
            {
                int beginIndex = ipv6AddrPfxLengthObj.getSize();
                int endIndex = firstOid.getSize();

                long ipv6IfIndexL = firstOid.getElementAt(beginIndex);
                System.out.println("ipv6IfIndex=" + ipv6IfIndexL);

                long [] ipv6AddrAddressOid = firstOid.getSubOid(beginIndex+1, endIndex);
                byte [] ipv6AddrAddressB = SnmpUtilities.longToByte(ipv6AddrAddressOid);
                InetAddress iAddr = InetAddress.getByAddress(ipv6AddrAddressB);
                System.out.println("ipv6AddrAddress=" + iAddr.getHostAddress());
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

        _pdu = new Ipv6AddrTablePdu(_context);
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
 * <code>TestIpv6AddrTablePdu.properties</code>, pass the name as first argument.
 */
public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    TestIpv6AddrTablePdu application = new TestIpv6AddrTablePdu(propFileName);
    application.init();
    application.start();
}

}
