/*
 * TagParser.java
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
 * <p>
 * this is a general mark language element parser
 */
public class TagParser {
    /**
     * Creates a new instance of TagParser
     */
    String src;
    // private String srcbig;
    int len;
    int[] elements;//number of objects in this objecter
    int id; //every object have an id to identify
    int kickedchild; //extracted object by getObject method
    int cursor;

    public TagParser(String s, int ord) {
        src = s;
        //clean anything that is out of tag
        if (src.indexOf('<') > 0) src = src.substring(src.indexOf('<'));
        if (src.lastIndexOf('>') < src.length() - 1) src = src.substring(0, src.lastIndexOf('>') + 1);

        String tmp = src;
        int e = 0;
        int i = tmp.indexOf('<');

        while (i != -1) {
            if (tmp.charAt(i + 1) != '/')
                e++;
            tmp = tmp.substring(i + 1);
            i = tmp.indexOf('<');
        }
        elements = new int[e];
        id = ord;
        kickedchild = 0;
        len = src.length();
        cursor = 0;
    }

    public TagParser(String s) {
        this(s, -1);

    }

    public int peekBegainTag(String tag) {
        String tg = GetParamBeginTag(tag);
        int i = src.substring(2).toUpperCase().indexOf(tg) + 2;
        return i;
    }

    public int peekEndTag(String tag) {
        String tg = GetEndTag(tag);
        int i = src.toUpperCase().indexOf(tg);
        return i;
    }

    /**
     * get specified element
     */
    public TagParser getObject(String tag) {
        int s = getObjectBegin(tag);
        if (s == -1) return null;
        int e = getObjectEnd(tag, s);
        String obj = src.substring(s, e);
        src = src.substring(0, s).concat(src.substring(e));
        elements[kickedchild] = s;//each kickedchild of elements correspond to a child that was extracted, its value referring to the position in tp.
        int len = e - s;
        for (int i = 0; i < kickedchild; i++) {
            if (elements[i] > s) elements[i] -= len;
        }
        return new TagParser(obj, kickedchild++);
    }

    /**
     * get value of an element
     */
    public String getValue() {
        int tb = src.lastIndexOf('/');
        if (tb == -1) return null;
        //if(getTag()!=src.substring(tb, len-1)) return null;
        int s = src.indexOf('>');
        int e = src.lastIndexOf("</");
        return src.substring(s + 1, e);
    }

    /**
     * get attribute of an element
     */
    public String getAttribute() {
        int s = src.indexOf(' ');
        int e = src.indexOf('>');
        return src.substring(s, e).trim();
    }

    public String getTag() {
        String tag = gettag();
        if (tag != null) return tag.toUpperCase();
        return null;
    }

    private String gettag() {
        int tb = src.lastIndexOf('/');
        String tag = null;
        if (tb != -1) {
            tag = src.substring(tb, len - 1);
            if (tb > tag.lastIndexOf('>'))//not match
                tag = tag.substring(tb + 1);
            if ((tag.toUpperCase()) != (src.substring(1, tag.length() + 1).toUpperCase())) tag = null;
        }
        if (tag == null)//ok,just get start tag
        {
            int i = src.indexOf(' ');
            tag = src.substring(1, i);
        }
        return tag;

    }

    /*
    public  String GetParent(String src,String tag) {
        String tmp=src;
        int start=src.i
        String uptag=src.
        return TrimEnd(TrimBegain(src,tag),tag);
    }
    */

    private static int GetLastBeginIndex(String src, String tag) {
        String s = src.toUpperCase();
        int ip = s.lastIndexOf(GetParamBeginTag(tag));
        int i = s.lastIndexOf(GetBeginTag(tag));
        int id = i > ip ? i : ip;//get last one , if not, -1
        return id;
    }

    private static int GetBeginIndex(String src, String tag) {
        String s = src.toUpperCase();
        int ip = s.indexOf(GetParamBeginTag(tag));
        int i = s.indexOf(GetBeginTag(tag));
        int id = i > ip ? ((ip == -1) ? i : ip) : ((i == -1) ? ip : i);//get first one , if not, -1
        return id;
    }

