Version 6.0:
17/10/2007

This is version 6.0 of the simple Java SNMPv1, SNMPv2c and SNMPv3 stack.
It can be downloaded from our homepage:
- http://www.westhawk.co.uk/resources/snmp/snmp6_0.zip        (2.0 Mb)
- http://www.westhawk.co.uk/resources/snmp/stubBrowser6_0.zip (53 K)

Intermediate changes can now be downloaded via cvs from SourceForge:
- http://sourceforge.net/projects/westhawksnmp/
Since we moved the sources to SourceForge, we felt that a major version
increment was in order.

Read the file StackUsage.html for general information how to use the stack.
The frequently asked questions can be found in faq.html.
See http://snmp.westhawk.co.uk/index.html

Please post any questions on the mailing list:
http://snmp.westhawk.co.uk/mailman/listinfo/snmp

This version was not tested against IPv6.

We would like to thank everyone that was kind enough to send us bug fixes
and/or made useful suggestions:
- Andy Chandler <achandler@visionael.com>
- Josh Bers <jbers@bbn.com>
- Stephen <schen@ziplip.net>
- Steve A. Cochran <steve@more.net>
- Tausif <tausifk@rediffmail.com>
- Vladislav V. Shikhov <vladsh@nateks.ru>

Sorry if we've left anyone out.

Birgit & Tim


********************************************
********************* BUG FIXES ************
********************************************

- Exception in SNMPv3 discovery will no longer result in a tied up 
  transmitter in the context.

- SNMPv3 discovery now uses the same retry interval as the original PDU 
  that initiated the discovery.

- Timeline is now saved when SNMP engine boots is zero, but SNMP engine 
  time is greater than zero.


********************************************
********************* OVERALL CHANGES ******
********************************************

- We had another look at the CERT Advisory CA-2002-03
  (http://www.cert.org/advisories/CA-2002-03.html)
  and we run the two trap tests from the 
  "PROTOS Test-Suite: c06-snmpv" 
  (http://www.ee.oulu.fi/research/ouspg/protos/testing/c06/snmpv1/) 
  against uk.co.westhawk.examplev1.ReceiveTrap:

  We fixed a couple of NullPointerException, ClassCastException and
  ArrayIndexOutOfBoundsException and now it runs without a glitch.


- We removed the two classes:
  # uk.co.westhawk.snmp.net.KvmSocket 
  # uk.co.westhawk.snmp.net.NetscapeSocket
    which have a knock-on effect throughout the stack, the examples and 
    the configuration files. 


********************************************
********************** NEW *****************
********************************************

*** New classes in package UK.CO.WESTHAWK.EXAMPLEV1:
- uk.co.westhawk.examplev1.GenericGetOne
  A small applet that is driven by javascript (or rather livescript) to 
  send a GetNext. 


********************************************
********************** DELETED *************
********************************************

*** Deleted classes in package UK.CO.WESTHAWK.SNMP.STACK:

- uk.co.westhawk.snmp.net.KvmSocket:
- uk.co.westhawk.snmp.net.NetscapeSocket:



********************************************
********************** CHANGES *************
********************************************

*** Changes in package UK.CO.WESTHAWK.SNMP.BEANS:

- uk.co.westhawk.snmp.beans.UsmDiscoveryBean:
  Uses SnmpContextv3Pool (instead of SnmpContextv3)
  Added method:
  # public void setRetryIntervals(int rinterval[])
  

*** Changes in package UK.CO.WESTHAWK.SNMP.STACK:

- uk.co.westhawk.snmp.stack.AsnDecoderBase:
  Changed method:
  # AsnSequence getAsnSequence(InputStream in)
    Fixed NullPointerException (CERT)


- uk.co.westhawk.snmp.stack.AsnObjectId:
  Changed methods:
  # AsnObjectId(InputStream in, int len)
  # public String toString(long v[])
    Fixed (both) ArrayIndexOutOfBoundsException (CERT)


- uk.co.westhawk.snmp.stack.AsnSequence:
  Changed method:
  # AsnObject replaceChild(AsnObject oldChild, AsnObject newChild)
    Fixed bug that meant first child element was never replaced


- uk.co.westhawk.snmp.stack.MultiResponsePdu:
- uk.co.westhawk.snmp.stack.TrapPduv1:
  Changed method:
  # void fillin(AsnTrapPduv1Sequence seq)
    Fixed ClassCastException (CERT)


- uk.co.westhawk.snmp.stack.Pdu:
  Added method:
  # public int[] getRetryIntervals()

  Changed method:
  # void fillin(AsnTrapPduv1Sequence seq)
    Fixed ClassCastException (CERT)


- uk.co.westhawk.snmp.stack.ResponsePdu:
  # Renamed method:
    (was) public void getErrorStatus(int errorStatus)
    (now) public void setErrorStatus(int errorStatus)
  # Renamed method:
    (was) public void getErrorIndex(int errorIndex)
    (now) public void setErrorIndex(int errorIndex)
  

- uk.co.westhawk.snmp.stack.SnmpContextv3Basis:
  Changed methods:
  # protected boolean addPdu(Pdu pdu, boolean checkDiscovery)
    Discovery is done (if needed) before the PDU is added to the context.
    This way an exception will not result in a tied up transmitter in the
    context.
  # void discoverIfNeeded(Pdu pdu)
    Copies the PDU's retry interval to the discovery bean


- uk.co.westhawk.snmp.stack.SnmpContextBasisFace:
  Removed constants:
  # public final static String NETSCAPE_SOCKET
  # public final static String KVM_SOCKET


- uk.co.westhawk.snmp.stack.SnmpContextPool:
- uk.co.westhawk.snmp.stack.SnmpContextv3Pool:
  Overall changes:
  Removed all the try/catch(java.io.IOException exc) wrappers around 
    context = getMatchingContext();
  so this exception propagates up.


- uk.co.westhawk.snmp.stack.TimeWindow:
  Added method:
  # public void clearTimeWindow(String snmpEngineId)

  Changed method:
  # public boolean updateTimeWindow(String snmpEngineId, int bootsA, 
    int timeA, boolean isAuthentic)
    Fixed bug that didn't save timeline when bootsA was zero (but timeA 
    was greater than zero)


- uk.co.westhawk.snmp.stack.Transmitter:
  Changed method:
  # void interruptMe()
    Test if variable 'me' isn't null
  

- uk.co.westhawk.snmp.stack.varbind:
  Changed methods:
  # public varbind(AsnObjectId Oid, AsnObject val)
  # Object setValue(AsnSequence vb)
    Fixed NullPointerException (CERT)
    Fixed ClassCastException (CERT)



********************************************************************************

Version 5.1:
19/06/2006

We would like to thank everyone that was kind enough to send us bug fixes
and/or useful suggestions:
- Steve A. Cochran <steve@more.net>
- Vincent Deconinck <vdeconinck@tiscalinet.be
- Miguel Ballesteros <Miguel.Ballesteros@gdc4s.com>

Sorry if we've left anyone out.


********************************************
********************* BUG FIXES ************
********************************************

- SnmpContextv3Pool no longer throws an IllegalArgumentException when
  authentication or privacy password isn't set.

- Threads are clean up (see changes below).



********************************************
********************** NEW *****************
********************************************

