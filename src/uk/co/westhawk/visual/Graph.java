//
// NAME
//      $RCSfile: Graph.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.8 $
// CREATED
//      $Date: 2006/11/29 16:12:51 $
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
import javax.swing.*;
import java.beans.*;

/**
 * <p>
 * The class Graph creates decoration labels around an actual
 * (BareGraph) graph.
 * </p>
 *
 * <p>
 * All the configurations of the BareGraph can be done from this class.
 * </p>
 *
 * @see BareGraph
 * @see GraphDataSet
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.8 $ $Date: 2006/11/29 16:12:51 $
 */
public class Graph extends JPanel implements PropertyChangeListener
{
    private static final String     version_id =
    "@(#)$Id: Graph.java,v 1.8 2006/11/29 16:12:51 birgit Exp $ Copyright Westhawk Ltd";

    final static Color    WHEAT = new Color(0xea, 0xea, 0xde);

    private static final String base = "graph";
    private static int nameCounter = 0;

    private GraphDataSet dataSet;
    private BareGraph   gra;

    private boolean doAxes = true;
    private GridBagLayout lay;
    private JLabel name;
    private JLabel value;
    private JLabel max;
    private JLabel min;
    private JLabel xrange;
    private JLabel units;
   
/**
 * The default constructor. You will need to call setDataSet.
 *
 * @see #setDataSet
 */
public Graph()
{
    this(null);
}

/**
 * The constructor with the data set.
 * @param d the data set
 */
public Graph(GraphDataSet d)
{
    super.setName(base + nameCounter++);
    setBackground(Graph.WHEAT);

    makeGraph();
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
    gra.setLog(b);
}

/**
 * Returns the log mode of the graph.
 *
 * @return is the graph drawing the <sup>10</sup>log() values or not
 * @see #setLog
 */
public boolean isLog()
{
    return gra.isLog();
}

/**
 * <p>
 * Makes the graph draw the axes or not. 
 * The default is <em>true</em>.
 * </p>
 * <p>
 * If the axes are off only the
 * line, with above the name, value and units are displayed.
 * </p>
 * <p>
 * If the axes are on the graph will have the axes, max, min and xrange
 * displayed also.
 * </p>
 *
 * @param b set the axes or not
 * @see #isAxes
 */
public void setAxes(boolean b)
{
    if (b != doAxes)
    {
        doAxes = b;
        gra.setAxes(b);
        changeAxes();
    }
}

/**
 * Returns the axes mode of the graph.
 *
 * @return is the graph drawing the axes or not
 * @see #setAxes
 */
public boolean isAxes()
{
    return doAxes;
}

/**
 * Returns the minimum value that is displayed in the graph.
 *
 * @return the minimum
 * @see #setMin
 */
public int getMin()
{
    return gra.getMin();
}

/**
 * Sets the minimum value that is displayed in the graph. If the axes mode is on, this
 * value will be displayed 
 *
 * @param m the minimum value
 * @see #getMin
 * @see #setAxes
 */
public void setMin(int m)
{
    gra.setMin(m);
    min.setText(String.valueOf(m));
}

/**
 * Returns the maximum value that is displayed in the graph.
 *
 * @return the maximum value
 * @see #setMax
 */
public int getMax()
{
    return gra.getMax();
}

/**
 * Sets the maximum value that is displayed in the graph. If the axes mode is on, this
 * value will be displayed 
 *
 * @param m the maximum
 * @see #getMax
 * @see #setAxes
 */
public void setMax(int m)
{
    gra.setMax(m);
    max.setText(String.valueOf(m));
}

/**
 * Sets the xrange value of the graph. If the axes mode is on, this
 * string will be displayed under the x-axis. The default is
 * <em>Time</em>.
 *
 * @param x the xrange string 
 * @see #setAxes
 */
public void setXRange(String x)
{
    xrange.setText(x);
}

/**
 * Sets the units value of the graph. This string will be displayed
 * above the graph. The default is <em>(b/s)</em>.
 *
 * @param u the units string
 */
public void setUnit(String u)
{
    units.setText(u);
}

/**
 * Sets the name (title) of the graph. This string will be displayed
 * above the graph. 
 *
 * @param n the name string
 */
public void setName(String n)
{
    name.setText(n);
}

/**
 * Sets the font of the labels around the graph. 
 *
 * @param f the font
 * @see #getFont
 */
public void setFont(Font f)
{
    super.setFont(f);
    if (max != null)
    {
        max.setFont(f);
        value.setFont(f);
        min.setFont(f);
        name.setFont(f);
        units.setFont(f);
        xrange.setFont(f);
        repaint();
    }
}

/**
 * Returns the font of the labels around the graph. 
 *
 * @return the font
 * @see #setFont
 */
public Font getFont()
{
    return super.getFont();
}

/**
 * Returns the colour of the axes of the graph. 
 *
 * @return the axes' colour
 * @see #setAxisColour
 */
public Color getAxisColour()
{
    return gra.getAxisColour();
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
    gra.setAxisColour(c);
}

/**
 * Returns the colour of the line of the graph. 
 *
 * @return the line's colour
 * @see #setGraphColour
 */
public Color getGraphColour()
{
    return gra.getGraphColour();
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
    gra.setGraphColour(c);
}

/**
 * Returns the per cent size of the axes in respect to the graph.
 *
 * @return the size (%) of the axes
 * @see #setAxisPercent
 */
public float getAxisPercent()
{
    return gra.getAxisPercent();
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
    gra.setAxisPercent(x);
}


/**
 * Sets the data set that the graph displays. The last value will be
 * displayed above the graph.
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
    gra.setDataSet(d);
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
    Integer newV = (Integer) evt.getNewValue();
    value.setText(newV.toString());
}

private void makeGraph()
{
    lay    = new GridBagLayout();
    max    = new JLabel("Maximum", JLabel.LEFT);
    value  = new JLabel("0", JLabel.LEFT);
    min    = new JLabel("Minimum", JLabel.LEFT);
    name   = new JLabel("The name goes here", JLabel.LEFT);
    units  = new JLabel("(b/s)", JLabel.LEFT);
    xrange = new JLabel("Time", JLabel.RIGHT);
    gra    = new BareGraph();

    this.setLayout(lay);
    // now put the bits in place;

    /*
     Layout:
                    name value units
         max      |
                  |  gra
         min      |
                              time
     */
    changeAxes();