    private static int GetEndIndex(String src, String tag) {
        String s = src.toUpperCase();
        int ip = s.indexOf(GetEndTag(tag));
        return ip;
    }

    private int getObjectBegin(String tag) {
        return GetBeginIndex(src, tag);
    }

    private int getObjectEnd(String tag, int beginindex) {
        if (beginindex == -1) return -1;
        int tmp = 0;
        /*
        if (src.indexOf(GetBeginTag(tag)) == 0)//begain tag must have got. no params
        {
            String wt = GetEndTag(tag);
            tmp = src.substring(0, src.lastIndexOf(wt) + wt.length());
        } else if (src.indexOf(GetParamBeginTag(tag)) == 0) //hava params
        {
            int end = src.indexOf('>');
            int l = GetParamBeginTag(tag).length();
            String s = src.substring(l - 1);
            int sectag = s.indexOf(GetParamBeginTag(tag));
            int endtag = s.indexOf(GetEndTag(tag));
            if (endtag != -1)//sectag don't exist
            {
                if (sectag == -1)
                    tmp = src.substring(0, endtag + l);
                else {
                    if (sectag > endtag) tmp = src.substring(0, endtag + l);
                    else
                        tmp = src.substring(0, end);
                }
            } else {
                tmp = src.substring(0, end);
            }
        }
        */
        int i = beginindex;
        String s = src.substring(i + 2);
        int j = GetBeginIndex(s, tag) + 2;  //second tag
        int k = GetEndIndex(s, tag) + 2;
        if (j < k && j != 1) {
            int h = 2;
            s = s.substring(j + 2);
            int hp = i + j;
            j = GetBeginIndex(s, tag) + 2;
            k = GetEndIndex(s, tag) + 2;
            // System.out.println(src.substring(hp, hp+4));
            while (h > 0) {
                if (j < k && j != 1) {
                    s = s.substring(j + 2);
                    h++;
                    hp += j + 2;
                    //System.out.println("j<k&&j!=1");
                    //System.out.println(src.substring(hp, hp+4));
                } else if (j < k && j == 1) {
                    h--;
                    s = s.substring(k + 2);
                    hp += k + 2;//GetEndTag(tag).length();
                    //System.out.println("j<k&&j==1");
                    // System.out.println(src.substring(hp, hp+4));
                    if (h == 0) break;
                } else if (j > k && k != 1) {
                    h--;
                    s = s.substring(k + 2);
                    hp += k + 2;//GetEndTag(tag).length();
                    //System.out.println("j>k&&k!=1");
                    //System.out.println(src.substring(hp, hp+4));
                    if (h == 0) break;
                } else if (j > k && k == 1) {
                    tmp = src.indexOf('>') + 1;
                    break;
                }
                j = GetBeginIndex(s, tag) + 2;
                k = GetEndIndex(s, tag) + 2;
            }
            //hp+=j;
            if (tmp == 0) tmp = hp + GetEndTag(tag).length() + 2;


        } else if (j < k && j == 1) tmp = i + k + GetEndTag(tag).length();
        else if (j > k & k != 1) tmp = i + k + GetEndTag(tag).length();
        else if (j >= k & k == 1) {
            s = src.substring(beginindex);
            tmp = s.indexOf('>') + 1 + beginindex;
        }

        return tmp;
    }


    public static String GetBeginTag(String tag) {
        return (new String("<")).concat(tag.concat(">"));
    }

    public static String GetParamBeginTag(String tag) {
        return (new String("<")).concat(tag);
    }

    public static String GetEndTag(String tag) {
        return (new String("</")).concat(tag.concat(">"));
    }

    /**
     * this will return a TagParser object
     */
    public static TagParser getObjectFromString(String s, String tag) {
        int start = s.toUpperCase().indexOf(GetParamBeginTag(tag));
        if (start == -1) return null;
        int stop = s.toUpperCase().indexOf(GetEndTag(tag));
        if (stop == -1)
            stop = s.indexOf(">");
        return new TagParser(s.substring(start, stop));
    }
}
