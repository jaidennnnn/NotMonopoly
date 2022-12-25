package gg.dragonfruit.network.packet.handler;

import java.util.List;

import gg.dragonfruit.util.network.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PacketDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> packets)
            throws Exception {
        if (!channelHandlerContext.channel().isActive() || !buf.isReadable()) {
            buf.release();
            return;
        }
        packets.addAll(PacketUtil.deserialize(buf));
    }
}
