/*
 * HhcParser.java
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

import javax.swing.*;
import java.util.*;
import java.io.*;

import org.yufeng.jchmlib.*;

/**
 * @author yufeng
 * <p>
 * parse the hhc file
 */
public class HhcParser {
    private ArrayList hhcEntry;
    int hhcsize;
    private ArrayList cacheEntry;
    int size;
    TagParser tp;
    public static int MAX_ENTRY = 600;
    public static String DIR_TMP = "dll/";//cached dir stored here.
    int[] isparents = new int[MAX_ENTRY + 1];

    /**
     * Creates a new instance of HhcParser
     * <p>
     * if the chm file doesn't have hhc file,
     * just use filename listed in pmgl setion to display in jtree
     */
    public HhcParser(ArrayList files) {
        isparents[0] = -1;
        hhcEntry = new ArrayList();
        FileEntry fe;
        HhcEntry he;
        int i;
        for (i = 0; i < files.size(); i++) {
            fe = (FileEntry) files.get(i);
            he = new HhcEntry(fe.entryName, i);
            hhcEntry.add(he);
        }
        setParentAllFromLoc(i);
        size = hhcEntry.size();
        //hhcEntry.size();
        hhcsize = hhcEntry.size();
    }

    /**
     * parse hhc file
     */
    public HhcParser(String hhc) {
        isparents[0] = -1;
        //clean tmp dir
        File dt = new File(DIR_TMP);
        if (!dt.exists()) dt.mkdir();
        File[] f = dt.listFiles();
        if (f != null) {
            for (int i = 0; i < f.length; i++)
                f[i].delete();
        }
        hhcEntry = new ArrayList();
        tp = new TagParser(hhc);
        tp = tp.getObject(HhcTag.BODY);
        createEntries();
    }

    /**
     * get start point of first hhc entry
     */
    public int[] resolveObject(String s, String tag) {
        int[] i = new int[2];
        i[0] = s.toUpperCase().indexOf(TagParser.GetParamBeginTag(tag));
        if (i[0] == -1) return null;
        i[1] = s.toUpperCase().indexOf(TagParser.GetEndTag(tag));
        if (i[1] == -1)
            i[1] = s.indexOf(">");
        else
            i[1] += TagParser.GetEndTag(tag).length();
        return i;
    }

    /**
     * create all hhc entries from hhc file
     */
    private void createEntries() {
        HhcEntry he, pa;
        int[] pos;
        int[] hierachicalparents = new int[MAX_ENTRY];
        int hpoint = 0;
        int i = 0;
        hierachicalparents[0] = -1;
        boolean f;
        String val = tp.getObject(HhcTag.UL).getValue();
        pos = resolveObject(val, HhcTag.OBJECT);
        int ul = val.toUpperCase().indexOf(TagParser.GetBeginTag(HhcTag.UL));
        int endul = val.toUpperCase().indexOf(TagParser.GetEndTag(HhcTag.UL));
        while (pos != null) {
            if (ul != -1 && pos[0] > ul) {
                hierachicalparents[++hpoint] = i - 1;
                setParentsFlag(i - 1);
            }
            if (endul != -1 && pos[0] > endul) {
                int n = 0;
                String ge = val.substring(endul, pos[0]).toUpperCase();
                while ((n = ge.indexOf(TagParser.GetEndTag(HhcTag.UL))) > -1) {
                    hpoint--;
                    ge = ge.substring(n + 2);
                }
            }
            he = new HhcEntry((new TagParser(val.substring(pos[0], pos[1]))), i++);
            val = val.substring(pos[1]);
            he.parent = hierachicalparents[hpoint];
            hhcEntry.add(he);
            if ((ul -= pos[1]) < 0) ul = val.toUpperCase().indexOf(TagParser.GetBeginTag(HhcTag.UL));
            if ((endul -= pos[1]) < 0) endul = val.toUpperCase().indexOf(TagParser.GetEndTag(HhcTag.UL));
            pos = resolveObject(val, HhcTag.OBJECT);
            // if(i>=MAX_ENTRY)break;
        }
        size = hhcEntry.size();
        //hhcEntry.size();
        hhcsize = hhcEntry.size();
    }

