package com.jtconnors.cgminerapi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class Globals {

    private static Globals singleton = new Globals();

    private Globals() {
    }

    public static Globals getInstance() {
        return singleton;
    }

    public static final String API_VERSION = "4.10.0";

    public static int cgminerPort;
    public static String cgminerHost;
    public static int httpPort;

    static {
        Properties properties = new Properties();
        try {
            properties.load(Globals.class.getResourceAsStream("/cgminerapi.properties"));
        } catch (IOException e)  {
            e.printStackTrace();
        }
        cgminerPort = Integer.parseInt(properties.getProperty("cgminerPort", "4028"));
        cgminerHost = System.getProperty("cgminerHost", "localhost");
        httpPort = Integer.parseInt(properties.getProperty("httpPort", "8000"));
    }

    /*
     * Command-line arguments help message supplied if user specifies
     * either "-help" or "--help" on command-line"
     */
    private static String[] helpMsg = {
        "Command-line options:\n",
        "  -cgminerHost:HOSTNAME (default: localhost)",
        "\t\tSpecify hostname (or IP Address) of socket",
        "  -cgminerPort:PORT_NUMBER (default 4028)",
        "\t\tSpecify port for socket connection to cgminer",
        "  -httpPort:PORT_NUMBER (default 8000)",
        "\t\t*ONLY VALID FOR HTTP SERVER*, Specify http server port",
        "  -help or --help",
        "\t\tPrint this screen for command-line argument options and exit",
        ""
    };

    private static void printCmdLineHelpMsg() {
        for (String str : helpMsg) {
            System.out.println(str);    
        }
    }

    /*
     * Parse command-line arguments in one central place, as there are many
     * main methods which could use these common flags.
     */
    public static void parseArgs(String[] args) {

        try {
            InetAddress.getLocalHost(); 
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        for (String arg : args) {
            if (arg.equals("-help") || arg.equals("--help")) {
                printCmdLineHelpMsg() ;
                System.exit(0);
            }          
            String[] subarg = arg.split(":");
            if (subarg[0].equals("-cgminerHost") && (subarg.length > 1)) {
                try {
                    InetAddress.getByName(subarg[1]);
                    cgminerHost = String.valueOf(subarg[1]);
                } catch (UnknownHostException e) {
                    System.out.println("Bad IP address: " +
                       subarg[1] + " supplied by command-line.");
                }
            } else if (subarg[0].equals("-cgminerPort") && (subarg.length > 1)) {
                cgminerPort = Integer.parseInt(subarg[1]); 
            } else if (subarg[0].equals("-httpPort") && (subarg.length > 1)) {
                httpPort = Integer.parseInt(subarg[1]); 
            }
        }
    }

    public static int getCgminerPort() {
        return cgminerPort;
    }

    public static void setCgminerPort(int cgminerPort) {
        Globals.cgminerPort = cgminerPort;
    }

    public static String getCgminerHost() {
        return cgminerHost;
    }

    public static void setCgminerHost(String host) {
        Globals.cgminerHost = host;
    }

    public static int getHttpPort() {
        return httpPort;
    }

    public static void setHttpPort(int httpPort) {
        Globals.httpPort = httpPort;
    }
}