*** New classes in package UK.CO.WESTHAWK.EXAMPLEV2:
- uk.co.westhawk.examplev2c.MonitorAsteriskGraph
- uk.co.westhawk.examplev2c.AsteriskChanTypeTablePdu
  First go at playing with the Asterisk SNMP stack.
  This uses the graphics package prefuse beta.
  http://prefuse.org/download/



********************************************
********************** CHANGES *************
********************************************

*** Changes in package UK.CO.WESTHAWK.SNMP.STACK:

- uk.co.westhawk.snmp.stack.SnmpContextv3Basis:
- uk.co.westhawk.snmp.stack.SnmpContextv3Pool:
  No longer check the authentication and privacy password when set. Only check
  it just before a sending or receiving a SNMPv3 PDU.

- uk.co.westhawk.snmp.stack.AbstractSnmpContext:
  Added method:
  # protected void freeTransmitters()
    This is used both in run() and in destroy()

*** Changes in package UK.CO.WESTHAWK.SNMP.BEANS:
- uk.co.westhawk.snmp.beans.UsmDiscoveryBean
  Changed methods:
  # public void startDiscovery()
    Just before PduException is thrown, call freeResources()


********************************************************************************

Version 5.0:
23/03/2006

This is version 5.0 of the simple Java SNMPv1, SNMPv2c and SNMPv3 stack.
It can be collected via our homepage:
<URL http://www.westhawk.co.uk/resources/snmp/snmp5_0.zip>        (1.9 Mb)
<URL http://www.westhawk.co.uk/resources/snmp/stubBrowser5_0.zip> (52 K)

Read the file StackUsage.html for general information how to use the stack.
The frequently asked questions can be found in faq.html.

This version is a small add-on to 4_14. 4_14 wasn't released to the public
domain, so hence the new number. 4_14 has a huge amount of changes, so it felt
a major version number incremen was appropriate.

*** New class in package UK.CO.WESTHAWK.SNMP.STACK:
- uk.co.westhawk.snmp.stack.BitsHelper
  Helper methods to the BITS construct



********************************************************************************

Version 4.14:
01/02/2006

We would like to thank everyone that was kind enough to send us bug fixes
and/or useful suggestions:
- Victor Kirk <Victor.Kirk@serco.com>
- Chris Barlock <barlock@us.ibm.com>
- Robert Kostes <rkostes@panix.com>
- Steven Bolton <sbolton@cereva.com>
- Josh Bers <jbers@bbn.com>
- Ian Dowse <iedowse@iedowse.com>

Sorry if we've left anyone out.


********************************************
********************* BUG FIXES ************
********************************************

- Destroying a context no longer creates a listener object that listens for
  incoming traps (or PDUs in general).

- When doing discovery in SNMPv3, the contexts are now destroyed.

- When receiving incoming packets, the value of the outgoing hostaddress is no
  longer overwritten.



********************************************
********************** NEW FEATURES ********
********************************************

- Stack is able to listen for incoming requests (and no longer for traps only).

- Stack is able to listen on multiple ports for requests and traps.

- Contexts (incoming and outgoing) can be bound to a local bind address.

- Released separate package stubBrowser4_14.zip that contains a new and
  experimantal Stub Browser. See documentation.

- In SNMPv3, if the contextEngineID is of length zero, the encoder will use
  the (discovered) snmpEngineId.

- Added support for DateAndTime text convention to AsnOctets.

- Added Printable interface to AsnOctets for users to implement their own
  code.

- Added a MultiSourcePdu that can do broadcast requests. Use with care!

- Build in support for IPv6.

- Build in support for 'Reliable SNMP'. See also 'Known Issues' below.

- Using Ant and no longer make.

- Replaced all shell scripts by Ant targets.

- Restructured the project directories and files (i.e. made src, classes, doc
  and lib directories).



********************************************
********************** KNOWN ISSUES ********
********************************************

- Build in support for 'Reliable SNMP' works with a TCP socket.
  It has limited functionality;
  + You can send requests and get a response,
  + You can receive traps,
  + You can receive requests, BUT it is NOT possible to send a response back.
    This is because the architecture has no facilities (yet) to keep the newly
    created (accepted) Socket and use it to send the response.



********************************************
********************** NEW *****************
********************************************

*** New classes in package UK.CO.WESTHAWK.SNMP.STACK:
- uk.co.westhawk.snmp.stack.SnmpContextv3Discovery
  Special context to do discovery.

- uk.co.westhawk.snmp.stack.SnmpContextv3Basis
  Forms the basis of (existing) SnmpContextv3 and (new)
  SnmpContextv3Discovery.

- uk.co.westhawk.snmp.stack.ListeningContextFace
- uk.co.westhawk.snmp.stack.ListeningContext
- uk.co.westhawk.snmp.stack.ListeningContextPool
  These classes and interface enable the stack to receive packets from
  more than one socket.
  These replace the (now deprecated) DefaultTrapContext.

- uk.co.westhawk.snmp.stack.AsnOctetsPrintableFace
- uk.co.westhawk.snmp.stack.DefaultAsnOctetsPrintable
  The interface and default implementation that is use by AsnOctets to print.

- uk.co.westhawk.snmp.stack.GetPdu
- uk.co.westhawk.snmp.stack.ReportPdu
- uk.co.westhawk.snmp.stack.ResponsePdu
- uk.co.westhawk.snmp.stack.MultiResponsePdu
  Additional PDUs.


*** New classes in package UK.CO.WESTHAWK.SNMP.NET:
- uk.co.westhawk.snmp.net.TCPSocket
  A wrapper class around the standard Socket to enable 'reliable' SNMP.
  See also 'Known Issues' above.

- uk.co.westhawk.snmp.net.StreamPortItem
  A holder class that associates the incoming packet stream with the
  remote port it came from.


*** New classes in package UK.CO.WESTHAWK.SNMP.EVENT:
- uk.co.westhawk.snmp.event.DecodedPduEvent
- uk.co.westhawk.snmp.event.RawPduEvent
- uk.co.westhawk.snmp.event.RawPduListener
- uk.co.westhawk.snmp.event.RawPduReceivedSupport
- uk.co.westhawk.snmp.event.RequestPduEvent
- uk.co.westhawk.snmp.event.RequestPduListener
- uk.co.westhawk.snmp.event.RequestPduReceivedSupport
  These classes and interfaces enable the stack to receive PDU requests as
  well (besides receiving only traps).


*** New class in package UK.CO.WESTHAWK.SNMP.PDU:
- uk.co.westhawk.snmp.pdu.InterfaceGetNextPduStub
  Autogenerated class by the (new) StubBrowser. It forms the basis of the
  (changed) InterfaceGetNextPdu.


*** New class in package UK.CO.WESTHAWK.SNMP.BEANS:
- uk.co.westhawk.snmp.beans.UsmBeingDiscoveredBean
  The bean that handles being discovered by SNMPv3 non-authoritative engines.






********************************************
********************** CHANGES *************
********************************************


*** Compiler:
- Stack now uses JDK 1.4.1 to compile code, generate javadoc and test the stack.
  However, none of the 1.4 specific packages are used (like java.nio).


*** Changes in package UK.CO.WESTHAWK.SNMP.STACK:

- Split up class uk.co.westhawk.snmp.stack.AsnDecoder into:
  # uk.co.westhawk.snmp.stack.AsnDecoderBase
  # uk.co.westhawk.snmp.stack.AsnDecoderv1
  # uk.co.westhawk.snmp.stack.AsnDecoderv2c
  # uk.co.westhawk.snmp.stack.AsnDecoderv3

