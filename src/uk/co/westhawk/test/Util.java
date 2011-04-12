// NAME
//      $RCSfile: Util.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.8 $
// CREATED
//      $Date: 2006/01/17 17:43:54 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//
/*
 * Copyright (C) 2000 - 2006 by Westhawk Ltd
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
package uk.co.westhawk.test;

import java.util.*;

import org.w3c.dom.*;

/**
 * The class Util contains utility methods for DOM nodes.
 * 
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.8 $ $Date: 2006/01/17 17:43:54 $
 */
public class Util
{   
    private static final String     version_id =
        "@(#)$Id: Util.java,v 1.8 2006/01/17 17:43:54 birgit Exp $ Copyright Westhawk Ltd";


/**
 * Returns the CDATA value of the subnode of specified node with the specified
 * tag. For example: 
 * <pre>
 *     &lt;test&gt;
 *        &lt;host&gt;localhost&lt;host&gt;
 *     &lt;/test&gt;
 * </pre>
 * would return <em>localhost</em>, for node being the <code>test</code>
 * node and tag being <code>host</code>.
 * 
 * @param node The parent DOM node
 * @param tag The tag of the node
 * @return the value
 */
public static String getCDataValue(Node node, String tag)
{
    String ret = null;
    Node child = getChildNode(node, tag);
    if (child != null)
    {
        NodeList grandchildren = child.getChildNodes();
        Node grandchild = grandchildren.item(0);
        if (grandchild != null)
        {
            ret = grandchild.getNodeValue();
        }
    }
    return ret;
}

/**
 * Return the child node of the specified node with the specified tag.
 *
 * @param node The parent DOM node
 * @param tag The tag of the node
 * @return the child node 
 */
public static Node getChildNode(Node node, String tag)
{
    Node res = null;
    NodeList children = node.getChildNodes();
    int len = (children != null) ? children.getLength() : 0;
    int i=0;
    while (i<len && res == null)
    {
        Node child = children.item(i);
        String nodeName = child.getNodeName();
        if (nodeName.equals(tag))
        {
            res = child;
        }
        i++;
    }
    return res;
}

/**
 * Returns the first element node of a document.
 *
 * @param doc The DOM document.
 * @return the node
 */
public static Element getTopElementNode(Document doc)
{
    Element element = null;
    if (doc != null)
    {
        NodeList children = doc.getChildNodes();
        int len = (children != null) ? children.getLength() : 0;

        int i=0;
        while (i<len && element == null)
        {
            Node child = children.item(i);
            String nodeName = child.getNodeName();

            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                element = (Element) child;
            }
            i++;
        }
    }
    return element;
}


}
