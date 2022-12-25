package gg.dragonfruit.network.connection;

import java.util.List;

import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.util.collection.GlueList;
import gg.dragonfruit.util.network.PacketUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Connection extends SimpleChannelInboundHandler<Packet.INCOMING> {
    public static List<Connection> LIST = new GlueList<>();
    Channel channel;

    public void sendPacket(Packet.OUTGOING... packets) {
        for (Packet.OUTGOING packet : packets) {
            channel.write(PacketUtil.serialize(packet));
        }

        channel.flush();
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
    }

    public void close() {
        channel.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet.INCOMING received) throws Exception {
        received.received(this);
    }
}