- Split up class uk.co.westhawk.snmp.stack.AsnEncoder into:
  # uk.co.westhawk.snmp.stack.AsnEncoderBase
  # uk.co.westhawk.snmp.stack.AsnEncoderv1
  # uk.co.westhawk.snmp.stack.AsnEncoderv2c
  # uk.co.westhawk.snmp.stack.AsnEncoderv3


- uk.co.westhawk.snmp.stack.SnmpConstants:
  General changes:
  # Added
    SMI_V2_UINTEGER32 = GAUGE
  # Renamed
    - UINTEGER32 to OBSOLETED_RFC1442_UINTEGER32


- uk.co.westhawk.snmp.stack.AsnObject:
  Changed methods:
  # AsnObject AsnMakeMe(InputStream in, byte t, int len, int pos, int headLength)
    Added constants to switch(type) that enable the stack to build
    incoming request PDUs.
  # int size() now can throw an EncodingException
  # void write(OutputStream out, int pos) now can throw an EncodingException


- uk.co.westhawk.snmp.stack.AsnObjectId:
  Added methods:
  # public AsnObjectId(long[] oida)
  # public long[] getOid()
  # public long[] getSubOid(int beginIndex, int endIndex)

  General changes:
  # AsnObjectId(InputStream in, int len)
  # int size():
    - handle empty OIDs succesfully
  # public String toString(long v[])
    - improved efficiency

  Changed methods:
  # int size() now can throw an EncodingException
  # void write(OutputStream out, int pos) now can throw an EncodingException


- uk.co.westhawk.snmp.stack.AsnOctets:
  Added methods:
  # public AsnOctets(java.util.Calendar cal)
  # public AsnOctets(java.net.Inet4Address, byte type)
  # public java.util.Calendar getCalendar()
  # public String toCalendar()
  # public InetAddress getIpAddress()
  # public static void setPrintable(AsnOctetsPrintableFace obj)
  # public String toString(AsnOctetsPrintableFace face)
  # public String toInternationalDisplayString()
  # public String toInternationalDisplayString(AsnOctetsPrintableFace face)

  Changed methods:
  # public String toString()
    Uses the DefaultAsnOctetsPrintable:
    - AsnOctetsPrintableFace.isPrintable(byte[] value)
      to test if the Octets are printable, and
    - AsnOctetsPrintableFace.toInternationalDisplayString(byte[] value)
      to convert the byte array to a string.
  # public String toDisplayString()
    Uses US-ASCII character set
  # public String toIpAddress()
    Convert to IPv4 or IPv6, depending on addres


- uk.co.westhawk.snmp.stack.SnmpContextBasisFace:
  General changes:
  # Added constant TCP_SOCKET
  See also 'Known Issues' above.

  Added methods:
  # public String getSendToHostAddress()
  # public String getReceivedFromHostAddress()
  # public String getBindAddress()
  # public boolean isDestroyed()
  # public void addTrapListener(TrapListener l, int port)
  # public void addRequestPduListener(RequestPduListener l)
  # public void addRequestPduListener(RequestPduListener l, int port
  # public void removeTrapListener(TrapListener l, int port)
  # public void removeRequestPduListener(RequestPduListener l)
  # public void removeRequestPduListener(RequestPduListener l, int port)
  # public Object clone()
  # public String getHashKey()

  Changed methods:
  # Added parameters Object obj to encodePacket()
  # Renamed:
    (was) public abstract Pdu processIncomingTrap(byte [] message)
    (now) public abstract Pdu processIncomingPdu(byte [] message)


- uk.co.westhawk.snmp.stack.AbstractSnmpContext:
  General changes:
  # class implements RawPduListener (and no longer TrapListener)
  # added (private) variable stopRequested that is used to stop the thread
  # uses ListeningContextPool (and no longer DefaultTrapContext)

  Added methods:
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace

  Changed methods:
  # Added parameter bindAddress (String) to constructor:
    protected AbstractSnmpContext(String host, int port, String bindAddress,
    String typeSocketA)
  # public String getHostAddress()
    is now deprecated, replaced by two new methods above
  # protected void activate()
    - Only starts thread when any PDU is expecting response (see
      Pdu.isExpectingResponse())
    - activate() is no longer called in constructor, but in sendPacket()
  # Renamed:
    (was) protected abstract void ProcessIncomingMessage(AsnDecoder rpdu,
          ByteArrayInputStream in)
    (now) protected abstract void processIncomingResponse(ByteArrayInputStream in)
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace


- uk.co.westhawk.snmp.stack.SnmpContext:
- uk.co.westhawk.snmp.stack.SnmpContextv2c:
  Added methods:
  # public String getHashKey()

  Changed methods:
  # Added parameter bindAddress (String) to constructor.
  # Renamed:
    (was) public abstract Pdu processIncomingTrap(byte [] message)
    (now) public abstract Pdu processIncomingPdu(byte [] message)
    This method is extended so it not only instantiates new Trap objects, but
    also (other) incoming request PDUs.


- uk.co.westhawk.snmp.stack.SnmpContextPool:
  General changes:
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace

  Added methods:
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace
  # public void destroyPool()

  Changed methods:
  # Added parameter bindAddress (String) to constructor.
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace


- uk.co.westhawk.snmp.stack.SnmpContextv2cPool:
  General changes:
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace

  Added methods:
  # public SnmpContextv2cPool(String host, int port, String comm,
    String typeSocket)
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace
  # public void destroyPool()

  Changed methods:
  # Added parameter bindAddress (String) to constructor.
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace


- uk.co.westhawk.snmp.stack.SnmpContextv3Face:
  Added methods:
  # public void setUsmAgent(UsmAgent newAgent)
  # public UsmAgent getUsmAgent()


- uk.co.westhawk.snmp.stack.SnmpContextv3:
  General changes:
  # class now extends the (new) class SnmpContextv3Basis. Most code has moved
  # to the parent class.

  Changed methods:
  # Added parameter bindAddress (String) to constructor.
  # Renamed:
    (was) public abstract Pdu processIncomingTrap(byte [] message)
    (now) public abstract Pdu processIncomingPdu(byte [] message)
    This method is extended so it not only instantiates new Trap objects, but
    also (other) incoming request PDUs.


- uk.co.westhawk.snmp.stack.SnmpContextv3Pool:
  General changes:
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace

  Added methods:
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace
  # see  uk.co.westhawk.snmp.stack.SnmpContextv3Face
  # public void destroyPool()

  Changed methods:
  # Added parameter bindAddress (String) to constructor.
  # see uk.co.westhawk.snmp.stack.SnmpContextBasisFace


- uk.co.westhawk.snmp.stack.PassiveSnmpContext:
- uk.co.westhawk.snmp.stack.PassiveSnmpContextv2c:
  Changed methods:
  # Added parameter bindAddress (String) to constructor.


- uk.co.westhawk.snmp.stack.DefaultTrapContext:
  This class is now deprecated.
  It is replaced by
  # uk.co.westhawk.snmp.stack.ListeningContextFace
  # uk.co.westhawk.snmp.stack.ListeningContext
  # uk.co.westhawk.snmp.stack.ListeningContextPool
  This class extends the (new) ListeningContext class.


