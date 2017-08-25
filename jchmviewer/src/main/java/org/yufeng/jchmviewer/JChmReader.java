/*
 * JChmReader.java
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

import org.yufeng.jchmlib.*;

/**
 * @author yufeng
 * when jchmserver get the request
 * it will give the response
 */
public class JChmReader {
    ChmManager cm;
    // String encode;
    int port;
    static String sorry = "Sorry, this file doesn't exist :)";

    public JChmReader(ChmManager chm, int ports) {
        cm = chm;
        // encode=enc;
        port = ports;

    }

    public JChmReader(int ports) {
        cm = null;
        // encode=enc;
        port = ports;
    }

    public void setChmSource(ChmManager chm) {
        cm = chm;
    }

    /**
     * get request, give response
     */
    public void serve(InputStream i, OutputStream o) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(i));
        String line, file = null, content = null;//"sorry, this page doesn't exist";
        if ((line = in.readLine()) != null) {
            file = line.substring(line.indexOf('/'), line.indexOf(" HTTP/")).trim();
        }
        /*
        System.out.println("..................yes,i'm getting " +line);
        while((line = in.readLine()).trim() .length()>3)
            System.out.println("..................yes,i'm getting " +line);
        */
        String sf = file.toLowerCase();
        String type = "text/html";  //;charset="+encode;
        if (sf.endsWith(".gif")) type = "image/gif";
        if (sf.endsWith(".jpg")) type = "image/jpeg";
        if (sf.endsWith(".mht")) type = "multipart/related";


        String tmp = "/frame.htmldd";
        String helpfile = "/jchmhelp/";
        byte[][] data = null;

        if (file.toLowerCase().startsWith(helpfile))  //read help file
        {
            if (type.compareTo("text/html") == 0)//";charset="+encode) //html file
            {
                content = getJchmHtml(file.substring(1));//don't need first /
                printHtml(o, content, type);
            } else {
                data = new byte[1][];
                data[0] = getJchmData(file.substring(1));
                printData(o, data, type);
            }
        } else if (cm != null) {
            //System.out.println("cm  "+file);
            if (file.toLowerCase().startsWith(tmp)) { //to show single pic, we have to use a created frame.htm
                content = getJchmHtml(tmp);
                printHtml(o, content, type);
            } else if (type.compareTo("text/html") == 0)//";charset="+encode) //html file
            {
                if ((content = cm.retrieveFile(file)) != null) {
                    // System.out.println("heeraaaaaaaaaaaaaaaaaa");
                    printHtml(o, content, type);
                }
            } else {                                              //other file
                if ((data = cm.retrieveObject(file)) != null)
                    printData(o, data, type);
            }
        }
        if (content == null && data == null)      //no file out,
        {
            content = "sorry, this page doesn't exist";
            printHtml(o, content, type);
        }
        in.close();
    }

    private byte[] getJchmData(String filename) {
        byte[] b = null;
        try {
            File f = new File(filename);
            DataInputStream fr = new DataInputStream(new FileInputStream(f));
            b = new byte[(int) f.length()];
            fr.read(b);
            fr.close();
            // f.delete();
        } catch (Exception e) {

        }
        return b;

    }

    private String getJchmHtml(String filename) {
        String content = null;
        try {
            File f = new File(filename);
            FileReader fr = new FileReader(f);
            char[] c = new char[(int) f.length()];
            fr.read(c);
            fr.close();
            content = new String(c);
            //f.delete();
        } catch (Exception e) {

        }
        return content;
    }

    private void printHtml(OutputStream o, String content, String type) {
        if (content != null) {
            try {
                PrintWriter out = new PrintWriter(o);
                out.println("HTTP/1.0200 OK");
                out.println("Content-Type: " + type);
                out.println("Connection: close");
                out.println("");
                out.println(content);
                out.close();
                //in.close();
            } catch (Exception e) {
            }
        }
    }

    private void printData(OutputStream o, byte[][] data, String type) {
        if (data != null) {
            try {
                DataOutputStream out = new DataOutputStream(o);
                out.writeBytes("HTTP/1.0200 OK\r\n");
                out.writeBytes("Content-Type: " + type + "\r\n");
                int len = 0;
                for (int j = 0; j < data.length; j++)
                    len += data[j].length;
                out.writeBytes("Content-Length: " + Integer.toString(len) + "\r\n");
                out.writeBytes("Connection: close\r\n");
                out.writeBytes("\r\n");
                for (int j = 0; j < data.length; j++)
                    out.write(data[j]);
                out.close();
                //in.close();
            } catch (Exception e) {
            }
        }
    }
}

    /*
    public void sserve(InputStream i, OutputStream o) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(i));
        PrintWriter out = new PrintWriter(o);
        String line, file = null, content = "sorry, this page doesn't exist";
        if ((line = in.readLine()) != null) {
            file = line.substring(line.indexOf('/'), line.indexOf(" HTTP/")).trim();
        }
        System.out.println("..................yes,i'm getting " + file);

        String sf = file.toLowerCase();
        String type = "text/html";  //;charset="+encode;
        if (sf.endsWith(".gif")) type = "image/gif";
        if (sf.endsWith(".jpg")) type = "image/jpg";

        out.println("HTTP/1.0200 OK");
        out.println("Content-Type: " + type);


        byte[][] data;
        try {

            if (cm != null) {
                if (type.compareTo("text/html") == 0)//";charset="+encode) //html file
                {
                    out.println("Connection: close");
                    out.println("");
                    content = cm.retrieveFile(file);
                    if (content == null)
                        out.print(sorry + file);
                    else {
                        //int start,stop;
                        // if((start=content.indexOf("<BODY"))==-1) start=content.indexOf("<body");
                        //  if((stop=content.indexOf("</BODY>"))==-1) stop=content.indexOf("</body>");
                        //  if (start!=-1&&stop!=-1)
                        //   content="<HTML>"+content.substring(start,stop+7)+"</HTML>"; //kick off head
                        out.print(content);
                    }
                } else {
                    data = cm.retrieveObject(file);
                    if (data == null)
                        out.print(sorry + file);
                    else {
                        int len = 0;
                        for (int j = 0; j < data.length; j++)
                            len += data[j].length;
                        out.println("Content-Length: " + Integer.toString(len));
                        out.println("Connection: close");
                        out.println("");
                        DataOutputStream fw = new DataOutputStream(new FileOutputStream("pica"));
                        for (int j = 0; j < data.length; j++)
                            for (int k = 0; k < data[j].length; k++) {
                                out.print((char) data[j][k]);
                                fw.writeByte(data[j][k]);
                            }
                        fw.close();
                    }
                }
            } else
                out.print(sorry + file);
        } catch (Exception e) {

        }
        out.close();
        in.close();
    }
    */
