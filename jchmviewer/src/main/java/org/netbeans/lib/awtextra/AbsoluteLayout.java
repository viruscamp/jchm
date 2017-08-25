package org.netbeans.lib.awtextra;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public class AbsoluteLayout implements LayoutManager2, Serializable {
    static final long serialVersionUID = -1919857869177070440L;
    protected Hashtable constraints = new Hashtable();

    public void addLayoutComponent(String name, Component comp) {
        throw new IllegalArgumentException();
    }

    public void removeLayoutComponent(Component comp) {
        this.constraints.remove(comp);
    }

    public Dimension preferredLayoutSize(Container parent) {
        int maxWidth = 0;
        int maxHeight = 0;
        for(Enumeration e = constraints.keys(); e.hasMoreElements();) {
            Component comp = (Component)e.nextElement();
            AbsoluteConstraints ac = (AbsoluteConstraints)this.constraints.get(comp);
            Dimension size = comp.getPreferredSize();

            int width = ac.getWidth();
            if (width == -1) {
                width = size.width;
            }

            int height = ac.getHeight();
            if (height == -1) {
                height = size.height;
            }

            if (ac.x + width > maxWidth) {
                maxWidth = ac.x + width;
            }

            if (ac.y + height > maxHeight) {
                maxHeight = ac.y + height;
            }
        }

        return new Dimension(maxWidth, maxHeight);
    }

    public Dimension minimumLayoutSize(Container parent) {
        int maxWidth = 0;
        int maxHeight = 0;
        for(Enumeration e = constraints.keys(); e.hasMoreElements();) {
            Component comp = (Component)e.nextElement();
            AbsoluteConstraints ac = (AbsoluteConstraints)this.constraints.get(comp);
            Dimension size = comp.getMinimumSize();

            int width = ac.getWidth();
            if (width == -1) {
                width = size.width;
            }

            int height = ac.getHeight();
            if (height == -1) {
                height = size.height;
            }

            if (ac.x + width > maxWidth) {
                maxWidth = ac.x + width;
            }

            if (ac.y + height > maxHeight) {
                maxHeight = ac.y + height;
            }
        }

        return new Dimension(maxWidth, maxHeight);
    }

    public void layoutContainer(Container parent) {
        for(Enumeration e = this.constraints.keys(); e.hasMoreElements();) {
            Component comp = (Component)e.nextElement();
            AbsoluteConstraints ac = (AbsoluteConstraints)this.constraints.get(comp);
            Dimension size = comp.getPreferredSize();
            int width = ac.getWidth();
            if (width == -1) {
                width = size.width;
            }

            int height = ac.getHeight();
            if (height == -1) {
                height = size.height;
            }

            comp.setBounds(ac.x, ac.y, width, height);
        }
    }

    public Dimension maximumLayoutSize(Container var1) {
        return new Dimension(2147483647, 2147483647);
    }

    public void addLayoutComponent(Component comp, Object constr) {
        if (!(constr instanceof AbsoluteConstraints)) {
            throw new IllegalArgumentException();
        } else {
            this.constraints.put(comp, constr);
        }
    }

    public float getLayoutAlignmentX(Container var1) {
        return 0.0F;
    }

    public float getLayoutAlignmentY(Container var1) {
        return 0.0F;
    }

    public void invalidateLayout(Container var1) {
    }
}