- uk.co.westhawk.snmp.stack.UsmAgent:
- uk.co.westhawk.snmp.stack.DefaultUsmAgent:
  Added methods:
  # public long getUsmStatsUnknownEngineIDs()
  # public long public long getUsmStatsNotInTimeWindows()

  Changed methods:
  # Changed signature of setSnmpContext
    (was) public void setSnmpContext(SnmpContextv3 context)
    (now) public void setSnmpContext(SnmpContextv3Basis context)


- uk.co.westhawk.snmp.stack.Pdu:
  Added methods:
  # protected boolean isExpectingResponse()
    Defaults to true
  # protected StringBuffer printVars(String title, Vector vars)

  Changed:
  # protected abstract void new_value(int n, varbind res)
  # protected abstract void tell_them()
    Are no longer abstract
  # Constructor:
    - Wrapped: Incrementing request ID in a synchronized block
    - initialise: answered = false


- uk.co.westhawk.snmp.stack.TimeWindow:
  General changes:
  # Using hostaddres instead of hostname

  Added methods:
  # public String toString()


- uk.co.westhawk.snmp.stack.TrapPduv1:
  General changes:
  # This class is no longer abstract

  Added method:
  # public TrapPduv1(SnmpContextPool con)
  # protected boolean isExpectingResponse()
    This method returns false


- uk.co.westhawk.snmp.stack.TrapPduv2:
  General changes:
  # This class is no longer abstract

  Added method:
  # protected boolean isExpectingResponse()
    This method returns false

- uk.co.westhawk.snmp.stack.InformPdu:
- uk.co.westhawk.snmp.stack.GetNextPdu:
- uk.co.westhawk.snmp.stack.GetBulkPdu:
- uk.co.westhawk.snmp.stack.SetPdu:
  These four classes are no longer abstract



*** Changes in package UK.CO.WESTHAWK.SNMP.PDU:

- uk.co.westhawk.snmp.pdu.UpSincePdu:
- uk.co.westhawk.snmp.pdu.InterfacePdu:
- uk.co.westhawk.snmp.pdu.GetPdu_vec:
- uk.co.westhawk.snmp.pdu.OneGetPdu:
- uk.co.westhawk.snmp.pdu.OneIntPdu:
  There classes now extend GetPdu (and no longer Pdu)

- uk.co.westhawk.snmp.pdu.InterfaceGetNextPdu:
  This class now extends the (new) InterfaceGetNextPduStub.
  Most of the methods have moved to the parent class.

- uk.co.westhawk.snmp.pdu.InterfacesPdu:
  Removed methods:
  # public static int getNumIfs(SnmpContextBasisFace con)

- uk.co.westhawk.snmp.pdu.DiscoveryPdu:
  Extends GetPdu (and no longer Pdu)
  Removed methods:
  # protected void new_value(int n, varbind a_var)
  # protected void tell_them()

- uk.co.westhawk.snmp.pdu.BlockPdu:
  Uses GetBulkPdu for the bulk request (and no longer OneGetBulkPdu)

- uk.co.westhawk.snmp.pdu.OneTrapPduv1:
  This class is now deprecated. Its parent class is no
  longer abstract so can be used.
  Added method:
  # public OneTrapPduv1(SnmpContextPool con)

- uk.co.westhawk.snmp.pdu.OneSetPdu:
- uk.co.westhawk.snmp.pdu.OneTrapPduv2:
- uk.co.westhawk.snmp.pdu.OneInformPdu:
  These classes are now deprecated. Their parent classes are no
  longer abstract so can be used.




*** Changes in package UK.CO.WESTHAWK.SNMP.NET:
- Change methods (in all classes + interface):
  # Added paramter bindAddr (String) to create() method:
    + public void create(int port, String bindAddr)
    + public void create(String host, int port, String bindAddr)

  # Changed signature of receive method: 
    (was) public ByteArrayInputStream receive(int maxRecvSize) throws IOException
    (now) public StreamPortItem receive(int maxRecvSize) throws IOException

- Removed method (in all classes + interface):
  # public String getHostAddress()
  and replaced it by:
  # public String getSendToHostAddress()
  # public String getReceivedFromHostAddress()



*** Changes in package UK.CO.WESTHAWK.SNMP.EVENT:
- uk.co.westhawk.snmp.event.TrapEvent
  This is now extends DecodedPduEvent, i.e. is always a decoded trap.
  None decoded traps are now part of the (new)
  uk.co.westhawk.snmp.event.RawPduEvent

- uk.co.westhawk.snmp.event.TrapReceivedSupport
  This does no longer fires an undecoded trap event, but only decoded ones.
  Changed signature:
  (was) public void fireTrapReceived(Pdu pdu)
  (now) public void fireTrapReceived(Pdu pdu, int hostPort)
  The hostPort indicates the port on which the trap was received



*** Changes in package UK.CO.WESTHAWK.SNMP.UTIL:
- uk.co.westhawk.snmp.util.SnmpUtilities
  Added methods:
  # public static byte longToByte(long l)
  # public static byte[] longToByte(long[] l)



*** Changes in package UK.CO.WESTHAWK.SNMP.BEANS:
- uk.co.westhawk.snmp.beans.UsmDiscoveryBean
  Uses SnmpContextv3 context (instead of SnmpContextv3Pool), which gets
  destroyed when discovery is done.

  Changed methods:
  # Added parameter bindAddr (String) to constructor

- uk.co.westhawk.snmp.beans.SNMPBean
  Added methods:
  # Constructor (String host, String port, String bindaddress, String socketType)
  # public String getBindAddress()
  # public void setBindAddress(String b)
  # public String getSocketType()
  # public void setSocketType(String t)

- uk.co.westhawk.snmp.beans.AscendActiveSessionBean 
- uk.co.westhawk.snmp.beans.DialogChannelStatusBean
- uk.co.westhawk.snmp.beans.OneNTPrintQBean 
- uk.co.westhawk.snmp.beans.OneNTServiceBean 
- uk.co.westhawk.snmp.beans.OneNTSharedResBean
  Added methods:
  # Constructor (String host, String port, String bindaddress)

- uk.co.westhawk.snmp.beans.IsHostReachableBean
  Added methods:
  # Constructor (String host, String port, String bindaddress)
  # public void freeResources()

- uk.co.westhawk.snmp.beans.InterfaceIndexesBean
  Using the (changed) InterfaceGetNextPdu
  Added methods:
  # Constructor (String host, String port, String bindaddress)
  # public Set getInterfaceIndexSet()

- uk.co.westhawk.snmp.beans.AnnexModemStatusBean
- uk.co.westhawk.snmp.beans.NcdPerfDataBean
  Uses GetPdu (instead of OneGetPdu)

  Added methods:
  # Constructor (String host, String port, String bindaddress)

- uk.co.westhawk.snmp.beans.OneInterfaceBean
  Uses InterfaceGetNextPdu (instead of InterfacePdu)

  Added methods:
  # Constructor (String host, String port, String bindaddress)

- uk.co.westhawk.snmp.beans.NTPrintQBean
- uk.co.westhawk.snmp.beans.NTServiceNamesBean
- uk.co.westhawk.snmp.beans.NTSharedResBean
- uk.co.westhawk.snmp.beans.NTUserNamesBean
  Uses GetNextPdu (instead of OneGetNextPdu)

  Added methods:
  # Constructor (String host, String port, String bindaddress)




*** Changes in package UK.CO.WESTHAWK.SNMP.SERVLET:
- uk.co.westhawk.servlet.Interfaces
- uk.co.westhawk.servlet.JeevesInterfaces
  Use the (changed) InterfaceGetNextPdu

