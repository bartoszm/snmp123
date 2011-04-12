// NAME
//      $RCSfile: getAllInterfacesUI.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.4 $
// CREATED
//      $Date: 2006/01/17 17:43:54 $
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

package uk.co.westhawk.examplev1;

import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*;

import java.util.*;
import java.io.*;
import java.net.*;

import uk.co.westhawk.tablelayout.*;
import uk.co.westhawk.visual.*;

/**
 * <p>
 * The getAllInterfacesUI application opens a connection to a servlet, parses
 * its output and shows the interface's speed as a Level.
 * </p>
 *
 * <p>
 * All parameters can be configured in the properties file. 
 * The name of the properties file can be passed as first argument to
 * this application. If there is no such argument, it will look for
 * <code>getAllInterfacesUI.properties</code>. If this file does not exist, the
 * application will use default parameters.
 * </p>
 *
 * @see Level
 * @see uk.co.westhawk.servlet.Interfaces
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.4 $ $Date: 2006/01/17 17:43:54 $
 */
public class getAllInterfacesUI extends JPanel
        implements Runnable 
{
    private static final String     version_id =
        "@(#)$Id: getAllInterfacesUI.java,v 1.4 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";

    /**
     * The servlet with all the Interface info. URL
     */
    public final static String SERVLET = "servlet";

    /**
     * The number of columns.
     */
    public final static String COLUMN = "column";

    /**
     * The polling interval (sec).
     */
    public final static String INTERVAL = "interval";

    /**
     * The foreground color of the application (text color). (rgb).
     */
    public final static String FG = "fg";

    /**
     * The background color of the application. (rgb).
     */
    public final static String BG = "bg";

    /**
     * The foreground color of the levels, when interface is up. (rgb).
     */
    public final static String LBG = "lbg";

    /**
     * The foreground color of the levels, when interface is not up. (rgb).
     */
    public final static String LFGU = "lfgu";

    /**
     * The background color of the levels. (rgb).
     */
    public final static String LFGD = "lfgd";

    // Parameters:
    private String servlet_str;
    private int column;
    private int interval;

    private Color fg,
                  bg,
                  lbg,
                  lfgu,
                  lfgd;

    private URL servlet;
    private Thread kick = null;
    private boolean isRunning = false;

    private JPanel levelPanel = new JPanel();
    private JLabel messageLabel = new JLabel(" ");
    private TableLayout table=null;
    private Hashtable   hash=null;
    private Util util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public getAllInterfacesUI(String propertiesFilename)
{
    util = new Util(propertiesFilename, this.getClass().getName());
}

public void init ()
{
    getParameters();
    this.setBackground (bg);
    this.setForeground (fg);

    servlet = null;
    try
    {
        if (servlet_str != null)
        {
            servlet = new URL(servlet_str);
        }
    }
    catch (MalformedURLException e)
    {
        setMessage("Malformed URL: " + e.getMessage());
    }
    catch(IllegalArgumentException e)
    {
        setMessage("Illegal Arguments: " + e.getMessage());
    }

    table = new TableLayout();
    this.setLayout(table);
    hash = new Hashtable();

    GridLayout grid = new GridLayout();
    grid.setColumns(column);
    grid.setRows(0);
    grid.setHgap(5);
    grid.setVgap(5);
    levelPanel.setLayout(grid);

    this.add("0 0", levelPanel);
    this.add("0 1 hH", messageLabel);
}


public void run() 
{
    URLConnection conn;
    BufferedReader inp;

    while (isRunning)
    {
        try
        {
            conn = servlet.openConnection();
            conn.setUseCaches(false);
            inp = new BufferedReader(
                  new InputStreamReader(servlet.openStream()));
                       
            String nr_str = inp.readLine();

            int nr;
            nr = 0;
            try
            {
                nr = Integer.valueOf(nr_str.trim()).intValue();
            }
            catch (NumberFormatException e)
            {
                setMessage("NumberFormatException " + nr_str + " " 
                      + e.getMessage());
            }

            if (nr>0)
            {
                for (int i=0; i<nr; i++)
                {
                    String str = inp.readLine();
                    parseInterface(new Integer(i), str);
                }
            }

            this.validate();
        }
        catch (IOException e)
        {
            setMessage("IOException(): " + e.getMessage());
        }


        try
        {
            Thread.sleep(interval*1000);
        }
        catch (java.lang.InterruptedException e)
        {
            ;// ignore it
        }
    }
}

public void start()
{
    if (servlet != null)
    {
        if (kick == null)
        {
            isRunning = true;

            kick = new Thread(this);
            kick.start();
        }
    }
}

public synchronized void stop()
{
    if (kick != null)
    {
        isRunning = false;
        kick = null;
    }
}

public void setMessage(String msg)
{
    messageLabel.setText(msg);
}

private void parseInterface(Integer linenr, String line)
{
    Level lev;

    StringTokenizer tok = new StringTokenizer(line, "\t\n\r,");
    int nr = tok.countTokens();

    String index="";
    double speed=0;
    int    opr_status=4;
    String description="";

    //System.out.println(getClass().getName() + ".parseInterface(): " + line);
    for (int i=0; i<nr; i++)
    {
        String s="";
        try
        {
            s = tok.nextToken();
        }
        catch (NoSuchElementException e) { }

        switch (i)
        {
            case 0:
                index = s;
                break;
            case 1:
                try
                {
                    speed = Double.valueOf(s.trim()).doubleValue();
                    if (speed < 0.0)
                    {
                        speed = 0.0;
                    }
                }
                catch (NumberFormatException e)
                {
                    setMessage("NumberFormatException " 
                          + s + " " + e.getMessage());
                }
                break;
            case 2:
                try
                {
                    opr_status = Integer.valueOf(s.trim()).intValue();
                }
                catch (NumberFormatException e)
                {
                    setMessage("NumberFormatException " 
                            + s + " " + e.getMessage());
                }
                break;
            case 3:
                description = s;
                break;
            default:
                ;
        }
    }

    /*
    System.out.println(
        "index=" + index +
        ", speed=" + speed +
        ", opr_status=" + opr_status +
        ", description=" + description);
    System.out.println();
    */

    if ((lev = (Level)hash.get(linenr)) == null)
    {
        lev = new Level();
        lev.setForeground(fg);
        lev.setBackground(bg);
        lev.setLevelBackground(lbg);

        if (opr_status == 1)
        {
            lev.setLevelForeground(lfgu);
        }
        else
        {
            lev.setLevelForeground(lfgd);
        }

        hash.put(linenr, lev);

        int ln = linenr.intValue();
        int col = linenr.intValue() % column;

        if (col == 0)
        {
            lev.setScale(true);
        }
        else
        {
            lev.setScale(false);
        }

        levelPanel.add(lev);
    }

    lev.setValue(speed);
    lev.setName(description + " (" + index + ")");
}

private void getParameters()
{
    servlet_str = util.getProperty(SERVLET);
    if (servlet_str == null)
    {
        setMessage("Missing paramet servlet");
    }

    column = getIntParameter(COLUMN, 5);
    interval = getIntParameter(INTERVAL, 5);

    fg = getColorParameter(FG, Color.black);
    bg = getColorParameter(BG, Color.lightGray);
    lbg = getColorParameter(LBG, Color.white);
    lfgu = getColorParameter(LFGU, Color.orange);
    lfgd = getColorParameter(LFGD, Color.red);
}

private int getIntParameter(String para, int def)
{
    String str = util.getProperty(para);
    int value = def;

    if (str != null)
    {
        try
        {
            value = Integer.valueOf(str.trim()).intValue();
        }
        catch (NumberFormatException e) { }
    }
    return value;
}

private int getColorParameter(String para, int def)
{
    String str = util.getProperty(para);
    int value = def;

    if (str != null)
    {
        try
        {
            value = Integer.valueOf(str.trim()).intValue();
        }
        catch (NumberFormatException e) { }
    }
    return value;
}

private Color getColorParameter(String para, Color def)
{
    String str = util.getProperty(para);
    Color value = def;

    if (str != null)
    {
        StringTokenizer tok = new StringTokenizer(str, "\t\n\r,");
        int nr = tok.countTokens();

        if (nr == 3)
        {
            try
            {
                int r = Integer.valueOf(tok.nextToken().trim()).intValue();
                int g = Integer.valueOf(tok.nextToken().trim()).intValue();
                int b = Integer.valueOf(tok.nextToken().trim()).intValue();
                value = new Color(r, g, b);
            }
            catch (NoSuchElementException e)
            {
                setMessage("NoSuchElementException: " + e.getMessage());
            }
            catch (NumberFormatException e)
            {
                setMessage("NumberFormatException: " + e.getMessage());
            }
        }
    }
    return value;
}

public static void main(String[] args)
{
    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    getAllInterfacesUI application = new getAllInterfacesUI(propFileName);

    JFrame frame = new JFrame();
    frame.setTitle(application.getClass().getName());
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(application, BorderLayout.CENTER);
    application.init();

    frame.addWindowListener(new WindowAdapter()
    {
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    });
    frame.setBounds(50, 50, 750, 500);
    frame.setVisible(true);
    application.start();
}

}
