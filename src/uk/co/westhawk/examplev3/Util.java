// NAME
//      $RCSfile: Util.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.7 $
// CREATED
//      $Date: 2008/12/15 15:52:26 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2001 - 2006 by Westhawk Ltd
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
 
package uk.co.westhawk.examplev3;

import java.net.*;
import java.io.*;
import java.util.*;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.util.*;

/**
 * <p>
 * The Util class for this package contains some convenience methods.
 * This class will try to find the/a properties file and load the
 * properties.
 * </p>
 *
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.7 $ $Date: 2008/12/15 15:52:26 $
 */
public class Util 
{
    private static final String     version_id =
        "@(#)$Id: Util.java,v 1.7 2008/12/15 15:52:26 tpanton Exp $ Copyright Westhawk Ltd";

    public final static String HOST = "host";
    public final static String BIND = "bind";
    public final static String PORT = "port";
    public final static String SOCKETTYPE = "sockettype";
    public final static String OID = "oid";

    public final static String CONTEXT_ENGINE_ID = "context_engine_id";
    public final static String CONTEXT_NAME = "context_name";
    public final static String USERNAME = "username";
    public final static String USER_AUTH_PASSWORD = "user_auth_password";
    public final static String AUTH_PROTOCOL = "auth_protocol";
    public final static String PRIV_PROTOCOL = "priv_protocol";
    public final static String USER_PRIV_PASSWORD = "user_priv_password";
    public final static String USE_AUTHENTICATION = "use_authentication";
    public final static String USE_PRIVACY = "use_privacy";

