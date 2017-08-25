/*
 *JChmServer.java
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
//package jchmlib;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author yufeng
 * when jchmwindow run
 * it will be started to serve
 */

public class JChmServer {
    ThreadGroup threadGroup;
    Listener listener;

    /**
     * This is the Server() constructor.  It must be passed a stream
     * to send log output to (may be null), and the limit on the number of
     * concurrent connections.
     **/
    public JChmServer(JChmReader cr) {
        threadGroup = new ThreadGroup(JChmServer.class.getName());
        try {
            listener = new Listener(threadGroup, cr.port, cr);
            listener.start();
            //this.maxConnections = maxConnections;
        } catch (Exception e) {

        }
    }

    public void stop() {
        if (listener != null) listener.close();
    }
}

/**
 * this is the server's listener
 */
class Listener extends Thread {
    ServerSocket listen_socket;    // The socket to listen for connections
    int port;                      // The port we're listening on
    JChmReader service;               // The service to provide on that port
    volatile boolean stop = false; // Whether we've been asked to stop

    public Listener(ThreadGroup group, int port)
            throws IOException {
        super(group, "Listener:" + port);
        listen_socket = new ServerSocket(port);
        // give it a non-zero timeout so accept() can be interrupted
        listen_socket.setSoTimeout(600000);
        this.port = port;
        // this.service = cr;
    }

    public void setService(JChmReader cr) {
        service = cr;
    }

    public Listener(ThreadGroup group, int port, JChmReader cr)
            throws IOException {
        super(group, "Listener:" + port);
        listen_socket = new ServerSocket(port);
        // give it a non-zero timeout so accept() can be interrupted
        listen_socket.setSoTimeout(600000);
        this.port = port;
        this.service = cr;
    }

    public void close() {
        this.stop = true;              // Set the stop flag
        this.interrupt();              // Stop blocking in accept()
        try {
            listen_socket.close();
        } // Stop listening.
        catch (IOException e) {
        }
    }

    /**
     * start to listen the socket
     */
    public void run() {
        while (!stop) {      // loop until we're asked to stop.
            try {
                Socket client = listen_socket.accept();
                Connection c = new Connection(client, service);
                c.start();
            } catch (InterruptedIOException e) {
            } catch (IOException e) {
            }
        }
    }
}

/**
 * it will handle the connection
 */
class Connection extends Thread {
    Socket client;     // The socket to talk to the client through
    JChmReader cr;   // The service being provided to that client

    public Connection(Socket client, JChmReader cr) {
        super("Server.Connection:" +
                client.getInetAddress().getHostAddress() +
                ":" + client.getPort());
        this.client = client;
        this.cr = cr;
    }

    public Connection(Socket client) {
        super("Server.Connection:" +
                client.getInetAddress().getHostAddress() +
                ":" + client.getPort());
        this.client = client;
        //this.cr= cr;
    }

    public void setService(JChmReader jcr) {
        cr = jcr;
    }

    public void run() {
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            cr.serve(in, out);
        } catch (IOException e) {
        }
    }
}
