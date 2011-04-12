// NAME
//      $RCSfile: JeevesInterfaces.java,v $
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
 * When accessed, the JeevesInterfaces servlet will collect once the
 * information about all the interfaces on a SNMP server. It will hold the
 * connection until it has received the information. Then it will print
 * the information to your webpage.
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
 *
 * @see InterfaceGetNextPdu
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 3.10 $ $Date: 2006/02/09 14:20:07 $
 */
public class JeevesInterfaces extends HttpServlet
{
    private static final String     version_id =
        "@(#)$Id: JeevesInterfaces.java,v 3.10 2006/02/09 14:20:07 birgit Exp $ Copyright Westhawk Ltd";

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        final int port = 161;
        final String comm = "public";

        InterfaceGetNextPdu intef;
        int numIfs = 0;
        SnmpContext context;
        String [] answers = null;
        String error = null;

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
            context.setCommunity(comm);
            numIfs = InterfaceGetNextPdu.getIfNumber(context);
            answers = new String[numIfs];
            int index = 0;
            for (int i=0; i<numIfs; i++)
            {
                // interface may not have consecutive numbers
                intef = new InterfaceGetNextPdu(context);
                intef.addOids(index);
                intef.send();
                if (intef.waitForSelf())
                {
                    index = intef.getIfIndex();
                    answers[i] = "no " + index 
                              + " - " + intef.getIfDescr()
                              + " - " + intef.getIfOperStatusStr();
                }
                else
                {
                    answers[i] = index + " - waited in vain";
                }
            }
        }
        catch (java.io.IOException exc)
        {
            error = "IOException " + exc.getMessage();
        }
        catch (PduException exc)
        {
            error = "PduException " + exc.getMessage();
        }

        res.setStatus(res.SC_OK);
        res.setContentType("text/html");

        //res.writeHeaders();
        PrintWriter out = res.getWriter();
        out.println("<html>");
        out.println("<head><title>"+getServletInfo()+"</title>");
        out.println("</head><body>");
        out.println("<h2>"+getServletInfo()+"</h2>");
        if (error != null)
        {
            out.println("<p> Error " + error + "</p>");
        }
        else
        {
            out.println("<p> Host "+host+", " + numIfs + " interfaces: ");
            out.println("<ul>");
            if (answers != null)
            {
                for (int i=0; i<answers.length; i++)
                {
                    out.println("<li>"+answers[i]+"</li>");
                }
            }
            out.println("</ul></p>");
        }
        out.println("</body></html>");
        out.flush();
        out.close();
    }

    public String getServletInfo()
    {
        return "Snmp Interface status checker";
    }

}

