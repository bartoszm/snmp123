// NAME
//      $RCSfile: MultipleHosts.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.3 $
// CREATED
//      $Date: 2006/01/17 18:02:47 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 2002 - 2006 by Westhawk Ltd
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
 
package uk.co.westhawk.examplev1;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;

/**
 * This application polls a number of hosts for their sysUptime and
 * measures the throughput (PDUs per second).
 * The UI shows the hosts, the number of request sent and the sysUptime. 
 *
 * @author <a href="mailto:thp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 1.3 $ $Date: 2006/01/17 18:02:47 $
 */
public class MultipleHosts extends JPanel
    implements Observer, ActionListener
{
    private final static String version_id =
            "@(#)$Id: MultipleHosts.java,v 1.3 2006/01/17 18:02:47 birgit Exp $ Copyright Westhawk Ltd";

    private String host0;
    private String host1;
    private String host2;
    private String host3;
    private String host4;
    private SnmpContext[] contexts = new SnmpContext[5];
    private String[] vals = new String[5];
    private JLabel hostL[] = new JLabel[5];
    private JLabel hostV[] = new JLabel[5];
    private JLabel hostC[] = new JLabel[5];
    private int[] counts = new int[5];
    private javax.swing.Timer tock = new javax.swing.Timer(200, this);

    private BorderLayout borderLayout3 = new BorderLayout();
    private JPanel hostPanel = new JPanel();
    private GridLayout gridLayout2 = new GridLayout();
    private JPanel host4panel = new JPanel();
    private JLabel hostV4 = new JLabel();
    private JLabel hostC4 = new JLabel();
    private JLabel hostL4 = new JLabel();
    private GridLayout gridLayout3 = new GridLayout();
    private JPanel host3panel = new JPanel();
    private JLabel hostC3 = new JLabel();
    private JLabel hostL3 = new JLabel();
    private JLabel hostV3 = new JLabel();
    private JLabel jLabel4 = new JLabel();
    private GridLayout gridLayout4 = new GridLayout();
    private JPanel host2panel = new JPanel();
    private JLabel hostC2 = new JLabel();
    private JLabel hostL2 = new JLabel();
    private JLabel hostV2 = new JLabel();
    private JLabel jLabel5 = new JLabel();
    private GridLayout gridLayout5 = new GridLayout();
    private JPanel host1panel = new JPanel();
    private JLabel hostC1 = new JLabel();
    private JLabel hostL1 = new JLabel();
    private JLabel hostV1 = new JLabel();
    private JLabel jLabel6 = new JLabel();
    private GridLayout gridLayout6 = new GridLayout();
    private JPanel host0panel = new JPanel();
    private JLabel hostC0 = new JLabel();
    private JLabel hostL0 = new JLabel();
    private JLabel hostV0 = new JLabel();
    private JLabel jLabel7 = new JLabel();
    private GridLayout gridLayout1 = new GridLayout();
    private JLabel jLabel10 = new JLabel();
    private JLabel errLine = new JLabel();
    private JPanel topPanel = new JPanel();
    private JLabel jLabel1 = new JLabel();
    private BorderLayout borderLayout2 = new BorderLayout();
    private JLabel speedL = new JLabel();

    private JPanel headerPanel = new JPanel();
    private GridLayout gridLayout7 = new GridLayout();
    private JLabel jLabel2 = new JLabel();
    private JLabel jLabel3 = new JLabel();
    private JLabel jLabel8 = new JLabel();
    private JLabel jLabel9 = new JLabel();

    private Date then;
    private long ocount = 0;
    private Util        util;


/**
 * Constructor.
 *
 * @param propertiesFilename The name of the properties file. Can be
 * null.
 */
public MultipleHosts(String propertiesFilename)
{
    util = new Util(propertiesFilename, this.getClass().getName());
}



/**
 *  Initialize the application
 */
public void init()
{
    host0 = util.getProperty("host0", "hort.westhawk.co.uk");
    host1 = util.getProperty("host1", "roke.westhawk.co.uk");
    host2 = util.getProperty("host2", "hands.westhawk.co.uk");
    host3 = util.getProperty("host3", "tolk.westhawk.co.uk");
    host4 = util.getProperty("host4", "ully.westhawk.co.uk");

    try
    {
        jbInit();
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }

}


/**
 *  Start the application
 */
public void start()
{
    initContexts();
    startPolling();
    setArrays();
    tock.setRepeats(true);
    tock.start();
}


/**
 *  Stop the application
 */
public void stop()
{
    destroyContexts();
    tock.stop();
}


public void actionPerformed(ActionEvent e)
{
    for (int i = 0; i < contexts.length; i++)
    {
        hostC[i].setText("" + counts[i]);
        hostV[i].setText(this.vals[i]);
        hostL[i].setText(contexts[i].getHost());
    }
    speedL.setText(calcSpeed());
    this.repaint();
}


/**
 * Get the data from the PDU's
 *
 *@param  o    Description of Parameter
 *@param  arg  Description of Parameter
 */
public void update(Observable o, Object arg)
{
    SysUpTime p = (SysUpTime) o;
    String text = "error";
    if (arg instanceof Exception)
    {
        // got an error
        text = ((Exception) arg).getMessage();
    }
    else if (arg instanceof Long)
    {
        // got a value
        text = arg.toString();
    }
    SnmpContextBasisFace c = p.getContext();
    // find the context number
    for (int i = 0; i < contexts.length; i++)
    {
        if ((contexts[i] == c) && (c != null))
        {
            // found it
            counts[i]++;
            //update the gui
            vals[i] = text;
            // request another
            doPoll(c);
            break;
        }
    }

}


/**
 *  Sets the arrays attribute of the MultipleHosts object
 */
void setArrays()
{
    hostL[0] = hostL0;
    hostL[1] = hostL1;
    hostL[2] = hostL2;
    hostL[3] = hostL3;
    hostL[4] = hostL4;

    hostC[0] = hostC0;
    hostC[1] = hostC1;
    hostC[2] = hostC2;
    hostC[3] = hostC3;
    hostC[4] = hostC4;

    hostV[0] = hostV0;
    hostV[1] = hostV1;
    hostV[2] = hostV2;
    hostV[3] = hostV3;
    hostV[4] = hostV4;
}


void initContexts()
{
    try
    {
        int port = SnmpContextBasisFace.DEFAULT_PORT;

        contexts[0] = new SnmpContext(host0, port);
        contexts[1] = new SnmpContext(host1, port);
        contexts[2] = new SnmpContext(host2, port);
        contexts[3] = new SnmpContext(host3, port);
        contexts[4] = new SnmpContext(host4, port);
    }
    catch (Exception x)
    {
        this.errLine.setText(x.getMessage());
        x.printStackTrace();
    }
}


void destroyContexts()
{
    for (int i = 0; i < contexts.length; i++)
    {
        if (contexts[i] != null)
        {
            contexts[i].destroy();
        }
    }
}


void startPolling()
{
    for (int i = 0; i < contexts.length; i++)
    {
        if (contexts[i] != null)
        {
            doPoll(contexts[i]);
        }
    }
}


String calcSpeed()
{
    String ret = "";
    long count = 0;
    Date now = new Date();
    for (int i = 0; i < counts.length; i++)
    {
        count += counts[i];
    }
    if (then != null)
    {
        long tdiff = now.getTime() - then.getTime();
        long cdiff = (count - ocount);
        long rate = cdiff * 1000 / tdiff;
        ret = "" + rate + " pdus/sec";

    }
    then = now;
    ocount = count;
    return ret;
}


void doPoll(SnmpContextBasisFace co)
{
    try
    {

        SysUpTime up = new SysUpTime(co, UpSincePdu.SYSUPTIME, this);
    }
    catch (Exception x)
    {
        this.errLine.setText(x.getMessage());
        x.printStackTrace();
    }
}


/**
 * Component initialization
 *
 *@exception  Exception  Description of Exception
 */
private void jbInit()
    throws Exception
{
    host4panel.setLayout(gridLayout2);
    hostV4.setHorizontalAlignment(SwingConstants.RIGHT);
    hostV4.setText("value4");
    hostC4.setHorizontalAlignment(SwingConstants.RIGHT);
    hostC4.setText("count4");
    hostL4.setHorizontalAlignment(SwingConstants.LEFT);
    hostL4.setText("host4");
    host3panel.setLayout(gridLayout3);
    hostC3.setHorizontalAlignment(SwingConstants.RIGHT);
    hostC3.setText("count3");
    hostL3.setHorizontalAlignment(SwingConstants.LEFT);
    hostL3.setText("host3");
    hostV3.setHorizontalAlignment(SwingConstants.RIGHT);
    hostV3.setText("value3");
    jLabel4.setText("3");
    host2panel.setLayout(gridLayout4);
    hostC2.setHorizontalAlignment(SwingConstants.RIGHT);
    hostC2.setText("count2");
    hostL2.setHorizontalAlignment(SwingConstants.LEFT);
    hostL2.setText("host2");
    hostV2.setHorizontalAlignment(SwingConstants.RIGHT);
    hostV2.setText("value2");
    jLabel5.setText("2");
    host1panel.setLayout(gridLayout5);
    hostC1.setHorizontalAlignment(SwingConstants.RIGHT);
    hostC1.setText("count1");
    hostL1.setHorizontalAlignment(SwingConstants.LEFT);
    hostL1.setText("host1");
    hostV1.setHorizontalAlignment(SwingConstants.RIGHT);
    hostV1.setText("value1");
    jLabel6.setText("1");
    host0panel.setLayout(gridLayout6);
    hostC0.setHorizontalAlignment(SwingConstants.RIGHT);
    hostC0.setText("count0");
    hostL0.setHorizontalAlignment(SwingConstants.LEFT);
    hostL0.setText("host0");
    hostV0.setHorizontalAlignment(SwingConstants.RIGHT);
    hostV0.setText("value0");
    jLabel7.setText("0");
    hostPanel.setLayout(gridLayout1);
    gridLayout1.setRows(6);
    gridLayout6.setColumns(4);
    gridLayout5.setColumns(4);
    gridLayout4.setColumns(4);
    gridLayout3.setColumns(4);
    gridLayout2.setColumns(4);
    jLabel10.setText("4");
    errLine.setHorizontalAlignment(SwingConstants.CENTER);
    errLine.setText("OK");
    jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel1.setText("Multiple query sample application");
    topPanel.setLayout(borderLayout2);
    speedL.setHorizontalAlignment(SwingConstants.CENTER);
    speedL.setText("speed");
    headerPanel.setLayout(gridLayout7);
    gridLayout7.setColumns(4);
    jLabel2.setText(" ");
    jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel3.setText("#Requests");
    jLabel9.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel9.setText("Uptime");
    jLabel8.setText("Host");
    headerPanel.add(jLabel2, null);
    headerPanel.add(jLabel8, null);
    headerPanel.add(jLabel3, null);
    headerPanel.add(jLabel9, null);
    host0panel.add(jLabel7, null);
    host0panel.add(hostL0, null);
    host0panel.add(hostC0, null);
    host0panel.add(hostV0, null);
    host1panel.add(jLabel6, null);
    host1panel.add(hostL1, null);
    host1panel.add(hostC1, null);
    host1panel.add(hostV1, null);
    host2panel.add(jLabel5, null);
    host2panel.add(hostL2, null);
    host2panel.add(hostC2, null);
    host2panel.add(hostV2, null);
    host3panel.add(jLabel4, null);
    host3panel.add(hostL3, null);
    host3panel.add(hostC3, null);
    host3panel.add(hostV3, null);
    host4panel.add(jLabel10, null);
    host4panel.add(hostL4, null);
    host4panel.add(hostC4, null);
    host4panel.add(hostV4, null);
    this.setLayout(borderLayout3);
    this.add(topPanel, BorderLayout.NORTH);
    this.add(hostPanel, BorderLayout.CENTER);
    this.add(errLine, BorderLayout.SOUTH);
    topPanel.add(jLabel1, BorderLayout.WEST);
    topPanel.add(speedL, BorderLayout.CENTER);
    hostPanel.add(headerPanel, null);
    hostPanel.add(host0panel, null);
    hostPanel.add(host1panel, null);
    hostPanel.add(host2panel, null);
    hostPanel.add(host3panel, null);
    hostPanel.add(host4panel, null);
}


/**
 * Simple PDU class that implements getting sysUptime
 *
 * @author <a href="mailto:thp@westhawk.co.uk">Tim Panton</a>
 */
class SysUpTime extends Pdu
{
    Long value;


    /**
     *  Constructor for the SysUpTime object
     *
     *@param  con                      Description of Parameter
     *@param  oid                      Description of Parameter
     *@param  o                        Description of Parameter
     *@exception  PduException         Description of Exception
     *@exception  java.io.IOException  Description of Exception
     */
    public SysUpTime(SnmpContextBasisFace con, String oid, Observer o)
        throws PduException, java.io.IOException
    {
        super(con);
        if (o != null)
        {
            addObserver(o);
        }
        addOid(oid);
        send();
    }


    protected void new_value(int n, varbind res)
    {
        AsnObject val = res.getValue();
        if (val instanceof AsnUnsInteger)
        {
            AsnUnsInteger va = (AsnUnsInteger) val;
            if (n == 0)
            {
                value = new Long(va.getValue());
            }
        }
        else
        {
            value = null;
        }
    }


    protected void tell_them()
    {
        notifyObservers(value);
    }
}

/**
 *  Main method
 *
 *@param  args  The command line arguments
 */
public static void main(String[] args)
{
    try
    {
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch (Exception e)
    {
    }

    String propFileName = null;
    if (args.length > 0)
    {
        propFileName = args[0];
    }
    MultipleHosts application = new MultipleHosts(propFileName);
    application.init();

    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setTitle(application.getClass().getName());
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(application, BorderLayout.CENTER);


    frame.setSize(800, 320);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation((d.width - frame.getSize().width) / 2,
                      (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);

    application.start();
}

}

