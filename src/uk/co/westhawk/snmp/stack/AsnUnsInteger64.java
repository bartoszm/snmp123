// NAME
//      $RCSfile: AsnUnsInteger64.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.16 $
// CREATED
//      $Date: 2009/03/05 13:11:33 $
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
 * This class represents ASN.1 64-bit unsigned integer. It is used for
 * COUNTER64.
 *
 * @see SnmpConstants#COUNTER64
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 3.16 $ $Date: 2009/03/05 13:11:33 $
 */
public class AsnUnsInteger64 extends AsnObject
{
    private static final String     version_id =
        "@(#)$Id: AsnUnsInteger64.java,v 3.16 2009/03/05 13:11:33 birgita Exp $ Copyright Westhawk Ltd";

    /**
     * The internal value of AsnUnsInteger64.
     */
    // TODO: move to BigInteger, since long is too small
    protected long value;

    /**
     * Constructor.
     *
     * @param v The value of the AsnUnsInteger64
     */
    public AsnUnsInteger64(long v)
    {
        this.value = v;
        this.type = COUNTER64;
    }


    /**
     * Constructor.
     *
     * @param in The input stream from which the value should be read
     * @param len The length of the AsnUnsInteger64
     */
    public AsnUnsInteger64(InputStream in, int len) throws IOException
    {
        byte data[] = new byte[len];
        if (len != in.read(data,0,len))
        {
            throw new IOException("AsnUnsInteger64(): Not enough data");
        }
        this.value = bytesToLong(data);
    }

    /**
     * Returns the value representation of the AsnUnsInteger64.
     *
     * @return The value of the AsnUnsInteger64
     */
    public long getValue()
    {
        return value;
    }

    /**
     * Returns the string representation of the AsnUnsInteger64.
     *
     * @return The string of the AsnUnsInteger64
     */
    public String toString()
    {
        return (String.valueOf(value));
    }

    /**
     * Returns the number of bytes the integer occupies.
     */
    int size() 
    {
        int  count, empty = 0x00, sign = 0x00;

        if (value < 0)
        {
            empty = 0xFF;
            sign  = 0x80;
        }

        // 64-bit integer.. change to 24 to write 32-bit long
        // loop through bytes in value while it is 'empty'
        for(count=56; count>0; count-=8)
        {
            if ( ((value >> count) & 0xFF) != empty) break;
        }

        // Check sign bit.. make sure negative's MSB bit is 1,
        // positives is 0
        // (0x00000080 = 0x00 0x80) 0xFFFFFF01 => 0xFF 0x01
        // (0x0000007F = 0x7F)      0xFFFFFF80 => 0x80
        if (((value >> count) & 0x80) != sign) count += 8;

        return (count>>3)+1;
    }


    /**
     * Output integer.
     */
    void write(OutputStream out, int pos) throws IOException
    {
        int  count, empty = 0x00, sign = 0x00;

        if (value < 0)
        {
            empty = 0xFF;
            sign  = 0x80;
        }

        // Get count
        for(count=56; count>0; count-=8)
        {
            if ( ((value >> count) & 0xFF) != empty) break;
        }
        if (((value >> count) & 0x80) != sign) count += 8;

        // Build header and write value
        AsnBuildHeader(out, COUNTER64, (count>>3)+1);
        if (debug > 10)
        {
            System.out.println("\tAsnUnsInteger64(): value = " + value
                + ", pos = " + pos);
        }
        for(; count>=0; count-=8)
        {
            out.write((byte)((value >> count) & 0xFF));
        }
    }

    /**
     * Changes an array of bytes into a long.
     * Thanks to Julien Conan (jconan@protego.net) for improving 
     * this method.
     *
     * @param data the array of bytes
     * @return the int representation of the array
     */
    protected long bytesToLong(byte[] data) throws IOException
    {
        DataInputStream dis = new DataInputStream(
              new ByteArrayInputStream(data));

        long val = 0;
        int size = data.length;

        for (int n=0; n<size; n++)
        {
            val = (val << 8) + dis.readUnsignedByte();
        }

        return val;
    }

    /**
     * Compares this object to the specified object.  The result is
     * <code>true</code> if and only if the argument is not
     * <code>null</code> and is an <code>AsnUnsInteger64</code> object that
     * contains the same <code>long</code> value as this object.
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     */
    public boolean equals(Object obj) 
    {
        if (obj instanceof AsnUnsInteger64) 
        {
            return value == ((AsnUnsInteger64)obj).value;
        }
        return false;
    }


    /**
     * Returns a hash code for this AsnUnsInteger64.
     *
     * @return  a hash code value for this object, equal to the 
     *          hash of the primitive <code>long</code> value represented 
     *          by this <code>AsnUnsInteger64</code> object. 
     */
    public int hashCode() 
    {
        // nicked from Long.hashCode
        return (int)(value ^ (value >>> 32));
    }
}
