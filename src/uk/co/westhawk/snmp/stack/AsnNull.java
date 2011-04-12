// NAME
//      $RCSfile: AsnNull.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.9 $
// CREATED
//      $Date: 2008/05/27 15:40:14 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1995, 1996 by West Consulting BV
 *
 * Permission to use, copy, modify, and distribute this software
 * for any purpose and without fee is hereby granted, provided
 * that the above copyright notices appear in all copies and that
 * both the copyright notice and this permission notice appear in
 * supporting documentation.
 * This software is provided "as is" without express or implied
 * warranty.
 * author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * original version by hargrave@dellgate.us.dell.com (Jordan Hargrave)
 */

/*
 * Copyright (C) 1996 - 2006 by Westhawk Ltd
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

import java.io.*;
import java.util.*;

/**
 * This class represents the ASN.1 Null object
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 3.9 $ $Date: 2008/05/27 15:40:14 $
 */
public class AsnNull extends AsnObject
{
    private static final String     version_id =
        "@(#)$Id: AsnNull.java,v 3.9 2008/05/27 15:40:14 birgita Exp $ Copyright Westhawk Ltd";

    /** 
     * Default Constructor.
     */
    public AsnNull() 
    {
        type = ASN_NULL;
    }

    /** 
     * Constructor.
     *
     * @param in The input stream from which the value should be read
     * @param len The length of the AsnInteger
     */
    public AsnNull(InputStream in, int len) 
    {
        this();
    }

    /** 
     * Returns the string representation of the AsnNull.
     *
     * @return The string of the AsnNull
     */
    public String toString()
    {
        return "AsnNull";
    }

    void write(OutputStream out, int pos) throws IOException 
    {
        AsnBuildHeader(out, ASN_NULL, 0);
    }

/**
 * Compares this object to the specified object.
 * The result is <code>true</code> if and only if the argument is not
 * <code>null</code> and is a <code>AsnNull</code> object.
 *
 * @param anObject the object to compare this <code>AsnNull</code> 
 *                 against.
 * @return <code>true</code> if the <code>AsnNull </code>are equal;
 *         <code>false</code> otherwise.
 */
public boolean equals(Object anObject) 
{
    if (this == anObject) 
    {
        return true;
    }
    if (anObject instanceof AsnNull) 
    {
        AsnNull anotherNull = (AsnNull)anObject;
        return true;
    }
    return false;
}


/**
 * Returns a hash code for this object. 
 * @return a hash code value for this object.
 */
public int hashCode() 
{
    int h = 5;
    return h;
}

}
