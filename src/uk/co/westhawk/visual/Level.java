//
// NAME
//      $RCSfile: Level.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.3 $
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

package uk.co.westhawk.visual;

import java.lang.*;
import java.awt.*; 
import javax.swing.*;
import java.util.*;
import uk.co.westhawk.tablelayout.*;

/**
 * <p>
 * The Level class is a JPanel with a LevelDraw in it. It will show a
 * Level, with a scale and under it a label.
 * </p>
 *
 * <p>
 * It is used by the getAllInterfaces application to visualise the interfaces.
 * </p>
 *
 * @see uk.co.westhawk.examplev1.getAllInterfacesUI
 * @see LevelDraw
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.3 $ $Date: 2006/01/17 17:43:54 $
 */
public class Level extends JPanel 
{
    private static final String     version_id =
        "@(#)$Id: Level.java,v 1.3 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";

    private JLabel      label;
    private LevelDraw   draw;
    private String      name;


/**
 * Constructs a Level with no label
 */
public Level() 
{
    draw = new LevelDraw();
    label = new JLabel("interface", JLabel.CENTER);

    TableLayout table = new TableLayout();
    this.setLayout(table);
    this.add("0 0 ", draw);
    this.add("0 1 hH", label);

    Vector v = new Vector(2);
    v.addElement(draw);
    v.addElement(label);
    table.sameWidth(v);
}


public void setName(String n)
{
    name = n;
    label.setText(name);
}

public void setValue(double v)
{
    draw.setValue(v);
}

public double getValue()
{
    return (draw.getValue());
}

public void setScale(boolean doScale)
{
    draw.setScale(doScale);
}

public void setForeground(Color fg)
{
    super.setForeground(fg);
    if (label != null)
    {
        label.setForeground(fg);
        draw.setForeground(fg);
    }
}

public void setBackground(Color bg)
{
    super.setBackground(bg);
    if (label != null)
    {
        label.setBackground(bg);
        draw.setBackground(bg);
    }
}

public void setLevelBackground(Color lbg)
{
    draw.setLevelBackground(lbg);
}

public void setLevelForeground(Color lfg)
{
    draw.setLevelForeground(lfg);
}


public String toString()
{
    return getClass().getName()
        + "["
        + "name=" + name
        + ",draw=" + draw
        + "]";
}

}
