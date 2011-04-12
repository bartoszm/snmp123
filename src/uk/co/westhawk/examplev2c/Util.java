// NAME
//      $RCSfile: Util.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.5 $
// CREATED
//      $Date: 2006/02/09 14:19:05 $
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
 
package uk.co.westhawk.examplev2c;

import java.net.*;
import java.io.*;
import java.util.*;

import uk.co.westhawk.snmp.stack.*;    

/**
 * <p>
 * The Util class for this package contains some convenience methods.
 * This class will try to find the/a properties file and load the
 * properties.
 * </p>
 *
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.5 $ $Date: 2006/02/09 14:19:05 $
 */
public class Util 
{
    private static final String     version_id =
        "@(#)$Id: Util.java,v 1.5 2006/02/09 14:19:05 birgit Exp $ Copyright Westhawk Ltd";

    public final static String HOST = "host";
    public final static String BIND = "bind";
    public final static String PORT = "port";
    public final static String SOCKETTYPE = "sockettype";
    public final static String COMM = "comm";
    public final static String OID = "oid";
    public final static String INTERVAL = "interval";

    Properties prop;

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
    File propFile = null;
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
            System.out.println("Util(): FileNotFoundException " + exc.getMessage());
        }
        catch (IOException exc)
        {
            System.out.println("Util(): IOException " + exc.getMessage());
        }
    }
    else
    {
        System.out.println("Util(): Cannot find properties file '" + propFile
              + "'. Will use default properties.");
    }
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
 * Returns the <code>comm</code> property. The default value will
 * be the default community name.
 *
 * @return The <code>comm</code> property.
 * @see #COMM
 * @see SnmpContextFace#DEFAULT_COMMUNITY
 */
public String getCommunity()
{
    String comm = prop.getProperty(COMM, SnmpContextFace.DEFAULT_COMMUNITY);
    return comm;
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

}
