/*
 * HhcTreeCellRenderer.java
 ***************************************************************************************
 * Author: Feng Yu. <yfbio@hotmail.com>
 *org.yufeng.jchmviewer 
 *version: 1.0
 ****************************************************************************************
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
**********************************************************************************************/

package org.yufeng.jchmviewer;

/**
 * @author yufeng
 * just use my specified icon to display jtree
 */

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HhcTreeCellRenderer extends JLabel implements TreeCellRenderer {
    /** Font used if the string to be displayed isn't a font. */
    static protected Font defaultFont;
    /** Icon to use when the item is collapsed. */
    static protected ImageIcon collapsedIcon;
    /** Icon to use when the item is expanded. */
    static protected ImageIcon expandedIcon;
    static protected ImageIcon leafIcon;

    /** Color to use for the background when selected. */
    static protected final Color SelectedBackgroundColor = Color.yellow;//new Color(0, 0, 128);

    static {
        try {
            defaultFont = new Font("SansSerif", 0, 12);
        } catch (Exception e) {
        }
        try {
            collapsedIcon = new ImageIcon("jchmimages/dir.gif");
            expandedIcon = new ImageIcon("jchmimages/open.gif");
            leafIcon = new ImageIcon("jchmimages/file.gif");
        } catch (Exception e) {
            System.out.println("Couldn't load images: " + e);
        }
    }

    /** Whether or not the item that was last configured is selected. */
    protected boolean selected;

    /**
     * This is messaged from JTree whenever it needs to get the size
     * of the component or it wants to draw it.
     * This attempts to set the font based on value, which will be
     * a TreeNode.
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row,
                                                  boolean hasFocus) {
        Font font;
        //String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);

	    /* Set the text. */
        //setText(stringValue);

        /* Tooltips used by the tree. */
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object obj = node.getUserObject();
        setText(obj.toString());
        setToolTipText(obj.toString());

        if (obj instanceof Boolean) //obj is what class? is boolean then
            setText("Retrieving data...");

        //if (obj instanceof HhcEntry) {}
        //else {//is icondata class, then

        /* Set the image. */
        if (expanded)
            setIcon(expandedIcon);
        else if (!leaf)
            setIcon(collapsedIcon);
        else
            setIcon(leafIcon);
        //}

	    /* Set the color and the font based on the SampleData userObject. */
	    /*
	    SampleData userObject = (SampleData)((DefaultMutableTreeNode)value).getUserObject();
        if(hasFocus)
            setForeground(Color.cyan);
        else
            setForeground(userObject.getColor());
        if(userObject.getFont() == null)
            setFont(defaultFont);
        else
            setFont(userObject.getFont());
        */

	    /* Update the selected flag for the next paint. */
        this.selected = selected;

        return this;
    }

    /**
     * paint is subclassed to draw the background correctly.  JLabel
     * currently does not allow backgrounds other than white, and it
     * will also fill behind the icon.  Something that isn't desirable.
     */
    public void paint(Graphics g) {
        Color bColor;
        Icon currentI = getIcon();

        if (selected)
            bColor = SelectedBackgroundColor;
        else if (getParent() != null)
	    /* Pick background color up from parent (which will come from
	       the JTree we're contained in). */
            bColor = getParent().getBackground();
        else
            bColor = getBackground();
        g.setColor(bColor);
        if (currentI != null && getText() != null) {
            int offset = (currentI.getIconWidth() + getIconTextGap());

            if (getComponentOrientation().isLeftToRight()) {
                g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
            } else {
                g.fillRect(0, 0, getWidth() - 1 - offset, getHeight() - 1);
            }
        } else
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
        super.paint(g);
    }
}
