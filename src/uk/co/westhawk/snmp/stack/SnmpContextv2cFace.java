// NAME
//      $RCSfile: SnmpContextv2cFace.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.7 $
// CREATED
//      $Date: 2006/01/17 17:43:54 $
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

/**
 * This interface contains the SNMP context interface that is needed by every 
 * PDU to send a SNMP v2c request.
 *
 * @see SnmpContext
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.7 $ $Date: 2006/01/17 17:43:54 $
 */
public interface SnmpContextv2cFace extends SnmpContextFace 
{
    static final String     version_id =
        "@(#)$Id: SnmpContextv2cFace.java,v 3.7 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";


}
