// NAME
//      $RCSfile: MonitorAsteriskActiveChannels.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.1 $
// CREATED
//      $Date: 2006/03/23 13:44:57 $
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

package uk.co.westhawk.examplev2c;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import uk.co.westhawk.snmp.util.*;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * <p>
 * This class is written to test the Asterisk host functionality.
 * </p>
 *
 * <p>
 * It walks the tree by creating a new AsteriskChanTablePdu out off the
 * previous one, and it collects the values of all the channels.
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
 * <code>MonitorAsteriskActiveChannels.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.1 $ $Date: 2006/03/23 13:44:57 $
 */
public class MonitorAsteriskActiveChannels implements Observer, Runnable
{
    private static final String     version_id =
        "@(#)$Id: MonitorAsteriskActiveChannels.java,v 1.1 2006/03/23 13:44:57 birgit Exp $ Copyright Westhawk Ltd";

    /**
     * Use 10 (sec) as interval
     */
    public final static long SLEEPTIME = 10000;
    private boolean _mayLoopStart;

    private AsteriskChanTablePdu _aPdu;
    private String _host;
    private int _port;
    private SnmpContextv2c _context;
    private Util _util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public MonitorAsteriskActiveChannels(String propertiesFilename)
{
    _util = new Util(propertiesFilename, this.getClass().getName());
}

public void init ()
{
    // AsnObject.setDebug(15);
    AsnObject.setDebug(6);
    _host = _util.getHost();
    String bindAddr = _util.getBindAddress();
    _port = _util.getPort(SnmpContextBasisFace.DEFAULT_PORT);
    String socketType = _util.getSocketType();
    String community = _util.getCommunity();

    try
    {
        _context = new SnmpContextv2c(_host, _port, bindAddr, socketType);
        _context.setCommunity(community);
        System.out.println("context: " + _context.toString());

        _mayLoopStart = true;
    }
    catch (IOException exc)
    {
        System.out.println("IOException: " + exc.getMessage());
        System.exit(0);
    }
}


/** 
 * Sends a request, asking for the active channels.
 */
public void getActiveChannels(SnmpContextBasisFace con, AsteriskChanTablePdu prev)
{
    _aPdu = new AsteriskChanTablePdu(con);
    _aPdu.addObserver(this);
    _aPdu.addOids(prev);
    try
    {
        _aPdu.send();
    }
    catch(java.io.IOException exc)
    {
        System.out.println("getActiveChannels(): IOException " + exc.getMessage());
    }
    catch(uk.co.westhawk.snmp.stack.PduException exc)
    {
        System.out.println("getActiveChannels(): PduException " + exc.getMessage());
    }
}


public void start()
{
    Thread me = new Thread(this);
    me.setPriority(Thread.MIN_PRIORITY);
    me.start();
}


public void run()
{
    while (_context != null)
    {
        if (_mayLoopStart == true)
        {
            System.out.println("\nrun: starting from the top .. ");
            getActiveChannels(_context, null);
            _mayLoopStart = false;
        }

        try
        {
            Thread.sleep(SLEEPTIME);
        }
        catch (InterruptedException ix)
        {
            ;
        }
    }
}


/**
 * Implementing the Observer interface. 
 * Receiving the response from * getActiveChannels().
 *
 * @param obs the pdu variable
 * @param ov the array of varbind (not used)
 *
 * @see AsteriskChanTablePdu
 * @see #getActiveChannels
 */
public void update(Observable obs, Object ov)
{
    AsteriskChanTablePdu prev;

    // got answer back on the active channels
    int errStatus = _aPdu.getErrorStatus();
    int errIndex = _aPdu.getErrorIndex();

    if (errStatus == AsnObject.SNMP_ERR_NOERROR)
    {
        prev = _aPdu;
        System.out.println(_aPdu.toString());
        getActiveChannels(_context, prev);
    }
    else if (errStatus == AsnObject.SNMP_ERR_NOSUCHNAME)
    {
        // It's not easy to distinguish between 
        // - the table isn't there (i.e. not active calls)
        // - we walked out off the table
        // - the table is there, but some rows are missing

        if (errIndex == 0)
        {
            // Cannot get the value of astNumChannels,
            // Mib isn't implemented
            _mayLoopStart = true;
        }
        else if (errIndex == 1)
        {
            // Cannot get value of astChanIndex:
            Integer value = _aPdu.getAstNumChannels();
            if (value.intValue() == 0)
            {
                System.out.println("update(): no active channels");
            }
            else
            {
                // we walked out off the table, since if the table
                // exists, astChanIndex should be available
            }
            _mayLoopStart = true;
        }
        else 
        {
            // I assume that just some rows are missing
            prev = _aPdu;
            System.out.println(_aPdu.toString());

            // check I got the flag correct:
            byte[] flags = _aPdu.getAstChanFlags();
            AsnOctets octs = new AsnOctets(flags);
            int bits = flags.length * 8;

            System.out.println("AstChanFlags: bits set are ..");
            System.out.print("\t");
            for (int i=0; i<bits; i++)
            {
                if (BitsHelper.isFlagged(octs, i) == true)
                {
                    System.out.print(i + ", ");
                }
            }
            System.out.println();

            getActiveChannels(_context, prev);
        }
    }
    else 
    {
        System.out.println("update(): " + _aPdu.getErrorStatusString() 
            + " @ errIndex " + errIndex);
        _mayLoopStart = true;
    }

    if (_mayLoopStart == true)
    {
        System.out.println("update(): going to sleep");
    }
}



/**
 * Main. To use a properties file different from
 * <code>MonitorAsteriskActiveChannels.properties</code>, pass the name as first argument.
 */
public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    MonitorAsteriskActiveChannels application = new MonitorAsteriskActiveChannels(propFileName);
    application.init();
    application.start();
}

}
