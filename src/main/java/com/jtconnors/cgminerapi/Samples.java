package com.jtconnors.cgminerapi;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import static com.jtconnors.cgminerapi.CLArgs.*;

import javax.json.Json;

public class Samples {

    private static final Logger LOGGER = 
            Logger.getLogger("com.jtconnors.cgminerapi");

    private static final String PROGNAME= "samples";
    private static CLArgs clArgs;

    static {
        clArgs = new CLArgs(MethodHandles.lookup().lookupClass(), PROGNAME);
        clArgs.addAllowableArg(CGMINERHOST, "localhost");
        clArgs.addAllowableArg(CGMINERPORT, "4028");
        clArgs.addAllowableArg(DEBUGLOG, "false");
    }
    
    private static void printParseReply(List<Reply> parseReply) {
        if (parseReply.size() <= 1) {
            for (Reply reply : parseReply) {
                LOGGER.log(Level.INFO, "Parsed Reply = {0}\n", reply);
            } 
        } else {
            for (int i=0; i<parseReply.size()-1; i++) {
                LOGGER.log(Level.INFO, "Parsed Reply = {0}", parseReply.get(i));
            }
            LOGGER.log(Level.INFO, "Parsed Reply = {0}\n",
                parseReply.get(parseReply.size()-1));
        }      
    }
    
    public static void main(String[] args) throws IOException {
        String cgminerHost;
        int cgminerPort;
        boolean debugLog;
        /*
         * Print out elasped time it took to get to here.  For argument's sake
         * we'll call this the startup time.
         */
        System.err.println("Startup time = " +
                (System.currentTimeMillis() -
                ManagementFactory.getRuntimeMXBean().getStartTime()) + "ms");

        clArgs.parseArgs(args);
        cgminerHost = clArgs.getProperty(CGMINERHOST);
        cgminerPort = Integer.parseInt(clArgs.getProperty(CGMINERPORT));
        debugLog = Boolean.parseBoolean(clArgs.getProperty(DEBUGLOG));
        if (!debugLog) {
            LOGGER.setLevel(Level.OFF);
        }
        Util.checkHostValidity(cgminerHost);
		APIConnection apiConn = new APIConnection(cgminerHost, cgminerPort); 
        LOGGER.log(Level.INFO, "cgminerHost = {0}", cgminerHost);
        LOGGER.log(Level.INFO, "cgminerPort = {0}", cgminerPort);
        LOGGER.log(Level.INFO, "debugLog = {0}\n", debugLog);
        
        // Issue a SUMMARY command using the Command class methods and
        // equest enum.  Convert to JSON String.
        String jsonString = new Command(Request.SUMMARY, null).toJSONString();
        String replyStr = apiConn.apiCall(jsonString);
        JSONParser parser = new JSONParser(replyStr);
        printParseReply(parser.parseReply());
        
        // Issue another SUMMARY command, this time using the Json class methods
        jsonString = Json.createObjectBuilder()
                .add("command", "summary")
                .build()
                .toString();
        replyStr = apiConn.apiCall(jsonString);
        parser = new JSONParser(replyStr);
        printParseReply(parser.parseReply());        
        
        // Issue a DEVS Command int JSON format
        jsonString = Json.createObjectBuilder()
                .add("command", "devs")
                .build()
                .toString();
        replyStr = apiConn.apiCall(jsonString);
        parser = new JSONParser(replyStr);
        printParseReply(parser.parseReply());
        
        // Issue an ASCDISABLE command on AISC 0
        jsonString = new Command(Request.ASCDISABLE, "0").toJSONString();
        replyStr = apiConn.apiCall(jsonString);
        parser = new JSONParser(replyStr);
        printParseReply(parser.parseReply());
        
        // Issue an ASCENABLE command on AISC 0
        jsonString = new Command(Request.ASCENABLE, "0").toJSONString();
        replyStr = apiConn.apiCall(jsonString);
        parser = new JSONParser(replyStr);
        printParseReply(parser.parseReply());

        LOGGER.log(Level.INFO, "Memory usage = {0}", 
                Runtime.getRuntime().totalMemory() -
                Runtime.getRuntime().freeMemory());
    } 
}

