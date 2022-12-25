package gg.dragonfruit.network;

import java.io.IOException;
import java.net.InetSocketAddress;

import gg.dragonfruit.network.connection.Connection;
import gg.dragonfruit.network.packet.handler.AutoReadHolderHandler;
import gg.dragonfruit.network.packet.handler.PacketDecoder;
import gg.dragonfruit.network.packet.registry.IncomingPacketRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Client {
    static EventLoopGroup GROUP = new NioEventLoopGroup();

    /**
     * Starts the client.
     * 
     * @param host
     * @param port
     * 
     * @throws IOException
     * @throws InterruptedException
     * 
     * @return Close future.
     */

    public static ChannelFuture start(String host, int port)
            throws IOException, InterruptedException {

        IncomingPacketRegistry.setInstance(new IncomingPacketRegistry() {

            @Override
            public void registerPackets() {
                // TODO Auto-generated method stub

            }

        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Client.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(GROUP)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.config().setTcpNoDelay(true);
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder", new PacketDecoder())
                                .addLast("flow_handler", new AutoReadHolderHandler());
                        pipeline.addLast("packet_handler", new Connection());
                    }
                });
        ChannelFuture f = bootstrap.connect(host, port).sync();
        System.out.println("Client has started...");
        return f.channel().closeFuture();
    }

    public static void stop() throws InterruptedException {
        GROUP.shutdownGracefully().sync();
    }
}
