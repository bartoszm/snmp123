// NAME
//      $RCSfile: TrapListener.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.5 $
// CREATED
//      $Date: 2006/01/17 17:43:53 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2001 - 2006 by Westhawk Ltd
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
package uk.co.westhawk.snmp.event;


/**
 * The listener interface for receiving decoded trap events.
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.5 $ $Date: 2006/01/17 17:43:53 $
 */
public interface TrapListener extends java.util.EventListener
{
    public static final String     version_id =
        "@(#)$Id: TrapListener.java,v 1.5 2006/01/17 17:43:53 birgit Exp $ Copyright Westhawk Ltd";


/**
 * Invoked when a trap is received.
 */
public void trapReceived(TrapEvent evt);

}