- All the servlets use PrintWriter (ServletResponse.getWriter()),
  instead of ServletOutputStream (ServletResponse.getOutputStream()).



*** Changes in package ORG.BOUNCYCASTLE.CRYPTO.*:
- Using version 1.27 (instead of 1.15) of the Bouncy Castle lcrypto API




Version 4.13:
20/11/2002

This is version 4.13 of the simple Java SNMPv1, SNMPv2c and SNMPv3 stack.
It can be collected via our homepage:
<URL http://www.westhawk.co.uk/resources/snmp/snmp4_13.zip> (1.5 Mb)

Read the file StackUsage.html for general information how to use the stack.
The frequently asked questions can be found in faq.html.

****** CHANGES / FIXES ****
- Fixed major performance problem due to synchronized(this) block in
  AbstractSnmpContext.run(). Removed the block.

- Removed the same block from DefaultTrapContext.run() as well.

- included stubs directory in the zip file.




Version 4.12:
11/11/2002

This is version 4.12 of the simple Java SNMPv1, SNMPv2c and SNMPv3 stack.
It can be collected via our homepage:
<URL http://www.westhawk.co.uk/resources/snmp/snmp4_12.zip> (1.5 Mb)

Read the file StackUsage.html for general information how to use the stack.
The frequently asked questions can be found in faq.html.

We would like to thank everyone that was kind enough to send us bug fixes
and/or useful suggestions:
- Balakrishnan <bala_tbn@yahoo.com>
- Eli Bishop <eli@graphesthesia.com>
- Ernest Jones <EJones@netopia.com>
- Michael(Mike) Waters, ERG Group
- Pete Kazmier <pete@kazmier.com>
- Seon Lee <slee@virtc.com>

Sorry if we've left anyone out.


****** BUG FIXES ****

- The trap listener can now be stopped.

- Using Pdu.waitForSelf() or the BlockPdu will handle a timeout correctly.
  Some times no timeout exception was passed or the isTimedOut flag was not
  set due to timing issues.

- SnmpContextPool/SnmpContextv2cPool no longer will throw 
  a ClassCastException when both SNMP versions were used with 
  the same parameters.

- Improved thread safety in the SnmpContext*Pool classes.

- Fixed problem that '1' was not recognised as OID, while it is valid.



****** NEW ****
- Experimented with the auto generation of Java code out off MIB file, using
  libsmi, XML, XSL, XSLT.
  See stubs directory.

- New class uk.co.westhawk.snmp.stack.SnmpContextBasisFace
  Forms the basis of all SnmpContext classes and interfaces.

- New class uk.co.westhawk.snmp.stack.SnmpContextPoolItem
  This class contains one context and one reference counter. The reference
  counter maintains how many objects reference this context. It is a helper
  class for the context pools, to improve its synchronisation.

** New Inform classes:
- uk.co.westhawk.snmp.stack.InformPdu
- uk.co.westhawk.snmp.pdu.OneInformPdu
- uk.co.westhawk.snmp.pdu.InformPdu_vec
  New classes to support sending (only!) an Inform.
  Note, Receiving an Inform and replying with a Response is NOT yet supported!

** New Passive classes:
- uk.co.westhawk.snmp.pdu.PassiveTrapPduv1
- uk.co.westhawk.snmp.pdu.PassiveTrapPduv2
- uk.co.westhawk.snmp.stack.PassiveSnmpContext
- uk.co.westhawk.snmp.stack.PassiveSnmpContextv2c
  The purpose of the Passive classes is to allow the stack to be used
  in environments where thread creation is unwanted, eg database JVMs such as
  Oracle JServer. See below for new package uk.co.westhawk.nothread.trap

** New package uk.co.westhawk.nothread.oscar:
- Simple example how to use Java in Oracle8i (tm).
  Written this as preparation to next new package (see below).

** New package uk.co.westhawk.nothread.trap:
- Added code, scripts and SQL files explaining how the stack can be used to
  send v1 and v2c traps in Oracle8i (tm).

** Distribution generates (also) snmpOracle<no>.jar
- This is a cut-down jar file of the stack that contains just the files 
  necessary to send v1 and v2c traps in Oracle8i (tm).



****** CHANGES ****

** General, overall changes:
- No longer using SnmpContextBasisFace.MSS
  The maximum number of bytes to receive (maxRecvSize) is a new parameter to
  the context. MSS is only used as default value for maxRecvSize.
  Note, this is not supported in the SnmpContextXXXPool classes!

- Added a flag isDestroyed to the SnmpContextXXXX classes. 
  An EncodingException will be thrown when the context is used after 
  being destroyed. 

- Using StringBuffer instead of String in a most of the toString() methods.



** Compiler:
  No longer using JDK 1.1.8. Stack now uses JDK 1.3.0 to compile code,
  generate javadoc and test the stack.

** Changes in package uk.co.westhawk.applet*:
- Moved applet1_0, applet1_1, appletv2c, appletv3 to
  examplev1, examplev2c, examplev3.
  Rewritten all applets into application.
  Application parameters can be configured in .properties file.

** Changes in package uk.co.westhawk.snmp.stack:
- varbind:
  + added constructor
    # public varbind(AsnObjectId Oid)
    # public varbind(AsnObjectId Oid, AsnObject val)
    Saves converting from String -> OID -> String

- Pdu:
  + fixed bug so waitForSelf() handles a timeout always correctly.
  + made sendme() protected so the PassivePdus can call it
    directly in addToTrans().
  + added methods
    # addOid(AsnObjectId oid)
    # addOid(AsnObjectId oid, AsnObject val)
    Saves converting from String -> OID -> String
  + amended method toString():
    # uses StringBuffer instead of String.
  + amended method fillin():
    # fills in the request id.
- SetPdu:
  + moved method addOid(String oid, AsnObject val) to Pdu
- GetBulkPdu:
  + amended method toString():
    # uses StringBuffer instead of String.

- SnmpContextFace:
- SnmpContextv2cFace:
- SnmpContextv3Face:
  + now extend SnmpContextBasisFace.

- AbstractSnmpContext:
  + added flag isDestroyed. EncodingException is thrown when Context is used
    after being destroyed.
  + revised destroy() 
  + made stop() deprecated.
  + renamed toString to getDebugString().
    Due to overloading, the user could not access this toString().
  + added methods:
    # protected void activate(): 
      will be overwritten by the PassiveContexts, see section "NEW".
    # public int getMaxRecvSize()
    # public void setMaxRecvSize(int no)
      replaces the use of SnmpContextBasisFace.MSS. maxRecvSize is passed 
      as parameter to the socket.receive() method.
  + run() catches all exceptions so that it keeps running no matter what.

- SnmpContext:
- SnmpContextv2c:
- SnmpContextv3:
  + maxRecvSize is passed as parameter to the socket.receive() method.
    It is no longer using SnmpContextBasisFace.MSS
  + amended method toString():
    # uses StringBuffer instead of String.
  + an EncodingException is thrown when the context is used after 
    being destroyed.

- DefaultTrapContext:
  + added methods:
    # public int getMaxRecvSize()
    # public void setMaxRecvSize(int no)
      replaces the use of SnmpContextBasisFace.MSS. maxRecvSize is passed 
      as parameter to the socket.receive() method.
    # public void destroy():
      fixes bug that the trap listener could not be stopped
  + run() catches all exceptions so that it keeps running no matter what.