    /**
     * set parent flag for parent items
     */
    private void setParentAllFromLoc(int i) {
        //the root entry has parent -1
        HhcEntry he, pa;
        for (int j = 0; j < i; j++) {
            pa = (HhcEntry) hhcEntry.get(j);
            for (int k = 0; k < i; k++) {
                if (k == j) continue;
                he = (HhcEntry) hhcEntry.get(k);
                if (he.parent > -1 || pa.parent == he.id) continue;
                //if(he==pa)break; //same one
                if (!he.URL.startsWith(pa.URL)) continue;
                int n = he.URL.lastIndexOf('/');
                String s = pa.URL;
                if (s.substring(s.length() - 1).compareTo("/") == 0) s = s.substring(0, s.length() - 1);
                if (s.concat(he.URL.substring(n)).compareTo(he.URL) == 0) {
                    he.parent = pa.id;
                    setParentsFlag(pa.id);
                }
            }
        }
    }

    /**
     * set parent flag for parent items
     */
    private void setParentsFlag(int id) {
        for (int i = 0; i < MAX_ENTRY + 1; i++) {
            if (id == isparents[i]) break;//existed
            if (isparents[i] == -1) {
                isparents[i] = id;
                isparents[i + 1] = -1; //last one is -1
                break;
            }
        }
    }

    public String getObj() {
        return (tp.getObject(HhcTag.UL).src);
    }

    /**
     * get one from hhcEntries
     */
    public HhcEntry getHhcEntry(int i) {
        if (i > hhcsize) return null;
        return (HhcEntry) hhcEntry.get(i);
    }

    /**
     * get root hhcentry
     */
    public HhcEntry getRootHhcEntry() {
        HhcEntry h;
        int n = 0;
        int id = 0;
        for (int i = 0; i < hhcEntry.size(); i++) {
            h = (HhcEntry) hhcEntry.get(i);
            if (h.parent == -1) {
                n++;
                id = i;
            }
        }
        if (n == 1) return (HhcEntry) hhcEntry.get(id);
        else//n>1
            return new HhcEntry(-1);
        // return null;
    }

    /**
     * judge whether an item of hhc entry is a parent
     */
    public boolean isParent(int id) {
        int i;
        for (i = 0; i < MAX_ENTRY + 1; i++) {
            if (id == isparents[i]) break;//existed
            if (isparents[i] == -1) break;
        }
        if (id == isparents[i]) return true;
        else return false;
    }

