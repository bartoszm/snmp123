// NAME
//      $RCSfile: JeevesUpSince.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.10 $
// CREATED
//      $Date: 2006/02/09 14:20:07 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1996 - 2006 by Westhawk Ltd
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


package uk.co.westhawk.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import java.util.*;
import java.io.*;

/**
 * <p>
 * When accessed, the JeevesUpSince servlet sends once a UpSincePdu request 
 * to the configured host to get the uptime of the host.
 * It will hold the connection until it has received the information. Then 
 * it will print the information to your webpage.
 * </p>
 *
 * <p>
 * It supports the GET action. The following parameter should be sent
 * in the URL: 
 * <ul>
 * <li>host - the hostname or IP address of the SNMP server, default is <em>your localhost</em></li>
 * </ul>
 * For example:
 * <pre>
 *     http:&lt;servlet URL&gt;?host=localhost
 * </pre>
 * </p>
 *
 * <p>
 * It will use port <em>161</em> and community name <em>public</em>.
 * </p>
 *
 * Some general information about <a
 * href="./Interfaces.html#_general_">servlets</a>.
 *
 * @see uk.co.westhawk.snmp.pdu.UpSincePdu
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 3.10 $ $Date: 2006/02/09 14:20:07 $
 */
public class JeevesUpSince extends HttpServlet
{
    private static final String     version_id =
        "@(#)$Id: JeevesUpSince.java,v 3.10 2006/02/09 14:20:07 birgit Exp $ Copyright Westhawk Ltd";

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        final int port = 161;

        SnmpContext context;
        String reps = "is not replying to snmp";

        String host = req.getParameter("host");
        if (host == null)
        {
            host = req.getRemoteHost();
            if (host == null)
            {
                host = "localhost";
            }
        }

        try 
        {
            context = new SnmpContext(host, port);
            UpSincePdu since = new UpSincePdu(context, null);
            if (since.waitForSelf())
            {
                Date when = since.getDate();
                if (when != null)
                {
                    reps = "up since " + when.toString();
                } 
                else 
                {
                    reps = "no date";
                }
            } 
            else 
            {
                reps = "waited in vain";
            }
        }
        catch (java.io.IOException exc)
        {
            // should log this
            reps = "IO exception: " + exc.getMessage();
        }
        catch (PduException exc)
        {
            reps = "Pdu exception: " + exc.getMessage();
        }

        try 
        {
            res.setStatus(res.SC_OK);
            res.setContentType("text/html");

            PrintWriter out = res.getWriter();
            out.println("<html><head><title>"+getServletInfo()+"</title>");
            out.println("</head><body>");
            out.println("<h2>"+getServletInfo()+"</h2>");
            out.println("<p> Host "+host+" "+reps+"</p>");
            out.println("</body></html>");
            out.flush();
            out.close();
        }
        catch (java.io.IOException exc)
        {
            // should log this
            ;
        }
    }

    public String getServletInfo()
    {
        return "Snmp uptime checker";
    }

}

