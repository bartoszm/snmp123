// NAME
//      $RCSfile: NTServiceNamesBean.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.13 $
// CREATED
//      $Date: 2006/01/25 18:08:56 $
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

package uk.co.westhawk.snmp.beans;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import java.awt.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import java.io.*;
import java.beans.*;

/**
 * <p>
 * This bean collects the names of the network services installed on
 * a NT server. The NT mib is described in the 
 *
 * <a href="http://premium.microsoft.com/msdn/library/winresource/dnwinnt/f1d/d25/s86a2.htm">LAN Manager MIB II for Windows NT Objects</a> .
 *
 * You will have to register to the MSDN before accessing this page.
 * </p>
 *
 * <p>
 * The properties in the parent classes should be set, before calling
 * the action() method. Via a PropertyChangeEvent the application/applet
 * will be notified. 
 * </p>
 *
 * @see SNMPBean#setHost
 * @see SNMPBean#setPort
 * @see SNMPBean#setCommunityName
 * @see SNMPRunBean#setUpdateInterval
 * @see SNMPBean#addPropertyChangeListener
 * @see SNMPBean#action
 * @see GetNextPdu
 * 
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.13 $ $Date: 2006/01/25 18:08:56 $
 *
 */
public class NTServiceNamesBean extends SNMPRunBean implements Observer
{
    private static final String     version_id =
        "@(#)$Id: NTServiceNamesBean.java,v 1.13 2006/01/25 18:08:56 birgit Exp $ Copyright Westhawk Ltd";

    public final static String svSvcName = "1.3.6.1.4.1.77.1.2.3.1.1";

    private int           svSvcName_len;
    private GetNextPdu    pdu;
    private Hashtable     serviceHash;

    private boolean       isGetNextInFlight;
    private Date          lastUpdateDate = null;


/**
 * The default constructor.
 */
public NTServiceNamesBean() 
{
    serviceHash = new Hashtable();
    svSvcName_len = svSvcName.length();
}

/**
 * The constructor that will set the host and the port no.
 *
 * @param h the hostname
 * @param p the port no
 * @see SNMPBean#setHost
 * @see SNMPBean#setPort
 */
public NTServiceNamesBean(String h, int p) 
{
    this(h, p, null);
}

/**
 * The constructor that will set the host, the port no and the local
 * bind address.
 *
 * @param h the hostname
 * @param p the port no
 * @param b the local bind address
 * @see SNMPBean#setHost
 * @see SNMPBean#setPort
 * @see SNMPBean#setBindAddress
 *
 * @since 4_14
 */
public NTServiceNamesBean(String h, int p, String b) 
{
    this();
    setHost(h);
    setPort(p);
    setBindAddress(b);
}

/**
 * Returns the date of the moment when this bean was last updated.
 * This might be null when the first time the update was not finished.
 *
 * @return the last update date
 */
public Date getLastUpdateDate()
{
    return lastUpdateDate;
}

/**
 * Returns the indices of the NT network services.
 * The OID of this network service is a concatenation of the 
 * name (svSvcName) OID and the network service specific index. 
 * The index should be used to get the other properties of this service.
 *
 * @see #getIndex(String)
 * @see #svSvcName
 */
public Enumeration getIndices()
{
    return serviceHash.elements();
}

/**
 * Returns the index of one of the services. 
 * The OID of this network service is a concatenation of the 
 * name (svSvcName) OID and the network service specific index. 
 * The index should be used to get the other properties of this service.
 *
 * @param name The name of the service
 * @return the service index, might be null if no service with such name
 * exists
 * @see #getIndices
 * @see #getNames
 */
public String getIndex(String name)
{
    String ret = null;
    if (name != null)
    {
        ret = (String) serviceHash.get(name);
    }
    return ret;
}

/**
 * Returns the names of the NT network services (the list
 * of svSvcName).
 */
public Enumeration getNames()
{
    return serviceHash.keys();
}

/**
 * Returns the number of NT network services.
 */
public synchronized int getCount()
{
    return serviceHash.size();
}

/**
 * This method starts the action of the bean. It will initialises 
 * all variables before starting.
 */
public void action()
{
    if (isHostPortReachable())
    {
        serviceHash.clear();
        lastUpdateDate = new Date();
        isGetNextInFlight = false;
        setRunning(true);
    }
}

/**
 * Implements the running of the bean.
 *
 * It will send the Pdu, if the previous one is not still in flight.
 * @see SNMPRunBean#isRunning()
 */
public void run()
{
    while (context != null && isRunning())
    {
        if (isGetNextInFlight == false)
        {
            // start the GetNext loop again
            isGetNextInFlight = true;
            pdu = new GetNextPdu(context);
            pdu.addObserver(this);
            pdu.addOid(svSvcName);
            try
            {
                pdu.send();
            }
            catch (PduException exc)
            {
                System.out.println("PduException " + exc.getMessage());
            }
            catch (IOException exc)
            {
                System.out.println("IOException " + exc.getMessage());
            }
        }

        try
        {
            Thread.sleep(interval);
        } 
        catch (InterruptedException ix)
        {
            ;
        }
    }
}

/**
 * This method is called when the Pdu response is received. When all
 * answers are received it will fire the property change event.
 *
 * The answers are stored in a hashtable, this is done because the speed
 * can only be calculated with the previous answer.
 *
 * @see SNMPBean#addPropertyChangeListener
 */
public void update(Observable obs, Object ov)
{
    varbind var;
    String hashKey;
    String oid, index, name;

    pdu = (GetNextPdu) obs;
    if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        var = (varbind) ov;
        oid = var.getOid().toString();
        if (oid.startsWith(svSvcName))
        {
            // index is the part of the oid AFTER the svSvcName
            index = oid.substring(svSvcName_len+1);

            name = ((AsnOctets) var.getValue()).getValue();

            // update the hashtable with the new answer
            serviceHash.put(name, index);

            // perform the GetNext on the just received answer
            pdu = new GetNextPdu(context);
            pdu.addObserver(this);
            pdu.addOid(oid);
            try
            {
                pdu.send();
            }
            catch (PduException exc)
            {
                System.out.println("PduException " + exc.getMessage());
            }
            catch (IOException exc)
            {
                System.out.println("IOException " + exc.getMessage());
            }
        }
        else
        {
            // the GetNext loop has ended
            lastUpdateDate = new Date();
            isGetNextInFlight = false;
            firePropertyChange("serviceNames", null, null);
        }
    }
    else
    {
        // the GetNext loop has ended
        lastUpdateDate = new Date();
        isGetNextInFlight = false;
        firePropertyChange("serviceNames", null, null);
    }
}


}