- SnmpContextPool:
- SnmpContextv2cPool:
  + fixed bug that caused ClassCastException by adding version number to
    hashKey.
  + improved thread safety by only using 1 hashtable (and no longer 2).
    Using SnmpContextPoolItem object (a new class) to store in
    hashtable contextPool.
  + added constructor that take the community name:
    # public SnmpContextPool(String host, int port, String comm, String typeSocket)
- SnmpContextv3Pool:
  + added version number to hashKey.
  + improved thread safety by only using 1 hashtable (and no longer 2).
    Using SnmpContextPoolItem object (a new class) to store in
    hashtable contextPool.
  + fixed problem that the proper context was not always created after
    parameters were changed, but before it was being used.

- AsnObject: 
  + added methods:
    # public String getRespTypeString(): 
      returns the response type as a String.

- AsnNull:
- AsnPrimitive:
- AsnInteger:
- AsnUnsInteger:
- AsnUnsInteger64:
  + added methods:
    # public boolean equals(Object anObject)
    # public int hashCode()

- AsnOctets:
  + added methods:
    # public AsnOctets(java.net.InetAddress iad)
      This will generate an object of ASN IPAddress type.
    # public boolean equals(Object anObject)
    # public int hashCode()
    # public long [] toSubOid(boolean length_implied)
    # private long getPositiveValue(int index)
  + amended method toString():
    # uses StringBuffer instead of String.

- AsnObjectId:
  + fixed problem that '1' was not recognised as OID, while it is valid.
  + added methods:
    # public void add(long sub_oid)
    # public void add(long[] sub_oid)
    # public void add(String s)
    # public boolean equals(Object anObject)
    # public int hashCode()
    # private long [] toArrayOfLongs(String s)
  + removed method same().
  + amended methods size() and write() so it deals with an empty OID
    or an OID of length 1.
  + amended method toString():
    # uses StringBuffer instead of String.
    # only prints the first 100 characters. If the array is longer then
      100, it concatenates "[.. cut ..]."

- Transmitter
  + amended method toString():
    # uses StringBuffer instead of String.

- TrapPduv1
  + amended method toString():
    # uses StringBuffer instead of String.
    # prints request id
  + amended method fillin():
    # try/catch DecodingException


** Changes in package uk.co.westhawk.snmp.pdu:
- BlockPdu: added methods
  + addOid(AsnObjectId oid)
  + addOid(AsnObjectId oid, AsnObject val)

- InterfacePdu: added methods to get sysUpTime, inOctects and outOctets.


** Changes in package org.bouncycastle.crypto.*:
- Using version 1.15 (instead of 1.08) of the Bouncy Castle crypto API


Version 4.10:
06/09/2001

This is version 4.10 of the simple Java SNMPv1, SNMPv2c and SNMPv3 stack.
It can be collected via our homepage:
<URL http://www.westhawk.co.uk/resources/snmp/snmp4_10.zip> (1.3 Mb)

Read the file StackUsage.html for general information how to use the stack.
The stack still uses JDK 1.1.X . 
The javadoc is generated with JDK 1.2.

The changes in this release are almost all bug fixes.
The most important change is that of AsnUnsInteger; using type long (instead
of type int). This does probably mean that you have to recompile and change
your code where it concerns TimeTick, Counter (i.e. Counter32) and Gauge from
type int to type long.

