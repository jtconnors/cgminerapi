/*
 * Copyright (c) 2020, Jim Connors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of this project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jtconnors.cgminerapi;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Command-line argument processing
 */
public class CLArgs {

    public static final String RESOURCE_NAME = "/cgminerapi.properties";

    public static final String HELP = "help";
    public static final String CGMINERHOST = "cgminerHost";
    public static final String CGMINERPORT = "cgminerPort";
    public static final String HTTPPORT = "httpPort";
    public static final String HTTPSPORT = "httpsPort";
    public static final String LOCALPORT = "localPort";
    public static final String REMOTEHOST = "remoteHost";
    public static final String REMOTEPORT = "remotePort";
    public static final String SSL = "ssl";
    public static final String DEBUGLOG = "debugLog";

    public static final String DASH = "-";
    public static final String DASH_HELP = "-" + HELP;
    public static final String DASH_CGMINERHOST = "-" + CGMINERHOST;
    public static final String DASH_CGMINERPORT = "-" + CGMINERPORT;
    public static final String DASH_HTTPPORT = "-" + HTTPPORT;
    public static final String DASH_HTTPSPORT = "-" + HTTPSPORT;
    public static final String DASH_LOCALPORT = "-" + LOCALPORT;
    public static final String DASH_REMOTEHOST = "-" + REMOTEHOST;
    public static final String DASH_REMOTEPORT = "-" + REMOTEPORT;
    public static final String DASH_SSL = "-" + SSL;
    public static final String DASH_DEBUGLOG = "-" + DEBUGLOG;

    private static Map<String, String> helpStrMap;

    /*
     * Associate a printable help string with each Command-line option
     */
    static {
        helpStrMap = new HashMap<>();
        helpStrMap.put(DASH_HELP,
            "  -help\n" +
            "\tPrint this screen for command-line argument options and exit");
        helpStrMap.put(DASH_CGMINERHOST,
            "  -cgminerHost:HOSTNAME (default: localhost)\n" +
            "\tSpecify hostname (or IP Address) of socket");
        helpStrMap.put(DASH_CGMINERPORT,
            "  -cgminerPort:PORT_NUMBER (default 4028)\n" +
            "\tSpecify port for socket connection to cgminer");
        helpStrMap.put(DASH_HTTPPORT,
            "  -httpPort:PORT_NUMBER (default 8000)\n" +
            "\tSpecify http server port");
        helpStrMap.put(DASH_HTTPSPORT,
            "  -httpsPort:PORT_NUMBER (default 8001)\n" +
            "\tSpecify HTTPS server port");
        helpStrMap.put(DASH_LOCALPORT,
            "  -localPort:PORT_NUMBER (default 4028)\n" +
            "\tSpecify proxy local port number");
        helpStrMap.put(DASH_REMOTEHOST,
            "  -remoteHost:HOSTNAME (default jtconnors.com)\n" +
            "\tSpecify proxy remote hostname (or IP Address)");
        helpStrMap.put(DASH_REMOTEPORT,
            "  -remotePort:PORT_NUMBER (default 4028)\n" +
            "\tSpecify proxy remote port number");
        helpStrMap.put(DASH_SSL,
            "  -ssl:{true|false} (default false)\n" +
            "\tEnable|Disable SSL/HTTPS");
        helpStrMap.put(DASH_DEBUGLOG,
            "  -debugLog:{true|false} (default false)\n" +
            "\tEnable|Disable debug logging");
    }

    private Properties properties;
    private String progName;
    private Set<String> clArgsSet;

    /**
     * Initializes a newly created {@code CLArgs} instance.  {@code CLArgs}
     * instances include a {@code Properties} object where individual properties
     * are stored in the following format: {@code progName.key=value}.
     * Initial propery values are read from a resource file that is bundled
     * relative to this application's project or jar file.
     * 
     * @param clazz {@code Class} instance of caller of this constructor
     * @param progName Progam name used by {@code properties} to store and
     * retrieve key-value pairs.
     */
    public CLArgs(Class<?> clazz, String progName) {
        this.progName = progName;
        this.clArgsSet = new HashSet<>();
        clArgsSet.add(DASH_HELP);
        properties = new Properties();
        try {
            properties.load(clazz.getResourceAsStream(RESOURCE_NAME));
        } catch (IOException e)  {
            e.printStackTrace();
        }
    }

    /**
     * Get the value in the {@code CLArgs} instance associated with the 
     * property named {@code key}
     * 
     * @param key the hashtable key 
     * @return the value in the properties list with the specified key value
     */
    public String getProperty(String key) {
        return properties.getProperty(progName + "." + key);
    }

