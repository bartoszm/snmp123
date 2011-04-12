// NAME
//      $RCSfile: SnmpContextv3Basis.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.17 $
// CREATED
//      $Date: 2009/03/05 15:51:42 $
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
 */

package uk.co.westhawk.snmp.stack;

import java.net.*;
import java.io.*;
import java.util.*;

import uk.co.westhawk.snmp.pdu.*;
import uk.co.westhawk.snmp.util.*;
import uk.co.westhawk.snmp.event.*;
import uk.co.westhawk.snmp.beans.*;

/**
 * This class contains the basis for the SNMP v3 contexts that is needed 
 * by every PDU to send a SNMP v3 request.
 *
 * <p>
 * This class will perform the v3 discovery of the SNMP engine ID and
 * time line if necessary. This is done with the classes
 * <code>TimeWindow</code> and <code>UsmDiscoveryBean</code>.
 * </p>
 *
 * <p>
 * Now that the stack can send traps and receive requests, 
 * it needs to be able to act as an
 * authoritative SNMP engine. This is done via the interface UsmAgent.
 * The DefaultUsmAgent is not guaranteed to work; agents (or rather 
 * authoritative engines) <em>should</em> provide a better implementation.
 * </p>
 *
 * <p>
 * This class will use the User Security Model (USM) as described in 
 * <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 * See also <a href="http://www.ietf.org/rfc/rfc3411.txt">RFC 3411</a>.
 * </p>
 *
 * <p>
 * It is advised to set all the properties of this class before any PDU,
 * using this class, is sent. 
 * All properties are being used to encode the message. Some properties are 
 * being used to decode the Response or Report PDU. 
 * When any of these last properties were changed in between flight there 
 * is a possibility the decoding fails, causing a
 * <code>DecodingException</code>. 
 * </p>
 * 
 * <p>
 * <code>destroy()</code> should be called when the context is no longer
 * used. This is the only way the threads will be stopped and garbage
 * collected.
 * </p>
 *
 * @see SnmpContextv3Face
 * @see SnmpContextv3Pool
 * @see TimeWindow
 * @see UsmAgent
 * @see DefaultUsmAgent
 * @see #setUsmAgent(UsmAgent)
 * @see uk.co.westhawk.snmp.beans.UsmDiscoveryBean
 *
 * @since 4_14
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.17 $ $Date: 2009/03/05 15:51:42 $
 */