We would like to thank Visionael Corp (http://www.visionael.com) for
their support.

We would like to thank everyone that was kind enough to send us bug fixes and
useful suggestions:
- Donnie Love (dlove@idsonline.com)
- Jin Huang (jhuang@infiniswitch.com)
- Julien Conan (jconan@protego.net)
- Ken Swanson (gks@navcomm1.dnrc.bell-labs.com)
- Maga Hegde (mhegde@zumanetworks.com)
- Steven Bolton (sbolton@cereva.com)

Sorry if we've left anyone out.

****** CHANGES ****

** Changes (mostly bug fixes) in package uk.co.westhawk.snmp.stack:
- AsnObject: moved all the constants to interface SnmpConstants
- AsnObject: improved converting bytes to length in getLengthPacket()
- AsnObjectId: the class is now represented as an array of type long (instead
  of type int)
- AsnInteger: improved converting bytes into int in bytesToInteger()
- AsnUnsInteger: using type long (instead of type int). int is too small for 
  all Counter32 values.
- AsnUnsInteger: improved converting bytes into unsigned int in bytesToLong()
- AsnUnsInteger64: improved converting bytes into unsigned int in
  bytesToLong()
- AsnOctets: when the value is an empty, it no longer causes a
  ArrayOutOfBoundsIndexException in toIpAddress() and in toHex()
- AsnOctets: added the method toDisplayString()
- Pdu: retry_intervals is no longer static
- Pdu: changed what error status is set in fillin() in case of a decoding
  problem, see getErrorStatus()
- TrapPduv1: because of AsnUnsInteger, timeticks is now a type long as well
- AsnEncoder.EncodeTrap1Pdu(): the IPAddress in a TrapPduv1 is now being
  correctly encoded as an IPADDRESS type (and no longer as the default
  TIMETICKS type) 
- TrapPduv2: fixed a bug so now the trap pdu will get filled in properly when
  being decoded


** Changes in package uk.co.westhawk.snmp.pdu:
- BlockPdu: added method getErrorIndex()

** Changes in package org.bouncycastle.crypto.*:
- Using version 1.08 (instead of 1.05) of the Bouncy Castle crypto API

** Overall changes:
- because of AsnUnsInteger, some variables are now of type long as well
  (instead of type int)
- changed some typos (whilest -> whilst)

*** NEW
- interface uk.co.westhawk.snmp.stack.SnmpConstants


Version 4.8
27/04/2001

We would like to thank Visionael Corp (http://www.visionael.com) for
their support.


****** CHANGES ****

- org/bouncycastle/crypto - Updated bouncycastle version to 105
- catch exception in pdu.fillin that can happen when agent makes an
  error in encoding the varbind list


Version 4.7:
13/04/2001

We would like to thank West Consulting BV (http://www.west.nl), 
Adherent Systems Ltd (http://www.adherent.com) and 
Visionael Corp (http://www.visionael.com) for their support. 

****** NEW ****

** Privacy to SNMPv3 

The stack now supports SNMPv3 privacy. Please read the
StackUsage.html and the javadoc for more information.

** Packages:
- org/bouncycastle/crypto - Using bouncycastle crypto instead of cryptix


****** CHANGES ****

- Fixed a number of problems with SNMPv2c 
- Fixed a number of problems with the GetBulkRequest
- Agent- and DecodingExceptions that occurs whilest decoding, will now
  be passed to 'arg' in you update(obs, arg).
- Moved around some debuging levels.

*** uk/co/westhawk/snmp/stack/GetBulkPdu.java
- Added setMaxRepetitions(int no)
- Made setMaxRepititions(int no) deprecated, use setMaxRepetitions(int no).
  
*** uk/co/westhawk/snmp/stack/SnmpContextv3Face.java
*** uk/co/westhawk/snmp/stack/SnmpContextv3.java
*** uk/co/westhawk/snmp/stack/SnmpContextv3Pool.java
- Added methods for privacy


Version 4.4:
07/03/2001

We would like to thank West Consulting BV (http://www.west.nl), 
Adherent Systems Ltd (http://www.adherent.com) and 
Visionael Corp (http://www.visionael.com) for their support. 

****** NEW ****

** Sending and Receiving Traps (v1, v2c, v3)

The stack now supports sending and receiving traps. Please read the
StackUsage.html and the javadoc for more information.

** Packages:
- uk.co.westhawk.snmp.event

** Classes:
- uk.co.westhawk.snmp.stack.TrapPduv1
- uk.co.westhawk.snmp.stack.TrapPduv2
- uk.co.westhawk.snmp.stack.DefaultTrapContext
- uk.co.westhawk.snmp.stack.DefaultUsmAgent
- uk.co.westhawk.snmp.stack.UsmAgent
- uk.co.westhawk.snmp.stack.AsnTrapPduv1Sequence
- uk.co.westhawk.snmp.pdu.OneTrapPduv1
- uk.co.westhawk.snmp.pdu.OneTrapPduv2


Version 4.3:
15/02/2001

****** NEW ****

** SNMPv3

We have implemented SNMPv3. It does not (yet) contain privacy and traps. 
For authentication the MD5 and SHA1 protocols can be used.

** Exceptions:
- PduException
- AgentException (extend PduException)
- DecodingException (extend PduException)
- EncodingException (extend PduException)

** Classes:
- We have implemented AsnUnsInteger64. We have not tested this though,
  since I couldn't access any MIB who implemented that.
- We have implemented GetBulkPdu.
- We have implemented BlockPdu that performs a sends a request and
  blocks until it receives a response.

** Packages:
- uk.co.westhawk.snmp.util
- uk.co.westhawk.snmp.net
- uk.co.westhawk.appletv3

** The Context structure:
- New interface SnmpContextFace and SnmpContextv3Face
- New class AbstractSnmpContext implements SnmpContextFace
- New class SnmpContextPool that shares SnmpContext objects
- New class SnmpContextv3 extends AbstractSnmpContext
- New class SnmpContextv3Pool that shares SnmpContextv3 objects


** SNMPv2c

We have implemented SNMPv2c. It does not (yet) contain traps.

** Packages: 
- uk.co.westhawk.appletv2c

** The Context structure:
- New class SnmpContextv2c extends SnmpContext
- New class SnmpContextv2cPool that shares SnmpContextv3 objects

****** CHANGES ****
1.
The community name has moved from Pdu to SnmpContext. Instead of using:
  Pdu pdu;
  pdu.send(community);

use:
  SnmpContext context;
  Pdu pdu;
  context.setCommunity(community);
  pdu.send();

Almost all Pdu constructors with the community name in it have been removed.

2.
Other Pdu methods have changed:
- send() may throw a PduException.
- added method getResponseVarbinds() throws PduException
- added method getRequestVarbinds() 
- The Pdu constructor uses SnmpContextFace (see above).

3.
- Changed class SnmpContext so it extends AbstractSnmpContext (instead of 
  Object)

4.
- Changed debug setup. See AsnObject.setDebug().

5.
- Besides the mandatory SNMPv3 objects (like COUNTER64), added
  NSAP_ADDRESS, UINTEGER32 to AsnObject.

6.
- Added AsnObject.getRespType().

7.
- AsnOctets.toString() will prefix any hexidecimal number with '0x'.
- AsnOctets.toString() has improved the test to see if the Octet is
  printable. 
- AsnOctets will handle an OPAQUE type as well.

8.
- Pdu.getErrorStatusString() will return 'timed out' instead of 'general
  error' when the request has timed out.


Version 3.3:
10/02/2000 - Manchester (UK), Westhawk Ltd.

We have fixed some thread problems in the pdu package.

Tim Panton (snmp@westhawk.co.uk)
Birgit Arkesteijn (snmp@westhawk.co.uk)



Version 3.0:
01/07/1999 - Manchester (UK), Westhawk Ltd.

We have moved to the UK, note our change of email address and webpages.

The setup of the classes has changed; 
- package names contain our company domain
    uk.co.westhawk.applet1_0
    uk.co.westhawk.applet1_1
    uk.co.westhawk.servlet
    uk.co.westhawk.snmp.beans (NEW)
    uk.co.westhawk.visual (NEW)
    uk.co.westhawk.snmp.pdu
    uk.co.westhawk.snmp.stack

Some methods names has changed to match the JDK 1.1 convention. No
backwards compatibility is provided!

The documentation (in javadoc format) has been improved. 

***** uk.co.westhawk.snmp.beans (JDK1.1) NEW

The package uk.co.westhawk.snmp.beans contains beans that can be used in
any Java Builder. It is written to ease the use of our SNMP stack.
They are simple enough to serve as example to write your own beans.

The (toplevel) bean SNMPBean contains some general information, that is
worthwhile reading.

Some Windows NT specific beans have been written, using the NT vendor
MIB.
There is an Ascend Router bean and an IBM Network Computer bean.

***** uk.co.westhawk.visual (JDK1.1) NEW

The package uk.co.westhawk.visual contains a graph that can be used to
represent numerical data. The graph can draw the 10log() representation
or the normal representation.

***** uk.co.westhawk.snmp.stack (JDK1.1)

Not much has changed since the previous version. Documentation has been
added. Some classes and methods have been restricted to access by this
class only. Some method names have changed to match the JDK 1.1 style.

***** uk.co.westhawk.snmp.pdu (JDK1.1)

Not much has changed since the previous version. Documentation has been
added. Some classes and methods have been restricted to access by this
class only. Some method names have changed to match the JDK 1.1 style.

We have added the Pdu:
- OneGetPdu
  This class is added to be consists with the other classes, like
  OneSetPdu, OneGetNextPdu.


***** uk.co.westhawk.servlet (JDK1.1)

We have added a servlet:
- ReachHostServlet
  This servlet returns a HTML form that can be used to find out if a
  host can be reached on a certain port.

The servlet Interfaces contains some general information, that is
worthwhile reading.


***** uk.co.westhawk.applet1.0 (JDK1.0)

These applets show how to use the pdu's and the servlets.
Documentation is improved.

***** uk.co.westhawk.applet1.1 (JDK1.1)

Every aspect of our SNMP stack can be found in one of the applets. These 
applets show how to use the pdus, the beans, the graph. The documentation 
is improved.

The html files may contain our (test) configuration, so you may have 
change that to your own.

Some applets can be used as applications as well. 

We have added an applet for every bean that is written:
- testAscendActiveSessionBean
- testDialogChannelStatusBean
- testInterfaceBean
- testNTPrintQBean
- testNTServiceNamesBean
- testNTSharedResBean
- testNTUserNamesBean
- testNcdPerfDataBean (contains the graph as well)

We have added an applet that used the SetPdu request:
- set_one



***** How to use the stack

The stack can be used in various ways, this makes it flexible on one
hand, but confusing on the other hand.

The easiest way to use the stack, is to use one of the beans from the
uk.co.westhawk.snmp.beans package. There should be enough documentation
in the code to help you. 

The beans form a bridge between the uk.co.westhawk.snmp.pdu (and
uk.co.westhawk.snmp.stack) package any applet or application you may
want to write.

Our bean gathers specific MIB information, i.e. the OID (Object
Identifier) is hard coded. The beans should be easy enough to understand, and
with little effort you can write a bean for your own purpose.

You can use, of course, the Pdu's from the uk.co.westhawk.snmp.pdu package 
directly, which offers you more flexibility. Our beans and applets will 
show you how, although this will require some basic knowledge of SNMP.



We also provide you with a couple of servlets. The benefits of servlets
are described in the package documentation. Servlets can be combined with
(simple) HTML or with an applet on the client side.


For more background on SNMP we recommend:
- The Simple Book by Marshall T. Rose (Prentice Hall, 1994).
- SNMP, SNMPv2 and RMON by William Stallings (Addison-Wesley, 1996).

Tim Panton (snmp@westhawk.co.uk)
Birgit Arkesteijn (snmp@westhawk.co.uk)


Version 2.0:
21/08/97

In this version two stacks have been joined:
- Version 1.1 of West Consulting BV 
  (see <URL: http://www.west.nl/archive/java/snmp/>)
- Our version Alpha2 of the stack
  (see <URL http://www.westhawk.co.uk/nblue4.html>)


The setup of the classes has changed; 
- package snmp has been split into
    snmp.pdu
    snmp.stack


***** snmp.stack (JDK1.1)

The package snmp.stack contains only those classes that are necessary to
send/receive SNMP requests.
For that purpose the classes:
- GetNextPdu
- SetPdu
  have been made abstract so they are consistent with the Pdu class.
  See snmp.pdu package for non-abstract versions.

The thread communication between Pdu, SnmpContext and Transmitter is
improved.

***** snmp.pdu (JDK1.1)

All Pdu's that did not belong strictly to the stack have been moved to
the package snmp.pdu.
We have added some Pdu's:
- OneGetNextPdu
- OneSetPdu
  These should be used instead of the old GetNextPdu and SetPdu.
  GetNextPdu and SetPdu are made abstract like Pdu to be more
  consistent.

- GetNextPdu_vec
- SetPdu_vec
  These Pdu are the vector versions of OneGetNextPdu and OneSetPdu. They
  request/set a vector of variables instead of one.

- InterfaceGetNextPdu
  This class does a GetNext on the interfaces (ifTable) of a host. It can be 
  used to loop over all interfaces in the ifTable.
  See for that purpose 
  * the servlet Interfaces or 
  * the applet1.1 getAllInterfaces


***** servlet (JDK1.1)

We have added a servlet:
- Interfaces
  This servlet loops at a regular interval over all interfaces of a
  certains host. The HOST, PORT and INTERVAL should be configured in the
  JavaServer.
  The applet1.0 getAllInterfaces is written to communicate with this
  servlet. 


***** applet1.0 (JDK1.0)

The applets are split up into the ones written with JDK1.0 and with
JDK1.1 This is done because at this time there are hardly any 1.1
Browsers.

The applet:
- getAllInterfaces
  This a very thin applet.
  It displays all the interface usage (bandwidth) of a particular host. It
  gets its information of a servlet (configurable), the so-called 3-tier
  setup. The host is configured in the servlet.


***** applet1.1 (JDK1.1)

The applets are split up into the ones written with JDK1.0 and with
JDK1.1 This is done because at this time there are hardly no 1.1
Browsers.

The applet:
- getAllInterfaces
  is the applet version of the servlet Interfaces. There is no display
  like applet1.0, just printing.




Tim Panton (snmp@westhawk.nl)
Birgit Arkesteijn (snmp@westhawk.nl)


Version 1.1:
30/10/96
Note the additional copyright, I changed jobs.

Second version of stack. Changes are:
	Bug in ASNInteger fixed.
	Smarter use of threads, separate Transmitter class which is re-useable.
	Application specific PDUs:
		 InterfacePdu	: stats on an interface
		 UpSincePdu	: gives the date that the agent started.
	Sync mode for PDUs, you can send a PDU and then block waiting for an 
	answer by calling Pdu.waitForSelf(), returns a boolean indicating if 
	an answer was recieved.
	More demo programs:
		get_one		: an applet which repeatedly gets a single 
				  ASNInteger (sysUptime is good) and displays 
				  thread activity.
		Interface	: an applet that shows the speed of an 
				  interface as calcuated from two sucessive 
				  measurements.
		Interfaces	: an applet that shows the state of all (if 
				  they fit) the interfaces on an agent.
		JeevesUpSince	: a servlet (for use in the Jeeves web server 
				  (SUN)) that displays the date a host was 
				  rebooted.
		JeevesInterfaces: a servlet that displays the status of the 
				  interfaces on an agent.
	(The demo directory contains some examples for use with the Jigsaw
	webserver (from W3C) )

	Note that the Interfaces demos assume that the interfaces are 
	contiguous and numbered from one. Livingstone Portmasters don't do this.
	The applets can all be seen by running the html file of the same name,
	the default hostname won't work, so you will have to edit them.

	To use the Jeeves and Jigsaw demos you will have to install the server
	of your choice and then add this directory to the server's classpath.
	Start the server and then follow the instructions for adding servlets.

The status of the stack is unchanged, it is still a work-in-progress, and
certainly not a robust commercial product. The code's none to clean either.

I'll try to keep releasing new versions and putting them on 
www.westhawk.co.uk

tpanton@ibm.net




Version 1.0:
11/03/96

This is a simple SNMP Version 1 stack.

Background.

It was written to learn Java and to see if it could be done.
The target application is _small_ applets that indicate the state of
a system. For example the throughput of a router or the battery power
remaining of a UPS. It is not intended to be used in a general purpose 
network management station. It has no MIB browsing capabilities, you have
to know the OID of what you want to monitor. There are _many_ other packages
that do MIB browsing and general network management. I wanted something lighter
that I could just pop in a (netscape) frame  and leave there all day.
In practice this means that it will start to become inefficient once you
have more than 10 or so active PDUs at a time, each PDU owns (is) a thread,
and holds it for ~20 seconds after the request is sent, so if your update
period is short or the number of PDUs is large then the number of threads
will rise rapidly. 

Copyright.

See the header of each file: Basically you can use it for whatever you want,
but you can't blame us for anything wrong with it, and you can't rewrite history
by removing our copyright text.

Status.

It is a work in progress, don't assume it is tested, or that it represents 
typical West quality, it was done as a prototype, and seemed to have some
value of it's own so we set it free :-).

Use.

The idea is that for each class of problem you should subclass the abstract
Pdu class. This design is based on the observation that most realworld numbers
you want to extract from an SNMP agent are based on two or more values from the
MIB. Throughput for example is caluculated from five numbers, so a throughput
Pdu would request all five, calculate the single throughput percentage and
then pass it back to whoever wanted to know. I include a trivial example - 
OneIntPdu which gets a single integer value from an Snmp agent.


Once you have such a Pdu class - or are happy with OneInt then uage is simple,
see get_one.java
The steps you have to follow are :
	1) create an SnmpContext , giving it the host name and port number
of your SNMP agent (If this is run in an applet then you an only 
connect back to that server - but you can watch the throughput on that,
which is what I use it for.)
	2) create a PDU, passing it the SnmpContext, add the OIDs you are
interested in with pdu.add_oid(oid). A better way would be to have the
PDU constructor add what it needs, but OnIntPdu is too dumb. 
	3) add an observer with pdu.addObserver(this), the argument is the
object that wishes to be notified when the new value arrives. It must implement
Observer, that is it must have a 	
	public void update(Observable obs, Object ov)
method. This method will get called when the value arrives.
	4) call
		 pdu.send("public");		
Actually the community name should really belong with the context, but I have 
not got arround to fixing that.
	5) sleep, or do something else, your update method will get called
automatically when the data has arrived, both the Context and PDU objects
spawn their own threads, so there is nothing further to do.


*** Contact snmp@westhawk.co.uk ***
