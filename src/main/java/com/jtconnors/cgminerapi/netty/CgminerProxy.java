/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/*
 * Code lifted and modified from the Netty Project as indicated by license
 * above.
 */

package com.jtconnors.cgminerapi.netty;

import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jtconnors.cgminerapi.CLArgs;
import com.jtconnors.cgminerapi.Util;
import static com.jtconnors.cgminerapi.CLArgs.*;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
 
public final class CgminerProxy {

    private static final String PROGNAME= "cgminerProxy";

    private static CLArgs clArgs;

    static {
        clArgs = new CLArgs(MethodHandles.lookup().lookupClass(), PROGNAME);
        clArgs.addAllowableArg(LOCALPORT, "49er");
        clArgs.addAllowableArg(REMOTEHOST, "4028");
        clArgs.addAllowableArg(REMOTEPORT, "4028");
        clArgs.addAllowableArg(DEBUGLOG, "false");
    }
 
    public static void main(String[] args) throws Exception {
        int localPort;
        String remoteHost;
        int remotePort;
        boolean debugLog;

        clArgs.parseArgs(args);
        localPort = Integer.parseInt(clArgs.getProperty(LOCALPORT));
        remoteHost = clArgs.getProperty(REMOTEHOST);
        remotePort = Integer.parseInt(clArgs.getProperty(REMOTEPORT));
        debugLog = Boolean.parseBoolean(clArgs.getProperty(DEBUGLOG));
        if (!debugLog) {
            Logger.getLogger("io.netty").setLevel(Level.OFF);    
        }
        Util.checkHostValidity(remoteHost);
        
        System.err.println("Proxying *:" + localPort + " to " + 
            remoteHost + ':' + remotePort + " ...");
 
        // Configure the bootstrap.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new CgminerProxyInitializer(remoteHost, remotePort))
            .childOption(ChannelOption.AUTO_READ, false);

            Channel ch = b.bind(localPort).sync().channel();

            /*
             * Print out elasped time it took to get to here.  For argument's 
             * sake we'll call this the startup time.
             */
            System.err.println("Startup time = " +
                    (System.currentTimeMillis() -
                    ManagementFactory.getRuntimeMXBean().getStartTime()) +
                    "ms");
                    
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
