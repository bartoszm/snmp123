// NAME
//      $RCSfile: DOMWriter.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.7 $
// CREATED
//      $Date: 2006/11/29 16:12:50 $
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

import java.io.*;
import org.w3c.dom.*;

/**
 * This class prints a DOM (document object model) representation of a
 * XML document.
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.7 $ $Date: 2006/11/29 16:12:50 $
 */
public class DOMWriter
{
    private static final String     version_id =
        "@(#)$Id: DOMWriter.java,v 1.7 2006/11/29 16:12:50 birgit Exp $ Copyright Westhawk Ltd";

   /** Default Encoding */
   public static  String PRINTWRITER_ENCODING = "UTF8";

   /** Canonical output (or not). */
   protected boolean canonical;

   /** The document header */
   protected String docHeader;

   /** The document type */
   protected String docType;


/**
 * The constructor. The dH and dT are needed since the information
 * regarding any DTD is not stored in the DOM.
 *
 * @param can Canonical of not
 * @param dH The document header
 * @param dT The document type
 */
public DOMWriter(boolean can, String dH, String dT)
{
    canonical = can;
    docHeader = dH;
    docType = dT;
}


/**
 * Prints the specified node with all its subnodes to System.out.
 * @param node The node to be printed.
 */
public void print(Node node) 
{
    PrintWriter w = new PrintWriter(new OutputStreamWriter(System.out), true);
    print(node, w);
}

/**
 * Prints the specified node with all its subnodes to a printwriter.
 * @param node The node to be printed.
 * @param w The writer to be printed to.
 */
public void print(Node node, PrintWriter w)
{
    print(node, 0, w);
}

/** 
 * Prints the specified node, recursively. 
 * @param node The node to be printed.
 * @param level The level of indentation
 * @param out The writer to be printed to.
 */
protected void print(Node node, int level, PrintWriter out)
{
    int endtagLevel = level;
    // is there anything to do?
    if (node == null)
    {
        return;
    }

    for (int i=0; i<level; i++)
    {
        out.print("  ");
    }
    int type = node.getNodeType();
    switch (type)
    {
        case Node.DOCUMENT_NODE:
        {
            out.println(docHeader);
            this.print(((Document)node).getDoctype(), 0, out);
            this.print(((Document)node).getDocumentElement(), 0, out);
            break;
        }

        // print document type
        case Node.DOCUMENT_TYPE_NODE:
        {
            out.println(docType);
            break;
        }
        // print element with attributes
        case Node.ELEMENT_NODE:
        {
            out.print('<');
            out.print(node.getNodeName());
            Attr attrs[] = sortAttributes(node.getAttributes());
            for (int i = 0; i < attrs.length; i++)
            {
                Attr attr = attrs[i];
                out.print(' ');
                out.print(attr.getNodeName());
                out.print("=\"");
                out.print(attr.getNodeValue());
                out.print('"');
            }

            NodeList children = node.getChildNodes();
            int len = (children != null) ? children.getLength() : 0;
            if (len == 1 
                  && 
                (children.item(0).getNodeType() == Node.CDATA_SECTION_NODE
                     ||
                 children.item(0).getNodeType() == Node.TEXT_NODE))
            {
                out.print('>');
                this.print(children.item(0), 0, out);
                endtagLevel = 0;
            }
            else
            {
                out.println('>');
                for (int i = 0; i < len; i++)
                {
                    this.print(children.item(i), (level+1), out);
                }
            }
            break;
        }

        // handle entity nodes
        case Node.ENTITY_NODE:
        {
            out.print("ENTITY ");
            out.print(node.getNodeName());
            Entity ent = (Entity)node;
            String sId = ent.getSystemId();
            if (sId != null)
            {
                out.print(" SYSTEM \"" + sId + "\"");
            }
            break;
        }

        // handle entity reference nodes
        case Node.ENTITY_REFERENCE_NODE:
        {
            if (canonical)
            {
                NodeList children = node.getChildNodes();
                if (children != null)
                {
                    int len = children.getLength();
                    for (int i = 0; i < len; i++)
                    {
                        this.print(children.item(i), level, out);
                    }
                }
            } 
            else
            {
                out.print('&');
                out.print(node.getNodeName());
                out.print(';');
            }
            break;
        }

        // print cdata sections
        case Node.CDATA_SECTION_NODE:
        {
            // have to normalize it.
            out.print(normalize(node.getNodeValue()));
            break;
        }

        // print text
        case Node.TEXT_NODE:
        {
            out.print(node.getNodeValue());
            break;
        }

        // print processing instruction
        case Node.PROCESSING_INSTRUCTION_NODE:
        {
            out.print("<?");
            out.print(node.getNodeName());
            String data = node.getNodeValue();
            if (data != null && data.length() > 0)
            {
                out.print(' ');
                out.print(data);
            }
            out.print("?>");
            break;
        }
        default:
        {
            out.print("Unknown type " + type);
        }
    }

    if (type == Node.ELEMENT_NODE)
    {
        for (int i=0; i<endtagLevel; i++)
        {
            out.print("  ");
        }
        out.print("</");
        out.print(node.getNodeName());
        out.println('>');
    }

} // print(Node)


/** 
 * Returns a sorted list of attributes. 
 * @param attrs The attributes.
 */
protected Attr[] sortAttributes(NamedNodeMap attrs)
{
  int len = (attrs != null) ? attrs.getLength() : 0;
  Attr array[] = new Attr[len];
  for (int i = 0; i < len; i++)
  {
     array[i] = (Attr)attrs.item(i);
  }
  for (int i = 0; i < len - 1; i++)
  {
     String name  = array[i].getNodeName();
     int    index = i;
     for (int j = i + 1; j < len; j++)
     {
        String curName = array[j].getNodeName();
        if (curName.compareTo(name) < 0)
        {
           name  = curName;
           index = j;
        }
     }
     if (index != i)
     {
        Attr temp    = array[i];
        array[i]     = array[index];
        array[index] = temp;
     }
  }

  return (array);

} 

/** 
 * Normalizes the given string. 
 * @param s The string.
 */
protected String normalize(String s) 
{
    StringBuffer str = new StringBuffer();

    int len = (s != null) ? s.length() : 0;
    for (int i = 0; i < len; i++) 
    {
        char ch = s.charAt(i);
        switch (ch) 
        {
            case '<': 
            {
                  str.append("&lt;");
                  break;
            }
            case '>': 
            {
                  str.append("&gt;");
                  break;
            }
            case '&': 
            {
                  str.append("&amp;");
                  break;
            }
            case '"': 
            {
                  str.append("&quot;");
                  break;
            }
            case '\r':
            case '\n': 
            {
                  if (canonical) 
                  {
                      str.append("&#");
                      str.append(Integer.toString(ch));
                      str.append(';');
                      break;
                  }
                  // else, default append char
            }
            default: 
            {
                  str.append(ch);
            }
        }
    }

    return (str.toString());

}

}

