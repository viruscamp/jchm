/*
 * HhcEntry.java
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

import java.io.*;

/**
 * @author yufeng
 * <p>
 * hhcentry is listed in hhc file
 * which has important information to locate file
 * so, it will be displayed in jtree
 */
class HhcEntry implements Serializable {
    int id;
    int parent; //
    String name;
    String URL;
    String type;

    public HhcEntry(int iden) {
        id = iden;
        parent = -1;
        name = "";
    }

    public HhcEntry(String url, int iden) {
        id = iden;
        parent = -1;
        name = url;
        URL = url;
    }

    public HhcEntry(TagParser tp, int iden) {
        id = iden;
        parent = -1;
        //get attribute
        String[] eat = tp.getAttribute().trim().split("=");
        if (eat.length > 1) {
            for (int i = 0; i < eat.length - 1; i++) {
                String s = eat[i].trim();
                int j = s.lastIndexOf(' ');
                if (j == -1) j = 0;
                if (s.substring(j).trim().toUpperCase().compareTo("TYPE") == 0) {
                    s = eat[i + 1].trim();
                    type = s.substring(s.indexOf('"') + 1, s.lastIndexOf('"'));
                }
            }
        }
        //get params
        TagParser tt;
        while ((tt = tp.getObject(HhcTag.param)) != null)//.getValue().trim().compareTo("")!=0)
            setParms(tt);//.getAttribute().trim().split("=");
    }

    private void setParms(TagParser tp) {
        String[] eat = tp.getAttribute().trim().split("=");
        if (eat.length > 1) {
            for (int i = 0; i < eat.length - 1; i++) {
                String s = eat[i].trim();
                int j = s.lastIndexOf(' ');
                if (j == -1) j = 0;
                if (s.substring(j).trim().toUpperCase().compareTo("NAME") == 0) {
                    s = eat[i + 1].trim();
                    s = s.substring(s.indexOf('"') + 1, s.lastIndexOf('"')).toUpperCase();
                    if (s.compareTo("NAME") == 0) name = getValue(eat, i + 1);
                    else if (s.compareTo("LOCAL") == 0) URL = new String("/").concat(getValue(eat, i + 1).trim());
                    //else  if(s.compareTo("COMMENT")==0)   

                }
            }

        }

    }

    private String getValue(String[] s, int ind) {
        String tmp;
        for (int i = ind; i < s.length; i++) {
            tmp = s[i].trim();
            if (tmp.substring(tmp.lastIndexOf(' ') + 1).toUpperCase().compareTo("VALUE") == 0) {
                return s[i + 1].substring(s[i + 1].indexOf('"') + 1, s[i + 1].lastIndexOf('"'));
            }
        }
        return null;
    }

    public String toString() //reload toString to show name in treeview
    {
        return name.toString();
    }


}