    /**
     * Get the value in the {@code CLArgs} instance associated with the 
     * property named {@code key}.  If it doesn't exist, return
     * the value specified by the {@code defaultValue} argument
     * 
     * @param key the hashtable key
     * @param defaultValue a default value
     * @return
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(progName + "." + key, defaultValue);
    }

    /**
     * Add the specified argument to the set of allowable command-line arguments
     * for this {@Code CLargs} instance and check to see if it contains a
     * property with the specified {@code key} parameter.  If not, add a new
     * property, specified by the {@code key} parameter, with a default value
     * specified by the {@code value} parameter
     * 
     * @param key the hashtable key
     * @param value the default value associated with the key
     */
    public void addAllowableArg(String key, String value) {
        if (properties.getProperty(key) == null) {
            properties.setProperty(key, value);
        }
        clArgsSet.add(DASH + key);
    }

    /**
     * Puts the key-value pair represented by the {@code key} and {@code value}
     * arguments into properties associated with this {@code CLArgs}
     * instance
     * 
     * @param key the hashtable key
     * @param value the value corresponding to key
     */
    public void setProperty(String key, String value) {
        properties.setProperty(progName + "." + key, value);
    }

    /**
     * Check if an argument appears on the list of command-line args
     *
     * @param arg the argument in question
     * @param args the list of command line arguments
     * @return {@code true} if argument appears on the command-line, otherwise
     * {@code false}
     */
    public static boolean isOnCmdLine(String arg, String[] args) {
        for (String cmdLineArg : args) {          
            String[] subarg = cmdLineArg.split(":");
            if (subarg[0].equals(arg)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return the value associated with an argument contained within a list of
     * command-line arguments ({@code args}).  Command-line arguments for this
     * method are expected to be of the form "-argument:value".  If no such
     * argument exists in the {@code args} list matching this form,
     * return {@code null}, otherwise return the associated value.
     *
     * @param arg the argument in question
     * @param args the list of command line arguments
     * @return the value associated with an argument on the {@code args} list,
     * or {@code null} if no such argument exists in the list
     */
    public static String getArgValue(String arg, String[] args) {
        for (String cmdLineArg : args) {          
            String[] subarg = cmdLineArg.split(":");
            if (subarg[0].equals(arg) && subarg.length > 1) {
                return subarg[1];
            }
        }
        return null;
    }

    /**
     * Process the Command-line arguments and set {@code CLArgs} instance
     * properties accordingly
     * @param args the list cf command-line arguments
     */
    public void parseArgs(String[] args) {
        if (isOnCmdLine(DASH_HELP, args)) {
            System.out.println("Command-line options:");
            for (String option : clArgsSet) {
                System.out.println(helpStrMap.get(option));
            }
            System.exit(0);
        }
        if (isOnCmdLine(DASH_CGMINERHOST, args)) {
            setProperty(CGMINERHOST, getArgValue(DASH_CGMINERHOST, args));
        }
        if (isOnCmdLine(CLArgs.DASH_CGMINERPORT, args)) {
            setProperty(CGMINERPORT, getArgValue(DASH_CGMINERPORT, args));
        }
        if (isOnCmdLine(CLArgs.DASH_LOCALPORT, args)) {
            setProperty(LOCALPORT, getArgValue(DASH_LOCALPORT, args));
        }
        if (isOnCmdLine(CLArgs.DASH_REMOTEHOST, args)) {
            setProperty(REMOTEHOST, getArgValue(DASH_REMOTEHOST, args));
        }
        if (isOnCmdLine(CLArgs.DASH_REMOTEPORT, args)) {
            setProperty(REMOTEPORT, getArgValue(DASH_REMOTEPORT, args));
        }
        if (isOnCmdLine(CLArgs.DASH_HTTPPORT, args)) {
            setProperty(HTTPPORT, getArgValue(DASH_HTTPPORT, args));
        }
        if (isOnCmdLine(CLArgs.DASH_HTTPSPORT, args)) {
            setProperty(HTTPSPORT, getArgValue(DASH_HTTPSPORT, args));
        }
        if (isOnCmdLine(CLArgs.DASH_SSL, args)) {
            setProperty(SSL, getArgValue(DASH_SSL, args));    
        }
        if (isOnCmdLine(CLArgs.DASH_DEBUGLOG, args)) {
            setProperty(DEBUGLOG, getArgValue(DASH_DEBUGLOG, args));    
        }
    }    
}