// NAME
//      $RCSfile: SnmpContextFace.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.13 $
// CREATED
//      $Date: 2006/02/09 14:19:05 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2000 - 2006 by Westhawk Ltd
 * <a href="www.westhawk.co.uk">www.westhawk.co.uk</a>
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

package uk.co.westhawk.snmp.stack;
import uk.co.westhawk.snmp.event.*;

/**
 * This interface contains the (basic) SNMP context interface that is needed 
 * by every PDU to send a SNMP v1 request. The context also
 * provides functionality to receive incoming PDUs.
 *
 * @see SnmpContext
 * @see SnmpContextv2c
 * @see SnmpContextv3
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 3.13 $ $Date: 2006/02/09 14:19:05 $
 */
public interface SnmpContextFace extends SnmpContextBasisFace
{
    static final String     version_id =
        "@(#)$Id: SnmpContextFace.java,v 3.13 2006/02/09 14:19:05 birgit Exp $ Copyright Westhawk Ltd";

    /**
     * The default community name. This is <em>public</em>.
     */
    public final static String DEFAULT_COMMUNITY = "public";

/**
 * Returns the community name.
 */
public String getCommunity();


/**
 * Sets the community name.
 * This community name will be used for all PDUs sent with this context.
 * The default community name is <em>public</em>.
 *
 * @see #DEFAULT_COMMUNITY
 */
public void setCommunity(String newCommunity);

}
