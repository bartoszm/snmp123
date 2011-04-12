// NAME
//      $RCSfile: OneNTSharedResBean.java,v $
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
 * This bean collects information about one NT shared resource installed
 * on a NT server. The NT mib is described in the 
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
 * @see #setIndex(String)
 * @see SNMPBean#addPropertyChangeListener
 * @see SNMPBean#action
 * @see GetPdu_vec
 * @see NTSharedResBean
 * 
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.13 $ $Date: 2006/01/25 18:08:56 $
 */
public class OneNTSharedResBean extends SNMPRunBean implements Observer 
{
    private static final String     version_id =
        "@(#)$Id: OneNTSharedResBean.java,v 1.13 2006/01/25 18:08:56 birgit Exp $ Copyright Westhawk Ltd";

    private GetPdu_vec pdu;

    private boolean isPduInFlight;
    private Date lastUpdateDate = null;

    private String index = "";
    private String name = "";
    private String path = "";
    private String comment = "";

    private final static int NR_OID = 3;
    private final static String svShareName = 
            "1.3.6.1.4.1.77.1.2.27.1.1";
    private final static String svSharePath =
            "1.3.6.1.4.1.77.1.2.27.1.2";
    private final static String svShareComment =
            "1.3.6.1.4.1.77.1.2.27.1.3";

/**
 * The default constructor.
 */
public OneNTSharedResBean() 
{
}

/**
 * The constructor that will set the host and the port no.
 *
 * @param h the hostname
 * @param p the port no
 * @see SNMPBean#setHost
 * @see SNMPBean#setPort
 */
public OneNTSharedResBean(String h, int p) 
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
public OneNTSharedResBean(String h, int p, String b) 
{
    this();
    setHost(h);
    setPort(p);
    setBindAddress(b);
}


/**
 * Sets the index of the NT shared resource that will be requested.
 * @param ind the index
 * @see #getIndex()
 * @see NTSharedResBean#getIndex(String)
 */
public void setIndex(String ind)
{
    if (ind != null && ind.length() > 0)
    {
        index = ind;
    }
}

/**
 * Returns the index of the NT shared resource.
 * @return the index
 * @see #setIndex(String)
 */
public String getIndex()
{
    return index;
}

/**
 * Returns the name of the NT shared resource.
 * @return the name
 */
public String getName()
{
    return name;
}

/**
 * Returns the local name (path) of the NT shared resource.
 * @return the path
 */
public String getPath()
{
    return path;
}

/**
 * Returns the comment associated with the NT shared resource.
 * @return the comment
 */
public String getComment()
{
    return comment;
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
 * This method starts sending the SNMP request. All properties should be
 * set before this method is called.
 *
 * The actual sending will take place in the run method.
 * It makes a new snmp context and initialises all variables before
 * starting.
 */
public void action() 
{
    if (isHostPortReachable())
    {
        lastUpdateDate = new Date();
        isPduInFlight = false;
        setRunning(true);
    }
}


/**
 * The run method according to the Runnable interface.
 * This method will send the Pdu request, if the previous one is not
 * still in flight.
 * @see SNMPRunBean#isRunning()
 */
public void run()
{
    while (context != null && isRunning())
    {
        String ind = getIndex();
        if (isPduInFlight == false && ind != null && ind.length() > 0)
        {
            isPduInFlight = true;
            pdu = new GetPdu_vec(context, NR_OID);
            pdu.addObserver(this);
            pdu.addOid(svShareName + "." + ind);
            pdu.addOid(svSharePath + "." + ind);
            pdu.addOid(svShareComment + "." + ind);
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
 * The update method according to the Observer interface, it will be
 * called when the Pdu response is received.
 * The property change event is fired.
 *
 * @see SNMPBean#addPropertyChangeListener
 */
public void update(Observable obs, Object ov)
{
    pdu = (GetPdu_vec) obs;
    varbind [] varbinds = (varbind []) ov;

    if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        AsnObject asnobj;
        name = ((AsnOctets) varbinds[0].getValue()).getValue();

        asnobj = varbinds[1].getValue();
        path = " ";
        if (asnobj instanceof AsnOctets)
        {
            path = ((AsnOctets) asnobj).getValue();
        }

        asnobj = varbinds[2].getValue();
        comment = " ";
        if (asnobj instanceof AsnOctets)
        {
            comment = ((AsnOctets) asnobj).getValue();
        }


        lastUpdateDate = new Date();
        isPduInFlight = false;
        firePropertyChange("NTService", null, null);
    }
}


}
