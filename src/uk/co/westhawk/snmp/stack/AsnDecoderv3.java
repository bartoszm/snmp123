// NAME
//      $RCSfile: AsnDecoderv3.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.9 $
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
 * This class contains the v3 specific methods to decode bytes into a Pdu.
 * We split the original class AsnDecoder into four classes.
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 3.9 $ $Date: 2009/03/05 12:48:59 $
 */
class AsnDecoderv3 extends AsnDecoderBase implements usmStatsConstants
{
    private static final String     version_id =
        "@(#)$Id: AsnDecoderv3.java,v 3.9 2009/03/05 12:48:59 birgita Exp $ Copyright Westhawk Ltd";


/**
 * Returns the msgId of the SNMPv3 asn sequence.
 */
int getMsgId(AsnSequence asnTopSeq) throws DecodingException
{
    int msgId = -1;
    AsnSequence asnHeaderData = getAsnHeaderData(asnTopSeq);
    AsnObject obj = asnHeaderData.getObj(0);
    if (obj instanceof AsnInteger)
    {
        AsnInteger value = (AsnInteger) obj;
        msgId = value.getValue();
    }
    else
    {
        String msg = "msgId should be of type AsnInteger"
            + " instead of " + obj.getRespTypeString();
        throw new DecodingException(msg);
    }
    return msgId;
}

/**
 * This method creates an AsnPduSequence out of the characters of the
 * InputStream for v3.
 *
 * @see AbstractSnmpContext#run
 * @see SnmpContextv3#processIncomingResponse
 * @see SnmpContextv3#processIncomingPdu
 */
AsnSequence DecodeSNMPv3(InputStream in)
throws IOException, DecodingException
{
    AsnSequence asnTopSeq = getAsnSequence(in);
    int snmpVersion = getSNMPVersion(asnTopSeq);
    if (snmpVersion != SnmpConstants.SNMP_VERSION_3)
    {
        String str = SnmpUtilities.getSnmpVersionString(snmpVersion);
        String msg = "Wrong SNMP version: expected SNMPv3, received "
            + str;
        throw new DecodingException(msg);
    }
    else
    {
        int securityModel = -1;
        AsnSequence asnHeaderData = getAsnHeaderData(asnTopSeq);
        AsnObject obj = asnHeaderData.getObj(3);
        if (obj instanceof AsnInteger)
        {
            AsnInteger value = (AsnInteger) obj;
            securityModel = value.getValue();
            if (securityModel != SnmpContextv3Face.USM_Security_Model)
            {
                String msg = "Wrong v3 Security Model: expected USM("
                    + SnmpContextv3Face.USM_Security_Model
                    + "), received "
                    + securityModel;
                throw new DecodingException(msg);
            }
        }
        else
        {
            String msg = "securityModel should be of type AsnInteger"
                + " instead of " + obj.getRespTypeString();
            throw new DecodingException(msg);
        }
    }
    return asnTopSeq;
}


/**
 * Processes the SNMP v3 AsnSequence.
 * See section 3.2 of <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 */
AsnPduSequence processSNMPv3(SnmpContextv3Basis context, AsnSequence asnTopSeq, byte[] message, boolean amIAuthoritative)
throws IOException, DecodingException
{
    AsnPduSequence pduSeq = null;

    // if not correct, I'll just skip a lot of tests.
    boolean isCorrect = asnTopSeq.isCorrect;

    AsnSequence asnHeaderData = getAsnHeaderData(asnTopSeq);
    //int msgId = ((AsnInteger)asnHeaderData.getObj(0)).getValue();
    //int maxSize = ((AsnInteger)asnHeaderData.getObj(1)).getValue();
    byte [] msgFlags = ((AsnOctets)asnHeaderData.getObj(2)).getBytes();
    boolean isUseAuthentication = isUseAuthentication(msgFlags[0]);
    boolean isUsePrivacy = isUsePrivacy(msgFlags[0]);

    AsnOctets asnSecurityParameters = (AsnOctets)asnTopSeq.getObj(2);
    AsnSequence usmObject = decodeUSM(asnSecurityParameters);

    byte [] engineIdBytes = ((AsnOctets)usmObject.getObj(0)).getBytes();
    String engineId = SnmpUtilities.toHexString(engineIdBytes);
    int boots = ((AsnInteger)usmObject.getObj(1)).getValue();
    int time = ((AsnInteger)usmObject.getObj(2)).getValue();
    String userName = ((AsnOctets)usmObject.getObj(3)).getValue();
    AsnOctets realFingerPrintObject = (AsnOctets)usmObject.getObj(4);
    byte [] realFingerPrint = realFingerPrintObject.getBytes();
    byte [] salt = ((AsnOctets)usmObject.getObj(5)).getBytes();

    TimeWindow tWindow = TimeWindow.getCurrent();
    if (amIAuthoritative == false)
    {
        /* engineId should not be empty, however net-snmp 5.3.0.1 has a
         * bug (1427410) in their trapsess; it sends an empty engineId
         */
        if (engineId.length() > 0 
                &&
            tWindow.isEngineIdOK(context.getReceivedFromHostAddress(),
            context.getPort(), engineId) == false)
        {
            String msg = "Received engine Id ('" + engineId + "') is not correct.";
            msg += " amIAuthoritative == false";
            throw new DecodingException(msg);
        }
        else
        {
            // This should only happen once, if for some reason the sendto
            // and receivedfrom addresses are different. 
            // It is a hack, but it needs to seem like the 'sendToHostAddress' 
            // has been discovered, else any other request sent to this
            // address will try to do another discovery and the process
            // starts again.
            String sendToHostAddress = context.getSendToHostAddress();
            String receivedFromHostAddress = context.getReceivedFromHostAddress();
            if (sendToHostAddress.equals(receivedFromHostAddress) == false)
            {
                String storedEngineId;
                storedEngineId = tWindow.getSnmpEngineId(sendToHostAddress, context.getPort()); 
                if (storedEngineId == null)
                {
                    tWindow.setSnmpEngineId(sendToHostAddress, context.getPort(), "00");
                }
            }
        }
    }
    else
    {
        // amIAuthoritative == true
        // Section 3.2 rfc
        // engineId of length '0' -> discovery.
        if (engineId.length() > 0 
                &&
            tWindow.isEngineIdOK(context.getUsmAgent().MYFAKEHOSTNAME,
                context.getPort(), engineId) == false)
        {
            String msg = "Received engine Id ('" + engineId + "') is not correct.";
            msg += " amIAuthoritative == true";
            throw new DecodingException(msg);
        }
    }

    if (userName.equals(context.getUserName()) == false)
    {
        String msg = "Received userName ('" + userName + "') is not correct";
        throw new DecodingException(msg);
    }

    // I'm not really supposed to encrypt before checking and doing
    // authentication, but I would like to use the pduSeq
    // So, I'll encrypt and save the possible exception.
    DecodingException encryptionDecodingException = null;
    IOException encryptionIOException = null;
    try
    {
        AsnObject asnScopedObject = asnTopSeq.getObj(3);
        AsnSequence asnPlainScopedPdu = null;
        if (isUsePrivacy == true)
        {
            // if decryption was used, the asnScopedObject would be AsnOctets
            byte[] privKey = null;
            int prot = context.getAuthenticationProtocol();
            if (prot == context.MD5_PROTOCOL)
            {
                byte[] passwKey = context.getPrivacyPasswordKeyMD5();
                privKey = SnmpUtilities.getLocalizedKeyMD5(passwKey, engineId);
            }
            else
            {
                byte[] passwKey = context.getPrivacyPasswordKeySHA1();
                privKey = SnmpUtilities.getLocalizedKeySHA1(passwKey, engineId);
            }

            AsnOctets asnEncryptedScopedPdu = (AsnOctets)asnScopedObject;
            byte[] encryptedText = asnEncryptedScopedPdu.getBytes();

            byte[] plainText = null;
            int pprot = context.getPrivacyProtocol();
            if (pprot == context.AES_ENCRYPT) 
            {
                plainText = SnmpUtilities.AESdecrypt(encryptedText,
                    privKey, boots, time, salt);
            }
            else
            {
                plainText = SnmpUtilities.DESdecrypt(encryptedText,
                    salt, privKey);
            }
 
            if (AsnObject.debug > 10)
            {
                System.out.println("Encrypted PDU: ");
                System.out.println("Decoding with : "+context.ProtocolNames[pprot]);
            }

            ByteArrayInputStream plainIn = new ByteArrayInputStream(plainText);
            asnPlainScopedPdu = getAsnSequence(plainIn);
        }
        else
        {
            asnPlainScopedPdu = (AsnSequence)asnScopedObject;
        }

        byte [] contextId = ((AsnOctets)asnPlainScopedPdu.getObj(0)).getBytes();
        String contextName = ((AsnOctets)asnPlainScopedPdu.getObj(1)).getValue();
        pduSeq = (AsnPduSequence) asnPlainScopedPdu.findPdu();
    }
    catch (DecodingException exc)
    {
        encryptionDecodingException = exc;
    }
    catch (IOException exc)
    {
        encryptionIOException = exc;
    }
    if (pduSeq != null && engineId.length() == 0)
    {
        pduSeq.setSnmpv3Discovery(true);
    }

    boolean userIsUsingAuthentication = context.isUseAuthentication();
    if (isCorrect == true && (isUseAuthentication != userIsUsingAuthentication))
    {
        String msg = "User " + userName + " does ";
        if (userIsUsingAuthentication == false)
        {
            msg += "not ";
        }
        msg += "support authentication, but received message ";

        if (isUseAuthentication)
        {
            msg += "with authentication.";
        }
        else
        {
            msg += "without authentication";
            msg += getUsmStats(pduSeq);
        }
        throw new DecodingException(msg);
    }

    boolean isAuthentic = false;
    if (isCorrect == true && isUseAuthentication == true)
    {
        int fpPos = realFingerPrintObject.getContentsPos();
        if (AsnObject.debug > 10)
        {
            int fpLength = realFingerPrintObject.getContentsLength();
            String str = "Pos finger print = " + fpPos
                + ", len = " + fpLength;
            SnmpUtilities.dumpBytes(str, realFingerPrint);
        }

        byte[] calcFingerPrint = null;
        // Replace the real finger print with the dummy finger print
        System.arraycopy(AsnEncoderv3.dummyFingerPrint, 0, 
              message, fpPos, realFingerPrint.length);
        int prot = context.getAuthenticationProtocol();
        if (prot == context.MD5_PROTOCOL)
        {
            byte[] passwKey = context.getAuthenticationPasswordKeyMD5();
            byte[] authkey = SnmpUtilities.getLocalizedKeyMD5(passwKey,
                  engineId);
            calcFingerPrint = SnmpUtilities.getFingerPrintMD5(authkey,
                  message);
        }
        else
        {
            byte[] passwKey = context.getAuthenticationPasswordKeySHA1();
            byte[] authkey = SnmpUtilities.getLocalizedKeySHA1(passwKey,
                  engineId);
            calcFingerPrint = SnmpUtilities.getFingerPrintSHA1(authkey,
                  message);
        }

        if (SnmpUtilities.areBytesEqual(realFingerPrint, calcFingerPrint) == false)
        {
            String msg = "Authentication comparison failed";
            throw new DecodingException(msg);
        }
        else
        {
            if (pduSeq != null && boots == 0 && time == 0)
            {
                pduSeq.setSnmpv3Discovery(true);
            }
            if (tWindow.isOutsideTimeWindow(engineId, boots, time))
            {
                String msg = "Message is outside time window";
                throw new DecodingException(msg);
            }
            isAuthentic = true;
        }
    }
    tWindow.updateTimeWindow(engineId, boots, time, isAuthentic);

    boolean userIsUsingPrivacy = context.isUsePrivacy();
    if (isCorrect == true && (isUsePrivacy != userIsUsingPrivacy))
    {
        String msg = "User " + userName + " does ";
        if (userIsUsingPrivacy == false)
        {
            msg += "not ";
        }
        msg += "support privacy, but received message ";
        if (isUsePrivacy)
        {
            msg += "with privacy.";
        }
        else
        {
            msg += "without privacy";
            msg += getUsmStats(pduSeq);
        }
        throw new DecodingException(msg);
    }

    if (encryptionDecodingException != null)
    {
        throw encryptionDecodingException;
    }
    if (encryptionIOException != null)
    {
        throw encryptionIOException;
    }

    if (pduSeq != null && isCorrect == false)
    {
        pduSeq.isCorrect = false;
    }
    return pduSeq;
}


private boolean isUseAuthentication(byte msgFlags)
{
    boolean isUseAuthentication = ((byte)(0x01) & msgFlags) > 0;
    return isUseAuthentication;
}


private boolean isUsePrivacy(byte msgFlags)
{
    boolean isUsePrivacy = ((byte)(0x02) & msgFlags) > 0;
    return isUsePrivacy;
}


private AsnSequence decodeUSM(AsnOctets asnSecurityParameters)
throws IOException
{
    byte [] usmBytes = asnSecurityParameters.getBytes();
    if (AsnObject.debug > 10)
    {
        SnmpUtilities.dumpBytes("Decoding USM:", usmBytes);
    }

    ByteArrayInputStream usmIn = new ByteArrayInputStream(usmBytes);
    AsnSequence usmOctets = new AsnSequence(usmIn, usmBytes.length, 
          asnSecurityParameters.getContentsPos());
    AsnSequence usmObject = (AsnSequence)usmOctets.getObj(0);
    return usmObject;
}


/**
 * Sometimes when an error occurs the usmStats is sent in the varbind
 * list.
 */
private String getUsmStats(AsnPduSequence pduSeq)
{
    String msg = "";
    AsnSequence varBind = (AsnSequence) pduSeq.getObj(3);
    int size = varBind.getObjCount();
    if (size > 0)
    {
        AsnSequence varSeq = (AsnSequence) varBind.getObj(0);
        varbind vb = new varbind(varSeq);
        AsnObjectId oid = vb.getOid();
        boolean found=false;
        int i=0;
        while (i< usmStatsOids.length && found==false)
        {
            AsnObjectId usmOid = new AsnObjectId(usmStatsOids[i]);
            found = (oid.startsWith(usmOid) == true);
            i++;
        }
        if (found == true)
        {
            i--;
            msg += ": " + usmStatsStrings[i] + " " + vb.getValue();
        }
        else
        {
            msg += ": " + vb;
        }
    }
    return msg;
}


}