    Properties prop;
    File propFile;


/**
 * Constructor.
 * The name of the properties file can be passed as parameter.
 * If the filename is null, it will look for a properties file, 
 * based on <code>&lt;classname&gt;.properties</code>. 
 * </p>
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 * @param classname The full name of the class using this class.
 */
public Util(String propertiesFilename, String classname)
{
    propFile = null;
    prop = new Properties();

    if (propertiesFilename == null)
    {
        propertiesFilename = getDefaultPropertiesFilename(classname);
        propFile = new File(propertiesFilename);
    }
    else
    {
        propFile = new File(propertiesFilename);
        if (propFile.exists() == false)
        {
            System.out.println("Util(): Cannot find properties file '" 
                  + propFile + "'.");

            propertiesFilename = getDefaultPropertiesFilename(classname);
            propFile = new File(propertiesFilename);
        }
    }

    if (propFile.exists() == true)
    {
        loadPropfile(propFile);
    }
    else
    {
        propFile = null;
        System.out.println("Util(): Cannot find properties file '" + propFile
              + "'. Will use default properties.");
    }
}


public void loadPropfile(File file)
{
    propFile = file;

    try
    {
        FileInputStream in = new FileInputStream(propFile);
        prop.load(in);
        in.close();
        System.out.println("Util(): Using properties file '" + propFile
              + "'.");
    }
    catch (FileNotFoundException exc)
    {
        propFile = null;
        System.out.println("Util(): FileNotFoundException " + exc.getMessage());
    }
    catch (IOException exc)
    {
        propFile = null;
        System.out.println("Util(): IOException " + exc.getMessage());
    }
}

public File getPropertiesFile()
{
    return propFile;
}

public static String getDefaultPropertiesFilename(String classname)
{
    String propertiesFilename;
    if (classname == null)
    {
        propertiesFilename = "default.properties";
    }
    else
    {
        /*
        int index = classname.lastIndexOf('.');
        propertiesFilename = classname.substring(index+1) + ".properties";
        */
        propertiesFilename = classname.replace('.', File.separatorChar) + ".properties";
    }
    return propertiesFilename;
}

/**
 * Returns the name of the localhost. If that cannot be found it will
 * return <code>localhost</code>.
 *
 * @return my host 
 */
public static String myHost()
{
    String str = null;
    try
    {
        InetAddress inetA = InetAddress.getLocalHost();
        str = inetA.getHostName();
    }
    catch (UnknownHostException exc) { }

    if (str == null)
    {
        str = "localhost";
    }
    return str;
}

/**
 * Returns the <code>host</code> property.
 *
 * @return The <code>host</code> property.
 * @see #HOST
 */
public String getHost()
{
    String host = prop.getProperty(HOST, myHost());
    return host;
}

/**
 * Returns the <code>bind</code> property.
 *
 * @return The <code>bind</code> property.
 * @see #BIND
 * @since 4_14
 */
public String getBindAddress()
{
    String bind = prop.getProperty(BIND);
    return bind;
}

/**
 * Returns the <code>port</code> property.
 *
 * @return The <code>port</code> property.
 * @see #PORT
 */
public String getPort()
{
    String port = prop.getProperty(PORT, "" + SnmpContextBasisFace.DEFAULT_PORT);
    return port;
}

/**
 * Returns the <code>port</code> property. 
 *
 * @param def The default value.
 * @return The <code>port</code> property.
 * @see #PORT
 */
public int getPort(int def)
{
    int port = getIntParameter(PORT, def);
    return port;
}

/**
 * Returns the <code>sockettype</code> property. The default value will
 * be the standard socket.
 *
 * @return The <code>sockettype</code> property.
 * @see #SOCKETTYPE
 * @see SnmpContextFace#STANDARD_SOCKET
 */
public String getSocketType()
{
    String socketType = prop.getProperty(SOCKETTYPE, SnmpContextFace.STANDARD_SOCKET);
    return socketType;
}


/**
 * Returns the <code>oid</code> property. 
 *
 * @param def The default value.
 * @return The <code>oid</code> property.
 * @see #OID
 */
public String getOid(String def)
{
    String oid = prop.getProperty(OID, def);
    return oid;
}


public String getContextEngineIdStr()
{
    String param = prop.getProperty(CONTEXT_ENGINE_ID);
    return param;
}

public byte[] getContextEngineId()
{
    String param = getContextEngineIdStr();
    byte[] engineId = new byte[0];
    if (param != null)
    {
        engineId = SnmpUtilities.toBytes(param);
    }
    return engineId;
}

public String getContextName()
{
    String contextName = prop.getProperty(CONTEXT_NAME, "");
    return contextName;
}

public String getUserName()
{
    String userName = prop.getProperty(USERNAME, "authUser");
    return userName;
}

public int getUseAuth()
{
    int auth = getIntParameter(USE_AUTHENTICATION, 1);
    return auth;
}

public String getUserAuthPassword()
{
    String passw = prop.getProperty(USER_AUTH_PASSWORD, "AuthPassword");
    return passw;
}

public int getAuthProcotol()
{
    String param = prop.getProperty(AUTH_PROTOCOL, "MD5");
    int proto = SnmpContextv3Face.MD5_PROTOCOL;
    if (param != null)
    {
        char f = param.charAt(0);
        if (f == 'S')
        {
            proto = SnmpContextv3Face.SHA1_PROTOCOL;
        }
        else
        {
            proto = SnmpContextv3Face.MD5_PROTOCOL;
        }
    }
    return proto;
}

public int getUsePriv()
{
    int priv = getIntParameter(USE_PRIVACY, 1);
    return priv;
}

public String getUserPrivPassword()
{
    String passw = prop.getProperty(USER_PRIV_PASSWORD, "PrivPassword");
    return passw;
}

public int getPrivProcotol()
{
    String param = prop.getProperty(PRIV_PROTOCOL, "DES");
    int proto = SnmpContextv3Face.DES_ENCRYPT;
    if (param != null)
    {
        char f = param.charAt(0);
        if (f == 'A')
        {
            proto = SnmpContextv3Face.AES_ENCRYPT;
        }
        else
        {
            proto = SnmpContextv3Face.DES_ENCRYPT;
        }
    }
    return proto;
}

public String getProperty(String key)
{
    return prop.getProperty(key);
}

public String getProperty(String key, String defaultValue)
{
    return prop.getProperty(key, defaultValue);
}

/**
 * Return the integer value of a property. If there is no property
 * <code>key</code>, or the value is not an integer, the default value
 * is returned.
 *
 * @param key The key
 * @param def The default value
 */
public int getIntParameter(String key, int def)
{
    int val = def;
    try
    {
        String v = prop.getProperty(key);
        if (v != null)
        {
            val = Integer.valueOf(v).intValue();
        }
    }
    catch (java.lang.NumberFormatException exc) { }
    catch (NullPointerException exc) { }
    return val;
}

public static boolean isNumber(String str)
{
    boolean res = false;
    try
    {
        int t = Integer.valueOf(str).intValue();
        res = true;
    }
    catch (NumberFormatException e) { }

    return res;
}

public static int getNumber(String str)
{
    int t=0;
    try
    {
        t = Integer.valueOf(str).intValue();
    }
    catch (NumberFormatException e) { }

    return t;
}

/**
 * Prints the oid, but checks first if it is one of the usmStats
 * error messages. If so, it translates it to the usmStats string.
 */
public static String printOid(AsnObjectId oid)
{
    String msg = "";
    boolean found=false;
    int i=0;
    while (i< usmStatsConstants.usmStatsOids.length && found==false)
    {
        AsnObjectId usmOid = new AsnObjectId(usmStatsConstants.usmStatsOids[i]);
        found = (oid.startsWith(usmOid) == true);
        i++;
    }
    if (found == true)
    {
        i--;
        msg = usmStatsConstants.usmStatsStrings[i];
    }
    else
    {
        msg = oid.toString();
    }
    return msg;
}

}
