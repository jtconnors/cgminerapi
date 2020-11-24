package com.jtconnors.cgminerapi.http;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.management.ManagementFactory;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.jtconnors.cgminerapi.APIConnection;
import com.jtconnors.cgminerapi.Command;
import com.jtconnors.cgminerapi.Globals;
import com.jtconnors.cgminerapi.InvalidQueryStringException; 
import com.jtconnors.cgminerapi.Util;

public class CgminerHttpServer {

    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static final String CONTEXT = "/cgminer";

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String queryStr = exchange.getRequestURI().getQuery();
        LOGGER.log(Level.INFO, "http query string = {0}", queryStr);
        try (OutputStream os = exchange.getResponseBody()) {
            try {
                String jsonCommandStr = Command.parseQueryString(queryStr).toJSONString();
                LOGGER.log(Level.INFO, "JSON  command = {0}", jsonCommandStr);
                String replyStr =
                    new APIConnection(Globals.cgminerHost, Globals.cgminerPort)
                        .apiCall(jsonCommandStr);      
                exchange.sendResponseHeaders(200, replyStr.length());
                os.write(replyStr.getBytes());
            } catch (InvalidQueryStringException e) {
                String errMsg = Util.exceptionStackTraceToString(e);
                LOGGER.log(Level.SEVERE, "{0}", errMsg);
                exchange.sendResponseHeaders(400, errMsg.length());
                os.write(errMsg.getBytes());
            } finally {
                LOGGER.log(Level.INFO, "\nMemory usage = {0}", 
                    Runtime.getRuntime().totalMemory() -
                    Runtime.getRuntime().freeMemory());
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Globals.parseArgs(args);
        LOGGER.log(Level.INFO, "cgminerHost = {0}", Globals.cgminerHost);
        LOGGER.log(Level.INFO, "cgminerPort = {0}", Globals.cgminerPort);
        LOGGER.log(Level.INFO, "httpPort = {0}", Globals.httpPort);
        HttpServer server = HttpServer.create(
            new InetSocketAddress(Globals.httpPort), 0);
        HttpContext context = server.createContext(CONTEXT);
        context.setHandler(CgminerHttpServer::handleRequest);
        /*
         * Print out elasped time it took to get to here.  For argument's sake
         * we'll call this the startup time.
         */
        LOGGER.log(Level.INFO, "\nStartup time = {0} milliseconds", 
            System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime());

        server.start();
    }
    
}
