// NAME
//      $RCSfile: BareGraph.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.9 $
// CREATED
//      $Date: 2006/11/29 16:12:50 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1998 - 2006 by Westhawk Ltd
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

package uk.co.westhawk.visual;
import java.awt.*;
import java.util.*;
import java.beans.*;

/**
 * <p>
 * The class BareGraph makes a graphical respresentation of a set of
 * integer values. The values are provided by the GraphDataSet class.
 * </p>
 *
 * <p>
 * The line that this class draws can be set to the <sup>10</sup>log()
 * representation of the values.
 * </p>
 *
 * @see Graph
 * @see GraphDataSet
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision: 1.9 $ $Date: 2006/11/29 16:12:50 $
 */
public class BareGraph extends Component implements PropertyChangeListener
{
    private static final String     version_id =
        "@(#)$Id: BareGraph.java,v 1.9 2006/11/29 16:12:50 birgit Exp $ Copyright Westhawk Ltd";

    GraphDataSet dataSet;

    boolean doLog = false;
    boolean doAxes = true;
    Color graphColour = Color.blue;
    Color axisColour  = Color.red;
    float axispercent = (float)5.0;
    double dmax = 50000000.0;
    double dmin = 0.0;
    int min= 0; 
    int max= 50000000; 

    int numYTicks = 11;
    int numXTicks = 11;
    int [] xline = new int[200];
    int [] yline = new int[200];

