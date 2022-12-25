package gg.dragonfruit.util.network;

import java.util.List;
import java.util.concurrent.Callable;

import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.packet.Packet.INCOMING;
import gg.dragonfruit.network.packet.registry.IncomingPacketRegistry;
import gg.dragonfruit.util.collection.GlueList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketUtil {

    public static List<Packet.INCOMING> deserialize(ByteBuf data)
            throws Exception {
        List<Packet.INCOMING> packets = new GlueList<>();

        for (ByteBuf packetBuf : splitPackets(data)) {
            int id = ByteBufUtil.readVarInt(packetBuf);

            Callable<INCOMING> packetBuilder = IncomingPacketRegistry.instance.get(id);
            Packet.INCOMING packet = packetBuilder.call();
            packet.deserialize(packetBuf);
            packets.add(packet);
        }

        data.discardReadBytes();
        return packets;
    }

    public static ByteBuf serialize(Packet.OUTGOING packet) {
        ByteBuf data = Unpooled.buffer();
        ByteBufUtil.writeVarInt(data, packet.getId());
        packet.serialize(data);
        int length = data.writerIndex();
        int lengthSize = ByteBufUtil.getVarIntSize(length);
        ByteBuf os = Unpooled.buffer(lengthSize + length);
        ByteBufUtil.writeVarInt(os, length);
        os.writeBytes(data);
        return os;
    }

    public static List<ByteBuf> splitPackets(ByteBuf buf) {
        List<ByteBuf> bufList = new GlueList<>();
        buf.markReaderIndex();
        byte[] lengthBytes = new byte[3];

        for (int position = 0; position < lengthBytes.length; ++position) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return bufList;
            }

            lengthBytes[position] = buf.readByte();

            if (lengthBytes[position] < 0) {
                continue;
            }

            ByteBuf byteBuf = Unpooled.wrappedBuffer(lengthBytes);

            try {
                int length = ByteBufUtil.readVarInt(byteBuf);
                if (buf.readableBytes() < length) {
                    buf.resetReaderIndex();
                    return bufList;
                }
                bufList.add(buf.readBytes(length));
            } finally {
                byteBuf.release();
            }
            return bufList;
        }
        return bufList;
    }
}
