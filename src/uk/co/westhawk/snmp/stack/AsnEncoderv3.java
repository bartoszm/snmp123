// NAME
//      $RCSfile: AsnEncoderv3.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.5 $
// CREATED
//      $Date: 2009/03/05 12:48:59 $
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
import uk.co.westhawk.snmp.util.*;
import java.io.*;
import java.util.*;

/**
 * This class contains the v3 specific methods to encode a Pdu into bytes.
 * We split the original class AsnEncoder into four classes.
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 3.5 $ $Date: 2009/03/05 12:48:59 $
 */
class AsnEncoderv3 extends AsnEncoderBase
{
    private static final String     version_id =
        "@(#)$Id: AsnEncoderv3.java,v 3.5 2009/03/05 12:48:59 birgita Exp $ Copyright Westhawk Ltd";

    // 12 zero octets
    static byte dummyFingerPrint[] =
    {
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
        (byte)(0x0),
    };

/**
 * Encode SNMPv3 packet into bytes.
 */
byte[] EncodeSNMPv3(SnmpContextv3Basis context,
    int contextMsgId, TimeWindowNode node,
    byte msg_type, int pduId, int errstat, int errind, Enumeration ve)
    throws IOException, EncodingException
{
    ByteArrayOutputStream bout;
    // Create authentication
    AsnSequence asnTopSeq = new AsnSequence();

    // msgGlobalData = HeaderData
    AsnSequence asnHeaderData = new AsnSequence();
    asnHeaderData.add(new AsnInteger(contextMsgId));
    asnHeaderData.add(new AsnInteger(context.getMaxRecvSize()));
    asnHeaderData.add(new AsnOctets(getMsgFlags(context, msg_type)));
    asnHeaderData.add(new AsnInteger(context.USM_Security_Model));

    // msgData = ScopedPdu (plaintext or encrypted)
    AsnSequence asnPlainScopedPdu = new AsnSequence();
    asnPlainScopedPdu.add(new AsnOctets(context.getContextEngineId()));
    asnPlainScopedPdu.add(new AsnOctets(context.getContextName()));
    // PDU sequence.
    AsnObject asnPduObject = EncodePdu(msg_type, pduId, errstat, errind, ve);
    asnPlainScopedPdu.add(asnPduObject);

    // asnSecurityParameters
    if (AsnObject.debug > 10)
    {
        System.out.println("\nEncode USM: node " + node.toString());
    }
    AsnSequence asnSecurityObject = new AsnSequence();
    byte [] engineIdBytes = SnmpUtilities.toBytes(node.getSnmpEngineId());
    asnSecurityObject.add(new AsnOctets(engineIdBytes));
    asnSecurityObject.add(new AsnInteger(node.getSnmpEngineBoots()));
    asnSecurityObject.add(new AsnInteger(node.getSnmpEngineTime()));
    asnSecurityObject.add(new AsnOctets(context.getUserName()));

    AsnOctets fingerPrintOct;
    if (context.isUseAuthentication())
    {
        fingerPrintOct = new AsnOctets(dummyFingerPrint);
    }
    else
    {
        fingerPrintOct = new AsnOctets("");
    }
    asnSecurityObject.add(fingerPrintOct);

    AsnOctets privOct;
    AsnOctets asnEncryptedScopedPdu = null;
    if (context.isUsePrivacy())
    {
        byte[] privKey = null;
        int aprot = context.getAuthenticationProtocol();
        if (aprot == context.MD5_PROTOCOL)
        {
            byte[] passwKey = context.getPrivacyPasswordKeyMD5();
            privKey = SnmpUtilities.getLocalizedKeyMD5(passwKey, 
                  node.getSnmpEngineId());
        }
        else
        {
            byte[] passwKey = context.getPrivacyPasswordKeySHA1();
            privKey = SnmpUtilities.getLocalizedKeySHA1(passwKey, 
                  node.getSnmpEngineId());
        }

        int pprot = context.getPrivacyProtocol();
        byte [] salt = null;
        if (pprot == context.AES_ENCRYPT) 
        {
            salt = SnmpUtilities.getSaltAES();
        }
        else
        {
            salt = SnmpUtilities.getSaltDES(node.getSnmpEngineBoots());
        }

        privOct = new AsnOctets(salt);
        bout = new ByteArrayOutputStream();
        asnPlainScopedPdu.write(bout);

        byte[] plaintext = bout.toByteArray();
        byte[] encryptedText = null;
        if (pprot == context.AES_ENCRYPT) 
        {
            encryptedText = SnmpUtilities.AESencrypt(plaintext, privKey,
                node.getSnmpEngineBoots(), node.getSnmpEngineTime(), salt);
        }
        else
        {
            encryptedText = SnmpUtilities.DESencrypt(plaintext, privKey, salt);
        }
        
        asnEncryptedScopedPdu = new AsnOctets(encryptedText);
        if (AsnObject.debug > 10)
        {
            System.out.println("Encrypted body  with "+context.ProtocolNames[pprot]);
        }
    }
    else
    {
        privOct = new AsnOctets("");
    }
    asnSecurityObject.add(privOct);

    ByteArrayOutputStream secOut = new ByteArrayOutputStream();
    asnSecurityObject.write(secOut);
    byte [] bytes = secOut.toByteArray();
    AsnOctets asnSecurityParameters = new AsnOctets(bytes);


    asnTopSeq.add(new AsnInteger(SnmpConstants.SNMP_VERSION_3));
    asnTopSeq.add(asnHeaderData);
    asnTopSeq.add(asnSecurityParameters);
    if (context.isUsePrivacy())
    {
        asnTopSeq.add(asnEncryptedScopedPdu);
    }
    else
    {
        asnTopSeq.add(asnPlainScopedPdu);
    }


    if (AsnObject.debug > 10)
    {
        System.out.println("\n" + getClass().getName() + ".EncodeSNMPv3(): ");
    }
    // Write SNMP object
    bout = new ByteArrayOutputStream();
    asnTopSeq.write(bout);

    int sz = bout.size();
    if (sz > context.getMaxRecvSize())
    {
        throw new EncodingException("Packet size ("+ sz 
            + ") is > maximum size (" + context.getMaxRecvSize() +")");
    }
    byte[] message = bout.toByteArray();

    // can only do this at after building the whole message
    if (context.isUseAuthentication())
    {
        byte[] calcFingerPrint = null;

        int prot = context.getAuthenticationProtocol();
        if (prot == context.MD5_PROTOCOL)
        {
            byte[] passwKey = context.getAuthenticationPasswordKeyMD5();
            byte[] authkey = SnmpUtilities.getLocalizedKeyMD5(passwKey, 
                  node.getSnmpEngineId());
            calcFingerPrint = SnmpUtilities.getFingerPrintMD5(authkey, 
                  message);
        }
        else
        {
            byte[] passwKey = context.getAuthenticationPasswordKeySHA1();
            byte[] authkey = SnmpUtilities.getLocalizedKeySHA1(passwKey, 
                  node.getSnmpEngineId());
            calcFingerPrint = SnmpUtilities.getFingerPrintSHA1(authkey, 
                  message);
        }

        int usmPos = asnSecurityParameters.getContentsPos();
        int fpPos = fingerPrintOct.getContentsPos();
        fpPos += usmPos;
        if (AsnObject.debug > 10)
        {
            int fpLength = fingerPrintOct.getContentsLength();
            String str = "Pos finger print = " + fpPos
                + ", len = " + fpLength;
            SnmpUtilities.dumpBytes(str, calcFingerPrint);
        }

        // Replace the dummy finger print with the real finger print
        System.arraycopy(calcFingerPrint, 0, 
              message, fpPos, dummyFingerPrint.length);
    }
    return message;
}


private byte[] getMsgFlags(SnmpContextv3Basis context, byte msg_type) throws EncodingException
{
    byte authMask = (byte)(0x0);
    if (context.isUseAuthentication())
    {
        authMask = (byte)(0x1);
    }
    byte privMask = (byte)(0x0);
    if (context.isUsePrivacy())
    {
        if (context.isUseAuthentication())
        {
            privMask = (byte)(0x2);
        }
        else
        {
            throw new EncodingException("Encryption without authentication is not allowed");
        }
    }
    byte reportMask = (byte)(0x0);
    if (context.isAuthoritative(msg_type) == false)
    {
        reportMask = (byte)(0x4);
    }
    byte [] msgFlags = new byte[1];
    msgFlags[0] = (byte) (authMask | privMask | reportMask);
    return msgFlags;
}

}
