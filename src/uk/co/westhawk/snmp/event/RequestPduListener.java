// NAME
//      $RCSfile: RequestPduListener.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.3 $
// CREATED
//      $Date: 2006/01/17 17:59:33 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2005 - 2006 by Westhawk Ltd
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
 * The listener interface for receiving decoded request pdu events.
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.3 $ $Date: 2006/01/17 17:59:33 $
 */
public interface RequestPduListener extends java.util.EventListener
{
    public static final String     version_id =
        "@(#)$Id: RequestPduListener.java,v 1.3 2006/01/17 17:59:33 birgit Exp $ Copyright Westhawk Ltd";


/**
 * Invoked when a pdu is received.
 */
public void requestPduReceived(RequestPduEvent evt);

}