    /**
     * get children of the specified item
     */
    public HhcEntry[] getChildEntry(HhcEntry parent) {
        File dt = new File(DIR_TMP);
        if (!dt.exists()) return null;
        File[] f = dt.listFiles(new HhcEntryFilter(parent.id));
        if (f == null) return null;
        HhcEntry[] hhcs = new HhcEntry[f.length];
        FileInputStream in = null;
        ObjectInputStream oin = null;
        for (int i = 0; i < f.length; i++) {
            try {
                in = new FileInputStream(f[i]);
                oin = new ObjectInputStream(in);
                hhcs[i] = (HhcEntry) oin.readObject();
                oin.close();
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return hhcs;
    }

    /**
     * release resources of hhcparser
     */
    public void dispose() {
        File dt = new File(DIR_TMP);
        if (dt.exists()) {
            File[] f = dt.listFiles();
            if (f != null) {
                for (int i = 0; i < f.length; i++)
                    f[i].delete();
            }
        }
        dt.deleteOnExit();
    }
}

    /*
    private HhcEntry setParentFromLoc(HhcEntry pa) {
        //the root entry has parent -1
        HhcEntry he;
        int i = hhcEntry.size();
        for (int k = 0; k < i; k++) {
            he = (HhcEntry) hhcEntry.get(k);
            if (pa.URL.startsWith(he.URL)) {
                int n = pa.URL.lastIndexOf('/');
                if (n == 0 && he.URL.concat(pa.URL.substring(n + 1)).compareTo(pa.URL) == 0) //under root dir
                {
                    pa.parent = he.id;
                    setParentsFlag(he.id);
                } else if (he.URL.concat(pa.URL.substring(n)).compareTo(pa.URL) == 0) {
                    pa.parent = he.id;
                    setParentsFlag(he.id);
                }
            }
        }
        return pa;

    }

    private void printHhc() {
        HhcEntry he;
        for (int i = 0; i < hhcEntry.size(); i++) {
            he = (HhcEntry) hhcEntry.get(i);
            System.out.println(he.toString());
            System.out.println(he.id);
            System.out.println(he.parent);

        }
    }

    private void createEntriesFromLoc() {
        isparents[0] = -1;
        tp = tp.getObject(HhcTag.UL);
        TagParser tt = tp.getObject(HhcTag.OBJECT);
        HhcEntry he, pa;
        int i = 0;
        while (tt != null) {
            System.out.println(i);
            he = new HhcEntry(tt, i++);
            hhcEntry.add(he);
            tt = tp.getObject(HhcTag.OBJECT);
            if (i >= MAX_ENTRY) break;
        }
        //hhcEntry.set(199,null);
        //System.out.println(hhcEntry.size());
        // size=i;
        //set parent
        setParentAllFromLoc(i);
        //more than 200
        String url;
        int sta;
        if (i == MAX_ENTRY) {
            //cache htm and html file entry, maintain dir entry and first level file
            int j = 0;
            while (j < hhcEntry.size()) {
                he = (HhcEntry) hhcEntry.get(j);
                url = he.URL;
                if (url == null) j++;
                else {
                    sta = url.lastIndexOf('.');
                    if (sta != -1 && sta + 1 < url.length()) {
                        if (url.substring(sta + 1).trim().toUpperCase().startsWith("HTM") && he.parent > 0) {
                            cacheHhcEntry(he);
                            hhcEntry.remove(j);
                        } else j++;
                    } else j++;
                }
                he = (HhcEntry) hhcEntry.get(j);
                if (he.parent <= 0) j++;
                else {
                    cacheHhcEntry(he);
                    hhcEntry.remove(j);
                }

                printHhc();
                //get others
                while (tt != null) {
                    System.out.println(i);
                    he = new HhcEntry(tt, i++);
                    he = setParentFromLoc(he);
                    url = he.URL;
                    sta = url.lastIndexOf('.');
                    if (sta != -1 && sta + 1 < url.length()) {
                        if (url.substring(sta + 1).trim().toUpperCase().startsWith("HTM") && he.parent > 0) {
                            cacheHhcEntry(he);
                        } else hhcEntry.add(he);
                    } else {
                        // he=setParent(he);
                        hhcEntry.add(he);
                    }
                    he = setParent(he);
                    if (he.parent <= 0) hhcEntry.add(he);
                    else {
                        cacheHhcEntry(he);
                    }

                    tt = tp.getObject(HhcTag.OBJECT);
                }
            }
            size = i;//hhcEntry.size();
            hhcsize = hhcEntry.size();
            System.out.println(hhcsize);
            printHhc();
        }
    }
    */


class HhcEntryFilter implements FilenameFilter {
    // File f=new File("lay");
    String pre;

    public HhcEntryFilter(int parentid) {
        pre = Integer.toString(parentid) + ".";
    }

    public boolean accept(File dir, String name) {
        return name.startsWith(pre);
        // if(dir.getParent()="D:\\lay"&&name.indexOf(name)>-1)return true;
        // else return false;
    }

}

class Type {
    public static String TEXT = "text/sitemap";
}