public abstract class SnmpContextv3Basis extends AbstractSnmpContext 
implements SnmpContextv3Face, Cloneable
{
    private static final String     version_id =
        "@(#)$Id: SnmpContextv3Basis.java,v 3.17 2009/03/05 15:51:42 birgita Exp $ Copyright Westhawk Ltd";

    protected String userName = Default_UserName;
    protected boolean useAuthentication = false;
    protected String userAuthenticationPassword;
    protected byte[] userAuthKeyMD5 = null;
    protected byte[] userAuthKeySHA1 = null;
    protected int authenticationProtocol = MD5_PROTOCOL;
    protected int privacyProtocol = DES_ENCRYPT ;
    protected boolean usePrivacy = false;
    protected String userPrivacyPassword;
    protected byte[] userPrivKeyMD5 = null;
    protected byte[] userPrivKeySHA1 = null;
    protected byte [] contextEngineId = new byte[0];
    protected String contextName = Default_ContextName;
    protected UsmAgent usmAgent = null;

    private Hashtable msgIdHash = new Hashtable(MAXPDU);
    private static int  next_id = 1;

/**
 * Constructor.
 *
 * @param host The host to which the PDU will be sent
 * @param port The port where the SNMP server will be
 * @see AbstractSnmpContext#AbstractSnmpContext(String, int)
 */
public SnmpContextv3Basis(String host, int port) throws java.io.IOException
{
    this(host, port, null, STANDARD_SOCKET);
}

/**
 * Constructor.
 * Parameter typeSocketA should be either STANDARD_SOCKET, TCP_SOCKET or a
 * fully qualified classname.
 *
 * @param host The host to which the Pdu will be sent
 * @param port The port where the SNMP server will be
 * @param typeSocketA The local address the server will bind to
 *
 * @see AbstractSnmpContext#AbstractSnmpContext(String, int, String)
 */
public SnmpContextv3Basis(String host, int port, String typeSocketA) 
throws java.io.IOException
{
    this(host, port, null, typeSocketA);
}

/**
 * Constructor.
 * Parameter typeSocketA should be either STANDARD_SOCKET, TCP_SOCKET or a
 * fully qualified classname.
 *
 * @param host The host to which the PDU will be sent
 * @param port The port where the SNMP server will be
 * @param bindAddress The local address the server will bind to
 * @param typeSocketA The type of socket to use. 
 *
 * @see AbstractSnmpContext#AbstractSnmpContext(String, int, String)
 * @see SnmpContextBasisFace#STANDARD_SOCKET
 * @see SnmpContextBasisFace#TCP_SOCKET
 * @since 4_14
 */
public SnmpContextv3Basis(String host, int port, String bindAddress, String typeSocketA) 
throws java.io.IOException
{
    super(host, port, bindAddress, typeSocketA);

    if (TimeWindow.getCurrent() == null)
    {
        TimeWindow timew = new TimeWindow();
    }
    setUsmAgent(createUsmAgent());
}

public int getVersion()
{
    return SnmpConstants.SNMP_VERSION_3;
}

/**
 * Returns the username.
 *
 * @return the username
 */
public String getUserName()
{
    return userName;
}

/**
 * Sets the username.
 * This username will be used for all PDUs sent with this context.
 * The username corresponds to the 'msgUserName' in 
 * <a href="http://www.ietf.org/rfc/rfc3414.txt">SNMP-USER-BASED-SM-MIB</a>.
 * The default value is "initial".
 *
 * @param newUserName The new username
 * @see #Default_UserName
 */
public void setUserName(String newUserName)
{
    userName = newUserName;
}

/**
 * Returns if authentication is used or not.
 * By default no authentication will be used.
 *
 * @return true if authentication is used, false if not
 */
public boolean isUseAuthentication()
{
    return useAuthentication;
}

/**
 * Sets whether authentication has to be used. 
 * By default no authentication will be used.
 *
 * @param newUseAuthentication The use of authentication
 */
public void setUseAuthentication(boolean newUseAuthentication)
{
    useAuthentication = newUseAuthentication;
}

/**
 * Returns the user authentication password.
 * This password will be transformed into the user authentication secret key.
 *
 * @return The user authentication password
 */
public String getUserAuthenticationPassword()
{
    return userAuthenticationPassword;
}

/**
 * Sets the user authentication password.
 * This password will be transformed into the user authentication secret
 * key. A user MUST set this password.
 *
 * @param newUserAuthPassword The user authentication password
 */
public void setUserAuthenticationPassword(String newUserAuthPassword)
{
    if (newUserAuthPassword != null
            &&
        newUserAuthPassword.equals(userAuthenticationPassword) == false)
    {
        userAuthenticationPassword = newUserAuthPassword;
        userAuthKeyMD5 = null;
        userAuthKeySHA1 = null;
    }
}

/**
 * Sets the protocol to be used for authentication.
 * This can either be MD5 or SHA-1.
 * By default MD5 will be used.
 *
 * @param protocol The authentication protocol to be used
 * @see #MD5_PROTOCOL
 * @see #SHA1_PROTOCOL
 */
public void setAuthenticationProtocol(int protocol)
throws IllegalArgumentException
{
    if (protocol == MD5_PROTOCOL || protocol == SHA1_PROTOCOL)
    {
        if (protocol != authenticationProtocol)
        {
            authenticationProtocol = protocol;
        }
    }
    else
    {
        throw new IllegalArgumentException("Authentication Protocol "
            + "should be MD5 or SHA1");
    }
}

/**
 * Returns the protocol to be used for authentication.
 * This can either be MD5 or SHA-1.
 * By default MD5 will be used.
 *
 * @return The authentication protocol to be used
 * @see #MD5_PROTOCOL
 * @see #SHA1_PROTOCOL
 */
public int getAuthenticationProtocol()
{
    return authenticationProtocol;
}

/**
 * Sets the protocol to be used for privacy.
 * This can either be DES or AES.
 * By default DES will be used.
 *
 * @param protocol The privacy protocol to be used
 * @see SnmpContextv3Face#AES_ENCRYPT
 * @see SnmpContextv3Face#DES_ENCRYPT
 */
public void setPrivacyProtocol(int protocol)
throws IllegalArgumentException
{
    if (protocol == DES_ENCRYPT || protocol == AES_ENCRYPT)
    {
        if (protocol != privacyProtocol)
        {
            privacyProtocol = protocol;
        }
    }
    else
    {
        throw new IllegalArgumentException("Privacy Encryption "
            + "should be AES or DES");
    }
}

/**
 * Returns the protocol to be used for privacy.
 * This can either be DES or AES.
 * By default DES will be used.
 *
 * @return The privacy protocol to be used
 * @see SnmpContextv3Face#AES_ENCRYPT
 * @see SnmpContextv3Face#DES_ENCRYPT
 */
public int getPrivacyProtocol()
{
    return privacyProtocol;    
}
    
byte[] getAuthenticationPasswordKeyMD5()
{
    if (userAuthKeyMD5 == null)
    {
        userAuthKeyMD5 = SnmpUtilities.passwordToKeyMD5(userAuthenticationPassword);
    }
    return userAuthKeyMD5;
}

byte[] getAuthenticationPasswordKeySHA1()
{
    if (userAuthKeySHA1 == null)
    {
        userAuthKeySHA1 = SnmpUtilities.passwordToKeySHA1(userAuthenticationPassword);
    }
    return userAuthKeySHA1;
}


byte[] getPrivacyPasswordKeyMD5()
{
    if (userPrivKeyMD5 == null)
    {
        userPrivKeyMD5 = SnmpUtilities.passwordToKeyMD5(userPrivacyPassword);
    }
    return userPrivKeyMD5;
}

byte[] getPrivacyPasswordKeySHA1()
{
    if (userPrivKeySHA1 == null)
    {
        userPrivKeySHA1 = SnmpUtilities.passwordToKeySHA1(userPrivacyPassword);
    }
    return userPrivKeySHA1;
}


/**
 * Returns if privacy is used or not.
 * By default privacy is not used.
 *
 * @return true if privacy is used, false if not
 */
public boolean isUsePrivacy()
{
    return usePrivacy;
}

/**
 * Sets whether privacy has to be used. 
 * By default privacy is not used.
 * Note, privacy (encryption) without authentication is not allowed.
 *
 * @param newUsePrivacy The use of privacy
 */
public void setUsePrivacy(boolean newUsePrivacy)
{
    usePrivacy = newUsePrivacy;
}

/**
 * Returns the user privacy password.
 * This password will be transformed into the user privacy secret key.
 *
 * @return The user privacy password
 */
public String getUserPrivacyPassword()
{
    return userPrivacyPassword;
}


/**
 * Sets the user privacy password.
 * This password will be transformed into the user privacy secret
 * key. A user <em>must</em> set this password in order to use privacy.
 *
 * @param newUserPrivacyPassword The user privacy password
 */
public void setUserPrivacyPassword(String newUserPrivacyPassword)
{
    if (newUserPrivacyPassword != null
            &&
        newUserPrivacyPassword.equals(userPrivacyPassword) == false)
    {
        userPrivacyPassword = newUserPrivacyPassword;
        userPrivKeyMD5 = null;
        userPrivKeySHA1 = null;
    }
}


/**
 * Sets the contextEngineID. 
 * See <a href="http://www.ietf.org/rfc/rfc3411.txt">RFC 3411</a>.
 *
 * A contextEngineID uniquely
 * identifies an SNMP entity that may realize an instance of a context
 * with a particular contextName.
 * 
 * <p>
 * Note, when the stack is an authoritative engine, this parameter should
 * equal the UsmAgent.getSnmpEngineId(). See the StackUsage
 * documentation for an explanation.
 * </p>
 *
 * <p>
 * If the contextEngineID is of length zero, the encoder will use the (discovered)
 * snmpEngineId.
 * </p>
 *
 * @see UsmAgent#getSnmpEngineId()
 * @param newContextEngineId The contextEngineID
 */
public void setContextEngineId(byte [] newContextEngineId)
throws IllegalArgumentException
{
    if (newContextEngineId != null)
    {
        contextEngineId = newContextEngineId;
    }
    else
    {
        throw new IllegalArgumentException("contextEngineId is null");
    }
}

/**
 * Returns the contextEngineID. 
 *
 * @return The contextEngineID
 */
public byte [] getContextEngineId()
{
    return contextEngineId;
}

/**
 * Sets the contextName. 
 * See <a href="http://www.ietf.org/rfc/rfc3411.txt">RFC 3411</a>.
 *
 * A contextName is used to name a context. Each contextName MUST be
 * unique within an SNMP entity.
 * By default this is "" (the empty String). 
 *
 * @param newContextName The contextName
 * @see #Default_ContextName
 */
public void setContextName(String newContextName)
{
    contextName = newContextName;
}

/**
 * Returns the contextName. 
 *
 * @return The contextName
 */
public String getContextName()
{
    return contextName;
}

/**
 * Adds a discovery pdu. This method adds the PDU (without checking if
 * discovery is needed).
 *
 * @param pdu the discovery pdu
 * @return pdu is succesful added
 * @see AbstractSnmpContext#addPdu(Pdu)
 * @see #addPdu(Pdu)
 */
public boolean addDiscoveryPdu(DiscoveryPdu pdu)
throws java.io.IOException, PduException
{
    // since this is a DiscoveryPdu we do not check for discovery :-)
    return this.addPdu(pdu, false);
}

/**
 * Adds a PDU. This method adds the PDU and blocks until it has all the
 * discovery parameters it needs.
 *
 * @param pdu the PDU
 * @return pdu is succesful added
 * @see AbstractSnmpContext#addPdu(Pdu)
 * @see #addDiscoveryPdu(DiscoveryPdu)
 */
public boolean addPdu(Pdu pdu)
throws java.io.IOException, PduException
{
    return this.addPdu(pdu, true);
}

/**
 * Creates the USM agent. 
 * @see DefaultUsmAgent
 * @see #isAuthoritative
 */
protected UsmAgent createUsmAgent()
{
    return new DefaultUsmAgent();
}

/**
 * Sets the UsmAgent, needed when this stack is used as authoritative
 * SNMP engine. This interface provides authentiation details, like its
 * clock and its Engine ID.
 * 
 * @see DefaultUsmAgent
 * @param agent The USM authoritative interface
 * @since 4_14
 */
public void setUsmAgent(UsmAgent agent)
{
    usmAgent = agent;
}

/**
 * Returns the UsmAgent.
 * @see #setUsmAgent
 * @since 4_14
 */
public UsmAgent getUsmAgent()
{
    return usmAgent;
}

/**
 * Adds a PDU. This method adds the PDU and checks if discovery is
 * needed depending on the parameter <code>checkDiscovery</code>.
 * If discovery is needed this method will block until it has done so.
 * Discovery is only needed if the stack is non authoritative.
 *
 * <p>
 * This method stores the SNMPv3 msgId and PDU
 * request id in a Hashtable. 
 * Since the encoding only happens once and every retry sends the same 
 * encoded packet, only one msgId is used.
 * </p>
 *
 * @param pdu the PDU
 * @param checkDiscovery check if discovery is needed
 * @return pdu is succesful added
 * @see AbstractSnmpContext#addPdu(Pdu)
 * @see #addDiscoveryPdu(DiscoveryPdu)
 * @see #addPdu(Pdu)
 */
protected boolean addPdu(Pdu pdu, boolean checkDiscovery)
throws java.io.IOException, PduException
{
    // TODO, when sending response or report, the msgId should be set!
    Integer msgId = pdu.snmpv3MsgId;
    if (msgId == null)
    {
        msgId = new Integer(next_id++);
    }
    else if (pdu.isExpectingResponse() == true)
    {
        // generate a new msgId, even if this is already set. The user
        // could be adding the same PDU more than once to the
        // context.
        msgId = new Integer(next_id++);
    }
    pdu.snmpv3MsgId = msgId;

    msgIdHash.put(msgId, new Integer(pdu.req_id));
    if (AsnObject.debug > 6)
    {
        System.out.println(getClass().getName() + ".addPdu(): msgId="
            + msgId.toString() + ", Pdu reqId=" + pdu.req_id);
    }

    if (checkDiscovery == true && isAuthoritative(pdu.getMsgType()) == false)
    {
        discoverIfNeeded(pdu);
    }

    boolean added = super.addPdu(pdu);
    return added;
}

/**
 * Removes a PDU. This removes the PDU from the AbstractSnmpContext and
 * clears the link with the SNMPv3 msgId.
 *
 * @param rid the PDU request id
 * @return whether the PDU has been successfully removed
 * @see AbstractSnmpContext#removePdu(int)
 */
public synchronized boolean removePdu(int rid)
{
    boolean removed = super.removePdu(rid);
    if (removed)
    {
        Enumeration keys = msgIdHash.keys();
        Integer msgIdI = null;
        boolean found = false;
        while (keys.hasMoreElements() && found == false)
        {
            msgIdI = (Integer) keys.nextElement();
            Integer pduIdI = (Integer) msgIdHash.get(msgIdI);
            found = (pduIdI.intValue() == rid);
        }
        if (found)
        {
            msgIdHash.remove(msgIdI);
        }
    }
    return removed;
}

/**
 * Encodes a discovery PDU packet. This methods encodes without checking
 * if the discovery parameters are all known.
 */
public byte[] encodeDiscoveryPacket(byte msg_type, int rId, int errstat, 
      int errind, Enumeration ve, Object obj) 
      throws java.io.IOException, EncodingException
{
    String engineId = "";
    TimeWindow tWindow = TimeWindow.getCurrent();
    if (tWindow.isSnmpEngineIdKnown(getSendToHostAddress(), hostPort) == true)
    {
        engineId = tWindow.getSnmpEngineId(getSendToHostAddress(), hostPort);
    }
    TimeWindowNode node = new TimeWindowNode(engineId, 0, 0);

    return actualEncodePacket(msg_type, rId, errstat, errind, ve, node,
    obj);
}

/**
 * Encodes a PDU. This is for internal use only and should
 * NOT be called by the developer. 
 * This is called by the the PDU itself and is added to the interface to
 * cover the different kind of Contexts.
 *
 * <p>
 * When the stack is 
 * <ul>
 *  <li>
 *    authoritative, the timeline details are retrieved from the UsmAgent.
 *  </li>
 *  <li>
 *    non authoritative, this methods first checks if all the discovery 
 *    parameters are known;
 *    <ul>
 *        <li>
 *            If so, it encodes and returns the bytes.
 *        </li>
 *        <li>
 *            If not, it will throw an EncodingException.
 *        </li>
 *    </ul>
 *  </li>
 * </ul>
 * </p>
 *
 * @see #isAuthoritative(byte)
 * @param msg_type  The message type
 * @param rId       The message id
 * @param errstat   The error status
 * @param errind    The error index
 * @param ve        The varbind list
 * @param obj       Additional object (only used in SNMPv3)
 * @return The encoded packet
 */
public byte[] encodePacket(byte msg_type, int rId, int errstat, 
      int errind, Enumeration ve, Object obj) 
      throws java.io.IOException, EncodingException
{
    TimeWindowNode node = null;
    if (isDestroyed == true)
    {
        throw new EncodingException("Context can no longer be used, since it is already destroyed");
    }
    else
    {
        TimeWindow tWindow = TimeWindow.getCurrent();
        if (isAuthoritative(msg_type) == true)
        {
            usmAgent.setSnmpContext(this);
            if (usmAgent.getSnmpEngineId() == null)
            {
                throw new EncodingException("UsmAgent "
                    + usmAgent.getClass().getName() 
                    + " should provide Engine ID!");
            }
            tWindow.updateTimeWindow(usmAgent.getSnmpEngineId(),
                usmAgent.getSnmpEngineBoots(), usmAgent.getSnmpEngineTime(),
                this.isUseAuthentication());
            node = tWindow.getTimeLine(usmAgent.getSnmpEngineId());
        }
        else
        {
            if (tWindow.isSnmpEngineIdKnown(getSendToHostAddress(), hostPort) == false)
            {
                throw new EncodingException("Engine ID of host " 
                      + getSendToHostAddress() 
                      + ", port " + hostPort 
                      + " is unknown (rId="
                      + rId + "). Perform discovery.");
            }
            String engineId = tWindow.getSnmpEngineId(getSendToHostAddress(), hostPort);
            node = new TimeWindowNode(engineId, 0, 0);

            if (isUseAuthentication())
            {
                if (tWindow.isTimeLineKnown(engineId) == true)
                {
                    node = tWindow.getTimeLine(engineId);
                }
                else
                {
                    throw new EncodingException("Time Line of Engine ID of host " 
                        + getSendToHostAddress() + ", port " + hostPort + " is unknown. "
                        + "Perform discovery.");
                }
            }
        }
    }
    return actualEncodePacket(msg_type, rId, errstat, errind, ve, node,
    obj);
}


/**
 * Checks the sanity of the context and returns an error message when it
 * is not correct.
 */
protected String checkContextSanity()
{
    String ret = null;
    if (usePrivacy == true) 
    {
        if (userPrivacyPassword == null) 
        {
            ret = "userPrivacyPassword is null, but usePrivacy is true";
        }
        else if (userPrivacyPassword.length() == 0) 
        {
            ret = "userPrivacyPassword is empty, but usePrivacy is true";
        }
        else if (useAuthentication == false) 
        {
            ret = "useAuthentication is false, but usePrivacy is true";
        }
    } 

    if (useAuthentication == true) 
    {
        if (userAuthenticationPassword == null) 
        {
            ret = "userAuthenticationPassword is null, but useAuthentication is true";
        }
        else if (userAuthenticationPassword.length() == 0) 
        {
            ret = "userAuthenticationPassword is empty, but useAuthentication is true";
        }
    }
    return ret;
}


/**
 * Does the actual encoding. 
 *
 * @see #encodeDiscoveryPacket
 * @see #encodePacket
 */
protected byte[] actualEncodePacket(byte msg_type, int rId, int errstat, 
      int errind, Enumeration ve, TimeWindowNode node, Object obj) 
      throws java.io.IOException, EncodingException
{
    AsnEncoderv3 enc = new AsnEncoderv3();
    String msg = checkContextSanity();
    if (msg != null)
    {
        throw new EncodingException(msg);
    }

    int msgId = ((Integer)obj).intValue();
    if (AsnObject.debug > 6)
    {
        System.out.println(getClass().getName() + ".actualEncodePacket(): msgId="
            + msgId + ", Pdu reqId=" + rId);
    }
    byte[] packet = enc.EncodeSNMPv3(this, msgId, node, 
          msg_type, rId, errstat, errind, ve);

    return packet;
}

/**
 * Processes an incoming SNMP v3 response.
 */
protected void processIncomingResponse(ByteArrayInputStream in)
throws DecodingException, IOException
{
    AsnDecoderv3 rpdu = new AsnDecoderv3();
    // don't have to check for context sanity here: if the request was
    // fine, so should be the response
    byte [] bu = null;
    // need to duplicate the message for V3 to rewrite 
    int nb = in.available();
    bu = new byte[nb];
    in.read(bu);
    in = new ByteArrayInputStream(bu);

    AsnSequence asnTopSeq = rpdu.DecodeSNMPv3(in);
    int msgId = rpdu.getMsgId(asnTopSeq);
    Integer rid = (Integer) msgIdHash.get(new Integer(msgId));
    if (rid != null)
    {
        if (AsnObject.debug > 6)
        {
            System.out.println(getClass().getName() + ".processIncomingResponse(): msgId="
                + msgId + ", Pdu reqId=" + rid);
        }
        Pdu pdu = getPdu(rid);
        try
        {
            AsnPduSequence pduSeq = rpdu.processSNMPv3(this, asnTopSeq, bu, false);
            if (pduSeq != null)
            {
                // got a message
                Integer rid2 = new Integer(pduSeq.getReqId());
                if (AsnObject.debug > 6)
                {
                    System.out.println(getClass().getName() + ".processIncomingResponse():"
                    + " rid2=" + rid2);
                }

                Pdu newPdu = null;
                if (rid2.intValue() != rid.intValue())
                {
                    newPdu = getPdu(rid2);
                    if (AsnObject.debug > 3)
                    {
                        System.out.println(getClass().getName() + ".processIncomingResponse(): "
                            + "pduReqId of msgId (" + rid.intValue() 
                            + ") != pduReqId of Pdu (" + rid2.intValue()
                            + ")");
                    }
                    if (newPdu == null)
                    {
                        if (AsnObject.debug > 3)
                        {
                            System.out.println(getClass().getName() + ".processIncomingResponse(): "
                                + "Using pduReqId of msgId (" + rid.intValue() + ")");
                        }
                    }
                }

                if (newPdu != null)
                {
                    pdu = newPdu;
                }
            }
            else
            {
                if (AsnObject.debug > 6)
                {
                    System.out.println(getClass().getName() + ".processIncomingResponse():"
                    + " pduSeq is null.");
                }
            }

            if (pdu != null)
            {
                pdu.fillin(pduSeq);
            }
            else
            {
                if (AsnObject.debug > 6)
                {
                    System.out.println(getClass().getName() + ".processIncomingResponse(): No Pdu with reqid " + rid.intValue());
                }
            }
        }
        catch (DecodingException exc)
        {
            if (pdu != null)
            {
                pdu.setErrorStatus(AsnObject.SNMP_ERR_DECODING_EXC, exc);
                pdu.fillin(null);
            }
            else
            {
                throw exc;
            }
        }
    }
    else
    {
        if (AsnObject.debug > 3)
        {
            System.out.println(getClass().getName() + ".processIncomingResponse(): Pdu of msgId " + msgId 
                + " is already answered");
        }
        rid = new Integer(-1);
    }
}

/**
 * Returns if we send this PDU in authoritative role or not.
 * The engine who sends a Response, a Trapv2 or a Report is
 * authoritative.
 *
 * @since 4_14
 * @return true if authoritative, false if not.
 */
// Note: for when adding INFORM
// When sending an INFORM, the receiver is the authoritative engine, so
// the INFORM does NOT have to be added to this list!
protected boolean isAuthoritative(byte msg_type)
{
    return (msg_type == AsnObject.GET_RSP_MSG
                ||
            msg_type == AsnObject.TRPV2_REQ_MSG
                ||
            msg_type == AsnObject.GET_RPRT_MSG);
}

void discoverIfNeeded(Pdu pdu)
throws java.io.IOException, PduException
{
    uk.co.westhawk.snmp.beans.UsmDiscoveryBean discBean = null;
    boolean isNeeded = false;

    TimeWindow tWindow = TimeWindow.getCurrent();
    String engineId = tWindow.getSnmpEngineId(getSendToHostAddress(), hostPort);
    if (engineId == null)
    {
        isNeeded = true;
        discBean = new uk.co.westhawk.snmp.beans.UsmDiscoveryBean(
                getSendToHostAddress(), hostPort, bindAddr, typeSocket);
        discBean.setRetryIntervals(pdu.getRetryIntervals());
    }

    if (isUseAuthentication())
    {
        if (isNeeded)
        {
            discBean.setAuthenticationDetails(userName,
                userAuthenticationPassword, authenticationProtocol);
        }
        else if (tWindow.isTimeLineKnown(engineId) == false)
        {
            isNeeded = true;
            discBean = new uk.co.westhawk.snmp.beans.UsmDiscoveryBean(
                    getSendToHostAddress(), hostPort, bindAddr, typeSocket);
            discBean.setAuthenticationDetails(userName,
                userAuthenticationPassword, authenticationProtocol);
            discBean.setRetryIntervals(pdu.getRetryIntervals());
        }

        if (isNeeded && isUsePrivacy())
        {
            discBean.setPrivacyDetails(userPrivacyPassword, privacyProtocol);
        }
    }

    if (isNeeded)
    {
        discBean.startDiscovery();

    }

    // If contextEngineId is null or of length zero, set
    // it to the snmpEngineId.
    if (contextEngineId == null || contextEngineId.length == 0)
    {
        engineId = tWindow.getSnmpEngineId(getSendToHostAddress(), hostPort);
        setContextEngineId(SnmpUtilities.toBytes(engineId));
    }
}


/**
 * Adds the specified request pdu listener to receive PDUs on the
 * specified listening context that matches this context.
 * This method will call usmAgent.setSnmpContext(this).
 *
 * <p>
 * Don't use the TCP_SOCKET when listening for request PDUs. It doesn't
 * provide functionality to send a response back. 
 * </p>
 *
 * @see AbstractSnmpContext#addRequestPduListener(RequestPduListener, ListeningContextPool)
 *
 * @param l The request PDU listener 
 * @param lcontext The listening context
 */
public void addRequestPduListener(RequestPduListener l, ListeningContextPool lcontext)
throws java.io.IOException
{
    super.addRequestPduListener(l, lcontext);

    usmAgent.setSnmpContext(this);
    TimeWindow tWindow = TimeWindow.getCurrent();
    if (usmAgent.getSnmpEngineId() == null)
    {
        throw new IOException("UsmAgent "
            + usmAgent.getClass().getName() 
            + " should provide Engine ID!");
    }
    tWindow.setSnmpEngineId(usmAgent.MYFAKEHOSTNAME, hostPort, usmAgent.getSnmpEngineId());
    tWindow.updateTimeWindow(usmAgent.getSnmpEngineId(),
        usmAgent.getSnmpEngineBoots(), usmAgent.getSnmpEngineTime(),
        this.isUseAuthentication());
}



/**
 * Copies all parameters into another SnmpContextv3.
 */
public Object cloneParameters(SnmpContextv3Face clContext) 
{
    clContext.setUserName(new String(userName));
    clContext.setUseAuthentication(useAuthentication);
    if (userAuthenticationPassword != null)
    {
        clContext.setUserAuthenticationPassword(
            new String(userAuthenticationPassword));
    }
    clContext.setAuthenticationProtocol(authenticationProtocol);

    clContext.setUsePrivacy(usePrivacy);
    if (userPrivacyPassword != null)
    {
        clContext.setUserPrivacyPassword(new String(userPrivacyPassword));
    }
    clContext.setPrivacyProtocol(privacyProtocol);

    clContext.setContextName(new String(contextName));

    int l = contextEngineId.length;
    byte[] newContextEngineId = new byte[l];
    System.arraycopy(contextEngineId, 0, newContextEngineId, 0, l);  
    clContext.setContextEngineId(newContextEngineId);

    clContext.setUsmAgent(usmAgent);
    return clContext;
}

/**
 * Returns the hash key. This key is built out of all properties. It
 * serves as key for a hashtable of (v3) contexts.
 *
 * @since 4_14
 * @return The hash key
 */
public String getHashKey()
{
    StringBuffer buffer = new StringBuffer();
    buffer.append(hostname);
    buffer.append("_").append(hostPort);
    buffer.append("_").append(bindAddr);
    buffer.append("_").append(typeSocket);
    buffer.append("_").append(useAuthentication);
    buffer.append("_").append(ProtocolNames[authenticationProtocol]);
    buffer.append("_").append(ProtocolNames[privacyProtocol]);
    buffer.append("_").append(userAuthenticationPassword);
    buffer.append("_").append(userName);
    buffer.append("_").append(usePrivacy);
    buffer.append("_").append(userPrivacyPassword);
    buffer.append("_").append(SnmpUtilities.toHexString(contextEngineId));
    buffer.append("_").append(contextName);
    buffer.append("_v").append(getVersion());

    return buffer.toString();
}

/**
 * Returns a string representation of the object.
 * @return The string
 */
public String toString()
{
    StringBuffer buffer = new StringBuffer(getClass().getName() + "[");
    buffer.append("host=").append(hostname);
    buffer.append(", sendToHost=").append(getSendToHostAddress());
    buffer.append(", port=").append(hostPort);
    buffer.append(", bindAddress=").append(bindAddr);
    buffer.append(", socketType=").append(typeSocket);
    buffer.append(", contextEngineId=").append(SnmpUtilities.toHexString(contextEngineId));
    buffer.append(", contextName=").append(contextName);
    buffer.append(", userName=").append(userName);
    buffer.append(", useAuthentication=").append(useAuthentication);
    buffer.append(", authenticationProtocol=").append(ProtocolNames[authenticationProtocol]);
    buffer.append(", userAuthenticationPassword=").append(userAuthenticationPassword);
    buffer.append(", usePrivacy=").append(usePrivacy);
    buffer.append(", privacyProtocol=").append(ProtocolNames[privacyProtocol]);
    buffer.append(", userPrivacyPassword=").append(userPrivacyPassword);
    buffer.append(", #trapListeners=").append(trapSupport.getListenerCount());
    buffer.append(", #pduListeners=").append(pduSupport.getListenerCount());
    buffer.append("]");
    return buffer.toString();
}


}
