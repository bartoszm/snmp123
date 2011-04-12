Version 6.1:
18/03/2009

This is version 6.1 of the simple Java SNMPv1, SNMPv2c and SNMPv3 stack.
Both releases can be downloaded from SourceForge:
- https://sourceforge.net/project/showfiles.php?group_id=182520

Intermediate changes can be downloaded via cvs from SourceForge:
- https://sourceforge.net/scm/?type=cvs&group_id=182520

Read the file StackUsage.html for general information how to use the stack.
The frequently asked questions can be found in faq.html.
See http://snmp.westhawk.co.uk/index.html

Please post any questions on our mailing list on SourceForge:
https://lists.sourceforge.net/lists/listinfo/westhawksnmp-snmp

This version was not tested against IPv6.

We would like to thank everyone that was kind enough to commission
paid work, send us bug fixes and/or made useful suggestions:
- Martin Bacon - EKM4 <mbacon@ekm5.net>
- Bob Kostes - AMT <rkostes@altmantech.com>
- Josh Bers - BBN <jbers@bbn.com>
- Nick Sheen <nsheen@tippingpoint.com>

Sorry if we've left anyone out.

Birgit & Tim


********************************************
********************* BUG FIXES ************
********************************************

- To enable deserialization, each of the AsnXXX classes sets its field
  'type' in its constructor.

- Before when changing a property in one of the XXXContextXXXPool
  classes, a new context was created without the previous being
  destroyed, causing leakages.
  Each of the Pool classes now destroys its previous
  context (if there is one) when a new one is created in
  getMatchingContext().
  Note, destroy() only happens within one instance of the Pool object.
  When calling 'new XXXContextXXXPool(a, b, c)' the previous context is
  not destroy, as is the case with the none-Pool classes.

- Made sure that for every context the 'bindAddr' field is included in
  the getHashKey() method.


********************************************
********************* OVERALL CHANGES ******
********************************************

- Added AES privacy encryption (SNMPv3).

- The Constructor parameter 'typeSocket' in each SnmpContextXXX class
  accepts, besides STANDARD_SOCKET and TCP_SOCKET, a fully qualified
  classname.


********************************************
********************** CHANGES *************
********************************************

*** Changes in package UK.CO.WESTHAWK.SNMP.UTIL:

- uk.co.westhawk.snmp.util.SnmpUtilities:
  # Added methods:
    + public static byte[] getSaltAES()
    + public final static byte[] getAESKey(byte[] secretPrivacyKey)
    + public static byte [] getAESInitialValue(int engineBoots,
          int engineTime, byte[] salt)
    + public static byte[] AESencrypt(byte[] plaintext, 
          byte[] secretPrivacyKey, int engineBoots, int engineTime,
          byte[] salt)
    + public final static byte[] AESdecrypt(byte[] ciphertext, 
          byte[] secretPrivacyKey, int engineBoots, int engineTime,
          byte[] salt)
    + final static void setBytesFromLong(byte[] ret, long value, 
          int offs)


*** Changes in package UK.CO.WESTHAWK.SNMP.STACK:

- uk.co.westhawk.snmp.stack.AbstractSnmpContext:
  # Constructor parameter 'typeSocketA' accepts, besides STANDARD_SOCKET
    and TCP_SOCKET, a fully qualified classname.

- uk.co.westhawk.snmp.stack.AsnDecoderv3:
- uk.co.westhawk.snmp.stack.AsnEncoderv3:
  # Added implementation of AES

- uk.co.westhawk.snmp.stack.SnmpContextv3Basis:
  # Added methods:
    + public void setPrivacyProtocol(int protocol)
    + public int getPrivacyProtocol()

- uk.co.westhawk.snmp.stack.SnmpContextv3Face:
  # Added methods:
    + public void setPrivacyProtocol(int protocol)
    + public int getPrivacyProtocol()

- uk.co.westhawk.snmp.stack.AsnInteger:
  # To enable deserialization:
    Set type = ASN_INTEGER in constructor.

- uk.co.westhawk.snmp.stack.AsnNull:
  # To enable deserialization:
    Set type = ASN_NULL in constructor.

- uk.co.westhawk.snmp.stack.AsnUnsInteger64:
  # To enable deserialization:
    Set this.type = COUNTER64 in constructor.

- uk.co.westhawk.snmp.stack.AsnObjectId:
  # To enable deserialization:
    + Changed class variable 'value' so it's empty.
    + Made default constructor protected.
    + Set type = ASN_OBJECT_ID in default constructor.
    + Called 'this()' in other constructors, except
      AsnObjectId(InputStream in, int len)
  # implements Comparable
  # Added methods:
    + public long removeLast()
    + public long lastElement()
    + public int compareTo(Object o)
    + public int compareTo(AsnObjectId b)
    + public int leftMostCompare(int n, AsnObjectId b)

- uk.co.westhawk.snmp.stack.ListeningContext:
  # Changed synchronization of some methods
  # Change signature of method:
    private void startListening()
  # Added methods:
    private void destroyIfNoListeners()
    public String getHashKey()

- uk.co.westhawk.snmp.stack.Pdu:
  # Changed NEXT_ID_LOCK object

- uk.co.westhawk.snmp.stack.SnmpContext:
- uk.co.westhawk.snmp.stack.SnmpContextv2c:
  # Changed getHashKey() method, so it includes the 'bindAddr' field.

- uk.co.westhawk.snmp.stack.ListeningContextPool:
- uk.co.westhawk.snmp.stack.SnmpContextPool:
- uk.co.westhawk.snmp.stack.SnmpContextv2cPool:
  # The previous context is destroyed (if there is any) when a new one
    is created in getMatchingContext()

- uk.co.westhawk.snmp.stack.SnmpContextv3Pool:
  # The previous context is destroyed (if there is any) when a new one
    is created in getMatchingContext()
  # Added methods:
    + public void setPrivacyProtocol(int protocol)
    + public int getPrivacyProtocol()

