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

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
 
public final class CgminerProxy {

    static int localPort;
    static String remoteHost;
    static int remotePort;

    static {
        Properties properties = new Properties();
        try {
            properties.load(CgminerProxy.class.getResourceAsStream("/cgminerapi.properties"));
        } catch (IOException e)  {
            e.printStackTrace();
        }
        localPort = Integer.parseInt(System.getProperty("cgminerProxy.localPort", "4028"));
        remoteHost = System.getProperty("cgminerProxy.remoteHost", "49er");
        remotePort = Integer.parseInt(System.getProperty("cgminerProxy.remotePort", "4028"));
    }
 
    public static void main(String[] args) throws Exception {
        // Disable Netty Logging
        Logger.getLogger("io.netty").setLevel(Level.OFF);
        System.err.println("Proxying *:" + localPort + " to " + remoteHost + ':' + remotePort + " ...");
 
        // Configure the bootstrap.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new CgminerProxyInitializer(remoteHost, remotePort))
            .childOption(ChannelOption.AUTO_READ, false)
            .bind(localPort).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