    // and change colour
    setBackgroundColour(name);
    setBackgroundColour(max);
    setBackgroundColour(min);
    setBackgroundColour(value);
    setBackgroundColour(xrange);
    setBackgroundColour(units);
}

private void changeAxes()
{
    this.removeAll();

    int x=0;
    if (doAxes)
    {
        // the max
        addToGridBag(lay, this, max, 
            x, 1, 0.10, 0.0, GridBagConstraints.HORIZONTAL,
            GridBagConstraints.NORTH);

        // the min
        addToGridBag(lay, this, min, 
            x, 2, 0.10, 0.0, GridBagConstraints.HORIZONTAL,
            GridBagConstraints.SOUTH);

        // the xrange
        addToGridBag(lay, this, xrange, 
            (x+1), 3, 1.0, 0.0, 3, 1, GridBagConstraints.HORIZONTAL,
            GridBagConstraints.EAST);

        x++;
    }

    // the graph
    // allow it to resize
    addToGridBag(lay, this, gra, 
        x, 1, 0.90, 1.0, 3, 2, GridBagConstraints.BOTH,
        GridBagConstraints.CENTER);

    // the name
    addToGridBag(lay, this, name, 
        x, 0, 0.0, 0.0, GridBagConstraints.NONE);

    // the value
    addToGridBag(lay, this, value, 
        (x+1), 0, 0.1, 0.0, GridBagConstraints.HORIZONTAL);

    // the units
    addToGridBag(lay, this, units, 
        (x+2), 0, 0.0, 0.0, GridBagConstraints.NONE);


}

void setBackgroundColour(Component j)
{
    j.setBackground(WHEAT);
}

protected void addToGridBag(GridBagLayout grid, Container cont, Component comp, 
    int x, int y, double wx, double wy) 
{
    addToGridBag(grid, cont, comp, x, y, wx, wy, GridBagConstraints.BOTH);
}

protected void addToGridBag(GridBagLayout grid, Container cont, Component comp, 
    int x, int y, double wx, double wy, int fill) 
{
    addToGridBag(grid, cont, comp, x, y, wx, wy, 
          fill, GridBagConstraints.CENTER);
}

protected void addToGridBag(GridBagLayout grid, Container cont, Component comp, 
    int x, int y, double wx, double wy, int fill, int anchor) 
{
    addToGridBag(grid, cont, comp, x, y, wx, wy, 
          1, 1, fill, anchor);
}

protected void addToGridBag(GridBagLayout grid, Container cont, Component comp, 
    int x, int y, double wx, double wy, int gw, int gh, int fill, 
    int anchor) 
{
    // now the constraints
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = x;
    gbc.gridy = y;
    gbc.weightx = wx;
    gbc.weighty = wy;
    gbc.fill = fill;
    gbc.gridwidth = gw;
    gbc.gridheight = gh;
    gbc.anchor = anchor;

    cont.add(comp);   
    grid.setConstraints(comp, gbc);
}

}