    private static final String base = "bareGraph";
    private static int nameCounter = 0;

/** 
 * The default constructor. You need to call setDataSet
 *
 * @see #setDataSet
 */
public BareGraph()
{
    this(null);
}

/** 
 * The constructor with the data set
 *
 * @param d the data set
 */
public BareGraph(GraphDataSet d)
{
    super();
    super.setName(base + nameCounter++);

    setMin(0);
    setMax(1000000);
    setDataSet(d);
}

/**
 * Makes the graph draw the <sup>10</sup>log() of the values or not.
 * The default is <em>false</em>.
 *
 * @param b set the <sup>10</sup>log() version on or not
 * @see #isLog
 */
public void setLog(boolean b)
{
   if (b != doLog)
   {
      doLog = b;
      setMax(max);
      setMin(min);
      allDone();
   }
}

/**
 * Returns the log mode of the graph.
 *
 * @return is the graph drawing the <sup>10</sup>log() values or not
 * @see #setLog
 */
public boolean isLog()
{
    return doLog;
}

/**
 * Makes the graph draw the axes or not. 
 * The default is <em>true</em>.
 *
 * @param b set the axes or not
 */
public void setAxes(boolean b)
{
    if (b != doAxes)
    {
        doAxes = b;
        allDone();
    }
}

/**
 * Returns the maximum value that is displayed in the graph.
 *
 * @return the maximum value
 * @see #setMax
 */
public int getMax()
{
    return max;
}

/**
 * Sets the maximum value that is displayed in the graph. 
 *
 * @param m the maximum
 * @see #getMax
 */
public void setMax(int m)
{
    max = m;
    dmax = max;
    if (doLog)
    {
        dmax = log10(max);
    }
    setNumYTicks();
}

/**
 * Returns the minimum value that is displayed in the graph.
 *
 * @return the minimum
 * @see #setMin
 */
public int getMin()
{
    return min;
}

/**
 * Sets the minimum value that is displayed in the graph. 
 *
 * @param m the minimum value
 * @see #getMin
 */
public void setMin(int m)
{
    min = m;
    dmin = min;
    if (doLog)
    {
        dmin = log10(min);
    }
    setNumYTicks();
}

/**
 * Returns the colour of the axes of the graph.
 *
 * @return the axes' colour
 * @see #setAxisColour
 */
public Color getAxisColour()
{
    return axisColour;
}

/**
 * Sets the colour of the axes of the graph.
 * The default is <em>Color.red</em>.
 *
 * @param c the axes' colour
 * @see #getAxisColour
 */
public void setAxisColour(Color c)
{
    if (c != axisColour)
    {
        axisColour = c;
        allDone();
    }
}

/**
 * Returns the colour of the line of the graph.
 *
 * @return the line's colour
 * @see #setGraphColour
 */
public Color getGraphColour()
{
    return graphColour;
}

/**
 * Sets the colour of the line of the graph.
 * The default is <em>Color.blue</em>.
 *
 * @param c the line's colour
 * @see #getGraphColour
 */
public void setGraphColour(Color c)
{
    if (c != graphColour)
    {
        graphColour = c;
        allDone();
    }
}

/**
 * Returns the per cent size of the axes in respect to the graph.
 *
 * @return the size (%) of the axes
 * @see #setAxisPercent
 */
public float getAxisPercent()
{
    return axispercent;
}

/**
 * Sets the per cent size of the axes in respect to the graph. The
 * default is <em>5.0 %</em>.
 *
 * @param x the size (%) of axes
 * @see #getAxisPercent
 */
public void setAxisPercent(float x)
{
    axispercent =x;
}

/**
 * Returns the preferred size.
 * 
 * @return the dimensions of the preferred size
 */
public Dimension getPreferredSize()
{
    int w, h;
    int cap = 100;
    if (dataSet != null)
    {
        cap = dataSet.getCapacity();
    }
    w = cap * 2 + 5;
    h = (int) (numYTicks * 5) + 5;
    return new Dimension(w, h);
}

/**
 * Returns the minimum size.
 * 
 * @return the dimensions of the minimum size
 */
public Dimension getMinimumSize()
{
    int w, h;
    int cap = 100;
    if (dataSet != null)
    {
        cap = dataSet.getCapacity();
    }
    w = cap * 1 + 2;
    h = (int) (numYTicks * 2) + 2;
    return new Dimension(w, h);
}


/**
 * Sets the data set that the graph displays. Depending on the log mode,
 * the line will represents the values of the <sup>10</sup>log() of the
 * values.
 *
 * @param d the data set.
 * @see #getDataSet
 */
public void setDataSet(GraphDataSet d)
{
    if (dataSet != null)
    {
        dataSet.removePropertyChangeListener(this);
    }

    dataSet = d;
    if (dataSet != null)
    {
        dataSet.addPropertyChangeListener(this);
    }
}

/**
 * Returns the data set that the graph displays.
 *
 * @return the data set.
 * @see #setDataSet
 */
public GraphDataSet getDataSet()
{
    return dataSet;
}

/**
 * Implements the PropertyChangeListener. Gets the update of the data.
 *
 * @param evt the property change event
 * @see java.beans.PropertyChangeListener
 */
public void propertyChange(PropertyChangeEvent evt)
{
    allDone();
}

/*
 * All draw methods
 *
 */

/**
 * Will repaint the graph 
 */
void allDone()
{
    repaint(250); // perhaps we can share the redraws.
}

Image buffer = null; // expensive so cache it
public void paint(Graphics g)
{
    Dimension osize = getSize();

    if ((buffer == null) || (osize.height != buffer.getHeight(null))
        || (osize.width != buffer.getWidth(null)))
    {
        if (buffer != null)
        {
            buffer.flush();
        }
        buffer = createImage(osize.width, osize.height);
    }

    Graphics gc = buffer.getGraphics();

    drawAll(gc);
    g.drawImage(buffer, 0, 0, this);
    gc.dispose();
}

/**
 * This method will draw the axes (if axes mode is on) and the line.
 * 
 * @param gc the graphics
 * @see #setAxes
 * @see #setLog
 */
void drawAll(Graphics gc)
{
    Dimension osize = getSize();

    gc.setColor(getBackground());
    gc.fillRect(0, 0, osize.width, osize.height);

    gc.clipRect(0, 0, osize.width, osize.height);
    
    if (doAxes)
    {
        gc.setColor(axisColour);
        gc = paintAxis(gc);
    }

    gc.setColor(getBackground());
    gc.fillRect(0, 0, osize.width, osize.height);  // trust the clip

    if (dataSet != null)
    {
        gc.setColor(graphColour);
        paintLine(gc);
    }
}


/**
 * This method will draw the axes.
 * 
 * @param gc the graphics
 * @return the (clipped) graphics for the line
 */
Graphics paintAxis(Graphics gc)
{
    int originx = 0;
    int originy = 0;

    Rectangle space = gc.getClipBounds();
    int yTickLen = (int) (space.width * axispercent / 100.0);
    if (yTickLen < 2)
    {
        yTickLen = 2;
    }
    else if (yTickLen > 5)
    {
        yTickLen = 5;
    }

    int xTickLen = (int) (space.height * axispercent / 100.0);
    if (xTickLen < 2)
    {
        xTickLen = 2;
    }
    else if (xTickLen > 5)
    {
        xTickLen = 5;
    }
    originx = yTickLen;
    originy = space.height - xTickLen;

    // do the Y axis
    int tickoff = space.height/(numYTicks-1);
    int x1 =0;
    int x2 = originx;
    int y1 =originy;

    for (int tick =0; tick < numYTicks; tick++)
    {
        gc.drawLine(x1, y1, x2, y1);
        y1-=tickoff;
    }
    gc.drawLine(originx, originy, originx, y1+tickoff);

    // do the X axis
    tickoff = space.width/(numXTicks-1);
    x1 =originx; 
    y1 = space.height;
    int y2 = originy;

    for (int tick =0; tick < numXTicks; tick++)
    {
        gc.drawLine(x1, y1, x1, y2);
        x1+=tickoff;
    }

    // now draw the X axis
    gc.drawLine(originx, originy, space.width, originy);
    
    gc = gc.create(originx+1, 0, 
                    space.width-originx-1, 
                    originy);
    return gc;

}

/**
 * This method will draw the line.
 * 
 * @param gc the graphics
 * @see #setLog
 */
void paintLine(Graphics g)
{
    double dvalue;
    int [] values = dataSet.getValues();
    // first decide the x scale factor;
    if ((dataSet == null) || (dataSet.getSize() < 1)) 
    {
        return; // nothing to draw
    }

    // the maximum number of values we are every going to get
    int capacity = dataSet.getCapacity();

    // the available number of values at the moment
    int nr = dataSet.getSize();
    Rectangle space= g.getClipBounds();

    // pixel width of x values
    int width = space.width;
    int xscale = (int) (width / capacity);
    if (xscale < 1)
    {
        xscale = 1;
    }
    width = xscale * capacity;
    if (width > space.width)
    {
        width = space.width;
    }

    // pixel width of y values
    float yscale = (space.height)/(float)(dmax - dmin);

    // offset into array
    int xstart = 0; 
    if (width  < (nr*xscale))
    {
        // we are lacking space 
        xstart = nr - (int)(width/xscale);
    }

    // if needed reallocate some space
    int totx = nr - xstart +1;
    if (xline.length < totx) 
    {
        xline = new int [totx+100];
        yline = new int [totx+100];
    }

    // now workout where to put the first point so that
    // the last one ends up on the right hand edge
    int x = width - (int)((nr -xstart -1) * xscale);
    if (x < 0)
    {
        x = 0;  // can't be negative
        System.out.println("negative start point in graph.");
    }

    int y;
    int ox = x; 
    x+=xscale;
    dvalue = values[xstart];
    if (doLog)
    {
        dvalue = log10(values[xstart]);
    }
    xstart++;
    int oy = space.height - (int)((dvalue-dmin) * yscale) ;
    int pt = 0;
    for (int i=xstart; i<nr; i++)
    {
        dvalue = values[i];
        if (doLog)
        {
            dvalue = log10(values[i]);
        }
        y = space.height - (int)((dvalue-dmin) * yscale) ;
        xline[pt] = x; yline[pt]= y; pt++;
        ox =x;
        oy =y;
        x+=xscale;
    }
    g.drawPolyline(xline, yline, pt);
}

private void setNumYTicks()
{
    numYTicks = 11;
    if (doLog)
    {
        numYTicks = (int) (dmax - dmin + 1);
        if (numYTicks < 5)
        {
            numYTicks *=2;
        }
        numYTicks++;
    }
}

/**
 * Returns the <sup>10</sup>log() of a value
 *
 * @return the <sup>10</sup>log()
 */
static double log10(int v)
{
    double dl = 0.0;
    if (v != 0.0)
    {
        dl = Math.log(v) / Math.log(10);
    }
    return dl;
}

}

