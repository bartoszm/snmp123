// NAME
//      $RCSfile: PduException.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.5 $
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
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.5 $ $Date: 2006/01/17 17:43:54 $
 */
public class PduException extends Exception 
{
    private static final String     version_id =
        "@(#)$Id: PduException.java,v 3.5 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";

/** 
 * Constructs a PduException with no specified detail message. 
 *
 */
public PduException() 
{
    super();
}

/** 
 * Constructs a PduException with the specified detail
 * message. 
 *
 * @param str The detail message.
 */
public PduException(String str) 
{
    super(str);
}


}
