// NAME
//      $RCSfile: SnmpUtilities.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.27 $
// CREATED
//      $Date: 2009/03/05 12:57:57 $
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

package uk.co.westhawk.snmp.util;
import java.io.*;
import java.util.*;
import uk.co.westhawk.snmp.stack.*;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.engines.*;


/**
 * This class contains utilities for key and authentication encoding.
 * See <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 1.27 $ $Date: 2009/03/05 12:57:57 $
 */
public class SnmpUtilities extends Object
{
    private static final String     version_id =
        "@(#)$Id: SnmpUtilities.java,v 1.27 2009/03/05 12:57:57 birgita Exp $ Copyright Westhawk Ltd";

    final static int ONEMEG = 1048576;
    final static int SALT_LENGTH = 8; // in bytes

    private static int salt_count = -1;
    private static long asalt;


/**
 * Returns the String representation of the SNMP version number.
 * @param version The version number
 * @return The corresponding String.
 */
public static String getSnmpVersionString(int version)
{
    String versionString;

    switch (version)
    {
        case SnmpConstants.SNMP_VERSION_1:
            versionString = "SNMPv1";
            break;
        case SnmpConstants.SNMP_VERSION_2c:
            versionString = "SNMPv2c";
            break;
        case SnmpConstants.SNMP_VERSION_3:
            versionString = "SNMPv3";
            break;
        default:
            versionString = "Unsupported version no " + version;
    }
    return versionString;
}

/**
 * Converts a hexadecimal ASCII string to a byte array. The method is case
 * insensitive, so F7 works as well as f7. The string should have
 * the form F7d820 and should omit the '0x'.
 * This method is the reverse of <code>toHexString</code>.
 *
 * @param hexStr The string representing a hexadecimal number
 * @return the byte array of hexStr
 * @see #toHexString(byte[])
 */
public static byte [] toBytes(String hexStr)
{
    byte mask = (byte) 0x7F;
    byte [] bytes = new byte[0];

    if (hexStr != null)
    {
        hexStr = hexStr.toUpperCase();
        int len = hexStr.length();
        bytes = new byte[(len/2)];
        int sPos=0;     // position in hexStr
        int bPos=0;     // position in bytes
        while (sPos<len)
        {
            char a = hexStr.charAt(sPos);
            char b = hexStr.charAt(sPos+1);

            int v1 = Character.digit(a, 16);
            int v2 = Character.digit(b, 16);
            int v3 = (int) (v1 * 16 + v2);
            bytes[bPos] = (byte) v3;

            sPos +=2;
            bPos++;
        }
    }
    return bytes;
}

/**
 * Converts one long value to its byte value.
 *
 * @param l The long value
 * @return It's byte value
 * @throws IllegalArgumentException when l is not in between 0 and 255.
 *
 * @see #longToByte(long[])
 * @since 4_14
 */
public static byte longToByte(long l) throws IllegalArgumentException
{
    byte ret = 0;
    if ((l < 0) || (l > 255))
    {
        throw new IllegalArgumentException("Valid byte values are between 0 and 255."
            + "Got " + l);
    }
    ret = (byte)(l);
    return ret;
}


/**
 * Converts an array of long values to its array of byte values.
 *
 * @param l The array of longs
 * @return The array of bytes
 * @throws IllegalArgumentException when one of the longs is not in between 0 and 255.
 *
 * @see #longToByte(long)
 * @since 4_14
 */
public static byte[] longToByte(long[] l) throws IllegalArgumentException
{
    int len = l.length;
    byte [] ret = new byte[len];
    for (int i=0; i<len; i++)
    {
        ret[i] = longToByte(l[i]);
    }
    return ret;
}


/**
 * Dumps (prints) the byte array. Debug method.
 * @param headerStr String that will be printed as header
 * @param bytes Bytes to be dumped as hex.
 */
public static void dumpBytes(String headerStr, byte[] bytes)
{
    StringBuffer buf = new StringBuffer(bytes.length);
    buf.append("\n");
    buf.append(headerStr).append("\n");
    buf.append("bytes.length: ").append(bytes.length).append("\n");
    int len = bytes.length;
    int i=0;
    for (i=0; i<len; i++)
    {
        buf.append(toHex(bytes[i]) + " ");
        if (0 == ((i+1) % 8))
        {
            buf.append("\n");
        }
    }
    buf.append("\n");
    System.out.println(buf.toString());
}

/**
 * Converts a byte array to a hexadecimal ASCII string.
 * The string will be in upper case and does not start with '0x'.
 * This method is the reverse of <code>toBytes</code>.
 *
 * @param bytes The byte array
 * @return The string representing the byte array
 * @see #toBytes(String)
 */
public static String toHexString(byte[] bytes)
{
    String str = "";
    if (bytes != null)
    {
        int len = bytes.length;
        for (int i=0; i<len; i++)
        {
            str += toHex(bytes[i]);
        }
    }
    return str;
}

/**
 * Converts one int to a hexadecimal ASCII string.
 *
 * @param val The integer
 * @return The hex string
 */
public static String toHex(int val)
{
    int val1, val2;

    val1 = (val >> 4) & 0x0F;
    val2 = (val & 0x0F);

    return ("" + HEX_DIGIT[val1] + HEX_DIGIT[val2]);
}

final static char[] HEX_DIGIT = {'0','1','2','3','4','5','6','7',
                     '8','9','A','B','C','D','E','F'};


/**
 * Compaires two byte arrays and returns if they are equal.
 *
 * @param array1 the first byte array
 * @param array2 the second byte array
 * @return whether they are equal of not.
 */
public static boolean areBytesEqual(byte[] array1, byte[] array2)
{
    boolean same = true;
    int len1 = array1.length;
    if (len1 == array2.length)
    {
        int i=0;
        while (i<len1 && same)
        {
            same = (array1[i] == array2[i]);
            i++;
        }
    }
    else
    {
        same = false;
    }
    return same;
}

/**
 * Converts the user's password and the SNMP Engine Id to the localized key
 * using the MD5 protocol.
 * Described in <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 *
 * @param passwKey The password key
 * @param engineId The SNMP engine Id
 * @see uk.co.westhawk.snmp.stack.SnmpContextv3#setUserAuthenticationPassword(String)
 * @see #passwordToKeyMD5(String)
 */
public static byte [] getLocalizedKeyMD5(byte[] passwKey, String engineId)
{
    byte [] ret = null;
    MD5Digest mdc = new MD5Digest();
    mdc.reset();

    byte [] beid = toBytes(engineId);
    if ((beid != null) && (passwKey != null))
    {
        // see page 169 of 0-13-021453-1 A Practical Guide to SNMP
        mdc.update(passwKey, 0, passwKey.length);
        mdc.update(beid, 0, beid.length);
        mdc.update(passwKey, 0, passwKey.length);
        ret = new byte[mdc.getDigestSize()];
        mdc.doFinal(ret, 0);
    }
    return ret;
}

/**
 * Converts the user's password and the SNMP Engine Id to the localized key
 * using the SHA protocol.
 *
 * @param passwKey The printable user password
 * @param engineId The SNMP engine Id
 * @see uk.co.westhawk.snmp.stack.SnmpContextv3#setUserAuthenticationPassword(String)
 */
public static byte [] getLocalizedKeySHA1(byte[] passwKey, String engineId)
{
    byte [] ret = null;
    SHA1Digest mdc = new SHA1Digest();
    mdc.reset();

    byte [] beid = toBytes(engineId);
    if ((beid != null) && (passwKey != null))
    {
        // see page 169 of 0-13-021453-1 A Practical Guide to SNMP
        mdc.update(passwKey, 0, passwKey.length);
        mdc.update(beid, 0, beid.length);
        mdc.update(passwKey, 0, passwKey.length);
        ret = new byte[mdc.getDigestSize()];
        mdc.doFinal(ret, 0);
    }
    return ret;
}


/**
 * Converts the user's password to an authentication key using the SHA1
 * protocol. Note, this is not the same as generating the localized key
 * as is
 * described in <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 *
 * @param password The printable user password
 * @see uk.co.westhawk.snmp.stack.SnmpContextv3#setUserAuthenticationPassword(String)
 * @see #getLocalizedKeyMD5
 */
public static byte [] passwordToKeySHA1(String password)
{
    SHA1Digest sha;
    byte [] ret =null;
    sha = new SHA1Digest();
    byte [] passwordBuf = new byte[64];
    int pl =  password.length();
    byte [] pass = new byte[pl];

    // copy to byte array - stripping off top byte
    for (int i=0;i<pl; i++)
    {
        pass[i] = (byte) (0xFF & password.charAt(i));
    }

    int count=0;
    int passwordIndex = 0;
    Date then = (AsnObject.debug > 1) ? new Date() : null ;

    synchronized (sha)
    {
        while (count < ONEMEG)
        {
            int cp = 0;
            int i=0;
            while (i<64)
            {
                int pim = passwordIndex % pl;
                int len = 64 - cp ;
                int pr = pl - pim;
                if (len > pr)
                {
                    len = pr;
                }
                System.arraycopy(pass, pim, passwordBuf, cp, len);
                i+= len;
                cp+=len;
                passwordIndex += len;
            }

            // need to optimize this.....
            sha.update(passwordBuf, 0, passwordBuf.length);
            count += 64;
        }
        // implicit that  ONEMEG % 64 == 0
        ret = new byte[sha.getDigestSize()];
        sha.doFinal(ret, 0);
    }

    if (AsnObject.debug > 1)
    {
        Date now = new Date();
        long diff = now.getTime() - then.getTime();
        System.out.println("(Complex) pass to key takes " + diff/1000.0);
    }

    return ret;
}
/**
 * Converts the user's password to an authentication key using the MD5
 * protocol. Note, this is not the same as generating the localized key
 * as is
 * described in <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 *
 * @param password The printable user password
 * @see uk.co.westhawk.snmp.stack.SnmpContextv3#setUserAuthenticationPassword(String)
 * @see #getLocalizedKeyMD5
 */
public static byte [] passwordToKeyMD5(String password)
{
    MD5Digest mdc;
    byte [] ret =null;
    mdc = new MD5Digest();
    byte [] passwordBuf = new byte[64];
    int pl =  password.length();
    byte [] pass = new byte[pl];

    // copy to byte array - stripping off top byte
    for (int i=0;i<pl; i++)
    {
        pass[i] = (byte) (0xFF & password.charAt(i));
    }

    int count=0;
    int passwordIndex = 0;
    Date then = (AsnObject.debug > 1) ? new Date() : null ;
    synchronized (mdc)
    {
        while (count < ONEMEG)
        {
            int cp = 0;
            int i=0;
            while (i<64)
            {
                int pim = passwordIndex % pl;
                int len = 64 - cp ;
                int pr = pl - pim;
                if (len > pr)
                {
                    len = pr;
                }
                System.arraycopy(pass, pim, passwordBuf, cp, len);
                i+= len;
                cp+=len;
                passwordIndex += len;
            }
            mdc.update(passwordBuf, 0, passwordBuf.length);
            count += 64;
        }
        // implicit that  ONEMEG % 64 == 0
        ret = new byte[mdc.getDigestSize()];
        mdc.doFinal(ret, 0);
    }

    if (AsnObject.debug > 1)
    {
        Date now = new Date();
        long diff = now.getTime() - then.getTime();
        System.out.println("(Complex) pass to key takes "+diff/1000.0);
    }

    return ret;
}


/**
 * Returns the 12 byte MD5 fingerprint.
 * @param key The key
 * @param message The message
 * @see #getFingerPrintSHA1
 */
public final static byte [] getFingerPrintMD5(byte [] key, byte [] message)
{
    if ((AsnObject.debug > 5) && (key.length != 16))
    {
        System.out.println("MD5 key length wrong");
    }
    return getFingerPrint(key, message, false);
}

/**
 * Returns the 12 byte SHA1 fingerprint.
 * @param key The key
 * @param message The message
 * @see #getFingerPrintMD5
 */
public final static byte [] getFingerPrintSHA1(byte [] key, byte [] message)
{
    if ((AsnObject.debug > 5) && (key.length != 20))
    {
        System.out.println("SHA1 key length wrong");
    }
    return getFingerPrint(key, message, true);
}


/**
 * Returns the DES salt.
 * The "salt" value is generated by concatenating the 32-bit
 * snmpEngineBoots value with a 32-bit counter value that the encryption
 * engine maintains. This 32-bit counter will be initialised to some
 * arbitrary value at boot time.
 *
 * <p>
 * See "A Practical Guide to SNMPv3 and Network Management" section 6.8
 * Privacy, p 194.
 * </p>
 *
 * @param snmpEngineBoots The (estimated) boots of the authoritative engine
 * @return The salt
 */
public final static byte[] getSaltDES(int snmpEngineBoots)
{
    if (salt_count == -1)
    {
        // initialise the 2nd part of the salt
        java.util.Random rand = new Random();
        salt_count = rand.nextInt();
    }
    byte [] salt = new byte[SALT_LENGTH];
    setBytesFromInt(salt, snmpEngineBoots, 0);
    setBytesFromInt(salt, salt_count, SALT_LENGTH/2);
    salt_count++;
    return salt;
}

/*
 *
 * The 64-bit integer is then put into the msgPrivacyParameters field encoded
 * as an OCTET STRING of length 8 octets.
 * The integer is then modified for the subsequent message.
 * We recommend that it is incremented by one until it reaches its
 * maximum value,
 * at which time it is wrapped.
 * An implementation can use any method to vary the value of the local
 * 64-bit integer,
 * providing the chosen method never generates a duplicate IV for the
 * same key.
 *
 */

/**
 * Returns the AES salt.
 * @return The salt
 */
public static byte[] getSaltAES()
{
    if (asalt == 0)
    {
        java.security.SecureRandom rand = new java.security.SecureRandom();
        asalt = rand.nextLong();
    } 
    else 
    {
        asalt ++;
    }
    byte [] tsalt = new byte[8];
    setBytesFromLong(tsalt, asalt, 0);
    return tsalt;
}

/**
 * Returns the DES key.
 * The 16-byte secret privacy key is made up of 8 bytes that
 * make up the DES key and 8 bytes used as a preinitialisation
 * vector.
 *
 * @param secretPrivacyKey The secret privacy key
 * @return The key
 */
public final static byte[] getDESKey(byte[] secretPrivacyKey)
throws PduException
{
    byte [] desKey = new byte[8];
    if (secretPrivacyKey.length < 16)
    {
        throw new PduException("SnmpUtilities.getDESKey():"
            + " secretPrivacyKey is < 16");
    }
    System.arraycopy(secretPrivacyKey, 0, desKey, 0, 8);
    return desKey;
}

/**
 * Returns the first 128 bits of the localized key Kul are used as the
 * AES encryption key.
 * @param secretPrivacyKey The secret privacy key
 * @return The key
 */
public final static byte[] getAESKey(byte[] secretPrivacyKey)
throws PduException
{
    byte [] aesKey = new byte[16];
    if (secretPrivacyKey.length < 16)
    {
        throw new PduException("SnmpUtilities.getAESKey():"
            + " secretPrivacyKey is < 16");
    }
    System.arraycopy(secretPrivacyKey, 0, aesKey, 0, 16);
    return aesKey;
}


/**
 * Returns the DES initial value.
 * The 16-byte secret privacy key is made up of 8 bytes that
 * make up the DES key and 8 bytes used as a preinitialisation
 * vector.
 * The initialization vector that is used by the DES algorithm is the
 * result of the 8-byte preinitialisation vector XOR-ed with the 8-byte
 * "salt".
 *
 * @param secretPrivacyKey The secret privacy key
 * @param salt The salt
 * @return The initial value
 */
public final static byte[] getDESInitialValue(byte[] secretPrivacyKey,
    byte [] salt) throws PduException
{
    byte [] initV = new byte[8];
    if (secretPrivacyKey.length < 16)
    {
        throw new PduException("SnmpUtilities.getInitialValue():"
            + " secretPrivacyKey is < 16");
    }

    int spk = 8;
    for (int i=0; i<initV.length; i++)
    {
        initV[i] = (byte) (secretPrivacyKey[spk] ^ salt[i]);
        spk++;
    }
    return initV;
}


/**
 * Returns the first 128 bits of the localized key Kul are used as the
 * AES encryption key.
 * RFC 3826 3.1.2.1 AES Encryption Key and IV.
 *
 * The 128-bit IV is obtained as the concatenation of the authoritative
 * SNMP engine's 32-bit snmpEngineBoots, the SNMP engine's 32-bit
 * snmpEngineTime, and a local 64-bit integer. The 64-bit integer is
 * initialized to a pseudo-random value at boot time.
 */
public static byte [] getAESInitialValue(int engineBoots, int engineTime, byte[] salt)
{
    byte ret[] = new byte[16];

    // The IV is concatenated as follows: the 32-bit snmpEngineBoots is
    // converted to the first 4 octets (Most Significant Byte first),
    setBytesFromInt(ret, engineBoots, 0);

    // the 32-bit snmpEngineTime is converted to the subsequent 4 octets
    // (Most Significant Byte first),
    setBytesFromInt(ret, engineTime, 4);

    // and the 64-bit integer is then converted to the last 8 octets
    // (Most Significant Byte first).
    ret[8] = salt[0];
    ret[9] = salt[1];
    ret[10] = salt[2];
    ret[11] = salt[3];
    ret[12] = salt[4];
    ret[13] = salt[5];
    ret[14] = salt[6];
    ret[15] = salt[7];
    return ret;
}


/*
Page 8.
The 64-bit integer must be placed in the privParameters field to enable the
receiving entity to compute the correct IV and to decrypt the message.
This 64-bit value is called the "salt" in this document.

Note that the sender and receiver must use the same IV value, i.e., they
must both use the same values of the individual components used to
create the IV.  In particular, both sender and receiver must use the
values of snmpEngineBoots, snmpEngineTime, and the 64-bit integer which
are contained in the relevant message (in the
msgAuthoritativeEngineBoots, msgAuthoritativeEngineTime, and
privParameters fields respectively).

3.1.3 Data Encryption
The data to be encrypted is treated as a sequence of octets.

The data is encrypted in Cipher Feedback mode with the parameter s set to 128
according to the definition of CFB mode given in Section 6.3 of [AES-MODE].
A clear diagram of the encryption and decryption process is given in
Figure 3 of [AES-MODE].

The plaintext is divided into 128-bit blocks. The last block may have
fewer than 128 bits, and no padding is required.

The first input block is the IV, and the forward cipher operation is
applied to the IV to produce the first output block. The first
ciphertext block is produced by exclusive-ORing the first plaintext
block with the first output block.  The ciphertext block is also used as
the input block for the subsequent forward cipher operation.

The process is repeated with the successive input blocks until a ciphertext
segment is produced from every plaintext segment.

The last ciphertext block is produced by exclusive-ORing the last
plaintext segment of r bits (r is less than or equal to 128) with the
segment of the r most significant bits of the last output block.
*/

/**
 * Encrypts bytes using AES.
 *
 * @param plaintext The plain bytes
 * @param secretPrivacyKey The secret privacy key
 * @param engineBoots
 * @param engineTime
 * @param salt The salt
 * @return The encrypted bytes
 * @throws uk.co.westhawk.snmp.stack.EncodingException
 */
public static byte[] AESencrypt(byte[] plaintext,
                                byte[] secretPrivacyKey,
                                int engineBoots,
                                int engineTime,
                                byte[] salt)
throws EncodingException
{
    byte[] aesKey = null;
    byte[] iv = null;
    try
    {
        aesKey = getAESKey(secretPrivacyKey);
        iv = getAESInitialValue(engineBoots, engineTime, salt);
    }
    catch (PduException exc)
    {
        throw new EncodingException(exc.getMessage());
    }
    // do some stuff
    AESEngine aes = new AESEngine();
    KeyParameter param = new KeyParameter(aesKey);
    aes.init(true, param);

    // no padding so
    int newL = plaintext.length;
    int bcount = newL / 16;
    byte[] result = new byte[newL];
    byte [] in = new byte[16];
    byte [] out = new byte[16];
    int posIn = 0;
    int posResult = 0;
    // initial input is the iv
    System.arraycopy(iv, 0, in, 0, 16);
    for (int b=0; b<bcount; b++)
    {
        aes.processBlock(in, 0, out, 0);
        for (int i=0;i < 16;i++)
        {
            in[i] = result[posResult] = (byte) (out[i] ^ plaintext[posIn]);
            posResult++;
            posIn++;
        }
    }
    // and the leftovers.
    if (posIn < newL)
    {
        aes.processBlock(in, 0, out, 0);
        for (int i =0;posIn < newL; i++)
        {
            result[posResult] = (byte) (out[i] ^ plaintext[posIn]);
            posResult++;
            posIn++;
        }
    }
    return result;
}

/**
 * Encrypts bytes using DES.
 * The plaintext needs to be a multiple of 8 octets. If it isn't, it
 * will be padded at the end.
 * This plaintext will be divided into 64-bit blocks. The plaintext for
 * each block is XOR-ed with the "ciphertext" of the previous block.
 * The result is then encrypted, added to the encrypted PDU portion of
 * the message, and used as the "ciphertext" for the next block. For the
 * first block, the initialization vector is used as the "ciphertext".
 *
 * @param plain The plain bytes
 * @param secretPrivacyKey The secret privacy key
 * @param salt The salt
 * @return The encrypted bytes
 */
public final static byte[] DESencrypt(byte[] plain, byte[] secretPrivacyKey,
    byte[] salt) throws EncodingException
{
    byte[] desKey = null;
    byte[] iv = null;
    try
    {
        desKey = getDESKey(secretPrivacyKey);
        iv = getDESInitialValue(secretPrivacyKey, salt);
    }
    catch (PduException exc)
    {
        throw new EncodingException(exc.getMessage());
    }

    // First pad the plain message with 0's
    int l = plain.length;
    int div = l / 8;
    int mod = l % 8;
    if (mod > 0)
    {
        div ++;
    }
    int newL = div*8;
    byte[] paddedOrig = new byte[newL];
    System.arraycopy(plain, 0, paddedOrig, 0, l);
    for (int i=l; i<newL; i++)
    {
        paddedOrig[i] = (byte) 0x0;
    }

    DESEngine des = new DESEngine();
    DESParameters param = new DESParameters(desKey);
    des.init(true, param);

    byte[] result = new byte[newL];
    byte [] in = new byte[8];
    byte [] cipherText = iv;
    int posIn = 0;
    int posResult = 0;
    for (int b=0; b<div; b++)
    {
        for (int i=0; i<8; i++)
        {
            in[i] = (byte) (paddedOrig[posIn] ^ cipherText[i]);
            posIn++;
        }
        des.processBlock(in, 0, cipherText, 0);
        System.arraycopy(cipherText, 0, result, posResult, cipherText.length);
        posResult += cipherText.length;
    }
    return result;
}

/**
 * Decryptes bytes using DES.
 * <ul>
 * <li>
 * If the length of the data portion is not a multiple of 8 bytes, the
 * message is discarded.
 * </li>
 * <li>
 * The first encrypted text block is decrypted. The decryption result is
 * XOR-ed with the initialization vector, and the result is the first
 * plaintext block.
 * </li>
 * <li>
 * The rest of the encrypted text blocks are treated similarly. They are
 * decrypted, with the results being XOR-ed with the previous encrypted
 * text block to obtain the plaintext block.
 * </li>
 * </ul>
 *
 * @param encryptedText The encrypted text
 * @param salt The salt
 * @param secretPrivacyKey The secret privacy key
 * @return The decrypted bytes
 */
public final static byte[] DESdecrypt(byte[] encryptedText, byte[] salt,
    byte[] secretPrivacyKey) throws DecodingException
{
    int l = encryptedText.length;
    int div = l / 8;
    int mod = l % 8;
    if (mod != 0)
    {
        throw new DecodingException("SnmpUtilities.decrypt():"
            + " The encrypted scoped PDU should be a multiple of 8 bytes");
    }

    byte[] desKey = null;
    byte[] iv = null;
    try
    {
        desKey = getDESKey(secretPrivacyKey);
        iv = getDESInitialValue(secretPrivacyKey, salt);
    }
    catch (PduException exc)
    {
        throw new DecodingException(exc.getMessage());
    }

    DESEngine des = new DESEngine();
    DESParameters param = new DESParameters(desKey);
    des.init(false, param);

    byte[] plain = new byte[l];
    byte [] in = new byte[8];
    byte [] out = new byte[8];
    byte [] cipherText = iv;
    int posPlain = 0;
    int posEncr = 0;
    for (int b=0; b<div; b++)
    {
        System.arraycopy(encryptedText, posEncr, in, 0, in.length);
        posEncr += in.length;
        des.processBlock(in, 0, out, 0);
        for (int i=0; i<8; i++)
        {
            plain[posPlain] = (byte)(out[i] ^ cipherText[i]);
            posPlain++;
        }
        System.arraycopy(in, 0, cipherText, 0, in.length);
    }
    return plain;
}

/*
3.1.4 Data Decryption.
In CFB decryption, the IV is the first input block, the first ciphertext
is used for the second input block, the second ciphertext is used for
the third input block, etc. The forward cipher function is applied to
each input block to produce the output blocks. The output blocks are
exclusive-ORed with the corresponding ciphertext blocks to recover the
plaintext blocks.

Page 9.
The last ciphertext block (whose size r is less than or equal to 128) is
exclusive-ORed with the segment of the r most significant bits of the
last output block to recover the last plaintext block of r bits.
*/

/**
 * Decrypts using AES. Note that it uses the _forward_ cipher mode.
 * @param ciphertext
 * @param secretPrivacyKey The secret privacy key
 * @param engineBoots
 * @param engineTime
 * @param salt The salt
 * @return The dencrypted bytes
 * @throws uk.co.westhawk.snmp.stack.DecodingException
 */
public final static byte[] AESdecrypt(byte[] ciphertext,
                                    byte[] secretPrivacyKey,
                                    int engineBoots,
                                    int engineTime,
                                    byte[] salt)
throws DecodingException
{
    byte[] aesKey = null;
    byte[] iv = null;
    try
    {
        aesKey = getAESKey(secretPrivacyKey);
        iv = getAESInitialValue(engineBoots, engineTime, salt);
    }
    catch (PduException exc)
    {
        throw new DecodingException(exc.getMessage());
    }
    // do some stuff
    AESEngine aes = new AESEngine();
    KeyParameter param = new KeyParameter(aesKey);
    aes.init(true, param);

    // no padding so
    int newL = ciphertext.length;
    int bcount = newL / 16;
    byte[] result = new byte[newL];
    byte [] in = new byte[16];
    byte [] out = new byte[16];
    int posIn = 0;
    int posResult = 0;
    // initial input is the iv
    System.arraycopy(iv, 0, in, 0, 16);
    for (int b=0; b<bcount; b++)
    {
        aes.processBlock(in, 0, out, 0);
        for (int i=0;i < 16;i++)
        {
            result[posResult] = (byte) (out[i] ^ ciphertext[posIn]);
            in[i] = ciphertext[posIn];
            posResult++;
            posIn++;
        }
    }
    // and the leftovers.
    if (posIn < newL)
    {
        aes.processBlock(in, 0, out, 0);
        for (int i =0;posIn < newL; i++)
        {
            result[posResult] = (byte) (out[i] ^ ciphertext[posIn]);
            posResult++;
            posIn++;
        }
    }
    return result;
}


// shared code for SHA and MD5
static byte [] getFingerPrint(byte [] key, byte [] message, boolean doSha)
{
    // see page 193 of 0-13-021453-1 A Practical Guide to SNMP
    byte [] k1 = new byte[64];
    byte [] k2 = new byte[64];
    // make the subkeys
    byte z1 = (byte) (0 ^ 0x36);
    byte z2 = (byte) (0 ^ 0x5c);
    int kl = key.length;
    int i = 0;
    while(i<kl)
    {
        k1[i] = (byte) (ifb(key[i]) ^ 0x36);
        k2[i] = (byte) (ifb(key[i]) ^ 0x5c);
        i++;
    }
    while (i<64)
    {
        k1[i] = z1;
        k2[i] = z2;
        i++;
    }
    // now prepend K1 to message and Hash the result
    byte [] interm = null;
    GeneralDigest mdc = doSha ? ((GeneralDigest)new SHA1Digest()) : ((GeneralDigest)new MD5Digest());
    mdc.reset();
    mdc.update(k1, 0, k1.length);
    mdc.update(message, 0, message.length);
    interm = new byte[mdc.getDigestSize()];
    mdc.doFinal(interm, 0);


    // prepend K2 to that and Hash it.
    byte [] rettmp = null;
    GeneralDigest mdc2 = doSha ? ((GeneralDigest)new SHA1Digest()) : ((GeneralDigest)new MD5Digest());
    mdc2.reset();
    mdc2.update(k2, 0, k2.length);
    mdc2.update(interm, 0, interm.length);
    rettmp = new byte[mdc2.getDigestSize()];
    mdc2.doFinal(rettmp, 0);

    // and shorten it to 12 bytes.
    byte [] ret = null;
    if (rettmp != null)
    {
        ret = new byte[12];
        System.arraycopy(rettmp, 0, ret, 0, 12);
    }
    return ret;
}

final static int ifb(byte b)
{
    return intFromByteWithoutStupidJavaSignExtension(b);
}

final static int intFromByteWithoutStupidJavaSignExtension(byte val)
{
    int ret = (0x7F) & val;
    if (val < 0)
    {
        ret += 128;
    }
    return ret;
}

final static void setBytesFromInt(byte[] ret, int value, int offs)
{
    int v = value ;
    int j = offs;
    ret[j++] = (byte)((v >>> 24) & 0xFF);
    ret[j++] = (byte)((v >>> 16) & 0xFF);
    ret[j++] = (byte)((v >>>  8) & 0xFF);
    ret[j++] = (byte)((v >>>  0) & 0xFF);
}

final static void setBytesFromLong(byte[] ret, long value, int offs)
{
    long v = value ;
    int j = offs;
    ret[j++] = (byte)((v >>> 56) & 0xFF);
    ret[j++] = (byte)((v >>> 48) & 0xFF);
    ret[j++] = (byte)((v >>> 40) & 0xFF);
    ret[j++] = (byte)((v >>> 32) & 0xFF);
    ret[j++] = (byte)((v >>> 24) & 0xFF);
    ret[j++] = (byte)((v >>> 16) & 0xFF);
    ret[j++] = (byte)((v >>>  8) & 0xFF);
    ret[j++] = (byte)((v >>>  0) & 0xFF);
}

}
